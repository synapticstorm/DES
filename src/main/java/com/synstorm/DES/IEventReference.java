package com.synstorm.DES;

@FunctionalInterface
public interface IEventReference {
    IEventResponse execute(final Object[] arguments);
}
