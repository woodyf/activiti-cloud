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
package org.activiti.cloud.services.query.rest.config;

import org.activiti.cloud.services.query.model.*;
import org.activiti.cloud.services.query.model.TaskCandidateGroupEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class QueryRepositoryConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {

        // Expose only repositories annotated with @RepositoryRestResource
        config.setRepositoryDetectionStrategy(RepositoryDetectionStrategies.ANNOTATED);

        //by default the ids are not exposed the the REST API
        config.exposeIdsFor(ProcessInstanceEntity.class)
                .exposeIdsFor(TaskEntity.class)
                .exposeIdsFor(ProcessVariableEntity.class)
                .exposeIdsFor(ProcessDefinitionEntity.class)
                .exposeIdsFor(ProcessModelEntity.class)
                .exposeIdsFor(BPMNSequenceFlowEntity.class)
                .exposeIdsFor(BPMNActivityEntity.class)
                .exposeIdsFor(TaskCandidateGroupEntity.class)
                .exposeIdsFor(TaskCandidateUserEntity.class)
                .exposeIdsFor(ServiceTaskEntity.class)
                .exposeIdsFor(ApplicationEntity.class);
    }

}
