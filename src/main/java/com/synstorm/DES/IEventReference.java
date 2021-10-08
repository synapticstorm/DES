package com.synstorm.DES;

@FunctionalInterface
public interface IEventReference<T extends IModelObject> {
    IEventResponse execute(final T object, final long time);
}
