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

import org.activiti.cloud.services.query.model.ProcessInstanceEntity;
import org.activiti.cloud.services.query.model.TaskEntity;
import org.hibernate.jpa.QueryHints;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;

public class EntityManagerFinder {

    private static final String VARIABLES = "variables";
    private static final String TASK_CANDIDATE_USERS = "taskCandidateUsers";
    private static final String TASK_CANDIDATE_GROUPS = "taskCandidateGroups";
    private final EntityManager entityManager;

    public EntityManagerFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<TaskEntity> findTaskWithVariables(String taskId) {
        EntityGraph<TaskEntity> entityGraph = entityManager.createEntityGraph(TaskEntity.class);

        entityGraph.addAttributeNodes(VARIABLES);

        return Optional.ofNullable(entityManager.find(TaskEntity.class,
                                                      taskId,
                                                      Map.of(QueryHints.HINT_LOADGRAPH, entityGraph)));
    }

    public Optional<TaskEntity> findTaskWithCandidateUsers(String taskId) {
        EntityGraph<TaskEntity> entityGraph = entityManager.createEntityGraph(TaskEntity.class);

        entityGraph.addAttributeNodes(TASK_CANDIDATE_USERS);

        return Optional.ofNullable(entityManager.find(TaskEntity.class,
                                                      taskId,
                                                      Map.of(QueryHints.HINT_LOADGRAPH, entityGraph)));
    }

    public Optional<TaskEntity> findTaskWithCandidateGroups(String taskId) {
        EntityGraph<TaskEntity> entityGraph = entityManager.createEntityGraph(TaskEntity.class);

        entityGraph.addAttributeNodes(TASK_CANDIDATE_GROUPS);

        return Optional.ofNullable(entityManager.find(TaskEntity.class,
                                                      taskId,
                                                      Map.of(QueryHints.HINT_LOADGRAPH, entityGraph)));
    }

    public Optional<ProcessInstanceEntity> findProcessInstanceWithVariables(String processInstanceId) {
        EntityGraph<ProcessInstanceEntity> entityGraph = entityManager.createEntityGraph(ProcessInstanceEntity.class);

        entityGraph.addAttributeNodes(VARIABLES);

        return Optional.ofNullable(entityManager.find(ProcessInstanceEntity.class,
                                                      processInstanceId,
                                                      Map.of(QueryHints.HINT_LOADGRAPH, entityGraph)));
    }
}
