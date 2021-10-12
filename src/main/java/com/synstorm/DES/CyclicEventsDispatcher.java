package com.synstorm.DES;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CyclicEventsDispatcher<M extends ModelObject> extends EventsDispatcher<M> {

    public CyclicEventsDispatcher() {
        super();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public EventResponse[] calculateNextTick() {
        if (eventTime.isEmpty())
            return emptyAnswer;

        currentTick = eventTime.remove();
        uniqueTime.remove(currentTick);

        final List<ModelEvent<M>> currentEvents = Optional.ofNullable(eventsDic.remove(currentTick))
                .stream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());

        final ModelEvent<M>[] active = currentEvents.parallelStream()
                .filter(ModelEvent::isActive)
                .toArray(size -> (ModelEvent<M>[]) new ModelEvent[size]);

        final EventResponse[] result = currentEvents.parallelStream()
                .map(ModelEvent::executeEvent)
                .filter(r -> !(r instanceof EmptyResponse))
                .toArray(EventResponse[]::new);

        if (active.length > 0)
            addEvents(active);

        return result;
    }
}
