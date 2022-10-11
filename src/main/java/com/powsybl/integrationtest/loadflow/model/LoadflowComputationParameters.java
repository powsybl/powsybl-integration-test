/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.model;

import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.model.ComputationParameters;
import com.powsybl.loadflow.LoadFlowParameters;

import java.util.Objects;

/**
 * Parameters required to run a loadflow computation. Contains a {@code Network} and a {@link LoadFlowParameters}.
 * This object basically behaves like a simple bean.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadflowComputationParameters implements ComputationParameters {

    private final Network network;

    private final LoadFlowParameters parameters;

    public LoadflowComputationParameters(Network network, LoadFlowParameters parameters) {
        this.network = Objects.requireNonNull(network);
        this.parameters = Objects.requireNonNull(parameters);
    }

    public Network getNetwork() {
        return network;
    }

    public LoadFlowParameters getParameters() {
        return parameters;
    }
}
