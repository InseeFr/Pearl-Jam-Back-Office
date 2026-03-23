package fr.insee.pearljam.domain.reporting.projection;

public record StateCountProjection(
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

        public static StateCountProjection empty(String id) {
                return new StateCountProjection(id, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

}
