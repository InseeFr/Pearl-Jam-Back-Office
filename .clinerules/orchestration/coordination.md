# Coordination des Agents

Ce document définit **comment l'orchestrateur choisit et enchaîne les agents**.
Il complète `orchestrateur.md` (identité, contexte, entry points) en isolant
la mécanique de coordination, pour faciliter les évolutions indépendantes.

## Agents Disponibles

| Agent | Fichier | Rôle court |
|---|---|---|
| LeCheckListeur | `agents/checklisteur.md` | Découpe les tâches, maintient `checklist.md` |
| LeCodeur | `agents/codeur.md` | Implémente le code (jamais les tests) |
| LeTesteur | `agents/testeur.md` | Écrit les tests (couverture 100%) |
| LeSuperviseurDeRegressions | `agents/superviseur-regressions.md` | Analyse les échecs de tests |
| LeRéparateur | `agents/reparateur.md` | Corrige les régressions |
| LeSuperviseurDeTache | `agents/superviseur-tache.md` | Valide la complétion, lance le build |
| LeRefactoAnalyste | `agents/refacto-analyste.md` | Propose un plan d'amélioration |
| LeRefactoAnalysteChallenger | `agents/refacto-challenger.md` | Challenge le plan de refactoring |

## Règles d'Orchestration

1. **Annonce systématique** — à chaque changement d'étape, affiche :

   ```
   ---
   [NomAgent] prend la main — Étape N du workflow-xxx
   Objectif : [description courte]
   ---
   ```

2. **Checklist vivante** — `checklist.md` est mise à jour à chaque transition
   d'agent. Responsabilité de mise à jour :

   | Action | Responsable |
   |---|---|
   | Création initiale + structure | LeCheckListeur |
   | Ajout de la section tests | LeCheckListeur |
   | Cocher les tâches d'implémentation | LeCodeur |
   | Cocher les tâches de test | LeTesteur |
   | Cocher les tâches de validation | LeSuperviseurDeTache |
   | Marquer le workflow comme terminé | LeSuperviseurDeTache |

   Chaque agent ne coche que **ses propres tâches**. Si un agent constate qu'une
   tâche hors de sa responsabilité est incorrecte, il signale le problème sans
   modifier la checklist.

   **Traçage des modifications** — chaque agent qui modifie `checklist.md` y
   appose un commentaire HTML en fin de fichier au format :

   ```markdown
   <!-- updated by: LeCodeur @ 2026-04-16T14:30 -->
   ```

   Permet de détecter les dérives (agent qui modifie hors de son périmètre) et
   d'auditer le déroulé du workflow a posteriori.

3. **Fichier manquant** — si un fichier `.md` référencé (skill, agent, workflow)
   est absent, alerte l'utilisateur immédiatement et propose de le créer.
   Aucun agent ne continue tant que la ressource n'est pas en place.

4. **Pas d'initiative silencieuse** — ne lance jamais un workflow suivant sans
   accord explicite de l'utilisateur.

## Protocole d'Escalade

Défini en détail dans `skills/escalation.md`. Rappel : **c'est l'orchestrateur
qui porte l'escalade**, pas les agents.

Les agents individuels sont stateless : ils ne savent pas combien de fois ils
ont été invoqués dans la boucle courante. Ils n'ont donc pas besoin de
référencer `skills/escalation.md` dans leur prompt.

Responsabilités de l'orchestrateur :

1. **Compter les itérations** pour chaque boucle identifiée dans le workflow
   actif (ex : allers-retours Réparateur ↔ SuperviseurRegressions).
2. **Tenir le compteur global** (25 interventions maximum par workflow, toutes
   boucles confondues — voir « Limites récapitulatives » ci-dessous pour la
   justification).
3. **Détecter le dépassement** avant de relancer l'agent qui franchirait la limite.
4. **Déclencher l'escalade** lorsque la limite est atteinte :
   - Charger le contenu de `skills/escalation.md` dans le contexte.
   - Demander à l'agent courant de produire le rapport selon le template, **en
     lui fournissant explicitement le template et l'historique des tentatives**.
   - À défaut (agent muet ou indisponible), produire le rapport lui-même à
     partir du journal d'interventions.
5. **Présenter le rapport à l'utilisateur** et attendre une décision avant
   toute reprise.

### Limites récapitulatives

| Workflow | Boucle | Max |
|---|---|---|
| coding | Réparateur ↔ SuperviseurRegressions | 3 |
| coding | Codeur ↔ SuperviseurDeTache (build KO) | 3 |
| testing | Testeur ↔ SuperviseurDeTache (test KO) | 3 |
| refactoring | Analyste ↔ Challenger (plan rejeté) | 2 |

Une limite globale de **25 interventions d'agent par workflow** s'applique en
complément pour prévenir les boucles combinées.

Justification du seuil : un refactoring multi-modules réaliste consomme
~20 interventions (3 itérations × ~6 agents enchaînés : Codeur, Testeur,
SuperviseurRegressions, Réparateur, SuperviseurDeTache, retours CheckListeur).
Le seuil précédent de 15 déclenchait l'escalade prématurément sur des cas
légitimes. 25 garde une marge contre les vraies boucles infinies sans
bloquer la production normale.
