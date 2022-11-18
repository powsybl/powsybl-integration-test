/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.jsonconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.DefaultContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.jsonconfig.TestPlanReader;
import com.powsybl.integrationtest.model.TestPlan;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationParameters;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationResults;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestCase;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.json.JsonSecurityAnalysisParameters;
import com.powsybl.security.json.SecurityAnalysisResultDeserializer;
import com.powsybl.security.monitor.StateMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JSON Reader for Security Analysis Test Plans.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestPlanReader implements
        TestPlanReader<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults, SecurityAnalysisTestCase> {

    private final ObjectMapper objectMapper;

    public SecurityAnalysisTestPlanReader() {
        objectMapper = JsonUtil.createObjectMapper().registerModule(new SATestCaseModule());
    }

    private static SecurityAnalysisTestCase buildFromJson(SecurityAnalysisTestCaseJson testCaseJson)
            throws IOException, URISyntaxException {
        Class<SecurityAnalysisTestPlanReader> cls = SecurityAnalysisTestPlanReader.class;

        // Load network from resources
        String network = testCaseJson.getInputNetwork();
        Path networkPath = Path.of(Objects.requireNonNull(cls.getResource("/" + network)).toURI());
        Network iNetwork = Importers.loadNetwork(Objects.requireNonNull(networkPath));
        // Load input params from resources
        String parameters = testCaseJson.getInputParameters();
        Path paramPath = Path.of(Objects.requireNonNull(cls.getResource("/" + parameters)).toURI());
        SecurityAnalysisParameters iParameters = JsonSecurityAnalysisParameters.read(paramPath);
        // Load contingencies from resources
        String contingencies = testCaseJson.getInputContingencies();
        ObjectMapper mapper = JsonUtil.createObjectMapper();
        mapper.registerModule(new ContingencyJsonModule());
        DefaultContingencyList contingencyList =
                mapper.readValue(cls.getResourceAsStream("/" + contingencies), DefaultContingencyList.class);
        List<Contingency> iContingencies = contingencyList.getContingencies();
        // Load state monitors from resources
        String stateMonitors = testCaseJson.getInputStateMonitors();
        Path stateMonitorsPath = Path.of(Objects.requireNonNull(cls.getResource("/" + stateMonitors)).toURI());
        List<StateMonitor> iStateMonitors = StateMonitor.read(stateMonitorsPath);

        // Load reference network from resources
        network = testCaseJson.getExpectedNetwork();
        networkPath = Path.of(Objects.requireNonNull(cls.getResource("/" + network)).toURI());
        Network refNetwork = Importers.loadNetwork(Objects.requireNonNull(networkPath));
        // Load reference results from resources
        String results = testCaseJson.getExpectedResults();
        Path resultsPath = Path.of(Objects.requireNonNull(cls.getResource("/" + results)).toURI());
        SecurityAnalysisResult refResults = SecurityAnalysisResultDeserializer.read(resultsPath);

        // Build test case
        SecurityAnalysisComputationParameters sacParameters
                = new SecurityAnalysisComputationParameters(iNetwork, iParameters, iContingencies,
                iStateMonitors);
        SecurityAnalysisComputationResults sacResults = new SecurityAnalysisComputationResults(refNetwork, refResults);
        return new SecurityAnalysisTestCase(testCaseJson.getId(), sacParameters, sacResults);
    }

    @Override
    public TestPlan<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults, SecurityAnalysisTestCase> extractTestPlan(InputStream input)
            throws IOException, URISyntaxException {
        SecurityAnalysisTestPlanJson satpJson = objectMapper.readValue(input, SecurityAnalysisTestPlanJson.class);
        List<SecurityAnalysisTestCase> testCases = new ArrayList<>();
        for (SecurityAnalysisTestCaseJson testCaseJson : satpJson.getTestCases()) {
            testCases.add(buildFromJson(testCaseJson));
        }
        return new TestPlan<>(testCases);
    }

    private static class SATestCaseModule extends SimpleModule {
        public SATestCaseModule() {
            addDeserializer(SecurityAnalysisTestCaseJson.class, new SecurityAnalysisTestCaseJsonDeserializer());
            addDeserializer(SecurityAnalysisTestPlanJson.class, new SecurityAnalysisTestPlanJsonDeserializer());
        }
    }
}
