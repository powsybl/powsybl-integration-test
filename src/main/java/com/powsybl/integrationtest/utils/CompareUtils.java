package com.powsybl.integrationtest.utils;

import java.util.List;
import java.util.Objects;

/**
 * A class providing helper functions to compare values in the context of powsybl integration testing
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public final class CompareUtils {

    /**
     * This is a toolbox with only static members
     */
    private CompareUtils() {
    }

    /**
     * Check that values difference is smaller than {@code delta}. Since the {@link Comparable#compareTo} method is
     * used for values comparison, it is advisable to use only numeric values in arguments.
     * If difference bewteen the vlaues is greater than {@code delta}, {@code errMessage} gets added to {@code errors}
     * list.
     *
     * @param value1     first value
     * @param value2     second value
     * @param delta      maximal tolerated difference between
     * @param errMessage message to add to errors list if check fails
     * @param errors     errors list
     */
    public static void assertDeltaMax(double value1, double value2, double delta, String errMessage, List<String> errors) {
        if (Math.abs(value1 - value2) > delta) {
            errors.add(errMessage + " (expected " + value2 + " but was " + value1 + ")");
        }
    }

    /**
     * Check that provided values are equal
     *
     * @param value1     First value
     * @param value2     Second value
     * @param errMessage Message to add to {@code errors} if values are not equal.
     * @param errors     errors list
     * @param <T>        type of values to compare
     */
    public static <T> void assertEquals(T value1, T value2, String errMessage, List<String> errors) {
        if (!Objects.equals(value1, value2)) {
            errors.add(errMessage + " (expected " + value2 + " but was " + value1 + ")");
        }
    }
}
