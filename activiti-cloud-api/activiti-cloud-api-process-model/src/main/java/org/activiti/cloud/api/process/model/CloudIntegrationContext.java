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
package org.activiti.cloud.api.process.model;

import java.util.Date;
import java.util.List;

import org.activiti.api.process.model.IntegrationContext;
import org.activiti.cloud.api.model.shared.CloudRuntimeEntity;

public interface CloudIntegrationContext extends IntegrationContext, CloudRuntimeEntity {

    public enum IntegrationContextStatus {
        INTEGRATION_REQUESTED, INTEGRATION_RESULT_RECEIVED, INTEGRATION_ERROR_RECEIVED
    }

    IntegrationContextStatus getStatus();

    Date getRequestDate();

    Date getResultDate();

    Date getErrorDate();

    String getErrorCode();

    String getErrorMessage();

    String getErrorClassName();

    List<StackTraceElement> getStackTraceElements();

}
