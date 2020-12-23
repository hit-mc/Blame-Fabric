package com.keuin.blame.data.enums;

public enum ActionType implements IntegerEnum {

    BLOCK_BREAK(1),
    BLOCK_PLACE(2),
    BLOCK_USE(3),
    ENTITY_ATTACK(4),
    ENTITY_USE(5),
    ITEM_USE(6);

    private final int value;

    ActionType(int value) {
        this.value = value;
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
        return "ActionType{" +
                "value=" + value +
                '}';
    }

}
