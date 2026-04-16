# Skill : Refactoring — Pearl Jam Back Office

## Objectif

Ce document définit le protocole d'analyse et de refactoring du projet.
Il sert de référence pour les agents LeRefactoAnalyste et LeRefactoAnalysteChallenger.

## Principes Directeurs

1. **SOLID & DRY** : Identifier les violations et la duplication
2. **KISS & YAGNI** : Simplifier, ne pas sur-ingénier
3. **Clean Code** : Nommage, taille des fonctions, gestion d'erreurs
4. **Testabilité** : Le code doit être testable en isolation
5. **Performance & Sécurité** : Fuites mémoire, boucles inefficaces, failles OWASP

## Structure d'Analyse

### 1. Diagnostic de Santé (Vue d'ensemble)

- Note globale (0-10) basée sur la dette technique
- Points forts du code actuel
- Risques critiques identifiés

### 2. Revue Détaillée

Pour chaque problème :

| Champ | Description |
|---|---|
| **Criticité** | Bloquant, Majeur, Mineur |
| **Localisation** | Fichier et ligne(s) |
| **Problème** | Description précise de la violation |
| **Recommandation** | Action corrective concrète |

### 3. Plan d'Implémentation

Roadmap itérative avec :
- Itérations ordonnées par priorité
- Fichiers impactés listés
- Risque de régression estimé
- Étapes d'implémentation détaillées

## Catalogue de Refactorings Courants

### R1 : Extraire un Port Out manquant

**Problème** : Le service domaine accède directement à une entité JPA.

**Avant :**
```java
// AVANT — Dans le domaine, import d'infrastructure (interdit)
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;

public List<InterviewerCountDto> getInterviewerCount(String campaignId) {
    // accès direct à l'entité JPA
}
```

**Après :**
```java
// APRÈS — Port Out dans le domaine
public interface InterviewerCountRepository {
    List<InterviewerCount> countByCampaign(String campaignId);
}

// Read Model dans le domaine
public record InterviewerCount(String interviewerId, String name, long count) {}

// Adaptateur dans l'infrastructure
@Repository
@RequiredArgsConstructor
public class InterviewerCountDaoAdapter implements InterviewerCountRepository {
    private final JdbcClient jdbcClient;

    @Override
    public List<InterviewerCount> countByCampaign(String campaignId) {
        return jdbcClient.sql("""
            SELECT interviewer_id, name, count(*)
            FROM survey_unit WHERE campaign_id = :id
            GROUP BY interviewer_id, name
            """)
            .param("id", campaignId)
            .query((rs, _) -> new InterviewerCount(
                rs.getString("interviewer_id"),
                rs.getString("name"),
                rs.getLong("count")
            ))
            .list();
    }
}
```

### R3 : Découper un Service trop gros (SRP)

**Problème** : Un service mélange autorisation, logique métier et mapping.

**Avant :**
```java
public class StateServiceImpl implements StateService {
    // Méthode de 80 lignes qui fait :
    // 1. Vérification des droits
    // 2. Récupération des données
    // 3. Transformation et mapping
    // 4. Gestion des exceptions
}
```

**Après :**
```java
// Service métier pur
public class StateServiceImpl implements StateService {
    private final StateRepository repository;

    public List<StateCount> getStateCount(String campaignId) {
        return repository.getStateCount(campaignId);
    }
}

// Autorisation dans un aspect ou un service dédié (infrastructure-security)
// Mapping dans un Presenter (api)
// Gestion d'erreurs dans un ExceptionHandler (api)
```

### R4 : Remplacer les DTOs techniques par des Read Models

**Problème** : Le service domaine retourne des DTOs techniques (`StateCountDto`).

**Après :**
```java
// Read Model dans le domaine
public record StateCount(
    String state,
    long count
) {}

// Le service retourne le Read Model
public List<StateCount> getStateCount(String campaignId);

// Le Presenter dans l'API transforme en DTO de réponse
public record StateCountResponse(String state, long count) {
    public static StateCountResponse fromDomain(StateCount model) {
        return new StateCountResponse(model.state(), model.count());
    }
}
```

### R5 : Externaliser le CSV/Export du Contrôleur

**Problème** : Logique d'export CSV dans un contrôleur.

**Après :**
```java
// Service d'export dans l'API (pas dans le domaine — c'est de la présentation)
@Component
@RequiredArgsConstructor
public class CampaignProgressCsvExporter {
    private final CampaignReportingPort reporting;

    public ResponseEntity<byte[]> exportCsv(String campaignId) {
        var stats = reporting.getCampaignsStats(List.of(campaignId));
        var csv = toCsv(stats);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv.getBytes());
    }

    private String toCsv(List<CampaignDailyStats> stats) {
        // logique de transformation CSV
    }
}

// Contrôleur simplifié
@GetMapping("/campaigns/{id}/export")
public ResponseEntity<byte[]> export(@PathVariable String id) {
    return csvExporter.exportCsv(id);
}
```

## Estimation de la Dette Technique

| Note | Niveau | Description |
|---|---|---|
| 9-10 | Excellent | Architecture clean, couverture 100%, 0 violation |
| 7-8 | Bon | Quelques violations mineures, architecture respectée |
| 5-6 | Moyen | Violations majeures isolées, refactoring ponctuel nécessaire |
| 3-4 | Dégradé | Violations systémiques, refactoring structurel nécessaire |
| 0-2 | Critique | Architecture non respectée, code non testable |
