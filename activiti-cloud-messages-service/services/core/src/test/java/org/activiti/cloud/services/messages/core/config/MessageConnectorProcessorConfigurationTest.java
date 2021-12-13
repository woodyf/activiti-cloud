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

import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.APP_NAME;
import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.MESSAGE_EVENT_CORRELATION_KEY;
import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.MESSAGE_EVENT_ID;
import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.MESSAGE_EVENT_NAME;
import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.MESSAGE_EVENT_OUTPUT_DESTINATION;
import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.MESSAGE_EVENT_TYPE;
import static org.activiti.cloud.services.messages.core.integration.MessageEventHeaders.SERVICE_FULL_NAME;
import java.util.Collections;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.api.process.model.builders.MessageEventPayloadBuilder;
import org.activiti.api.process.model.events.MessageDefinitionEvent;
import org.activiti.api.process.model.payloads.MessageEventPayload;
import org.activiti.cloud.services.messages.core.correlation.Correlations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({ TestChannelBinderConfiguration.class, MessageConnectorProcessorConfigurationTest.TestConfigurationContext.class })
@ActiveProfiles("binding")
class MessageConnectorProcessorConfigurationTest {

    @SpringBootApplication
    static class Application {

    }

    protected ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${activiti.cloud.application.name}")
    protected String activitiCloudApplicationName;

    @Value("${spring.application.name}")
    protected String springApplicationName;

    @TestConfiguration
    static class TestConfigurationContext {

        @Bean
        @BridgeFrom("errorChannel")
        MessageChannel errorQueue() {
            return MessageChannels.queue()
                    .get();
        }

        @Bean
        @BridgeFrom("discardChannel")
        MessageChannel discardQueue() {
            return MessageChannels.queue()
                    .get();
        }
    }

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    protected MessageGroupStore messageGroupStore;

    @Test
    public void test() {
        //given
        String messageEventName = "start";
        Message<?> startMessage = startMessageDeployedEvent(messageEventName);
        String correlationId = correlationId(startMessage);
        removeMessageGroup(correlationId);

        send(startMessage);

        Message<byte[]> result = outputDestination.receive(0L, "commandConsumerZ");
        System.out.println(new String(result.getPayload()));
    }
    
    protected void send(Message<?> message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message.getPayload());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        this.inputDestination.send(MessageBuilder.withPayload(json)
                .copyHeaders(message.getHeaders())
                .build());
    }

    protected MessageBuilder<MessageEventPayload> messageBuilder(String messageName,
            String correlationKey,
            String businessKey) {
        MessageEventPayload payload = MessageEventPayloadBuilder.messageEvent(messageName)
                .withCorrelationKey(correlationKey)
                .withBusinessKey(businessKey)
                .withVariables(Collections.singletonMap("key", businessKey))
                .build();
        return MessageBuilder.withPayload(payload)
                .setHeader(MESSAGE_EVENT_NAME, messageName)
                .setHeader(MESSAGE_EVENT_CORRELATION_KEY, correlationKey)
                .setHeader(MESSAGE_EVENT_ID, UUID.randomUUID())
                .setHeader(APP_NAME, activitiCloudApplicationName)
                .setHeader(MESSAGE_EVENT_OUTPUT_DESTINATION, "commandConsumerZ")
                .setHeader(SERVICE_FULL_NAME, springApplicationName);
    }

    protected Message<MessageEventPayload> startMessageDeployedEvent(String messageName) {
        return messageBuilder(messageName,
                null, null).setHeader(MESSAGE_EVENT_TYPE,
                        MessageDefinitionEvent.MessageDefinitionEvents.START_MESSAGE_DEPLOYED.name())
                .build();
    }

    protected String correlationId(Message<?> message) {
        return Correlations.getCorrelationId(message);
    }

    protected void removeMessageGroup(String correlationId) {
        messageGroupStore.removeMessageGroup(correlationId);
    }

}