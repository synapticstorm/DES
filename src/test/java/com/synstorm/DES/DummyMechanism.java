package com.synstorm.DES;

import org.jetbrains.annotations.NotNull;

public class DummyMechanism {
    //region Fields
    private final int duration;
    private final int delay;
    //endregion

    //region Constructors
    public DummyMechanism(final int duration, final int delay) {
        this.duration = duration;
        this.delay = delay;
    }
    //endregion

    //region Public Methods
    public ModelEvent<DummyObject> createModelEvent() {
        return new ModelEvent<>(this::evaluate, new DummyObject(), duration, delay);
    }
    //endregion

    //region Private Methods
    private IEventResponse evaluate(final @NotNull DummyObject object, final long time) {
        System.out.println("Event with duration: " + duration);
        return new DummyResponse();
    }
    //endregion
}
