package com.synstorm.DES;

import gnu.trove.set.hash.TLongHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventsDispatcher {
    //region Fields
    protected long currentTick;
    protected final ConcurrentHashMap<Long, List<ModelEvent>> eventsDic;

    protected final TLongHashSet uniqueTime;
    protected final Queue<Long> eventTime;
    //endregion

    //region Constructors
    public EventsDispatcher() {
        currentTick = 0;
        eventTime = new PriorityQueue<>();
        uniqueTime = new TLongHashSet();
        eventsDic = new ConcurrentHashMap<>();
    }
    //endregion

    //region Public Methods
    public long getCurrentTick() {
        return currentTick;
    }

    public void addEvents(final ModelEvent[] modelEvents) {
        final Map<Long, List<ModelEvent>> groupedEvents = Arrays.stream(modelEvents)
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
    public IEventResponse[] calculateNextTick() {
        currentTick = eventTime.remove();
        uniqueTime.remove(currentTick);

        final List<ModelEvent> currentEvents = Optional.ofNullable(eventsDic.remove(currentTick))
                .stream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());

        return currentEvents.parallelStream()
                .map(ModelEvent::executeEvent)
                .filter(r -> !(r instanceof EmptyResponse))
                .toArray(IEventResponse[]::new);
    }

    public long updateState() {
        return eventTime.peek() != null ? eventTime.peek() : Long.MAX_VALUE;
    }
    //endregion

    //region Private Methods
    private boolean addEvent(@NotNull final ModelEvent event) {
        if (event.isReady()) {
            event.updateEvent(currentTick);
        } else if (event.isWaitingInQueue()) {
            event.postponeEvent(currentTick);
        } else {
            return false;
        }

        return true;
    }
    //endregion
}
