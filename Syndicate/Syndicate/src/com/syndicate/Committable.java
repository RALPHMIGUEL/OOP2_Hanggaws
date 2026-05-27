package com.syndicate;

public interface Committable {
    void executeStrategy(Hideout hideout, Operation operation);
    double calculateRisk();
    String getName();
}
