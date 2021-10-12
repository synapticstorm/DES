package com.synstorm.DES;

@FunctionalInterface
public interface EventReference<T extends ModelObject> {
    EventResponse execute(final T object, final long time);
}
