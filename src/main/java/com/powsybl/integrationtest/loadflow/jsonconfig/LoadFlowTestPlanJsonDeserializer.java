/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadFlowTestPlanJsonDeserializer extends StdDeserializer<LoadFlowTestPlanJson> {

    protected LoadFlowTestPlanJsonDeserializer() {
        super(LoadFlowTestPlanJson.class);
    }

    @Override
    public LoadFlowTestPlanJson deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String version = null;
        List<LoadFlowTestCaseJson> testCases = null;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            switch (jsonParser.getCurrentName()) {
                case "version":
                    jsonParser.nextToken(); // skip
                    version = jsonParser.getValueAsString();
                    break;
                case "testCases":
                    jsonParser.nextToken();
                    testCases = jsonParser.readValueAs(new TypeReference<ArrayList<LoadFlowTestCaseJson>>() { });
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + jsonParser.getCurrentName());
            }
        }

        if (!"1.0".equals(version)) {
            //Only 1.0 version is supported for now
            throw new AssertionError("Version different than 1.0 not supported.");
        }
        return new LoadFlowTestPlanJson(testCases);
    }
}
