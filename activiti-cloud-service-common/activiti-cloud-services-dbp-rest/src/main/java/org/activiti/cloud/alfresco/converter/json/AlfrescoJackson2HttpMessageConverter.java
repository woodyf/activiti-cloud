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
package org.activiti.cloud.alfresco.converter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.cloud.alfresco.rest.model.EntryResponseContent;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

public class AlfrescoJackson2HttpMessageConverter<T> extends MappingJackson2HttpMessageConverter {

    private final PagedModelConverter pagedCollectionModelConverter;
    private final ObjectMapper objectMapper;

    public AlfrescoJackson2HttpMessageConverter(PagedModelConverter pagedCollectionModelConverter, ObjectMapper objectMapper) {
        super(objectMapper);
        this.pagedCollectionModelConverter = pagedCollectionModelConverter;
        this.objectMapper = objectMapper;
        setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Override
    protected void writeInternal(Object object,
                                 @Nullable Type type,
                                 HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof MappingJacksonValue) {
            MappingJacksonValue mappingJacksonValueObject = ((MappingJacksonValue) object);
            mappingJacksonValueObject.setValue(transformObject(mappingJacksonValueObject.getValue()));
            defaultWriteInternal(mappingJacksonValueObject, type, outputMessage);
        } else {
            defaultWriteInternal(transformObject(object), type, outputMessage);
        }
    }

    private Object transformObject(Object object) {
        if (object instanceof PagedModel) {
            return pagedCollectionModelConverter.pagedCollectionModelToListResponseContent((PagedModel<EntityModel<T>>) object);
        } else if (object instanceof CollectionModel) {
            return pagedCollectionModelConverter.resourcesToListResponseContent((CollectionModel<EntityModel<T>>) object);
        } else if (object instanceof EntityModel) {
            return new EntryResponseContent<>(((EntityModel<T>) object).getContent());
        }
        return object;
    }

    protected void defaultWriteInternal(Object object,
                                        @Nullable Type type,
                                        HttpOutputMessage outputMessage) throws IOException {
        super.writeInternal(object,
            type,
            outputMessage);
    }

    @Override
    public boolean canWrite(Type type,
                            Class<?> clazz,
                            MediaType mediaType) {
        return !String.class.equals(type) && super.canWrite(type,
            clazz,
            mediaType);
    }

}
