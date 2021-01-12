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
package org.activiti.cloud.api.process.model.impl.events;

import java.util.List;
import java.util.Objects;
import org.activiti.api.process.model.IntegrationContext;
import org.activiti.api.process.model.events.IntegrationEvent;
import org.activiti.cloud.api.model.shared.impl.events.CloudRuntimeEventImpl;
import org.activiti.cloud.api.process.model.events.CloudIntegrationFailedEvent;

public class CloudIntegrationFailedEventImpl extends CloudRuntimeEventImpl<IntegrationContext, IntegrationEvent.IntegrationEvents>
        implements CloudIntegrationFailedEvent {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private String errorMessage;
    private String errorClassName;
    private List<StackTraceElement> stackTraceElements;

    public CloudIntegrationFailedEventImpl() {
    }

    public CloudIntegrationFailedEventImpl(IntegrationContext integrationContext,
                                                  String errorCode,
                                                  String errorMessage,
                                                  String errorClassName,
                                                  List<StackTraceElement> stackTraceElements) {
        super(integrationContext);
        if (getEntity() != null) {
            setEntityId(getEntity().getId());
        }

        if(integrationContext.getProcessInstanceId() != null) {
            setProcessInstanceId(integrationContext.getProcessInstanceId());
        }
        if(integrationContext.getProcessDefinitionId() != null) {
            setProcessDefinitionId(integrationContext.getProcessDefinitionId());
        }
        if(integrationContext.getProcessDefinitionVersion() != null) {
            setProcessDefinitionVersion(integrationContext.getProcessDefinitionVersion());
        }
        if(integrationContext.getProcessDefinitionKey() != null) {
            setProcessDefinitionKey(integrationContext.getProcessDefinitionKey());
        }
        if(integrationContext.getBusinessKey() != null) {
            setBusinessKey(integrationContext.getBusinessKey());
        }

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorClassName = errorClassName;
        this.stackTraceElements = stackTraceElements;
    }

    @Override
    public IntegrationEvent.IntegrationEvents getEventType() {
        return IntegrationEvent.IntegrationEvents.INTEGRATION_FAILED;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getErrorClassName() {
        return errorClassName;
    }

    @Override
    public List<StackTraceElement> getStackTraceElements() {
        return stackTraceElements;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(errorClassName, errorCode, errorMessage, stackTraceElements);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CloudIntegrationFailedEventImpl other = (CloudIntegrationFailedEventImpl) obj;
        return Objects.equals(errorClassName, other.errorClassName) &&
               Objects.equals(errorCode, other.errorCode) &&
               Objects.equals(errorMessage, other.errorMessage) &&
               Objects.equals(stackTraceElements, other.stackTraceElements);
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        StringBuilder builder = new StringBuilder();
        builder.append("CloudIntegrationFailedEventImpl [errorCode=")
               .append(errorCode)
               .append(", errorMessage=")
               .append(errorMessage)
               .append(", errorClassName=")
               .append(errorClassName)
               .append(", stackTraceElements=")
               .append(stackTraceElements != null ? stackTraceElements.subList(0,
                                                                               Math.min(stackTraceElements.size(),
                                                                                        maxLen)) : null)
               .append(", toString()=")
               .append(super.toString())
               .append("]");
        return builder.toString();
    }
}
