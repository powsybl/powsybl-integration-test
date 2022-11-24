/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.loadflow;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationParameters;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationRunner;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import com.powsybl.loadflow.json.LoadFlowResultSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * A component that creates loadflow test cases reference files.
 * Use the main method, linking to a parameters file to output your own reference files (network and results).
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowTestcaseCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowTestcaseCreator.class);

    private final LoadFlowComputationRunner runner;

    public LoadFlowTestcaseCreator(LoadFlowComputationRunner runner) {
        this.runner = runner;
    }

    public void createResults(String exportName, Path outputDir, Network network, LoadFlowParameters lfParameters) {
        LoadFlowComputationParameters parameters = new LoadFlowComputationParameters(network, lfParameters);
        LoadFlowComputationResults results = runner.computeResults(parameters);
        // Export network
        Properties properties = new Properties();
        properties.put(XMLExporter.ANONYMISED, "false");
        network.write("XIIDM", properties, outputDir.resolve(exportName));
        // Export results
        LoadFlowResultSerializer.write(results.getResult(), outputDir.resolve(exportName + ".json"));
        LOGGER.info("Exported results to [" + outputDir + "], with name [" + exportName + "]");
    }

    public static void main(String[] args) throws IOException {
        Path parametersFilePath = Path.of(args[0]);
        if (!Files.exists(parametersFilePath) || Files.isDirectory(parametersFilePath)
                || !Files.isReadable(parametersFilePath)) {
            throw new IOException("Invalid parameters file : " + args[0]);
        }
        LoadFlowTestcaseCreatorParameters params = LoadFlowTestcaseCreatorParameters.load(parametersFilePath);

        for (LoadFlowTestcaseCreatorParameters.Parameters parameters : params.getParameters()) {
            LoadFlowTestcaseCreator creator = new LoadFlowTestcaseCreator(new LoadFlowComputationRunner());

            Network network = Network.read(parameters.getNetworkPath());
            LoadFlowParameters lfParams = JsonLoadFlowParameters.read(parameters.getLfParametersPath());

            Path outputDir = parameters.getOutputPath();
            Files.createDirectories(outputDir);
            creator.createResults(parameters.getTestCaseName(), outputDir, network, lfParams);
        }
    }

}
