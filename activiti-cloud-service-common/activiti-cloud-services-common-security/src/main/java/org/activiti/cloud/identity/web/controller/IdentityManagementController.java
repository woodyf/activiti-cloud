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
package org.activiti.cloud.identity.web.controller;

import java.util.List;
import java.util.Set;
import org.activiti.cloud.identity.GroupSearchParams;
import org.activiti.cloud.identity.IdentityManagementService;
import org.activiti.cloud.identity.UserSearchParams;
import org.activiti.cloud.identity.model.Group;
import org.activiti.cloud.identity.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/identity", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityManagementController {

    private final IdentityManagementService identityManagementService;

    public IdentityManagementController(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getUsers(@RequestParam(value = "search", required = false) String search,
                                                  @RequestParam(value = "role", required = false)  Set<String> roles,
                               @RequestParam(value = "group", required = false)  Set<String> groups) {

        UserSearchParams userSearchParams = new UserSearchParams();
        userSearchParams.setSearch(search);
        userSearchParams.setGroups(groups);
        userSearchParams.setRoles(roles);

        return identityManagementService.findUsers(userSearchParams);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public List<Group> getGroups(@RequestParam(value = "search", required = false) String search,
                                                  @RequestParam(value = "role", required = false)  Set<String> roles) {

        GroupSearchParams groupSearchParams = new GroupSearchParams();
        groupSearchParams.setSearch(search);
        groupSearchParams.setRoles(roles);

        return identityManagementService.findGroups(groupSearchParams);
    }

}
