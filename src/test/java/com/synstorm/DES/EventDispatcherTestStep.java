package com.synstorm.DES;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventDispatcherTestStep {
    public int tick;
    public ModelEvent<DummyObject>[] add;
    public int answers;

    public EventDispatcherTestStep(int tick, int answers, ModelEvent<DummyObject>[] add) {
        this.tick = tick;
        this.answers = answers;

        this.add = add;
    }

    public EventDispatcherTestStep(int tick, int answers, int[] add) {
        this.tick = tick;
        this.answers = answers;

        this.add = modelEvents(add);
    }

    public EventDispatcherTestStep(int tick, int answers, int[][] add) {
        this.tick = tick;
        this.answers = answers;

        this.add = modelEvents(add);
    }

    public EventDispatcherTestStep(int tick, int answers) {
        this.tick = tick;
        this.answers = answers;

        this.add = new ModelEvent[] { };
    }

    private ModelEvent[] modelEvents(int[] durations) {
        final ModelEvent[] events = new ModelEvent[durations.length];
        for (int i = 0; i < durations.length; i++) {
            events[i] = new DummyMechanism(durations[i],0).createModelEvent();
            events[i].prepareEvent();
        }
        return events;
    }

    private ModelEvent[] modelEvents(int[][] durations) {
        final ModelEvent[] events = new ModelEvent[durations.length];
        for (int i = 0; i < durations.length; i++) {
            events[i] = new DummyMechanism(durations[i][0], durations[i][1]).createModelEvent();
            events[i].prepareEvent();
        }
        return events;
    }

    public static void runTestScript (EventsDispatcher ed, List<EventDispatcherTestStep> steps) {
        IEventResponse[] response = new IEventResponse[]{ };
        int testStepNumber = 1;
        for (EventDispatcherTestStep step : steps
        ) {
            long currentTick = ed.getCurrentTick();
            assertEquals("Incorrect tick at step "+testStepNumber, step.tick, currentTick);
            assertEquals("Incorrect number of answers at step "+testStepNumber, step.answers, response.length);
            ed.addEvents(step.add);

            if (testStepNumber < steps.size()) {
                response = ed.calculateNextTick();
            }
//            System.out.println("Completed step " + testStepNumber);
            testStepNumber++;
        }
    }
}
