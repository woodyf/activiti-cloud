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
package org.activiti.cloud.services.audit.jpa.events;

import org.activiti.api.process.model.Deployment;
import org.activiti.cloud.api.process.model.events.CloudApplicationDeployedEvent;
import org.activiti.cloud.services.audit.jpa.converters.json.ApplicationJpaJsonConverter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = ApplicationDeployedAuditEventEntity.APPLICATION_DEPLOYED_EVENT)
@DiscriminatorValue(value = ApplicationDeployedAuditEventEntity.APPLICATION_DEPLOYED_EVENT)
@DynamicInsert
public class ApplicationDeployedAuditEventEntity extends AuditEventEntity {

    protected static final String APPLICATION_DEPLOYED_EVENT = "ApplicationDeployedEvent";

    @Convert(converter = ApplicationJpaJsonConverter.class)
    @Column(columnDefinition = "text")
    private Deployment deployment;

    public ApplicationDeployedAuditEventEntity() {
    }

    public ApplicationDeployedAuditEventEntity(CloudApplicationDeployedEvent cloudEvent) {
        super(cloudEvent);
        setDeployment(cloudEvent.getEntity());
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ApplicationDeployedAuditEventEntity [deployment=")
                .append(deployment)
                .append(", toString()=")
                .append(super.toString())
                .append("]");
        return builder.toString();
    }
}
