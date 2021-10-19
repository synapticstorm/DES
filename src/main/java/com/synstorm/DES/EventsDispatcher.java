package com.synstorm.DES;

import gnu.trove.map.hash.TLongObjectHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public final class EventsDispatcher<M extends ModelObject> {

    private long currentTick;
    private final TLongObjectHashMap<ModelEvent<M>[]> eventsDic;
    private final PriorityQueue<Long> eventTime;

    private static final EventResponse[] emptyAnswer = new EventResponse[0];

    public EventsDispatcher() {
        currentTick = 0;
        eventTime = new PriorityQueue<>();
        eventsDic = new TLongObjectHashMap<>();
    }

    public long getCurrentTick() {
        return currentTick;
    }

    @SuppressWarnings("unchecked")
    public void addEvents(final ModelEvent<M>[] modelEvents) {
        final Map<Long, List<ModelEvent<M>>> groupedEvents = Arrays.stream(modelEvents)
                .parallel()
                .filter(e -> e.updateTime(currentTick))
                .collect(Collectors.groupingBy(ModelEvent::getEventTime));

        groupedEvents.forEach((tick, events) -> {
            final ModelEvent<M>[] add = (ModelEvent<M>[]) events.toArray(ModelEvent[]::new);
            if (eventsDic.containsKey(tick)) {
                final ModelEvent<M>[] src = eventsDic.get(tick);
                final ModelEvent<M>[] dst = (ModelEvent<M>[]) new ModelEvent[src.length + add.length];
                System.arraycopy(src, 0, dst, 0, src.length);
                System.arraycopy(add, 0, dst, src.length, add.length);
                eventsDic.put(tick, dst);
            } else {
                eventTime.add(tick);
                eventsDic.put(tick, add);
            }
        });
    }

    @NotNull
    public EventResponse[] calculateNextTick() {
        if (eventTime.isEmpty())
            return emptyAnswer;

        currentTick = eventTime.remove();
        final ModelEvent<M>[] currentEvents = eventsDic.remove(currentTick);

        return Arrays.stream(currentEvents)
                .parallel()
                .map(ModelEvent::executeEvent)
                .filter(EventResponse::notEmpty)
                .toArray(EventResponse[]::new);
    }

    public long peekHead() {
        return eventTime.peek() != null ? eventTime.peek() : Long.MAX_VALUE;
    }
}
