/*
 * Copyright 2017 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.starter.tests.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MQServiceTaskIT {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    public void shouldContinueExecution() throws Exception {
        //given
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", "John");
        variables.put("lastName", "Smith");
        variables.put("age", 19);

        //when
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("noDelegate", variables);
        assertThat(procInst).isNotNull();


        //then
        // the execution should arrive in the human tasks which follows the service task
        Thread.sleep(500);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInst.getProcessInstanceId()).list();
        assertThat(tasks).isNotNull();
        assertThat(tasks).extracting(Task::getName).containsExactly("Schedule meeting after service");

        // the variable "age" should be updated based on ServiceTaskConsumerHandler.receive
        Map<String, Object> updatedVariables = runtimeService.getVariables(procInst.getId());
        assertThat(updatedVariables)
                .containsEntry("firstName", "John")
                .containsEntry("lastName", "Smith")
                .containsEntry("age", 20);

    }
}
