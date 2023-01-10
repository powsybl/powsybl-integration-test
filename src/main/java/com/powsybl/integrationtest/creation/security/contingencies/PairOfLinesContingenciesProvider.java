/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.contingencies;

import com.google.auto.service.AutoService;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.LineContingency;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VoltageLevel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ContingenciesSupplier} which creates contingencies for every pair of lines that are parallel.
 * Two lines are parallel when they share the same voltage levels.
 * The contingencies created here are N-2 contingencies since they are created from two lines.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
@AutoService(ContingenciesSupplier.class)
public class PairOfLinesContingenciesProvider implements ContingenciesSupplier {

    @Override
    public List<Contingency> getContingencies(final Network network, Object configuration) {
        List<Contingency> contingencies = new ArrayList<>();
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
