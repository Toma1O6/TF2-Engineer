package dev.toma.engineermod.util;

/**
 * Math utility class
 *
 * @author Toma
 * @since 1.0
 */
public final class Mth {

    /**
     * Returns squared value.
     * @param n Input value
     * @return Squared input
     */
    public static double sqr(double n) {
        return n * n;
    }

    /**
     * Returns squared value.
     * @param n Input value
     * @return Squared input
     */
    public static float sqr(float n) {
        return n * n;
    }

    /**
     * Private constructor, this is just a utility class
     */
    private Mth() {}
}
