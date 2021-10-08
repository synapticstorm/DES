package com.synstorm.DES;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CyclicEventsDispatcherTest {

    private EventsDispatcher ed;

    @Before
    public void setUp() {

        ed = new CyclicEventsDispatcher();
    }

    @Test
    public void singleEventAtZero () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[] { 10 })); // one dimensional array -> durations of the events
            add(new EventDispatcherTestStep(10, 1 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void singleEventAtZeroWithDelay () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[][] {{ 10, 5 }})); // two dim array -> duration and delay of the event
            add(new EventDispatcherTestStep(15, 1 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void zeroDurationEvent () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[] { 0, 1 }));
            add(new EventDispatcherTestStep(0, 1 ));
            add(new EventDispatcherTestStep(1, 1 )); // zero duration event loops dispatcher!
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void zeroDurationEventWithDelayCoincide () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[][] {{1, 0}, {0, 1} }));
            add(new EventDispatcherTestStep(1, 2 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void zeroDurationEventWithDelayNotCoincide () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[][] { {1,1}, {1,0} }));
            add(new EventDispatcherTestStep(1, 1 ));
            add(new EventDispatcherTestStep(2, 2 ));
            add(new EventDispatcherTestStep(3, 1 ));
            add(new EventDispatcherTestStep(4, 2 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void zeroDurationEventWithDelay () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[][] {{1, 0}, {0, 10}}));
            add(new EventDispatcherTestStep(1, 1 ));
            add(new EventDispatcherTestStep(2, 1 ));
            add(new EventDispatcherTestStep(3, 1 ));
            add(new EventDispatcherTestStep(4, 1 ));
            add(new EventDispatcherTestStep(5, 1 ));
            add(new EventDispatcherTestStep(6, 1 ));
            add(new EventDispatcherTestStep(7, 1 ));
            add(new EventDispatcherTestStep(8, 1 ));
            add(new EventDispatcherTestStep(9, 1 ));
            add(new EventDispatcherTestStep(10, 2 ));
            add(new EventDispatcherTestStep(11, 1 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void twoEventsAtZeroSameLength () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[] { 5, 5 }));
            add(new EventDispatcherTestStep(5, 2 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void twoEventsAtZeroDifferentLength () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[] { 5, 10 }));
            add(new EventDispatcherTestStep(5, 1 ));
            add(new EventDispatcherTestStep(10, 2 ));
            add(new EventDispatcherTestStep(15, 1 ));
            add(new EventDispatcherTestStep(20, 2 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void twoEventsAtZeroDifferentLengthSameEndOneDelayed () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[][] { {5, 5}, {10, 0} }));
            add(new EventDispatcherTestStep(10, 2 ));
            add(new EventDispatcherTestStep(20, 2 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void twoEventsAtZeroDifferentLengthSameEndBothDelayed () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[][] { {5, 5}, {6, 4} }));
            add(new EventDispatcherTestStep(10, 2 ));
            add(new EventDispatcherTestStep(20, 2 ));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void singleChainedEvents () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[] { 10 }));
            add(new EventDispatcherTestStep(10, 1, new int[] { 10 }));
            add(new EventDispatcherTestStep(20, 2, new int[][] {{ 10, 5 }}));
            add(new EventDispatcherTestStep(30, 2));
            add(new EventDispatcherTestStep(35, 1));
            add(new EventDispatcherTestStep(40, 2));
            add(new EventDispatcherTestStep(50, 3));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

    @Test
    public void multipleChainedEvents () {

        List<EventDispatcherTestStep> testScript = new ArrayList<EventDispatcherTestStep>(){{
            add(new EventDispatcherTestStep(0, 0, new int[] { 10, 10 }));
            add(new EventDispatcherTestStep(10, 2, new int[] { 10, 5 }));
            add(new EventDispatcherTestStep(15, 1, new int[][] {{2, 3}}));
            add(new EventDispatcherTestStep(20, 5));
            add(new EventDispatcherTestStep(25, 2));
            add(new EventDispatcherTestStep(30, 5));
        }};

        EventDispatcherTestStep.runTestScript(ed, testScript);
    }

}
