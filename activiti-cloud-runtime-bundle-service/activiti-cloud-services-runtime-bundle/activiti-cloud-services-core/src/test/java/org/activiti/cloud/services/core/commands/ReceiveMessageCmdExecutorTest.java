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
package org.activiti.cloud.services.core.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.activiti.api.process.model.payloads.ReceiveMessagePayload;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReceiveMessageCmdExecutorTest {

    @InjectMocks
    private ReceiveMessageCmdExecutor subject;

    @Mock
    private ProcessAdminRuntime processAdminRuntime;

    @Test
    public void signalProcessInstancesCmdExecutorTest() {
        ReceiveMessagePayload payload = new ReceiveMessagePayload("messageName",
                                                                  "correlationKey",
                                                                  Collections.emptyMap());

        assertThat(subject.getHandledType()).isEqualTo(ReceiveMessagePayload.class.getName());

        subject.execute(payload);

        verify(processAdminRuntime).receive(payload);
    }
}
