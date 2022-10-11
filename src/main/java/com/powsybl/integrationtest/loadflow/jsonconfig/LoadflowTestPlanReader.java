/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.jsonconfig.TestPlanReader;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationParameters;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadflowTestCase;
import com.powsybl.integrationtest.model.TestPlan;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import com.powsybl.loadflow.json.LoadFlowResultDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadflowTestPlanReader implements TestPlanReader<LoadflowComputationParameters, LoadflowComputationResults, LoadflowTestCase> {

    private final ObjectMapper objectMapper;

    public LoadflowTestPlanReader() {
        objectMapper = JsonUtil.createObjectMapper().registerModule(new LoadflowTestCaseModule());
    }

    @Override
    public TestPlan<LoadflowComputationParameters, LoadflowComputationResults, LoadflowTestCase> extractTestPlan(InputStream input) throws IOException {
        LoadflowTestPlanJson lftpJson = objectMapper.readValue(input, LoadflowTestPlanJson.class);
        List<LoadflowTestCase> testCases = new ArrayList<>();
        for (LoadflowTestCaseJson testCaseJson : lftpJson.getTestCases()) {
            testCases.add(buildFromJson(testCaseJson));
        }
        return new TestPlan<>(testCases);
    }

    /**
     * Build a LoadflowTestCase using its JSON representation
     *
     * @param testCaseJson JSON representation of a LoadflowTestCase
     * @return the built LoadflowTestCase
     * @throws IOException if provided information is not reachable for some reason
     */
    private static LoadflowTestCase buildFromJson(LoadflowTestCaseJson testCaseJson) throws IOException {
        Class<LoadflowTestPlanReader> cls = LoadflowTestPlanReader.class;

        LoadFlowParameters inputParameters = JsonLoadFlowParameters.read(
                Objects.requireNonNull(cls.getResourceAsStream("/" + testCaseJson.getInputParameters())));
        Network inputNetwork = Importers.loadNetwork(testCaseJson.getInputNetwork(),
                Objects.requireNonNull(cls.getResourceAsStream("/" + testCaseJson.getInputNetwork())));
        LoadFlowResult expectedResults = LoadFlowResultDeserializer.read(
                Objects.requireNonNull(cls.getResourceAsStream("/" + testCaseJson.getExpectedResults())));
        Network expectedNetwork = Importers.loadNetwork(testCaseJson.getExpectedNetwork(),
                Objects.requireNonNull(cls.getResourceAsStream("/" + testCaseJson.getExpectedNetwork())));

        return new LoadflowTestCase(testCaseJson.getId(),
                new LoadflowComputationParameters(inputNetwork, inputParameters),
                new LoadflowComputationResults(expectedNetwork, expectedResults));
    }

    /**
     * A JSON serialization module for Loadflow configuration objects
     */
    static class LoadflowTestCaseModule extends SimpleModule {
        public LoadflowTestCaseModule() {
            addDeserializer(LoadflowTestCaseJson.class, new LoadflowTestCaseJsonDeserializer());
            addDeserializer(LoadflowTestPlanJson.class, new LoadflowTestPlanJsonDeserializer());
        }
    }
}
