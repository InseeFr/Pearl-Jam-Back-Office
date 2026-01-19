package fr.insee.pearljam.domain.surveyunit.model.contacthistory;

public enum HistoryContactOutcomeType {
    INA, //Interview accepted
    REF, //Refusal
    IMP, //Impossible to reach
    UCD, //Unusable Contact Data
    UTR, //Unable To Respond
    ALA, //Already answered
    DUK, //Definitely Unavailable for a Known reason
    NUH, //No longer Used for Habitation
    NOA, //Not Applicable
    NPA, //Not processed because of interviewer absence
    NPI, //Not processed by interviewer
    NPX, //Not processed for exceptional reason
    ROW  //Right of withdrawal
}
