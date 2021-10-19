package com.synstorm.DES;

import org.jetbrains.annotations.Contract;

import java.util.EnumMap;

public class ModelEvent<M extends ModelObject> {
    //region Fields
    private final int eventDuration;
    private long eventTime;
    private long postponeTime;
    private final int eventDelay;
    private final EventReference<M> eventReference;
    private final M eventObject;
    private EventState eventState;

    private final EnumMap<EventState, StateProcessor> stateProcessorMap;
    //endregion

    //region Constructors
    public ModelEvent(final EventReference<M> reference, final M object, final int duration, final int delay) {
        eventTime = 0;
        postponeTime = 0;
        eventDuration = duration;
        eventDelay = delay;
        eventState = EventState.Waiting;
        eventReference = reference;
        eventObject = object;

        stateProcessorMap = new EnumMap<>(EventState.class);
        stateProcessorMap.put(EventState.Active, this::processActiveEvent);
        stateProcessorMap.put(EventState.Postponed, this::processPostponedEvent);
        stateProcessorMap.put(EventState.WaitingInQueue, this::processWaitingInQueueEvent);
    }
    //endregion

    //region Public Methods
    @Contract(pure = true)
    public long getEventTime() {
        return eventTime;
    }

    @Contract(pure = true)
    public boolean isReady() {
        return eventState == EventState.Ready;
    }

    @Contract(pure = true)
    public boolean isActive() {
        return eventState == EventState.Active;
    }

    @Contract(pure = true)
    public boolean isWaiting() {
        return eventState == EventState.Waiting;
    }

    @Contract(pure = true)
    public boolean isWaitingInQueue() {
        return eventState == EventState.WaitingInQueue;
    }

    @Contract(pure = true)
    public boolean isPostponed() {
        return eventState == EventState.Postponed;
    }

    public EventResponse executeEvent() {
        return stateProcessorMap.get(eventState).process();
    }

    public boolean updateTime(final long tick) {
        boolean result = true;
        if (isReady()) {
            eventTime = tick + eventDelay + eventDuration;
            eventState = EventState.Active;
        } else if (isWaitingInQueue()) {
            postponeTime = tick + eventDuration;
            eventState = EventState.Postponed;
        } else {
            result = false;
        }

        return result;
    }

    public void prepareEvent() {
        if (isWaiting())
            eventState = EventState.Ready;
    }

    public void disruptEvent() {
        if (isActive() || isPostponed())
            eventState = EventState.WaitingInQueue;
        else if (isReady())
            eventState = EventState.Waiting;
    }
    //endregion

    private EventResponse processActiveEvent() {
        eventState = EventState.Waiting;
        return eventReference.execute(eventObject, eventTime);
    }

    private EventResponse processPostponedEvent() {
        eventTime = postponeTime;
        eventState = EventState.Active;
        return EmptyResponse.INSTANCE;
    }

    private EventResponse processWaitingInQueueEvent() {
        eventState = EventState.Waiting;
        return EmptyResponse.INSTANCE;
    }

    @FunctionalInterface
    private interface StateProcessor {
        EventResponse process();
    }
}
