# Contexte de la tâche


On y retrouve pour chaque UE du site à lire les informations suivantes :

- le libellé de la collecte
- l'identifiant de l'UE
- *l'issue de contact ?? à enlever si on résoud le bug sur l'INA*
- le nom prénom de l'enquêteur en charge de l'UE
- une colonne actions composée de 2 boutons :
    - accès au questionnaire de l'UE en lecture seule. Ouvre le questionnaire dans un nouvel onglet
    - édition du commentaire gestionnaire de l'UE. Ouvre une pop-up permettant de le consulter/modifier ; si un commentaire est renseigné ici, on le retrouvera dans la liste d'UE terminées lorsqu'elle sera validée par la DEM

---

# Tâche à réaliser

- Faire un nouveau controller SurveyUnitToReviewController dans le package  fr.insee.pearljam.api.reporting.controller
- les objets créés doivent contenir le nom SurveyUnitToReview
- l'ancien endpoint présentait des problèmes de performance et on veut intégrer de la pagination dans le nouveau
- 

##### Contrat d’interface

###### Liste des UE à relire

Ancien endpoint utilisé à déprécier: Pour chaque campagne de l'utilisateur: /api/campaign/{campaignId}/survey-units?state=TBR


- **URL** : `GET /api/reporting/survey-units/to-review` 
- **Description**: Récupère les unités à lire pour un site.
- **Pagination** : Implémentée côté back
- **Tri** : Implémenté côté back.
- **Recherche** : Implémentée côté back - Le champ de recherche permet de filtrer les observations affichées. La recherche fonctionne sur le libellé de la campagne, l'identifiant de l'UE, et le nom prénom de l'enquêteur. (insensible à la casse, recherche partielle, multi-champs).

Paramètres de requête (query params)

| Nom      | Type   | Obligatoire | Description |
|----------|--------|------------|-------------|
| `page`   | number | non        | Numéro de page (défaut : 0) |
| `size`   | number | non        | Nombre d’éléments par page (défaut : 10) |
| `sort`   | string | non        | Critère de tri (ex: `campaignLabel,asc`) |
| `search` | string | non        | Terme de recherche appliqué sur :<br>- libellé de campagne<br>- identifiant UE<br>- nom/prénom de l’enquêteur |




Réponse attendue
```
{
  "content": [
    {
      "id": "string",
      "campaignLabel": "string",
      "contactOutcome": ENUMcontactOutcome,
      "interviewerNameLabel": "string",
      "viewed": "boolean",
      "readOnlyUrl" : "string"
      "lastComment": "string"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 2,
   "totalPages": 1
   }
```

ENUMcontactOutcome - > TODO: en français  
INA,//Interview accepted  
REF,//Refusal  
IMP,//Impossible to reach  
UCD,//Unusable Contact Data  
UTR,//Unable To Respond  
ALA,//Already answered  
DUK,//Definitely Unavailable for a Known reason  
NUH,//No longer Used for Habitation  
NOA//Not Applicable


