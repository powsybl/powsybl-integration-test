/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.jsonconfig;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * DTO for the JSON representation of an SA Test Case.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestCaseJson {

    private final String id;

    private final String inputNetwork;

    private final String inputParameters;

    private final String inputContingencies;

    private final String expectedNetwork;

    private final String expectedResults;
    private final String inputStateMonitors;

    public SecurityAnalysisTestCaseJson(String id, String inputNetwork, String inputParameters) {
        this.id = Objects.requireNonNull(id);
        this.inputNetwork = Objects.requireNonNull(inputNetwork);
        this.inputParameters = Objects.requireNonNull(inputParameters);
        this.inputContingencies = "contingencies/" + id + ".json";
        this.inputStateMonitors = "statemonitors/" + id + ".json";
        this.expectedNetwork = "sareferences/" + id + ".xiidm";
        this.expectedResults = "sareferences/" + id + ".json";
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public String getInputNetwork() {
        return inputNetwork;
    }

    @Nonnull
    public String getInputParameters() {
        return inputParameters;
    }

    @Nonnull
    public String getInputContingencies() {
        return inputContingencies;
    }

    public String getInputStateMonitors() {
        return inputStateMonitors;
    }

    @Nonnull
    public String getExpectedNetwork() {
        return expectedNetwork;
    }

    @Nonnull
    public String getExpectedResults() {
        return expectedResults;
    }
}
