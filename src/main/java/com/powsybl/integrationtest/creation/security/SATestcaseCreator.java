/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.security;

import com.powsybl.contingency.ContingencyList;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * A component that creates SA test cases reference files.
 * Use the main method, linking to a parameters file to output your own reference files (network and results).
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SATestcaseCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SATestcaseCreator.class);

    private final SecurityAnalysisComputationRunner runner;

    public SATestcaseCreator(SecurityAnalysisComputationRunner runner) {
        this.runner = runner;
    }

    public void createResults(String exportName, Network network, SecurityAnalysisParameters saParams,
                              ContingencyList contingencyList, List<StateMonitor> stateMonitorList, Path outputDir)
            throws IOException {
        SecurityAnalysisComputationParameters parameters = new SecurityAnalysisComputationParameters(network, saParams,
                contingencyList.getContingencies(network), stateMonitorList);
        SecurityAnalysisComputationResults results = runner.computeResults(parameters);
        // Export network
        Properties properties = new Properties();
        properties.put(XMLExporter.ANONYMISED, "false");
        Exporters.export("XIIDM", network, properties, outputDir.resolve(exportName));
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
            SATestcaseCreator creator = new SATestcaseCreator(new SecurityAnalysisComputationRunner());

            Network network = Importers.loadNetwork(parameters.getNetworkPath());
            SecurityAnalysisParameters saParams = JsonSecurityAnalysisParameters.read(parameters.getSAParametersPath());
            ContingencyList cList = ContingencyList.load(parameters.getContingenciesListPath());
            List<StateMonitor> smList = StateMonitor.read(parameters.getStateMonitorsListPath());

            Path outputDir = parameters.getOutputPath();
            Files.createDirectories(outputDir);
            creator.createResults(parameters.getTestCaseName(), network, saParams, cList, smList, outputDir);
        }
    }
}
