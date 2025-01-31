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
package org.activiti.cloud.services.query.events.handlers;

import org.activiti.api.task.model.events.TaskCandidateUserEvent;
import org.activiti.cloud.api.model.shared.events.CloudRuntimeEvent;
import org.activiti.cloud.api.task.model.events.CloudTaskCandidateUserAddedEvent;
import org.activiti.cloud.services.query.model.QueryException;
import org.activiti.cloud.services.query.model.TaskCandidateUserEntity;

import javax.persistence.EntityManager;

public class TaskCandidateUserAddedEventHandler implements QueryEventHandler {

    private final EntityManager entityManager;

    public TaskCandidateUserAddedEventHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void handle(CloudRuntimeEvent<?, ?> event) {
        CloudTaskCandidateUserAddedEvent taskCandidateUserAddedEvent = (CloudTaskCandidateUserAddedEvent) event;
        org.activiti.api.task.model.TaskCandidateUser taskCandidateUser = taskCandidateUserAddedEvent.getEntity();

        try {
            TaskCandidateUserEntity entity = new TaskCandidateUserEntity(taskCandidateUser.getTaskId(),
                                                                         taskCandidateUser.getUserId());
            entityManager.persist(entity);
        } catch (Exception cause) {
            throw new QueryException("Error handling TaskCandidateUserAddedEvent[" + event + "]",
                                     cause);
        }
    }

    @Override
    public String getHandledEvent() {
        return TaskCandidateUserEvent.TaskCandidateUserEvents.TASK_CANDIDATE_USER_ADDED.name();
    }
}
