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
package org.activiti.cloud.services.identity.keycloak;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.api.runtime.shared.security.PrincipalGroupsProvider;
import org.activiti.api.runtime.shared.security.PrincipalRolesProvider;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

public class HxpIdpClientPrincipalDetailsProvider implements PrincipalGroupsProvider, PrincipalRolesProvider {


    public HxpIdpClientPrincipalDetailsProvider() {
    }


    @Override
    public List<String> getGroups(Principal principal) {
        return List.of("hr");
    }

    @Override
    public List<String> getRoles(Principal principal) {
        return List.of("ACTIVIT_USER", "ACTIVITI_ADMIN", "ACTIVITI_DEVOPS");
    }

    protected UserResource userResource(Principal principal) {
        return null;
    }

    protected String subjectId(Principal principal) {
        return Optional.of(principal)
                       .map(Principal::getName)
                       .orElseThrow(this::securityException);
    }

    protected SecurityException securityException() {
        return new SecurityException("Invalid Keycloak principal subject id");
    }

}
