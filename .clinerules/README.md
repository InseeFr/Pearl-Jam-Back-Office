# .clinerules — Système d'Agents IA pour Pearl Jam Back Office

## Vue d'ensemble

Ce dossier contient les prompts, skills et workflows pour piloter un LLM
(Mistral Vibe, Claude, GPT) en mode agentique sur le projet Pearl Jam Back Office.

## Structure

```
.clinerules/
├── orchestrateur.md              # Point d'entrée — System Prompt principal
├── agents/                       # Définition de chaque agent spécialisé
│   ├── checklisteur.md           # Découpe les tâches, maintient la checklist
│   ├── codeur.md                 # Implémente le code (jamais les tests)
│   ├── testeur.md                # Écrit les tests (couverture 100%)
│   ├── superviseur-regressions.md # Analyse les échecs de tests
│   ├── reparateur.md             # Corrige les régressions
│   ├── superviseur-tache.md      # Valide la complétion, lance le build
│   ├── refacto-analyste.md       # Propose un plan de refactoring
│   └── refacto-challenger.md     # Challenge le plan de refactoring
├── skills/                       # Connaissances partagées entre agents
│   ├── clean-code.md             # Règles SOLID, Clean Code, conventions Java 25
│   ├── hexagonal-architecture.md # Architecture du projet, règles d'import
│   ├── testing.md                # Standards de tests, patterns par couche
│   ├── refactoring.md            # Catalogue de refactorings, grille d'évaluation
│   └── project-context.md        # Commandes build, config, Liquibase, Docker
├── workflows/                    # Workflows opérationnels
│   ├── workflow-coding.md        # Développement d'une feature
│   ├── workflow-testing.md       # Ajout de tests
│   └── workflow-refactoring.md   # Amélioration de code existant
└── README.md                     # Ce fichier
```

## Utilisation

### Commandes principales

| Commande | Description |
|---|---|
| `workflow-coding [description]` | Développer une nouvelle feature |
| `workflow-testing` | Ajouter les tests pour la dernière feature |
| `workflow-refactoring [fichier]` | Analyser et améliorer du code existant |
| `status` | Voir l'état de la checklist en cours |
| `agent [nom]` | Activer manuellement un agent |

### Configuration pour Mistral Vibe / Cline

1. **System Prompt** : Charger `orchestrateur.md` comme prompt système
2. **Context** : Les fichiers `skills/*.md` sont chargés comme contexte partagé
3. **Agents** : Chaque fichier `agents/*.md` est chargé quand l'agent est activé
4. **Workflows** : Les fichiers `workflows/*.md` décrivent les transitions

### Bonnes Pratiques pour les Prompts

Ces fichiers suivent les bonnes pratiques de prompt engineering :

- **Rôle explicite** : Chaque agent a un rôle clairement défini
- **Few-shot examples** : Des exemples concrets du projet sont inclus
- **Format de sortie structuré** : Chaque agent a un template de réponse
- **Contraintes négatives** : Ce que l'agent ne doit JAMAIS faire
- **Transitions explicites** : Les conditions de passage d'un agent à l'autre

## Contexte Technique

- **Java** : 25
- **Spring Boot** : 4.0.5
- **Architecture** : Hexagonale multi-modules Maven
- **Tests** : JUnit 5 + AssertJ + Mockito + MockMvc
- **CI/CD** : SonarCloud
