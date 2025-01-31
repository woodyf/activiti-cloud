/*
 * Copyright 2017-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cloud.services.query.test;

import org.activiti.cloud.services.test.identity.IdentityTokenProducer;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;

@TestComponent
public class ProcessDefinitionRestTemplate extends BaseProcessDefinitionRestTemplate {

    protected ProcessDefinitionRestTemplate(TestRestTemplate testRestTemplate,
                                            IdentityTokenProducer identityTokenProducer) {
        super(testRestTemplate,
              identityTokenProducer);
    }

    @Override
    protected String getProcessDefinitionsURL() {
        return "/v1/process-definitions";
    }

}
