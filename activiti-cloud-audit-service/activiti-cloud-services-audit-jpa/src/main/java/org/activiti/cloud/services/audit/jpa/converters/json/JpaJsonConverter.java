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
package org.activiti.cloud.services.audit.jpa.converters.json;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.BPMNActivity;
import org.activiti.api.process.model.BPMNError;
import org.activiti.api.process.model.BPMNMessage;
import org.activiti.api.process.model.BPMNSequenceFlow;
import org.activiti.api.process.model.BPMNSignal;
import org.activiti.api.process.model.BPMNTimer;
import org.activiti.api.process.model.Deployment;
import org.activiti.api.process.model.IntegrationContext;
import org.activiti.api.process.model.MessageSubscription;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.runtime.model.impl.BPMNActivityImpl;
import org.activiti.api.runtime.model.impl.BPMNErrorImpl;
import org.activiti.api.runtime.model.impl.BPMNMessageImpl;
import org.activiti.api.runtime.model.impl.BPMNSequenceFlowImpl;
import org.activiti.api.runtime.model.impl.BPMNSignalImpl;
import org.activiti.api.runtime.model.impl.BPMNTimerImpl;
import org.activiti.api.runtime.model.impl.DeploymentImpl;
import org.activiti.api.runtime.model.impl.IntegrationContextImpl;
import org.activiti.api.runtime.model.impl.MessageSubscriptionImpl;
import org.activiti.api.runtime.model.impl.ProcessDefinitionImpl;
import org.activiti.api.runtime.model.impl.ProcessInstanceImpl;
import org.activiti.api.runtime.model.impl.VariableInstanceImpl;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.impl.TaskImpl;
import org.activiti.cloud.services.audit.api.AuditException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JpaJsonConverter<T> implements AttributeConverter<T, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        {
            SimpleModule module = new SimpleModule("mapCommonModelInterfaces",
                                                   Version.unknownVersion());
            SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver() {
                //this is a workaround for https://github.com/FasterXML/jackson-databind/issues/2019
                //once version 2.9.6 is related we can remove this @override method
                @Override
                public JavaType resolveAbstractType(DeserializationConfig config,
                                                    BeanDescription typeDesc) {
                    return findTypeMapping(config,
                                           typeDesc.getType());
                }
            };

            resolver.addMapping(ProcessDefinition.class,
                                ProcessDefinitionImpl.class);
            resolver.addMapping(VariableInstance.class,
                                VariableInstanceImpl.class);
            resolver.addMapping(ProcessInstance.class,
                                ProcessInstanceImpl.class);
            resolver.addMapping(Task.class,
                                TaskImpl.class);
            resolver.addMapping(BPMNActivity.class,
                                BPMNActivityImpl.class);
            resolver.addMapping(BPMNSequenceFlow.class,
                                BPMNSequenceFlowImpl.class);
            resolver.addMapping(BPMNSignal.class,
            					BPMNSignalImpl.class);
            resolver.addMapping(BPMNTimer.class,
                                BPMNTimerImpl.class);
            resolver.addMapping(BPMNError.class,
                                BPMNErrorImpl.class);
            resolver.addMapping(BPMNMessage.class,
                                BPMNMessageImpl.class);
            resolver.addMapping(MessageSubscription.class,
                                MessageSubscriptionImpl.class);
            resolver.addMapping(IntegrationContext.class,
                                IntegrationContextImpl.class);
            resolver.addMapping(Deployment.class,
                                DeploymentImpl.class);

            module.setAbstractTypes(resolver);

            objectMapper.registerModule(module);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    private Class<T> entityClass;

    public JpaJsonConverter(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String convertToDatabaseColumn(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new AuditException("Unable to serialize object.",
                                     e);
        }
    }

    @Override
    public T convertToEntityAttribute(String entityTextRepresentation) {
        try {
            if(entityTextRepresentation != null && entityTextRepresentation.length() > 0) {
                return objectMapper.readValue(entityTextRepresentation,
                                              entityClass);
            } else {
                return null;
            }

        } catch (IOException e) {
            throw new AuditException("Unable to deserialize object.",
                                        e);
        }
    }
}
