/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.security.contingencies;

import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.LineContingency;
import com.powsybl.iidm.network.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Efficient implementation for ContingenciesProvider.
 * Creates a list of contingencies with the following rules:
 * N-1 contingencies: Create a contingency for each {@link com.powsybl.contingency.ContingencyElementType} in the network.
 * N-2 contingencies: Create a contingency for each pair of line with same voltage levels.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public class EfficientContingenciesProvider implements ContingenciesProvider {

    @Override
    public List<Contingency> getContingencies(final Network network) {
        List<Contingency> contingencies = new ArrayList<>();
        // Get and add every element of the network in a list of contingencies (N-1)
        network.getGenerators().forEach(e -> contingencies.add(Contingency.generator(e.getId())));
        network.getStaticVarCompensators().forEach(e -> contingencies.add(Contingency.staticVarCompensator(e.getId())));
        network.getShuntCompensators().forEach(e -> contingencies.add(Contingency.shuntCompensator(e.getId())));
        network.getBranches().forEach(e -> contingencies.add(Contingency.branch(e.getId())));
        network.getHvdcLines().forEach(e -> contingencies.add(Contingency.hvdcLine(e.getId())));
        //TODO: Add busbarSections once fixed
//        network.getBusbarSections().forEach(e -> contingencies.add(Contingency.busbarSection(e.getId())));
        network.getDanglingLines().forEach(e -> contingencies.add(Contingency.danglingLine(e.getId())));
        network.getThreeWindingsTransformers().forEach(e -> contingencies.add(Contingency.threeWindingsTransformer(e.getId())));
        network.getLoads().forEach(e -> contingencies.add(Contingency.load(e.getId())));
        //TODO: Add switches?
//        Iterable<Switch> switches = network.getSwitches();

        // Create pairs of line for every parallel lines
        // For each set of voltage level, we store the list of lines with those voltage levels
        HashMap<Set<VoltageLevel>, List<Line>> voltageLevelListHashMap = new HashMap<>();
        Set<Set<Line>> pairLine = new HashSet<>();
        for (Line line : network.getLines()) {
            // Get line's voltage levels
            Set<VoltageLevel> voltageLevelSet = line.getTerminals().stream().map(t -> t.getVoltageLevel()).collect(Collectors.toSet());
            List<Line> lineList = new ArrayList<>();
            if (voltageLevelListHashMap.containsKey(voltageLevelSet)) {
                lineList = voltageLevelListHashMap.get(voltageLevelSet);
                // Create new pair with all lines in the list
                lineList.forEach(l -> pairLine.add(Set.of(line, l)));
            }
            // Add line to the list of lines with those same voltage levels
            lineList.add(line);
            voltageLevelListHashMap.put(voltageLevelSet, lineList);
        }

        // Add all N-2 contingencies to the list of contingencies
        pairLine.forEach(pl -> {
            List<ContingencyElement> contingencyElements = new ArrayList<>();
            pl.forEach(p -> contingencyElements.add(new LineContingency(p.getId())));
            contingencies.add(new Contingency(String.valueOf(pl), contingencyElements));
        });

        return contingencies;
    }
}
