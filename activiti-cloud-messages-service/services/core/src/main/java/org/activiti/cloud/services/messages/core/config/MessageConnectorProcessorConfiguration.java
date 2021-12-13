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
package org.activiti.cloud.services.messages.core.config;

import static org.activiti.cloud.services.messages.core.config.MessagesCoreAutoConfiguration.MESSAGE_CONNECTOR_AGGREGATOR_FACTORY_BEAN;
import static org.activiti.cloud.services.messages.core.config.MessagesCoreAutoConfiguration.MESSAGE_CONNECTOR_INTEGRATION_FLOW;
import java.util.List;
import org.activiti.cloud.services.messages.core.advice.MessageConnectorHandlerAdvice;
import org.activiti.cloud.services.messages.core.aggregator.MessageConnectorAggregator;
import org.activiti.cloud.services.messages.core.integration.MessageConnectorIntegrationFlowFunctional;
import org.activiti.cloud.services.messages.core.router.CommandConsumerMessageRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor;

@Configuration
@DependsOn(MESSAGE_CONNECTOR_AGGREGATOR_FACTORY_BEAN)
@ConditionalOnProperty(name = "activiti.stream.cloud.functional.binding", havingValue = "enabled")
public class MessageConnectorProcessorConfiguration {

    @Autowired
    private MessageAggregatorProperties properties;

    @Bean
    @DependsOn(MESSAGE_CONNECTOR_AGGREGATOR_FACTORY_BEAN)
    @ConditionalOnMissingBean(name = MESSAGE_CONNECTOR_INTEGRATION_FLOW)
    @ConditionalOnProperty(name = "activiti.stream.cloud.functional.binding", havingValue = "enabled")
    public IntegrationFlow messageConnectorIntegrationFlowFunctional(
            MessageConnectorAggregator aggregator,
            IdempotentReceiverInterceptor interceptor,
            List<MessageConnectorHandlerAdvice> adviceChain,
            CommandConsumerMessageRouter router) {
        return new MessageConnectorIntegrationFlowFunctional(
                aggregator,
                interceptor,
                adviceChain,
                properties,
                router);
    }
}
