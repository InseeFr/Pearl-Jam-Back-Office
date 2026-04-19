# Context : Pearl Jam — Patterns de tests

> Snapshot. Dernière MAJ : 2026-04-19.
>
> Pour la stratégie générale Fake vs Mock, AssertJ, patterns par couche :
> voir `skills/testing.md`.

## Organisation

### Emplacement des tests

| Type | Emplacement |
|---|---|
| Test unitaire | `[module]/src/test/java/.../[feature]Test.java` |
| Test d'intégration | `pearljam-api/src/test/java/.../[feature]IT.java` |
| Fake | `[module]/src/test/java/.../fake/[Port]Fake.java` |

### Packages de doublures

- **Nouveau code** : `fake/` uniquement
- **Legacy** : `dummy/` et `stub/` coexistent — migrer quand on modifie le test

## Tailles de ports (référentiel projet)

Pour appliquer la règle `≤ 6 méthodes → Fake, > 6 → Mockito` :

| Port | Méthodes | Doublure |
|---|---|---|
| `CommentRepository` | 1 | Fake |
| `DateService` | 1 | Fake (`FixedDateService`) |
| `VisibilityRepository` | 5 | Fake |
| `VisibilityService` | ~10 | Fake pour contrôleur, Mockito sinon |
| `StateRepository` | 19 | Mockito |
| `CampaignService` | 25 | Fake pour contrôleur, Mockito sinon |
| `CampaignRepository` | 28 | Mockito |
| `SurveyUnitRepository` | 32 | Mockito |

**Exception contrôleurs** : toujours Fake (flags/getters > Mockito pour tester HTTP).

## Legacy vs cible

| Aspect | Legacy | Cible |
|---|---|---|
| 404 dans le contrôleur | `if (result == null) return NOT_FOUND` | Exception métier + `ExceptionControllerAdvice` |
| Tests contrôleur | Mockito + `ResponseEntity` direct | Fake + MockMvc + `apiErrorMatches()` |
| Assertions | JUnit `assertEquals`, `assertNull` | AssertJ `assertThat`, `assertThatThrownBy` |
| Nommage tests | `testGetCampaign01()` | `shouldReturnCampaignWhenExists()` |
| Port domain | `Optional<CampaignDB>` (fuite JPA) | `Optional<Campaign>` (modèle domain) |
| Retour service | `null` pour "pas trouvé" | Exception métier |
| Packages doublures | `dummy/`, `stub/` | `fake/` |

**Règle de migration** : ne pas refondre le legacy par principe. Migrer
quand on modifie un test pour une autre raison.

## Utilitaires partagés (à réutiliser, pas réinventer)

| Classe | Usage |
|---|---|
| `MockMvcTestUtils.apiErrorMatches(status, path, msg)` | Vérifier la structure d'erreur API |
| `MockMvcTestUtils.createExceptionControllerAdvice()` | `ExceptionControllerAdvice` partagé |
| `JsonTestHelper.toJson(object)` | Sérialiser pour comparaison JSON |
| `AuthenticatedUserTestHelper.AUTH_ADMIN` | Token admin pré-configuré |
| `FixedDateService` | Fake de `DateService` (timestamp fixe `1735689600000L`) |

## Exemple Fake projet — port sortant

```java
// domain/campaign/service/fake/VisibilityRepositoryFake.java
public class VisibilityRepositoryFake implements VisibilityRepository {

    private final List<Visibility> visibilities = new ArrayList<>();

    public void save(Visibility v) {
        if (!visibilities.contains(v)) visibilities.add(v);
    }

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String ouId) {
        return visibilities.stream()
                .filter(v -> v.campaignId().equals(campaignId))
                .filter(v -> v.organizationalUnitId().equals(ouId))
                .findFirst();
    }

    @Override
    public void updateDates(Visibility update) throws VisibilityNotFoundException {
        var toRemove = findVisibility(update.campaignId(), update.organizationalUnitId())
            .orElseThrow(VisibilityNotFoundException::new);
        visibilities.remove(toRemove);
        visibilities.add(update);
    }
}
```

## Exemple Fake projet — port entrant (pour contrôleur)

```java
// api/campaign/controller/fake/CampaignServiceFake.java
@RequiredArgsConstructor
public class CampaignServiceFake implements CampaignService {

    @Getter private boolean deleted = false;
    @Getter private boolean deleteForced = false;
    @Setter private boolean shouldThrowCampaignNotFoundException = false;

    @Override
    public void delete(String id, boolean force) throws CampaignNotFoundException {
        deleteForced = force;
        if (shouldThrowCampaignNotFoundException) throw new CampaignNotFoundException();
        deleted = true;
    }

    // Méthodes non testées ici → signal clair
    @Override
    public List<CampaignDto> getAllCampaigns() {
        throw new UnsupportedOperationException("Not used in this test");
    }
}
```

## Tests d'architecture

Règles dans `ModuleBoundariesArchTests.java` :

| Règle | Statut |
|---|---|
| API → JPA repositories | Interdit |
| Domain → API | Interdit |
| Domain → Infrastructure (sauf entities) | Toléré temporairement |
| Contracts → API ou Infrastructure | Interdit |
| Infrastructure → API | Interdit |

## Couverture par feature — checklist

- [ ] Cas nominal
- [ ] Chaque exception métier (`assertThatThrownBy`)
- [ ] Cas limites : null, empty, liste vide
- [ ] Branches conditionnelles (if/else, switch)
- [ ] Codes HTTP 200, 400, 404, 409 selon l'endpoint
- [ ] JSON complexe comparé avec JSONAssert
- [ ] État du Fake vérifié après action
- [ ] Ne pas toucher aux tests legacy sauf refactoring explicite
