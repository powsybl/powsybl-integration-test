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

    private String inputContingencies;

    private String expectedNetwork;

    private String expectedResults;
    private String inputStateMonitors;

    public SecurityAnalysisTestCaseJson(String id, String inputNetwork, String inputParameters, String inputContingencies, String inputStateMonitors, String expectedNetwork,
                                        String expectedResults) {
        this.id = Objects.requireNonNull(id);
        this.inputNetwork = Objects.requireNonNull(inputNetwork);
        this.inputParameters = Objects.requireNonNull(inputParameters);
        setInputContingencies(inputContingencies);
        setInputStateMonitors(inputStateMonitors);
        setExpectedNetwork(expectedNetwork);
        setExpectedResults(expectedResults);
    }

    private void setInputContingencies(String inputContingencies) {
        if (inputContingencies != null) {
            this.inputContingencies = inputContingencies;
        } else {
            this.inputContingencies = "contingencies/" + id + ".json";
        }
    }

    private void setInputStateMonitors(String inputStateMonitors) {
        if (inputStateMonitors != null) {
            this.inputStateMonitors = inputStateMonitors;
        } else {
            this.inputStateMonitors = "statemonitors/" + id + ".json";
        }
    }

    private void setExpectedNetwork(String expectedNetwork) {
        if (expectedNetwork != null) {
            this.expectedNetwork = expectedNetwork;
        } else {
            this.expectedNetwork = "sareferences/" + id + ".xiidm";
        }
    }

    private void setExpectedResults(String expectedResults) {
        if (expectedResults != null) {
            this.expectedResults = expectedResults;
        } else {
            this.expectedResults = "sareferences/" + id + ".json";
        }
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
