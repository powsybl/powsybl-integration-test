/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.contingency.*;
import com.powsybl.contingency.contingency.list.DefaultContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.integrationtest.creation.security.contingencies.ContingenciesSuppliers;
import com.powsybl.integrationtest.creation.security.contingencies.ContingenciesSupplier;
import com.powsybl.integrationtest.creation.security.statemonitors.StateMonitorsSupplier;
import com.powsybl.integrationtest.creation.security.statemonitors.StateMonitorsSuppliers;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationParameters;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationResults;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationRunner;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.converter.SecurityAnalysisResultExporters;
import com.powsybl.security.json.JsonSecurityAnalysisParameters;
import com.powsybl.security.monitor.StateMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A component that creates SA test cases reference files.
 * Use the main method, linking to a parameters file to output your own reference files (network and results).
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 * @author Théo Le Colleter <theo.lecolleter at artelys.com>
 */
public class SATestcaseCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SATestcaseCreator.class);

    private final SecurityAnalysisComputationRunner runner;

    private final ContingenciesSupplier contingenciesSupplier;

    private final StateMonitorsSupplier stateMonitorsSupplier;

    public SATestcaseCreator(SecurityAnalysisComputationRunner runner, ContingenciesSupplier contingenciesSupplier, StateMonitorsSupplier stateMonitorsSupplier) {
        this.runner = runner;
        this.contingenciesSupplier = contingenciesSupplier;
        this.stateMonitorsSupplier = stateMonitorsSupplier;
    }

    public void createResults(String exportName, Network network, SecurityAnalysisParameters saParams, Path outputDir, Path outputDirContingencies, Path outputDirStateMonitors, final Object contingenciesConfiguration, final Object stateMonitorsConfiguration)
            throws IOException {
        // Create a list of contingencies
        final List<Contingency> contingencies = contingenciesSupplier.getContingencies(network, contingenciesConfiguration);
        // Create a list of stateMonitors based on those contingencies
        final List<StateMonitor> stateMonitors = stateMonitorsSupplier.getStateMonitors(network, contingencies, stateMonitorsConfiguration);

        // Export contingencies in a .json file
        ObjectMapper mapper = JsonUtil.createObjectMapper();
        mapper.registerModule(new ContingencyJsonModule());
        DefaultContingencyList contingencyList = new DefaultContingencyList("contingencies", contingencies);
        OutputStream outputStream = Files.newOutputStream(outputDirContingencies.resolve(exportName + ".json"));
        mapper.writeValue(outputStream, contingencyList);

        // Export state monitors in a .json file
        OutputStream outputStreamStateMonitors = Files.newOutputStream(outputDirStateMonitors.resolve(exportName + ".json"));
        mapper.writeValue(outputStreamStateMonitors, stateMonitors);

        SecurityAnalysisComputationParameters parameters = new SecurityAnalysisComputationParameters(network, saParams,
                contingencies, stateMonitors);
        SecurityAnalysisComputationResults results = runner.computeResults(parameters);
        // Export network
        Properties properties = new Properties();
        properties.put(XMLExporter.ANONYMISED, "false");
        network.write("XIIDM", properties, outputDir.resolve(exportName));
        // Export results
        SecurityAnalysisResultExporters.getExporter("JSON").export(results.getResults(),
                Files.newBufferedWriter(outputDir.resolve(exportName + ".json")));
        LOGGER.info("Exported results to [" + outputDir + "], with name [" + exportName + "]");
    }

    public static void main(String[] args) throws IOException {
        Path parametersFilePath = Path.of(args[0]);
        if (!Files.exists(parametersFilePath) || Files.isDirectory(parametersFilePath)
                || !Files.isReadable(parametersFilePath)) {
            throw new IOException("Invalid parameters file : " + args[0]);
        }
        SATestcaseCreatorParameters params = SATestcaseCreatorParameters.load(parametersFilePath);
        for (SATestcaseCreatorParameters.Parameters parameters : params.getParameters()) {
            // Get state monitors provider parameters
            SATestcaseCreatorParameters.StateMonitorsSupplierParameters stateMonitorsSupplierParameters = parameters.getStateMonitorsSupplierParameters();
            // Fetch and set chosen implementation
            StateMonitorsSupplier stateMonitorsSupplier = StateMonitorsSuppliers.getInstance(stateMonitorsSupplierParameters.getName());
            // Get contingencies provider parameters
            SATestcaseCreatorParameters.ContingenciesSupplierParameters contingenciesSupplierParameters = parameters.getContingenciesSupplierParameters();
            // Fetch and set chosen implementation
            ContingenciesSupplier contingenciesSupplier = ContingenciesSuppliers.getInstance(contingenciesSupplierParameters.getName());

            SATestcaseCreator creator = new SATestcaseCreator(new SecurityAnalysisComputationRunner(), contingenciesSupplier, stateMonitorsSupplier);

            Network network = Network.read(parameters.getNetworkPath());
            SecurityAnalysisParameters saParams = JsonSecurityAnalysisParameters.read(parameters.getSAParametersPath());

            String testCaseName = parameters.getTestCaseName();
            Object contingenciesConfiguration = contingenciesSupplierParameters.getConfiguration();
            Object stateMonitorsConfiguration = stateMonitorsSupplierParameters.getConfiguration();
            Path outputDir = parameters.getOutputPath();
            Path outputDirContingencies = parameters.getContingenciesOutputPath();
            Path outputDirStateMonitors = parameters.getStateMonitorsOutputPath();
            Files.createDirectories(outputDir);
            Files.createDirectories(outputDirContingencies);
            Files.createDirectories(outputDirStateMonitors);
            creator.createResults(testCaseName, network, saParams, outputDir, outputDirContingencies, outputDirStateMonitors, contingenciesConfiguration, stateMonitorsConfiguration);

        }
    }
}
