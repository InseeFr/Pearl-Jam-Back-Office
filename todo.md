# Plan d'Amelioration — Configuration .clinerules

## Date : 16/04/2026
## Objectif : Optimiser les prompts/skills/workflows pour Pearl Jam Back Office

---

## FAIT

### Session initiale

- [x] Supprimer `workflow_sample.md` (doublon des workflows separes)
- [x] Supprimer `refacto.md` (doublon de `skills/refactoring.md` + `agents/refacto-analyste.md`)
- [x] Ajouter limites d'iteration dans les 3 workflows (anti boucle infinie)
- [x] Ajouter protocole d'escalade dans l'orchestrateur
- [x] Ajouter garde "skill manquant -> STOP" dans codeur, testeur, refacto-analyste, superviseur-tache
- [x] Clarifier responsabilite de mise a jour de `checklist.md` (table dans checklisteur.md)
- [x] Corriger reference `skills/project-context.md` -> `project-context.md` dans orchestrateur
- [x] Retirer emojis des templates de reponse des agents (moins de pollution dans les logs)

### Session du 16/04/2026 (9 commits sur feat/ia-skills)

- [x] Nettoyer emojis residuels dans skills clean-code, refactoring, testing (commit `bd19adf7`)
- [x] Extraire `skills/escalation.md` et factoriser le template BLOCAGE dans les 3 workflows (commit `4b0f0910`)
- [x] Aligner limite Testeur<->SuperviseurDeTache : 5 -> 3 tours (commit `4b0f0910`)
- [x] Remplacer les diagrammes ASCII des workflows par des tables de transition (commit `987d391a`)
- [x] Expliciter orchestrateur = proprietaire du protocole d'escalade (commit `17acdae7`)
- [x] Ajouter garde "skill manquant -> STOP" dans refacto-challenger et superviseur-regressions (commit `3dac7734`)
- [x] Deplacer `project-context.md` dans `skills/` (commit `c1987cb4`)
- [x] Ajouter exemples concrets (few-shot) dans codeur, testeur, superviseur-tache (commit `8a61e876`)
- [x] Ajouter arbre de decision Regression vs Evolution legitime dans superviseur-regressions (commit `c62a447e`)
- [x] Decouper orchestrateur : identite + entry points / `orchestration/coordination.md` (commit `ea316a36`)
- [x] Ajouter limite globale de 15 interventions par workflow (commit `4b0f0910`)

---

## A FAIRE

### Moyenne Priorite

- [ ] Tester un workflow complet de bout en bout et noter les points de friction
- [ ] Ajouter un skill `sonar-rules.md` avec les exceptions specifiques au projet
      (pas une copie de la doc SonarCloud — uniquement les regles custom et faux positifs)
- [x] Enrichir `skills/clean-code.md` avec des exemples specifiques Pearl Jam
      (commit `df9b176b` — magic value "REA", SRP MessageServiceImpl, records reporting)
- [x] Documenter les decisions d'architecture en attente dans `hexagonal-architecture.md`
      section "Points d'Attention" (commit `438da75c` — exemples reels du codebase)

### Basse Priorite

- [ ] Ajouter des tests de non-regression des prompts
      (garder 2-3 scenarios de reference pour verifier que les agents se comportent correctement)
- [ ] Evaluer si le RefactoChallenger justifie un agent dedie
      (sa grille pourrait etre integree dans le RefactoAnalyste en auto-critique)
- [ ] Tracer l'agent qui modifie `checklist.md` via commentaire HTML
      (`<!-- updated by: LeCodeur @ 2026-04-16T14:30 -->`) pour detecter les derives
- [ ] Mecanisme de reprise apres interruption (statut persistant du workflow en cours)

---

## DECISIONS PRISES

| Decision | Date | Raison |
|---|---|---|
| Pas de script `update-avancement.sh` | 16/04 | `checklist.md` suffit pour le tracking |
| Pas de fichier `avancement.md` | 16/04 | Doublon avec checklist.md |
| Pas de diagrammes Mermaid/PlantUML separes | 16/04 | `hexagonal-architecture.md` couvre deja la structure |
| Escalade integree aux workflows, pas d'agent dedie | 16/04 | Un agent "Escalation" serait trop leger |
| Emojis retires des templates agents | 16/04 | Pollution des logs, tokens gaspilles |
| Escalade centralisee dans `skills/escalation.md`, non dupliquee | 16/04 | Factoriser le template BLOCAGE evite ~60 lignes redondantes |
| Orchestrateur = proprietaire de l'escalade (pas les agents) | 16/04 | Agents stateless ne peuvent pas compter leurs propres invocations |
| Limite Testeur<->SuperviseurDeTache : 5 -> 3 | 16/04 | Alignement avec les autres boucles ; 3 echecs = malentendu, pas un flake |
| Diagrammes ASCII remplaces par tables | 16/04 | Plus compact, plus lisible pour un LLM, moins de tokens |
| Exemples few-shot utilisent tous le cas "endpoint interviewers par campagne" | 16/04 | Coherence pedagogique : un meme scenario de bout en bout |
| `project-context.md` dans `skills/` | 16/04 | Alignement avec les autres documents de reference |
| Coordination sortie dans `orchestration/coordination.md` (pas `skills/`) | 16/04 | `skills/` est lu par les agents ; la coordination est lue par l'orchestrateur seul |
