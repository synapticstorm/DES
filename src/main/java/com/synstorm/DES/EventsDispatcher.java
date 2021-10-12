package com.synstorm.DES;

import gnu.trove.set.hash.TLongHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class EventsDispatcher<M extends ModelObject> {

    private long currentTick;
    private final ConcurrentHashMap<Long, List<ModelEvent<M>>> eventsDic;
    private final TLongHashSet uniqueTime;
    private final Queue<Long> eventTime;

    private static final EventResponse[] emptyAnswer = new EventResponse[0];

    public EventsDispatcher() {
        currentTick = 0;
        eventTime = new PriorityQueue<>();
        uniqueTime = new TLongHashSet();
        eventsDic = new ConcurrentHashMap<>();
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void addEvents(final ModelEvent<M>[] modelEvents) {
        final Map<Long, List<ModelEvent<M>>> groupedEvents = Arrays.stream(modelEvents)
                .parallel()
                .filter(this::addEvent)
                .collect(Collectors.groupingBy(ModelEvent::getEventTime));

        final List<Long> ticks = groupedEvents.keySet()
                .parallelStream()
                .filter(tick -> !uniqueTime.contains(tick))
                .collect(Collectors.toList());

        eventTime.addAll(ticks);
        uniqueTime.addAll(ticks);

        groupedEvents.entrySet().parallelStream()
                .forEach(e -> {
                    eventsDic.putIfAbsent(e.getKey(), new ArrayList<>());
                    eventsDic.get(e.getKey()).addAll(e.getValue());
                });
    }

    @NotNull
    public EventResponse[] calculateNextTick() {
        if (eventTime.isEmpty())
            return emptyAnswer;

        currentTick = eventTime.remove();
        uniqueTime.remove(currentTick);

        final List<ModelEvent<M>> currentEvents = Optional.ofNullable(eventsDic.remove(currentTick))
                .stream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());

        return currentEvents.parallelStream()
                .map(ModelEvent::executeEvent)
                .filter(r -> r != EmptyResponse.INSTANCE)
                .toArray(EventResponse[]::new);
    }

    public long peekHead() {
        return eventTime.peek() != null ? eventTime.peek() : Long.MAX_VALUE;
    }

    private boolean addEvent(@NotNull final ModelEvent<M> event) {
        if (event.isReady()) {
            event.updateEvent(currentTick);
        } else if (event.isWaitingInQueue()) {
            event.postponeEvent(currentTick);
        } else {
            return false;
        }

        return true;
    }
}
