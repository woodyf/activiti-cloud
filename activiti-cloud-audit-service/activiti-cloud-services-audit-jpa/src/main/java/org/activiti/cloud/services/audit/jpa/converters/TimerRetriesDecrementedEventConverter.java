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
package org.activiti.cloud.services.audit.jpa.converters;

import org.activiti.api.process.model.events.BPMNTimerEvent;
import org.activiti.cloud.api.model.shared.events.CloudRuntimeEvent;
import org.activiti.cloud.api.model.shared.impl.events.CloudRuntimeEventImpl;
import org.activiti.cloud.api.process.model.events.CloudBPMNTimerRetriesDecrementedEvent;
import org.activiti.cloud.api.process.model.impl.events.CloudBPMNTimerRetriesDecrementedEventImpl;
import org.activiti.cloud.services.audit.jpa.events.AuditEventEntity;
import org.activiti.cloud.services.audit.jpa.events.TimerRetriesDecrementedAuditEventEntity;

public class TimerRetriesDecrementedEventConverter extends BaseEventToEntityConverter {

    public TimerRetriesDecrementedEventConverter(EventContextInfoAppender eventContextInfoAppender) {
        super(eventContextInfoAppender);
    }

    @Override
    public String getSupportedEvent() {
        return BPMNTimerEvent.TimerEvents.TIMER_RETRIES_DECREMENTED.name();
    }

    @Override
    protected TimerRetriesDecrementedAuditEventEntity createEventEntity(CloudRuntimeEvent cloudRuntimeEvent) {
        return new TimerRetriesDecrementedAuditEventEntity((CloudBPMNTimerRetriesDecrementedEvent) cloudRuntimeEvent);
    }

    @Override
    protected CloudRuntimeEventImpl<?, ?> createAPIEvent(AuditEventEntity auditEventEntity) {
        TimerRetriesDecrementedAuditEventEntity timerEventEntity = (TimerRetriesDecrementedAuditEventEntity) auditEventEntity;

        return new CloudBPMNTimerRetriesDecrementedEventImpl(timerEventEntity.getEventId(),
                                                             timerEventEntity.getTimestamp(),
                                                             timerEventEntity.getTimer(),
                                                             timerEventEntity.getProcessDefinitionId(),
                                                             timerEventEntity.getProcessInstanceId());
    }
}
