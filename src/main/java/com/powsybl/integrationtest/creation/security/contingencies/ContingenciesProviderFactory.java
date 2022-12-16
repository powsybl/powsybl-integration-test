/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.contingencies;

import com.powsybl.contingency.ContingenciesProvider;

import java.util.HashMap;

/**
 * Factory class that allows to choose an implementation of {@link ContingenciesProvider} based on a given string.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public class ContingenciesProviderFactory {

    /**
     * Create an instance of the chosen implementation of {@link ContingenciesProvider}
     * @param provider
     * @param contingenciesRate
     * @return Instance of the chosen implementation of {@link ContingenciesProvider}
     */
    public ContingenciesProvider createContingenciesProvider(String provider, HashMap<Class, Double> contingenciesRate) {
        switch (provider) {
            case "default":
                return new EfficientContingenciesProvider(contingenciesRate);
            default:
                throw new IllegalArgumentException("Unknown provider " + provider);
        }
    }
}
