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

import static com.powsybl.integrationtest.utils.SampleUtils.createSamples;
/**
 * Default implementation for the StateMonitorsProvider.
 * Select elements randomly in network's elements, to create a sample with a size determined by a given percentage.
 * A seed is used for reproducibility.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public class DefaultStateMonitorsProvider implements StateMonitorsProvider {

    private HashMap<Class, Double> stateMonitorsRate;

    private Random r;

    public DefaultStateMonitorsProvider(HashMap<Class, Double> stateMonitorsRate) {
        this.stateMonitorsRate = stateMonitorsRate;

        this.r = new Random();
        this.r.setSeed(0);
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

        HashMap<Class, Set<String>> sample = createSamples(elementsList, stateMonitorsRate, r);

        // Create state monitors from this sample
        List<StateMonitor> stateMonitors = new ArrayList<>();
        Set<String> sampleBranchIds = sample.getOrDefault(Branch.class, Collections.emptySet());
        Set<String> sampleVoltageLevelIds = sample.getOrDefault(VoltageLevel.class, Collections.emptySet());
        Set<String> sampleThreeWindingsTransformer = sample.getOrDefault(ThreeWindingsTransformer.class, Collections.emptySet());
        StateMonitor stateMonitor = new StateMonitor(
                new ContingencyContext(null, ContingencyContextType.ALL),
                sampleBranchIds,
                sampleVoltageLevelIds,
                sampleThreeWindingsTransformer
        );
        stateMonitors.add(stateMonitor);

        return stateMonitors;
    }
}
