/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.statemonitors;

import com.google.auto.service.AutoService;
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
@AutoService(StateMonitorsSupplier.class)
public class RandomStateMonitorsProvider implements StateMonitorsSupplier {

    private HashMap<String, Double> stateMonitorsRate;

    private Random r;

    @Override
    public List<StateMonitor> getStateMonitors(Network network, List<Contingency> contingencies) {
        // Get and create a list for each type of elements that can be monitored (branches, voltage levels and three windings transformers)
        List<String> voltageLevelIds = new ArrayList<>();
        List<String> threeWindingsTransformerIds = new ArrayList<>();
        List<String> branchIds = new ArrayList<>();
        network.getVoltageLevels().forEach(vl -> voltageLevelIds.add(vl.getId()));
        network.getThreeWindingsTransformers().forEach(threeWindingsTransformer -> threeWindingsTransformerIds.add(threeWindingsTransformer.getId()));
        network.getBranches().forEach(branch -> branchIds.add(branch.getId()));

        // Add those lists in a list
        HashMap<String, List<String>> elementsList = new HashMap<>();
        elementsList.put(VoltageLevel.class.getSimpleName(), voltageLevelIds);
        elementsList.put(ThreeWindingsTransformer.class.getSimpleName(), threeWindingsTransformerIds);
        elementsList.put(Branch.class.getSimpleName(), branchIds);

        HashMap<String, Set<String>> sample = createSamples(elementsList, stateMonitorsRate, r);

        // Create state monitors from this sample
        List<StateMonitor> stateMonitors = new ArrayList<>();
        Set<String> sampleBranchIds = sample.getOrDefault(Branch.class.getSimpleName(), Collections.emptySet());
        Set<String> sampleVoltageLevelIds = sample.getOrDefault(VoltageLevel.class.getSimpleName(), Collections.emptySet());
        Set<String> sampleThreeWindingsTransformer = sample.getOrDefault(ThreeWindingsTransformer.class.getSimpleName(), Collections.emptySet());
        StateMonitor stateMonitor = new StateMonitor(
                new ContingencyContext(null, ContingencyContextType.ALL),
                sampleBranchIds,
                sampleVoltageLevelIds,
                sampleThreeWindingsTransformer
        );
        stateMonitors.add(stateMonitor);

        return stateMonitors;
    }

    public void setConfiguration(Object configuration) {
        this.stateMonitorsRate = new HashMap<>();
        ((HashMap<String, Number>) configuration).forEach((element, rate) -> this.stateMonitorsRate.put(element, rate.doubleValue()));
        this.r = new Random();
        this.r.setSeed(0);
    }
}
