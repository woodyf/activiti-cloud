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
package org.activiti.cloud.services.messages.core.release;

import static org.activiti.cloud.services.messages.core.support.Predicates.MESSAGE_SENT;
import static org.activiti.cloud.services.messages.core.support.Predicates.MESSAGE_WAITING;
import static org.activiti.cloud.services.messages.core.support.Predicates.START_MESSAGE_DEPLOYED;

import java.util.Collection;

import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

public class MessageSentReleaseHandler implements MessageGroupReleaseHandler {

    public MessageSentReleaseHandler() {
    }

    @Override
    public Boolean handle(MessageGroup group) {
        if (canRelease(group)) {
            return true;
        }

        return null;
    }

    protected boolean canRelease(MessageGroup group) {
        Collection<Message<?>> messages = group.getMessages();

        return messages.stream().anyMatch(MESSAGE_WAITING.or(START_MESSAGE_DEPLOYED))
                && messages.stream().anyMatch(MESSAGE_SENT);
    }

}
