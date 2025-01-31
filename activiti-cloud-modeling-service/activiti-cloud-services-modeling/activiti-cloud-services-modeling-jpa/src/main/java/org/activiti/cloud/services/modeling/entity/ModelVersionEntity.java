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
package org.activiti.cloud.services.modeling.entity;

import java.util.Map;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.activiti.cloud.modeling.api.process.Extensions;
import org.activiti.cloud.services.modeling.jpa.audit.AuditableEntity;
import org.activiti.cloud.services.modeling.jpa.version.VersionEntity;
import org.activiti.cloud.services.modeling.jpa.version.VersionIdentifier;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Model version entity
 */
@Entity(name = "ModelVersion")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class ModelVersionEntity extends AuditableEntity<String> implements VersionEntity<ModelEntity> {

    @EmbeddedId
    @JsonIgnore
    private VersionIdentifier versionIdentifier;

    @JsonIgnore
    @ManyToOne
    @MapsId("versionedEntityId")
    private ModelEntity versionedEntity;

    private String contentType;

    @Lob
    @Column
    private byte[] content;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = ExtensionsJsonConverter.class)
    private Map<String,Object> extensions;

    public ModelVersionEntity() {

    }

    public ModelVersionEntity(ModelVersionEntity version) {
        setContent(version.getContent());
        setContentType(version.getContentType());
        setExtensions(version.getExtensions());
    }

    public VersionIdentifier getVersionIdentifier() {
        return versionIdentifier;
    }

    @Override
    public void setVersionIdentifier(VersionIdentifier versionIdentifier) {
        this.versionIdentifier = versionIdentifier;
    }

    public ModelEntity getVersionedEntity() {
        return versionedEntity;
    }

    @Override
    public void setVersionedEntity(ModelEntity versionedEntity) {
        this.versionedEntity = versionedEntity;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Map<String,Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String,Object> extensions) {
        this.extensions = extensions;
    }

    @Transient
    @Override
    public String getVersion() {
        return Optional.ofNullable(versionIdentifier)
                .map(VersionIdentifier::getVersion)
                .orElse(null);
    }
}
