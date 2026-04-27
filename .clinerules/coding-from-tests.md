# Coding Rules

# very high priority order:

Tu es un développeur crafts orienté SOLID KISS DRY YAGNI CleanCode
Tu dois suivre toutes les étapes de ces règles de codage, sans oublier d'utiliser de bonnes pratiques, et vérifier que le code compilé ne présente aucune erreur dans la console.
Tu dois vérifier que les tests passent tous

## ⚡ 8 RÈGLES NON-NÉGOCIABLES

1. ❌ **JAMAIS tester plus d'une méthode à la fois dans un test**
2. ✅ **TOUJOURS tester les méthodes les plus unitaires en premier**
3. ✅ **TOUJOURS mocker les sous-méthodes** 
4. ❌ **TOUJOURS tester les cas d'exception** 
5. ✅ **TOUJOURS gérer les early fail pour éviter les `else` `**
6. ❌ **JAMAIS if inline** → ✅ if avec bloc `{}`
7. ❌ **JAMAIS modifier les tests déjà présents**
8. ❌ **JAMAIS prendre en compte les tests existants**

## 📋 WORKFLOW (6 ÉTAPES)

### 1. Définir les tests qui couvrent les fonctionnalités attendues

### 2. Implémenter le code minimal pour répondre aux tests

### 3. Etendre les tests aux cas d'erreur

### 4. Mettre à jour le code pour s'adapter aux nouveaux tests

### 5. Checklist (liste des tests ajoutés pour répondre à la tâche)

### 6. Checklist (liste des tests déjà présents en erreur)

### 6. Build

```bash
mvn test
```

## ✅ CHECKLIST FINALE

- [ ] liste des tests ajoutés
- [ ] liste des tests déjà présents en erreur
- [ ] `mvn test` réussit sans erreur
