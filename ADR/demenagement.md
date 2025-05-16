```mermaid
sequenceDiagram
        actor Enquêté
        actor Platine
        actor Foo Fighters
        actor Queen Back Office
        actor Queen Front
        actor Pearl Back Office
        actor Pearl Front
        Enquêté->>Platine: Indique un déménagement (DEMENAGÉ=1)
        Foo Fighters->>Platine: Batch de Synchronisation
        Foo Fighters->>Queen Back Office: Demande de Reinitialisation du Questionnaire
        Foo Fighters->>Pearl Back Office: Demande de Reinitialisation du Questionnaire
        Pearl Back Office->>Pearl Front: Envoie d'une notification
        Pearl Front->>Pearl Back Office: Reinitialisation lors de la prochaine Synchro
        Queen Front->>Queen Back Office: Reinitialisation lors de la prochaine Synchro

```