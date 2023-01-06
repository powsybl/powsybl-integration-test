/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.utils;

import java.util.*;

/**
 * A class used to select randomly elements in a list, in order to create samples.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public final class SampleUtils {
    private  SampleUtils() {
    }

    /**
     * As an input, there is a hashmap where the key is a class, and the value associated is the list of all elements of this class.
     * This method creates a sample of elements for each of these lists, where the size is determined with a rate parameter and the elements
     * selected randomly with a seed for reproducibility.
     * @param elementsList list of all elements.
     * @return A hashmap similar to the input, except that the associated list of elements is a reduced set (as it is a sample).
     */
    public static <T> HashMap<String, Set<T>> createSamples(HashMap<String, List<T>> elementsList, HashMap<String, Double> rates, Random r) {
        HashMap<String, Set<T>> sample = new HashMap<>();
        elementsList.forEach((elementClassName, elementIds) -> {
            Set<T> elementsSample = createSample(elementIds, rates.getOrDefault(elementClassName, 0.0), r);

            // Add sample of this type of element to the sample
            sample.put(elementClassName, elementsSample);
        });

        return sample;
    }

    /**
     * Creates a subset of elements from a given list of elements and a rate.
     * The given list is shuffled, we then fill a set until it reach the percentage asked.
     * @param elements
     * @param rate
     * @param r
     * @return A subset of elements from the given list.
     * @param <T>
     */
    public static <T> Set<T> createSample(List<T> elements, Double rate, Random r) {
        Set<T> elementsSample = new HashSet<>();
        // Shuffle the list with a random seed for reproducibility
        Collections.shuffle(elements, r);

        // Create sample
        int wholeSetSize = elements.size();
        int sampleSize = (int) Math.ceil((wholeSetSize * rate) / 100.0);
        for (int i = 0; i < sampleSize; i++) {
            elementsSample.add(elements.get(i));
        }

        return elementsSample;
    }
}
