package com.keuin.blame.data.enums;

public enum ObjectType implements IntegerEnum {

    BLOCK(1), ENTITY(2);

    private final int value;

    ObjectType(int value) {
        this.value = value;
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
        return "ObjectType{" +
                "value=" + value +
                '}';
    }

}
