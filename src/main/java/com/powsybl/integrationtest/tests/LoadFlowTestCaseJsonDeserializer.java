/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadFlowTestCaseJsonDeserializer extends StdDeserializer<LoadFlowTestCaseJson> {

    protected LoadFlowTestCaseJsonDeserializer() {
        super(LoadFlowTestCaseJson.class);
    }

    @Override
    public LoadFlowTestCaseJson deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        return LoadFlowTestCaseJson.parseJson(jsonParser);
    }
}
