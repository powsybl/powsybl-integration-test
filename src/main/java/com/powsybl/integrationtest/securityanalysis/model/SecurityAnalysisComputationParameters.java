/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.model;

import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.model.ComputationParameters;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.monitor.StateMonitor;

import java.util.List;

/**
 * Security Analysis computation parameters. Contains the network and the {@link SecurityAnalysisParameters} to use to
 * run the computation.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisComputationParameters implements ComputationParameters {
    private final SecurityAnalysisParameters saParameters;
    private final Network network;
    private final List<Contingency> contingencies;

    private final List<StateMonitor> stateMonitors;

    public SecurityAnalysisComputationParameters(Network network, SecurityAnalysisParameters saParameters,
                                                 List<Contingency> contingencies, List<StateMonitor> stateMonitors) {
        this.saParameters = saParameters;
        this.network = network;
        this.contingencies = contingencies;
        this.stateMonitors = stateMonitors;
    }

    public Network getNetwork() {
        return network;
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }

    public SecurityAnalysisParameters getSecurityAnalysisParameters() {
        return saParameters;
    }

    public List<StateMonitor> getStateMonitors() {
        return stateMonitors;
    }
}
