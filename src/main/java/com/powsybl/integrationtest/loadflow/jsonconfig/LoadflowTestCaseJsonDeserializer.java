/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadflowTestCaseJsonDeserializer extends StdDeserializer<LoadflowTestCaseJson> {

    protected LoadflowTestCaseJsonDeserializer() {
        super(LoadflowTestCaseJson.class);
    }

    @Override
    public LoadflowTestCaseJson deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        return LoadflowTestCaseJson.parseJson(jsonParser);
    }
}
