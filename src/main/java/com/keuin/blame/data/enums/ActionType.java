package com.keuin.blame.data.enums;

public enum ActionType implements IntegerEnum {

    NULL(0, "NULL"),
    BLOCK_BREAK(1, "BREAK_BLOCK"),
    BLOCK_PLACE(2, "PLACE_BLOCK"),
    BLOCK_USE(3, "USE_BLOCK"),
    ENTITY_ATTACK(4, "ATTACK_ENTITY"),
    ENTITY_USE(5, "USE_ENTITY"),
    ITEM_USE(6, "USE_ITEM"),
    TELEPORTED_BY(7, "TELEPORTED_BY");

    private final int value;
    private final String typeString;

    ActionType(int value, String typeString) {
        this.value = value;
        this.typeString = typeString;
    }

    public static ActionType parseInt(int value) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.value == value)
                return actionType;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return typeString;
    }

}
