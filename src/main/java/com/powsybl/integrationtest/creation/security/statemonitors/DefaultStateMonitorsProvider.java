/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.statemonitors;

import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyContext;
import com.powsybl.contingency.ContingencyContextType;
import com.powsybl.iidm.network.*;
import com.powsybl.security.monitor.StateMonitor;

import java.util.*;

/**
 * Default implementation for the StateMonitorsProvider.
 * Select elements randomly in network's elements, to create a sample with a size determined by a given percentage.
 * A seed is used for reproducibility.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public class DefaultStateMonitorsProvider implements StateMonitorsProvider {

    private int stateMonitorsRate;

    private Random r;

    public DefaultStateMonitorsProvider(final int stateMonitorsRate) {
        this.stateMonitorsRate = stateMonitorsRate;
        this.r = new Random();
        this.r.setSeed(0);
    }

    public void setStateMonitorsRate(int stateMonitorsRate) {
        this.stateMonitorsRate = stateMonitorsRate;
    }

    public void setSeed(final int seed) {
        this.r.setSeed(seed);
    }

    /**
     * As an input, there is a hashmap where the key is a class, and the value associated is the list of all elements of this class in the network.
     * This method creates a sample of elements for each of these lists, where the size is determined with {@link #stateMonitorsRate} and the elements
     * selected randomly with a seed for reproducibility.
     * @param elementsList list of all elements.
     * @return A hashmap similar to the input, except that the associated list of elements is a reduced set (as it is a sample).
     */
    public HashMap<Class, Set<String>> createSample(HashMap<Class, List<String>> elementsList) {

        HashMap<Class, Set<String>> sample = new HashMap<>();
        elementsList.forEach((clazz, elementIds) -> {
            // Shuffle the list with a random seed for reproducibility
            Collections.shuffle(elementIds, r);

            Set<String> elementsSample = new HashSet<>();

            // Create sample
            int wholeSetSize = elementIds.size();
            int sampleSize = (int) Math.ceil((wholeSetSize * stateMonitorsRate) / 100.0);
            for (int i = 0; i < sampleSize; i++) {
                elementsSample.add(elementIds.get(i));
            }

            // Add sample of this type of element to the sample
            sample.put(clazz, elementsSample);
        });

        return sample;
    }

    @Override
    public List<StateMonitor> createStateMonitorList(Network network, List<Contingency> contingencies) {
        // Get and create a list for each type of elements that can be monitored (branches, voltage levels and three windings transformers)
        List<String> voltageLevelIds = new ArrayList<>();
        List<String> threeWindingsTransformerIds = new ArrayList<>();
        List<String> branchIds = new ArrayList<>();
        network.getVoltageLevels().forEach(vl -> voltageLevelIds.add(vl.getId()));
        network.getThreeWindingsTransformers().forEach(threeWindingsTransformer -> threeWindingsTransformerIds.add(threeWindingsTransformer.getId()));
        network.getBranches().forEach(branch -> branchIds.add(branch.getId()));

        // Add those lists in a list
        HashMap<Class, List<String>> elementsList = new HashMap<>();
        elementsList.put(VoltageLevel.class, voltageLevelIds);
        elementsList.put(ThreeWindingsTransformer.class, threeWindingsTransformerIds);
        elementsList.put(Branch.class, branchIds);

        HashMap<Class, Set<String>> sample = new HashMap<>();
        sample = createSample(elementsList);

        // Create state monitors from this sample
        List<StateMonitor> stateMonitors = new ArrayList<>();
        Set<String> sampleBranchIds = sample.getOrDefault(Branch.class, new HashSet<>());
        Set<String> sampleVoltageLevelIds = sample.getOrDefault(VoltageLevel.class, new HashSet<>());
        Set<String> sampleThreeWindingsTransformer = sample.getOrDefault(ThreeWindingsTransformer.class, new HashSet<>());
        for (Contingency contingency : contingencies) {
            StateMonitor stateMonitor = new StateMonitor(
                    new ContingencyContext(contingency.getId(), ContingencyContextType.ALL),
                    sampleBranchIds,
                    sampleVoltageLevelIds,
                    sampleThreeWindingsTransformer
            );
            stateMonitors.add(stateMonitor);
        }

        return stateMonitors;
    }
}
