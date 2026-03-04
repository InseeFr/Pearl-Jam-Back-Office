package fr.insee.pearljam.domain.count.model;

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
    Long total) {}
