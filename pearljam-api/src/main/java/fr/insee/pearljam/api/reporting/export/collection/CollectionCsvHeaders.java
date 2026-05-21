package fr.insee.pearljam.api.reporting.export.collection;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum CollectionCsvHeaders {
    // specific headers
    CAMPAIGN_LABEL("Enquête"),
    INTERVIEWER_ID("Idep"),
    INTERVIEWER_LABEL("Nom Prénom enquêteur"),
    ORGANIZATION_UNIT_LABEL("Site"),
    ALLOCATED_SITE("Confiées Site"),
    ALLOCATED_INTERVIEWERS("Confiées Enquêteurs"),


    // common headers
    COLLECTION_RATE("Taux de collecte"),
    WASTE_RATE("Taux de déchet"),
    OUT_OF_SCOPE_RATE("Taux de hors champ"),
    ACCEPTED("Enquêtes acceptées"),
    REFUSED("Refus"),
    UNREACHABLE("Impossible à joindre"),
    OUT_OF_SCOPE("Hors champ"),
    TOTAL_OUTCOMES("Total traitées"),
    ABSENCE_INTERVIEWER("Absence enquêteur"),
    OTHER_REASONS("Autres motifs"),
    TOTAL_CLOSED("Total closes");

    @Getter
    private final String headerName;

    CollectionCsvHeaders(String headerName) {
        this.headerName = headerName;
    }

    public static List<CollectionCsvHeaders> commonHeaders(CollectionCsvHeaders allocatedType) {
        return List.of(
                COLLECTION_RATE, WASTE_RATE, OUT_OF_SCOPE_RATE,
                ACCEPTED, REFUSED, UNREACHABLE, OUT_OF_SCOPE, TOTAL_OUTCOMES,
                ABSENCE_INTERVIEWER, OTHER_REASONS, TOTAL_CLOSED, allocatedType
        );
    }


    public static List<CollectionCsvHeaders> buildHeaders(List<CollectionCsvHeaders> prefixHeaders, CollectionCsvHeaders allocatedType) {
        List<CollectionCsvHeaders> headers = new ArrayList<>(prefixHeaders);
        headers.addAll(commonHeaders(allocatedType));
        return Collections.unmodifiableList(headers);
    }
}
