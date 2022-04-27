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
package org.activiti.cloud.services.common.security.keycloak.jwt;

import com.nimbusds.jose.shaded.json.JSONObject;
import java.util.Collections;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class KeycloakJwtAdaptor implements JwtAdapter {

    private final Jwt jwt;
    private JwtAuthenticationToken authenticationToken;

    public KeycloakJwtAdaptor(Jwt jwt, JwtAuthenticationToken authenticationToken){
        this.jwt = jwt;
        this.authenticationToken = authenticationToken;
    }

    public Jwt getJwt() {
        return jwt;
    }

    @Override
    public List<String> getRoles() {
        JSONObject realm_access = jwt.getClaim("realm_access");
        if(realm_access == null) {
            return List.of("ACTIVITI_USER", "ACTIVITI_ADMIN", "ACTIVITI_DEVOPS", "ACTIVITI_MODELER");
        } else {
            return getRoles(realm_access);
        }
    }

    @Override
    public String getUserName() {
        if(authenticationToken != null){
            return authenticationToken.getName();
        }
        return jwt.getClaim("preferred_username");
    }

    @Override
    public List<String> getGroups() {
        return Collections.emptyList();
    }

    private List<String> getRoles(JSONObject getRolesParent) {
        return (List<String>) getRolesParent.get("roles" );
    }
}
