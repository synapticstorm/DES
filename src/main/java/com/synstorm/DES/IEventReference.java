package com.synstorm.DES;

@FunctionalInterface
public interface IEventReference {
    IEventResponse execute(final IModelObject object, final long time);
}
