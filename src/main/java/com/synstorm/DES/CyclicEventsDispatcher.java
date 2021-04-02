package com.synstorm.DES;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CyclicEventsDispatcher extends EventsDispatcher {
    //region Fields
    //endregion

    //region Constructors
    public CyclicEventsDispatcher() {
        super();
    }
    //endregion

    //region Public Methods
    @NotNull
    public IEventResponse[] calculateNextTick() {
        currentTick = eventTime.remove();
        uniqueTime.remove(currentTick);

        final List<ModelEvent> currentEvents = Optional.ofNullable(eventsDic.remove(currentTick))
                .stream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());

        final ModelEvent[] active = currentEvents.parallelStream()
                .filter(ModelEvent::isActive)
                .toArray(ModelEvent[]::new);

        final IEventResponse[] result = currentEvents.parallelStream()
                .map(ModelEvent::executeEvent)
                .filter(r -> !(r instanceof EmptyResponse))
                .toArray(IEventResponse[]::new);

        if (active.length > 0)
            addEvents(active);

        return result;
    }
    //endregion
}
