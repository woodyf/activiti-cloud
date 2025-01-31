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
package org.activiti.cloud.services.messages.core.advice;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.integration.handler.advice.AbstractHandleMessageAdvice;
import org.springframework.messaging.Message;

public abstract class AbstractMessageConnectorHandlerAdvice extends AbstractHandleMessageAdvice
                                                            implements MessageConnectorHandlerAdvice {
    @Override
    protected Object doInvoke(MethodInvocation invocation,
                              Message<?> message) throws Throwable {

        if (canHandle(message)) {
            return doHandle(message);
        }

        return invocation.proceed();
    }

    public abstract boolean canHandle(Message<?> message);

    public abstract <T> T doHandle(Message<?> message);

    @Override
    public String getComponentType() {
        return this.getClass().getSimpleName();
    }

}
