package com.powsybl.integrationtest.utils;

import com.powsybl.integrationtest.loadflow.jsonconfig.LoadFlowTestCaseJson;
import com.powsybl.integrationtest.loadflow.jsonconfig.LoadFlowTestPlanJson;
import com.powsybl.integrationtest.loadflow.jsonconfig.LoadFlowTestPlanReader;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadFlowTestCase;
import com.powsybl.integrationtest.loadflow.model.LoadFlowTestRunner;
import com.powsybl.loadflow.json.LoadFlowResultSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class LoadFlowTestPlanUpdater {

    private LoadFlowTestPlanUpdater() {
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        LoadFlowTestPlanReader reader = new LoadFlowTestPlanReader();
        LoadFlowTestRunner runner = new LoadFlowTestRunner();
        Path resourceDirectory = Paths.get("src", "test", "resources");
        try (InputStream res = Files.newInputStream(resourceDirectory.resolve("loadFlowTestPlan.json"))) {
            LoadFlowTestPlanJson jsonPlan = reader.readTestPlan(res);

            for (LoadFlowTestCaseJson testCaseJson : jsonPlan.getTestCases()) {
                LoadFlowTestCase testCase = LoadFlowTestPlanReader.buildFromJson(testCaseJson, resourceDirectory);
                LoadFlowComputationResults results = runner.runTestsWithoutChecks(testCase);
                results.getNetwork().write("XIIDM", null, resourceDirectory.resolve(testCaseJson.getExpectedNetwork()));
                LoadFlowResultSerializer.write(results.getResult(), resourceDirectory.resolve(testCaseJson.getExpectedResults()));
            }
        }
    }
}
