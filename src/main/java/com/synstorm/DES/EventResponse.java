package com.synstorm.DES;

public interface EventResponse {
    default boolean notEmpty() {
        return this != EmptyResponse.INSTANCE;
    }
}
