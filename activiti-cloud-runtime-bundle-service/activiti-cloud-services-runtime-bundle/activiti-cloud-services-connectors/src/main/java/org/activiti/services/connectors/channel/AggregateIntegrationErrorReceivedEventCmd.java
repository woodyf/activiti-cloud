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

package org.activiti.services.connectors.channel;

import org.activiti.cloud.api.process.model.IntegrationError;
import org.activiti.cloud.api.process.model.impl.events.CloudIntegrationErrorReceivedEventImpl;
import org.activiti.cloud.services.events.configuration.RuntimeBundleProperties;
import org.activiti.cloud.services.events.listeners.ProcessEngineEventsAggregator;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

class AggregateIntegrationErrorReceivedEventCmd implements Command<Void> {

    private final IntegrationError integrationError;
    private final RuntimeBundleProperties runtimeBundleProperties;
    private final ProcessEngineEventsAggregator processEngineEventsAggregator;

    AggregateIntegrationErrorReceivedEventCmd(IntegrationError integrationError,
        RuntimeBundleProperties runtimeBundleProperties,
        ProcessEngineEventsAggregator processEngineEventsAggregator) {
        this.integrationError = integrationError;
        this.runtimeBundleProperties = runtimeBundleProperties;
        this.processEngineEventsAggregator = processEngineEventsAggregator;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (runtimeBundleProperties.getEventsProperties()
            .isIntegrationAuditEventsEnabled()) {
            CloudIntegrationErrorReceivedEventImpl integrationErrorReceived = new CloudIntegrationErrorReceivedEventImpl(
                integrationError.getIntegrationContext(),
                integrationError.getErrorCode(),
                integrationError.getErrorMessage(),
                integrationError.getErrorClassName(),
                integrationError.getStackTraceElements());
                processEngineEventsAggregator.add(integrationErrorReceived);
        }

        return null;
    }
}
