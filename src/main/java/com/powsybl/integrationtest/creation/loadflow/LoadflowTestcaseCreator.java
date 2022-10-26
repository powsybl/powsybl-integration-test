/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.loadflow;

import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationParameters;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationRunner;
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
public class LoadflowTestcaseCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadflowTestcaseCreator.class);

    private final LoadflowComputationRunner runner;

    public LoadflowTestcaseCreator(LoadflowComputationRunner runner) {
        this.runner = runner;
    }

    public void createResults(String exportName, Path outputDir, Network network, LoadFlowParameters lfParameters) {
        LoadflowComputationParameters parameters = new LoadflowComputationParameters(network, lfParameters);
        LoadflowComputationResults results = runner.computeResults(parameters);
        // Export network
        Properties properties = new Properties();
        properties.put(XMLExporter.ANONYMISED, "false");
        Exporters.export("XIIDM", network, properties, outputDir.resolve(exportName));
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
        LoadflowTestcaseCreatorParameters params = LoadflowTestcaseCreatorParameters.load(parametersFilePath);

        for (LoadflowTestcaseCreatorParameters.Parameters parameters : params.getParameters()) {
            LoadflowTestcaseCreator creator = new LoadflowTestcaseCreator(new LoadflowComputationRunner());

            Network network = Importers.loadNetwork(parameters.getNetworkPath());
            LoadFlowParameters lfParams = JsonLoadFlowParameters.read(parameters.getLfParametersPath());

            Path outputDir = parameters.getOutputPath();
            Files.createDirectories(outputDir);
            creator.createResults(parameters.getTestCaseName(), outputDir, network, lfParams);
        }
    }

}
