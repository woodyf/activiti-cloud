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
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.activiti.cloud.services.rest.controllers;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.CreateProcessInstancePayload;
import org.activiti.api.process.model.payloads.ReceiveMessagePayload;
import org.activiti.api.process.model.payloads.SignalPayload;
import org.activiti.api.process.model.payloads.StartMessagePayload;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.model.payloads.UpdateProcessPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedModelAssembler;
import org.activiti.cloud.api.process.model.CloudProcessInstance;
import org.activiti.cloud.services.core.ProcessDiagramGeneratorWrapper;
import org.activiti.cloud.services.core.ProcessVariablesPayloadConverter;
import org.activiti.cloud.services.core.pageable.SpringPageConverter;
import org.activiti.cloud.services.rest.api.ProcessInstanceController;
import org.activiti.cloud.services.rest.assemblers.ProcessInstanceRepresentationModelAssembler;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class ProcessInstanceControllerImpl implements ProcessInstanceController {

    private final RepositoryService repositoryService;

    private final ProcessDiagramGeneratorWrapper processDiagramGenerator;

    private final ProcessInstanceRepresentationModelAssembler representationModelAssembler;

    private final AlfrescoPagedModelAssembler<ProcessInstance> pagedCollectionModelAssembler;

    private final ProcessRuntime processRuntime;

    private final SpringPageConverter pageConverter;

    private final ProcessVariablesPayloadConverter variablesPayloadConverter;

    @Autowired
    public ProcessInstanceControllerImpl(RepositoryService repositoryService,
                                         ProcessDiagramGeneratorWrapper processDiagramGenerator,
                                         ProcessInstanceRepresentationModelAssembler representationModelAssembler,
                                         AlfrescoPagedModelAssembler<ProcessInstance> pagedCollectionModelAssembler,
                                         ProcessRuntime processRuntime,
                                         SpringPageConverter pageConverter,
                                         ProcessVariablesPayloadConverter variablesPayloadConverter) {
        this.repositoryService = repositoryService;
        this.processDiagramGenerator = processDiagramGenerator;
        this.representationModelAssembler = representationModelAssembler;
        this.pagedCollectionModelAssembler = pagedCollectionModelAssembler;
        this.processRuntime = processRuntime;
        this.pageConverter = pageConverter;
        this.variablesPayloadConverter = variablesPayloadConverter;
    }

    @Override
    public PagedModel<EntityModel<CloudProcessInstance>> getProcessInstances(Pageable pageable) {
        Page<ProcessInstance> processInstancePage = processRuntime.processInstances(pageConverter.toAPIPageable(pageable));
        return pagedCollectionModelAssembler.toModel(pageable,
                pageConverter.toSpringPage(pageable, processInstancePage),
                representationModelAssembler);
    }

    @Override
    public EntityModel<CloudProcessInstance> startProcess(@RequestBody StartProcessPayload startProcessPayload) {
        startProcessPayload = variablesPayloadConverter.convert(startProcessPayload);

        return representationModelAssembler.toModel(processRuntime.start(startProcessPayload));
    }

    @Override
    public EntityModel<CloudProcessInstance> startCreatedProcess(@PathVariable String processInstanceId,
                                                                 @RequestBody(required = false) StartProcessPayload startProcessPayload) {
        if (startProcessPayload == null) {
            startProcessPayload = ProcessPayloadBuilder.start().build();
        }
        startProcessPayload = variablesPayloadConverter.convert(startProcessPayload);
        return representationModelAssembler.toModel(processRuntime.startCreatedProcess(processInstanceId, startProcessPayload));
    }

    @Override
    public EntityModel<CloudProcessInstance> createProcessInstance(@RequestBody CreateProcessInstancePayload createProcessInstancePayload) {
        return representationModelAssembler.toModel(processRuntime.create(createProcessInstancePayload));
    }

    @Override
    public EntityModel<CloudProcessInstance> getProcessInstanceById(@PathVariable String processInstanceId) {
        return representationModelAssembler.toModel(processRuntime.processInstance(processInstanceId));
    }

    @Override
    public String getProcessDiagram(@PathVariable String processInstanceId) {
        ProcessInstance processInstance = processRuntime.processInstance(processInstanceId);

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        return new String(processDiagramGenerator.generateDiagram(bpmnModel,
            processRuntime.processInstanceMeta(processInstance.getId())
                .getActiveActivitiesIds(),
            emptyList(),
            emptyList()),
            StandardCharsets.UTF_8);
    }

    @Override
    public ResponseEntity<Void> sendSignal(@RequestBody SignalPayload cmd) {
        processRuntime.signal(cmd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public EntityModel<CloudProcessInstance> suspend(@PathVariable String processInstanceId) {
        return representationModelAssembler.toModel(processRuntime.suspend(ProcessPayloadBuilder.suspend(processInstanceId)));

    }

    @Override
    public EntityModel<CloudProcessInstance> resume(@PathVariable String processInstanceId) {
        return representationModelAssembler.toModel(processRuntime.resume(ProcessPayloadBuilder.resume(processInstanceId)));
    }

    @Override
    public EntityModel<CloudProcessInstance> deleteProcessInstance(@PathVariable String processInstanceId) {
        return representationModelAssembler.toModel(processRuntime.delete(ProcessPayloadBuilder.delete(processInstanceId)));
    }

    @Override
    public EntityModel<CloudProcessInstance> updateProcess(@PathVariable String processInstanceId,
                                                        @RequestBody UpdateProcessPayload payload) {
        if (payload != null) {
            payload.setProcessInstanceId(processInstanceId);

        }

        return representationModelAssembler.toModel(processRuntime.update(payload));
    }

    @Override
    public PagedModel<EntityModel<CloudProcessInstance>> subprocesses(@PathVariable String processInstanceId,
                                                                       Pageable pageable) {
        Page<ProcessInstance> processInstancePage = processRuntime.processInstances(pageConverter.toAPIPageable(pageable),
                ProcessPayloadBuilder.subprocesses(processInstanceId));

        return pagedCollectionModelAssembler.toModel(pageable,
                pageConverter.toSpringPage(pageable, processInstancePage),
                representationModelAssembler);
    }

    @Override
    public EntityModel<CloudProcessInstance> sendStartMessage(@RequestBody StartMessagePayload startMessagePayload) {
        startMessagePayload = variablesPayloadConverter.convert(startMessagePayload);

        ProcessInstance processInstance = processRuntime.start(startMessagePayload);

        return representationModelAssembler.toModel(processInstance);
    }

    @Override
    public ResponseEntity<Void> receive(@RequestBody ReceiveMessagePayload receiveMessagePayload) {
        processRuntime.receive(receiveMessagePayload);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
