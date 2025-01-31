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
package org.activiti.cloud.starter.rb.configuration;

import org.activiti.cloud.common.messaging.config.ActivitiMessagingDestinationTransformer;
import org.activiti.engine.impl.bpmn.behavior.VariablesPropagator;
import org.activiti.engine.impl.event.EventSubscriptionPayloadMappingProvider;
import org.activiti.runtime.api.impl.ExtensionsVariablesMappingProvider;
import org.activiti.spring.boot.ProcessEngineAutoConfiguration;
import org.activiti.spring.process.ProcessVariablesInitiator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import static org.activiti.spring.boot.ProcessEngineAutoConfiguration.BEHAVIOR_FACTORY_MAPPING_CONFIGURER;

@AutoConfigureBefore(ProcessEngineAutoConfiguration.class)
@Configuration
@Import(RuntimeBundleSwaggerConfig.class)
@PropertySource("classpath:rb.properties")
public class ActivitiCloudEngineAutoConfiguration {

    @Bean(BEHAVIOR_FACTORY_MAPPING_CONFIGURER)
    @ConditionalOnMissingBean(name = BEHAVIOR_FACTORY_MAPPING_CONFIGURER)
    public SignalBehaviourConfigurer signalBehaviourConfigurator(ApplicationContext applicationContext,
        ExtensionsVariablesMappingProvider variablesMappingProvider, ProcessVariablesInitiator processVariablesInitiator,
        EventSubscriptionPayloadMappingProvider eventSubscriptionPayloadMappingProvider, VariablesPropagator variablesPropagator) {
        return new SignalBehaviourConfigurer(applicationContext, variablesMappingProvider, processVariablesInitiator,
            eventSubscriptionPayloadMappingProvider, variablesPropagator);
    }

    @Bean
    public ActivitiConnectorDestinationMappingStrategy runtimeBundleConnectorDestinationMappingStrategy(ActivitiMessagingDestinationTransformer destinationTransformer) {
        return new ActivitiConnectorDestinationMappingStrategy(destinationTransformer);
    }
}
