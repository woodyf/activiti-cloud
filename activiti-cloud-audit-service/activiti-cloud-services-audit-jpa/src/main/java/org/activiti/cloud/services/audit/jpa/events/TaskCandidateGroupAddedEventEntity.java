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

import org.activiti.api.task.model.TaskCandidateGroup;
import org.activiti.api.task.model.impl.TaskCandidateGroupImpl;
import org.activiti.cloud.api.task.model.events.CloudTaskCandidateGroupAddedEvent;
import org.activiti.cloud.services.audit.jpa.converters.json.TaskCandidateGroupJpaJsonConverter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = TaskCandidateGroupAddedEventEntity.TASK_CANDIDATE_GROUP_ADDED_EVENT)
@DiscriminatorValue(value = TaskCandidateGroupAddedEventEntity.TASK_CANDIDATE_GROUP_ADDED_EVENT)
@DynamicInsert
public class TaskCandidateGroupAddedEventEntity extends AuditEventEntity {

    protected static final String TASK_CANDIDATE_GROUP_ADDED_EVENT = "TaskCandidateGroupAddedEvent";

    @Convert(converter = TaskCandidateGroupJpaJsonConverter.class)
    @Column(columnDefinition = "text")
    private TaskCandidateGroupImpl candidateGroup;

    public TaskCandidateGroupAddedEventEntity() {
    }

    public TaskCandidateGroupAddedEventEntity(CloudTaskCandidateGroupAddedEvent cloudEvent) {
        super(cloudEvent);
        setCandidateGroup(cloudEvent.getEntity());
    }

    public TaskCandidateGroup getCandidateGroup() {
        return candidateGroup;
    }

    public void setCandidateGroup(TaskCandidateGroup candidateGroup) {
        this.candidateGroup = new TaskCandidateGroupImpl(candidateGroup.getGroupId(),candidateGroup.getTaskId());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskCandidateGroupAddedEventEntity [candidateGroup=")
               .append(candidateGroup)
               .append(", toString()=")
               .append(super.toString())
               .append("]");
        return builder.toString();
    }

}
