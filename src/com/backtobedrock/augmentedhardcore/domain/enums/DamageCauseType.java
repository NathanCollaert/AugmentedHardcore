package com.backtobedrock.augmentedhardcore.domain.enums;

public enum DamageCauseType {
    BLOCK,
    ENTITY,
    ENVIRONMENT;

    @Override
    public String toString() {
        return this.name().toLowerCase().replaceAll("_", " ");
    }
}
