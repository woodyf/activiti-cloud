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

import org.activiti.api.task.model.events.TaskRuntimeEvent;
import org.activiti.cloud.api.task.model.events.CloudTaskCompletedEvent;
import org.activiti.cloud.api.task.model.events.CloudTaskCreatedEvent;
import org.activiti.cloud.api.task.model.impl.events.CloudTaskCompletedEventImpl;
import org.activiti.cloud.api.task.model.impl.events.CloudTaskCreatedEventImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueryEventHandlerContextTest {

    private QueryEventHandlerContext context;

    @Mock
    private QueryEventHandler handler;

    @BeforeEach
    public void setUp() {
        doReturn(TaskRuntimeEvent.TaskEvents.TASK_CREATED.name()).when(handler).getHandledEvent();
        context = new QueryEventHandlerContext(Collections.singleton(handler));
    }

    @Test
    public void handleShouldSelectHandlerBasedOnEventType() {
        //given
        CloudTaskCreatedEvent event = new CloudTaskCreatedEventImpl();

        //when
        context.handle(event);

        //then
        verify(handler).handle(event);
    }

    @Test
    public void handleShouldDoNothingWhenNoHandlerIsFoundForTheGivenEvent() {
        //given
        CloudTaskCompletedEvent event = new CloudTaskCompletedEventImpl();

        //when
        context.handle(event);

        //then
        verify(handler, never()).handle(any());
    }
}
