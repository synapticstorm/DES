package com.synstorm.DES;

import org.jetbrains.annotations.Contract;

import java.util.EnumMap;

public class ModelEvent {
    //region Fields
    private final long id;
    private final int eventDuration;
    private long eventTime;
    private long postponeTime;
    private final int eventDelay;
    private final IEventReference eventReference;
    private final Object[] eventArguments;
    private EventState eventState;

    private final EnumMap<EventState, StateProcessor> stateProcessorMap;
    //endregion

    private static long modelActionIdCounter = 0;
    private static final IEventResponse emptyResponse = new EmptyResponse();

    //region Constructors
    public ModelEvent(final IEventReference reference, final Object[] arguments, final int duration, final int delay) {
        id = incrementId();
        eventTime = 0;
        postponeTime = 0;
        eventDuration = duration;
        eventDelay = delay;
        eventState = EventState.Waiting;
        eventReference = reference;
        eventArguments = arguments;

        stateProcessorMap = new EnumMap<>(EventState.class);
        stateProcessorMap.put(EventState.Active, this::processActiveEvent);
        stateProcessorMap.put(EventState.Postponed, this::processPostponedEvent);
        stateProcessorMap.put(EventState.WaitingInQueue, this::processWaitingInQueueEvent);
        stateProcessorMap.put(EventState.Waiting, this::processWaitingEvent);
    }
    //endregion

    private static long incrementId() {
        return ++modelActionIdCounter;
    }

    //region Public Methods
    @Contract(pure = true)
    public long getEventTime() {
        return eventTime;
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

    public IEventResponse executeEvent() {
        return stateProcessorMap.get(eventState).process();
    }

    public void updateEvent(final long tick) {
        eventTime = tick + eventDelay + eventDuration;
        eventState = EventState.Active;
    }

    public void postponeEvent(final long tick) {
        postponeTime = tick + eventDuration;
        eventState = EventState.Postponed;
    }

    public void inactivateEventInQueue() {
        eventState = EventState.WaitingInQueue;
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }
    //endregion

    protected IEventResponse processActiveEvent() {
        eventState = EventState.Waiting;
        return eventReference.execute(eventArguments);
    }

    private IEventResponse processPostponedEvent() {
        eventTime = postponeTime;
        eventState = EventState.Active;
        return emptyResponse;
    }

    private IEventResponse processWaitingInQueueEvent() {
        eventState = EventState.Waiting;
        return emptyResponse;
    }

    private IEventResponse processWaitingEvent() {
        return emptyResponse;
    }

    @FunctionalInterface
    private interface StateProcessor {
        IEventResponse process();
    }
}
