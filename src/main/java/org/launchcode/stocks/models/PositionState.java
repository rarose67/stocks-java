package org.launchcode.stocks.models;

public enum PositionState {

    ACTIVE ("ACTIVE"),
    INACTIVE ("INACTIVE"),
    DELETED ("DELETED");


    private final String name;

    PositionState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

