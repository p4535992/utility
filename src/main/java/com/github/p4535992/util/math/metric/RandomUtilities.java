package com.github.p4535992.util.math.metric;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by 4535992 on 31/12/2015.
 */
public class RandomUtilities {

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randomInt(int min, int max) {
        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        //ThreadLocalRandom.current().nextInt(min, max + 1);
        // nextInt is normally exclusive of the top value,so add 1 to make it inclusive
       /* Random rand = new Random();
        return randomNum = rand.nextInt((max - min) + 1) + min;*/
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


}
