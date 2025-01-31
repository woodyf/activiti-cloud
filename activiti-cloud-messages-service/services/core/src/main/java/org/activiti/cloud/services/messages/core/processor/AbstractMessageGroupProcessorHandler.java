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
package org.activiti.cloud.services.messages.core.processor;

import org.springframework.integration.store.MessageGroup;

public abstract class AbstractMessageGroupProcessorHandler implements MessageGroupProcessorHandler {

    @Override
    public Object handle(MessageGroup group) {

        if (canProcess(group)) {
            return process(group);
        }

        return null;
    }

    protected abstract Object process(MessageGroup group);

    protected abstract boolean canProcess(MessageGroup group);
}
