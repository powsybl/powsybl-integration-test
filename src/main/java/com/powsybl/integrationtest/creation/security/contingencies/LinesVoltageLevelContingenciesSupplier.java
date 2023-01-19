/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.contingencies;

import com.google.auto.service.AutoService;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;

import java.util.*;

/**
 * Implementation of {@link ContingenciesSupplier} which creates and returns a list of contingencies of lines with voltage levels with different nominal voltage values.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
@AutoService(ContingenciesSupplier.class)
public class LinesVoltageLevelContingenciesSupplier implements ContingenciesSupplier {

    /**
     * Creates and returns a list of contingencies of lines with voltage levels with different nominal voltage values.
     * <p> <u><i>Example:</i></u>
     *  <ul>
     *      <li>LineA with voltage levels with nominal voltage values 400kv and 220kv, create a contingency with LineA</li>
     *      <li>LineB with voltage levels with nominal voltage values 400kv and 400kv, same values then no contingency</li>
     * </ul>
     */
    @Override
    public List<Contingency> getContingencies(final Network network, HashMap<String, ?> configuration) {
        List<Contingency> contingencies = new ArrayList<>();
        // Find lines with different voltage levels and save their id
        Set<String> vlChangesLines = new HashSet<>();
        for (Line line : network.getLines()) {
            if (line.getTerminal1().getVoltageLevel().getNominalV() != line.getTerminal2().getVoltageLevel().getNominalV()) {
                vlChangesLines.add(line.getId());
            }
        }

        for (String line : vlChangesLines) {
            contingencies.add(Contingency.line(line));
        }

        return contingencies;
    }
}
