package fr.insee.pearljam.domain.reporting.query;

public record StateCountQueryResponse(
        String entityId,
        Long nvmCount,
        Long nnsCount,
        Long anvCount,
        Long vinCount,
        Long vicCount,
        Long prcCount,
        Long aocCount,
        Long apsCount,
        Long insCount,
        Long wftCount,
        Long wfsCount,
        Long tbrCount,
        Long finCount,
        Long cloCount,
        Long nvaCount,
        Long total) {

        public static StateCountQueryResponse empty(String id) {
                return new StateCountQueryResponse(id, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

}
