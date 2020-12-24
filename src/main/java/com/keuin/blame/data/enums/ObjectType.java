package com.keuin.blame.data.enums;

public enum ObjectType implements IntegerEnum {

    NULL(0, "NULL"), BLOCK(1, "BLOCK"), ENTITY(2, "ENTITY");

    private final int value;
    private final String typeString;

    ObjectType(int value, String typeString) {
        this.value = value;
        this.typeString = typeString;
    }

    public static ObjectType parseInt(int value) {
        for (ObjectType objectType : ObjectType.values()) {
            if (objectType.value == value)
                return objectType;
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
