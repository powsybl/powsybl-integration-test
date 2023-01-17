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
import java.util.stream.Collectors;


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

    private final HashMap<ContingenciesSupplier, HashMap<String, ?>> contingenciesSuppliersHashMap;

    private final HashMap<StateMonitorsSupplier, HashMap<String, ?>> stateMonitorsSuppliersHashMap;

    public SATestcaseCreator(SecurityAnalysisComputationRunner runner, HashMap<ContingenciesSupplier, HashMap<String, ?>> contingenciesSuppliersHashMap, HashMap<StateMonitorsSupplier, HashMap<String, ?>> stateMonitorsSuppliersHashMap) {
        this.runner = runner;
        this.contingenciesSuppliersHashMap = contingenciesSuppliersHashMap;
        this.stateMonitorsSuppliersHashMap = stateMonitorsSuppliersHashMap;
    }

    public void createResults(String exportName, Network network, SecurityAnalysisParameters saParams, Path outputDir, Path outputDirContingencies, Path outputDirStateMonitors)
            throws IOException {
        Map<String, Contingency> contingencyMap = new HashMap<>();
        List<Contingency> allContingencies = new ArrayList<>();
        // Add all contingencies in a list, regardless of contingencies' duplications
        contingenciesSuppliersHashMap.forEach((supplier, conf) -> allContingencies.addAll(supplier.getContingencies(network, conf)));
        // Avoid duplications by adding contingency in a map with a unique id based on the sorted elements' id
        for (Contingency contingency : allContingencies) {
            String contingencyId = String.valueOf(contingency.getElements().stream().map(elt -> elt.getId()).sorted().collect(Collectors.toList()));
            contingencyMap.put(contingencyId, contingency);
        }
        List<Contingency> contingencies = new ArrayList<>(contingencyMap.values()) ;

        Map<String, StateMonitor> stateMonitorsMap = new HashMap<>();
        List<StateMonitor> allStateMonitors = new ArrayList<>();
        // Add all stateMonitors in a list, regardless of stateMonitors' duplications
        stateMonitorsSuppliersHashMap.forEach((supplier, conf) -> allStateMonitors.addAll(supplier.getStateMonitors(network, contingencies, conf)));
        // Avoid duplications by adding stateMonitors in a map with a unique id based on the sorted ids
        for (StateMonitor stateMonitor : allStateMonitors) {
            String sortedBranchIds = String.valueOf(stateMonitor.getBranchIds().stream().sorted().collect(Collectors.toList()));
            String sortedVoltageLevelIds = String.valueOf(stateMonitor.getVoltageLevelIds().stream().sorted().collect(Collectors.toList()));
            String sortedThreeWindingsTransformerIds = String.valueOf(stateMonitor.getThreeWindingsTransformerIds().stream().sorted().collect(Collectors.toList()));
            String stateMonitorId = sortedBranchIds + sortedVoltageLevelIds + sortedThreeWindingsTransformerIds;
            stateMonitorsMap.put(stateMonitorId, stateMonitor);
        }
        List<StateMonitor> stateMonitors = new ArrayList<>(stateMonitorsMap.values());

        // Export contingencies in a .json file
        ObjectMapper mapper = JsonUtil.createObjectMapper();
        mapper.registerModule(new ContingencyJsonModule());
        DefaultContingencyList contingencyList = new DefaultContingencyList("contingencies", contingencies);
        OutputStream outputStream = Files.newOutputStream(outputDirContingencies.resolve(exportName + ".json"));
        mapper.writeValue(outputStream, contingencyList);

        // Export state monitors in a .json file
        OutputStream outputStreamStateMonitors = Files.newOutputStream(outputDirStateMonitors.resolve(exportName + ".json"));
        mapper.writeValue(outputStreamStateMonitors, stateMonitors);

        SecurityAnalysisComputationParameters parameters = new SecurityAnalysisComputationParameters(network, saParams, contingencies, stateMonitors);
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
            HashMap<ContingenciesSupplier, HashMap<String, ?>> contingenciesSuppliersHashMap = new HashMap<>();
            for (SATestcaseCreatorParameters.ContingenciesSupplierParameters contingenciesSupplierParameters : parameters.getContingenciesSuppliersParameters()) {
                // Fetch and associate configuration to implementation
                ContingenciesSupplier contingenciesSupplier = ContingenciesSuppliers.getInstance(contingenciesSupplierParameters.getName());
                contingenciesSuppliersHashMap.put(contingenciesSupplier, contingenciesSupplierParameters.getConfiguration());
            }
            HashMap<StateMonitorsSupplier, HashMap<String, ?>> stateMonitorsSuppliersHashMap = new HashMap<>();
            for (SATestcaseCreatorParameters.StateMonitorsSupplierParameters stateMonitorsSupplierParameters : parameters.getStateMonitorsSuppliersParameters()) {
                // Fetch and set chosen implementation
                StateMonitorsSupplier stateMonitorsSupplier = StateMonitorsSuppliers.getInstance(stateMonitorsSupplierParameters.getName());
                stateMonitorsSuppliersHashMap.put(stateMonitorsSupplier, stateMonitorsSupplierParameters.getConfiguration());
            }

            SATestcaseCreator creator = new SATestcaseCreator(new SecurityAnalysisComputationRunner(), contingenciesSuppliersHashMap, stateMonitorsSuppliersHashMap);

            Network network = Network.read(parameters.getNetworkPath());
            SecurityAnalysisParameters saParams = JsonSecurityAnalysisParameters.read(parameters.getSAParametersPath());

            String testCaseName = parameters.getTestCaseName();
            Path outputDir = parameters.getOutputPath();
            Path outputDirContingencies = parameters.getContingenciesOutputPath();
            Path outputDirStateMonitors = parameters.getStateMonitorsOutputPath();
            Files.createDirectories(outputDir);
            Files.createDirectories(outputDirContingencies);
            Files.createDirectories(outputDirStateMonitors);
            creator.createResults(testCaseName, network, saParams, outputDir, outputDirContingencies, outputDirStateMonitors);

        }
    }
}
