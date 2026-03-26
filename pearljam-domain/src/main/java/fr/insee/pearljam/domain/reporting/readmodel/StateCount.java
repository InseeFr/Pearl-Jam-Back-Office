package fr.insee.pearljam.domain.reporting.readmodel;

public record StateCount(
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

        public static StateCount empty(String id) {
                return new StateCount(id, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

}
