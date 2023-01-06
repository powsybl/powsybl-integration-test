/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.statemonitors;

import com.powsybl.integrationtest.creation.security.statemonitors.StateMonitorsProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


public class StateMonitorsProviders {
    private StateMonitorsProviders() {

    }

    private static final Map<String, StateMonitorsProvider> PROVIDERS = new HashMap<>();

    static {
        ServiceLoader<StateMonitorsProvider> loader = ServiceLoader.load(StateMonitorsProvider.class);
        loader.forEach(p -> PROVIDERS.put(p.getClass().getSimpleName(), p));
    }

    public static StateMonitorsProvider getInstance(String providerName) {
        return PROVIDERS.get(providerName);
    }
}
