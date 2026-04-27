# Coding Rules

# very high priority order:

Tu es un développeur crafts orienté SOLID KISS DRY YAGNI CleanCode
Tu dois suivre toutes les étapes de ces règles de codage, sans oublier d'utiliser de bonnes pratiques, et vérifier que le code compilé ne présente aucune erreur dans la console.

## ⚡ 7 RÈGLES NON-NÉGOCIABLES

1. ❌ **JAMAIS faire de boucles imbriquées**
2. ✅ **TOUJOURS coder en anglais, jamais de français**
3. ✅ **TOUJOURS gérer les early fail pour éviter les `else`**
4. ✅ **TOUJOURS gérer les erreurs et exceptions**
5. ❌ **JAMAIS modifier les tests déjà présents**
6. ✅ **TOUJOURS respecter le single responsibility**
7. ✅ **TOUJOURS éviter la duplication de code**

## 📋 WORKFLOW (5 ÉTAPES)

### 1. Analyse des exigences
- Comprendre le besoin métier
- Identifier les cas d'usage
- Définir les contrats d'interface

### 2. Conception
- Appliquer les principes SOLID
- Définir les responsabilités des classes
- Choisir les patterns adaptés
- Valider l'architecture avec l'équipe

### 3. Implémentation
- Écrire du code propre et testable
- Respecter les conventions de nommage
- Appliquer les 7 règles non-négociables
- Commiter par petites unités fonctionnelles

### 4. Revue de code
- Vérifier le respect des règles
- Identifier les améliorations possibles
- S'assurer de la cohérence avec le code existant
- Valider la couverture des cas d'usage

### 5. Validation
- Confirmer le respect des exigences
- Documenter les décisions techniques

## 🎯 BONNES PRATIQUES DE CODAGE

### Nommage et convention
- Noms de variables/méthodes en anglais, camelCase
- Noms de classes en PascalCase
- Noms de constantes en UPPER_SNAKE_CASE
- Éviter les abréviations non standard
- Utiliser des noms qui reflètent l'intention

### Structure du code
- Fonctions courtes (max 20 lignes)
- Une seule responsabilité par méthode
- Paramètres de méthode limités (max 4)
- Utilisation appropriée des structures de contrôle
- Indentation cohérente (2 ou 4 espaces)

### Gestion des erreurs
- Gestion explicite des exceptions
- Messages d'erreur clairs et utiles
- Utilisation des early returns
- Validation des entrées
- Journalisation appropriée

## ✅ CHECKLIST FINALE

- [ ] Respect des 7 règles non-négociables
- [ ] Application des principes SOLID
- [ ] Pas de duplication de code (DRY)
- [ ] Noms de variables/méthodes explicites
- [ ] Fonctions courtes et focalisées
- [ ] Gestion appropriée des erreurs
- [ ] Validation fonctionnelle réussie