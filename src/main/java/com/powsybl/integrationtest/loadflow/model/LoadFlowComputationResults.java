/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.model;

import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.model.ComputationResults;
import com.powsybl.loadflow.LoadFlowResult;

import java.util.Objects;

/**
 * Represents the results of a loadflow computation. Contains both a {@link Network}, that contains all numeric results.
 * Also contains a {@link LoadFlowResult} object that helps user to access the meta-data relative to the computation.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowComputationResults implements ComputationResults {

    private final Network network;
    private final LoadFlowResult result;

    public LoadFlowComputationResults(Network network, LoadFlowResult result) {
        this.network = Objects.requireNonNull(network);
        this.result = Objects.requireNonNull(result);
    }

    public Network getNetwork() {
        return network;
    }

    public LoadFlowResult getResult() {
        return result;
    }
}
