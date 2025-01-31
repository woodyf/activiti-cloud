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
package org.activiti.cloud.services.identity.keycloak.mapper;

import java.util.List;
import org.activiti.cloud.identity.model.Group;
import org.activiti.cloud.services.identity.keycloak.client.KeycloakClient;
import org.activiti.cloud.services.identity.keycloak.model.KeycloakGroup;
import org.activiti.cloud.services.identity.keycloak.model.KeycloakRoleMapping;

public class KeycloakGroupToGroup {

  private final KeycloakClient keycloakClient;
  private final KeycloakRoleMappingToRole keycloakRoleMappingToRole;

  public KeycloakGroupToGroup(KeycloakClient keycloakClient, KeycloakRoleMappingToRole keycloakRoleMappingToRole) {
    this.keycloakClient = keycloakClient;
    this.keycloakRoleMappingToRole = keycloakRoleMappingToRole;
  }

  public Group toGroup(KeycloakGroup kGroup) {
    Group group = new Group();
    group.setId(kGroup.getId());
    group.setName(kGroup.getName());
    List<KeycloakRoleMapping> userRoleMapping = keycloakClient.getGroupRoleMapping(group.getId());
    group.setRoles(keycloakRoleMappingToRole.toRoles(userRoleMapping));
    return group;
  }

}
