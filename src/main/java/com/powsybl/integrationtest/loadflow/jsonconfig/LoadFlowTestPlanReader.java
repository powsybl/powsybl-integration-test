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
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationParameters;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadFlowTestCase;
import com.powsybl.integrationtest.model.TestPlan;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import com.powsybl.loadflow.json.LoadFlowResultDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowTestPlanReader implements TestPlanReader<LoadFlowComputationParameters, LoadFlowComputationResults, LoadFlowTestCase> {

    private final ObjectMapper objectMapper;

    public LoadFlowTestPlanReader() {
        objectMapper = JsonUtil.createObjectMapper().registerModule(new LoadFlowTestCaseModule());
    }

    @Override
    public TestPlan<LoadFlowComputationParameters, LoadFlowComputationResults, LoadFlowTestCase> extractTestPlan(InputStream input)
            throws IOException, URISyntaxException {
        LoadFlowTestPlanJson lftpJson = objectMapper.readValue(input, LoadFlowTestPlanJson.class);
        List<LoadFlowTestCase> testCases = new ArrayList<>();
        for (LoadFlowTestCaseJson testCaseJson : lftpJson.getTestCases()) {
            testCases.add(buildFromJson(testCaseJson));
        }
        return new TestPlan<>(testCases);
    }

    /**
     * Build a {@link LoadFlowTestCase} using its JSON representation
     *
     * @param testCaseJson JSON representation of a {@link LoadFlowTestCase}
     * @return the built LoadFlowTestCase
     * @throws IOException if provided information is not reachable for some reason
     */
    private static LoadFlowTestCase buildFromJson(LoadFlowTestCaseJson testCaseJson) throws IOException, URISyntaxException {
        Class<LoadFlowTestPlanReader> cls = LoadFlowTestPlanReader.class;

        LoadFlowParameters inputParameters = JsonLoadFlowParameters.read(
                Objects.requireNonNull(cls.getResourceAsStream("/" + testCaseJson.getInputParameters())));
        Path networkFilePath = Path.of(Objects.requireNonNull(
                cls.getResource("/" + testCaseJson.getInputNetwork())).toURI());
        Network inputNetwork = Importers.loadNetwork(Objects.requireNonNull(networkFilePath));
        LoadFlowResult expectedResults = LoadFlowResultDeserializer.read(
                Objects.requireNonNull(cls.getResourceAsStream("/" + testCaseJson.getExpectedResults())));
        Path expectedNetworkPath = Path.of(Objects.requireNonNull(
                cls.getResource("/" + testCaseJson.getExpectedNetwork())).toURI());
        Network expectedNetwork = Importers.loadNetwork(Objects.requireNonNull(expectedNetworkPath));

        return new LoadFlowTestCase(testCaseJson.getId(),
                new LoadFlowComputationParameters(inputNetwork, inputParameters),
                new LoadFlowComputationResults(expectedNetwork, expectedResults));
    }

    /**
     * A JSON serialization module for LoadFlow configuration objects
     */
    static class LoadFlowTestCaseModule extends SimpleModule {
        public LoadFlowTestCaseModule() {
            addDeserializer(LoadFlowTestCaseJson.class, new LoadFlowTestCaseJsonDeserializer());
            addDeserializer(LoadFlowTestPlanJson.class, new LoadFlowTestPlanJsonDeserializer());
        }
    }
}
