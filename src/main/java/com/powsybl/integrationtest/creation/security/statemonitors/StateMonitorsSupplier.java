/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.statemonitors;

import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.monitor.StateMonitor;

import java.util.HashMap;
import java.util.List;

/**
 * Component used to create security analysis test cases, as it provides a list of StateMonitors.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public interface StateMonitorsSupplier {

    /**
     * Create and return a list of StateMonitor with every element that can be monitored (branches, voltage levels, three windings transformers).
     * Based on a list of contingencies, since at the creation of a StateMonitor, a contingency id must be given.
     * @param network, list of contingencies
     * @return a list of StateMonitor
     */
    List<StateMonitor> getStateMonitors(Network network, List<Contingency> contingencies, HashMap<String, ?> configuration);
}
