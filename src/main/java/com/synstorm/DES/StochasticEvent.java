package com.synstorm.DES;

import java.util.Random;

public class StochasticEvent<M extends ModelObject> extends ModelEvent<M> {
    //region Fields
    private final Random rnd;
    private final double deltaRange;
    //endregion

    // default values
    private static long initialSeed = 32167L;
    private static double rangePercent = 0.1;

    //region Constructors
    public StochasticEvent(final EventReference<M> reference, final M object, final int duration, final int delay, final long seed) {
        super(reference, object, duration, delay);
        rnd = new Random(initialSeed + seed);
        deltaRange = duration * rangePercent;
    }
    //endregion

    //region Static
    public static void init(final long seed, final double range) {
        initialSeed = seed;
        rangePercent = range;
    }
    //endregion

    //region Public Methods
    public void updateEvent(final long tick) {
        super.updateEvent(tick + calcDelta());
    }
    //endregion

    //region Package-local Methods
    //endregion

    //region Protected Methods
    //endregion

    //region Private Methods
    private long calcDelta() {
        return (long) (rnd.nextGaussian() * deltaRange);
    }
    //endregion
}
