/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.contingencies;

import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;

import java.util.HashMap;
import java.util.List;

/**
 * Interface allowing to implement different contingencies' provider.
 *
 * Different from {@link com.powsybl.contingency.ContingenciesProvider} since we need to set the configuration according to the input.
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public interface ContingenciesSupplier {

    /**
     * Set the configuration according to the input.
     * <p>Create and return a list of {@link Contingency} which is created according to different strategy, depending on the implementation used.</p>
     * @param network
     * @param configuration
     * @return a list of {@link Contingency}
     */
    List<Contingency> getContingencies(Network network, HashMap<String, ?> configuration);
}
