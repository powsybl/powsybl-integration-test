/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.model;

import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.model.ComputationResults;
import com.powsybl.security.SecurityAnalysisResult;

/**
 * Security Analysis computation results. Contains the network (post-contingencies) and the corresponding
 * {@link SecurityAnalysisResult}.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisComputationResults implements ComputationResults {
    private final Network network;
    private final SecurityAnalysisResult results;

    public SecurityAnalysisComputationResults(Network network, SecurityAnalysisResult results) {
        this.network = network;
        this.results = results;
    }

    public Network getNetwork() {
        return network;
    }

    public SecurityAnalysisResult getResults() {
        return results;
    }
}
