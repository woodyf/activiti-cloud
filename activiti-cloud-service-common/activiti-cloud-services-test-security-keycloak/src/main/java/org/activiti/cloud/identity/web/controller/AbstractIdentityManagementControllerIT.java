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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@Disabled
public abstract class AbstractIdentityManagementControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void should_returnUsers_when_searchByUsername() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?search=hr"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username", is("hradmin")))
            .andExpect(jsonPath("$[1].username", is("hruser")));
    }

    @Test
    public void should_returnUsers_when_searchByGroup() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?group=hr"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].username", is("hradmin")))
            .andExpect(jsonPath("$[1].username", is("hruser")))
            .andExpect(jsonPath("$[2].username", is("johnsnow")));
    }

    @Test
    public void should_returnUsers_when_searchByEmail() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?search=hr@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is("hruser")));
    }

    @Test
    public void should_returnUsers_when_searchByLastName() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?search=snow"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is("johnsnow")));
    }

    @Test
    public void should_returnUsers_when_searchByFirstName() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?search=john"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is("johnsnow")));
    }

    @Test
    public void should_returnOnlyUsers_when_searchByUsernameAndRoleUser() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?search=johnsnow&role=ACTIVITI_USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is("johnsnow")));
    }

    @Test
    public void should_returnOnlyAdmins_when_searchByUsernameAndRoleAdmin() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?search=hr&role=ACTIVITI_ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is("hradmin")));
    }

    @Test
    public void should_returnOnlyAdmins_when_searchByRoleAdmin() throws Exception {
        mockMvc
            .perform(get("/v1/identity/users?role=ACTIVITI_ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].username", is("admin")))
            .andExpect(jsonPath("$[1].username", is("hradmin")))
            .andExpect(jsonPath("$[2].username", is("testactivitiadmin")))
            .andExpect(jsonPath("$[3].username", is("testadmin")));
    }

    @Test
    public void should_returnGroups_when_searchByName() throws Exception {
        mockMvc
            .perform(get("/v1/identity/groups?search=group"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("salesgroup")))
            .andExpect(jsonPath("$[1].name", is("testgroup")));
    }

    @Test
    public void should_returnGroups_when_searchByNameAndRole() throws Exception {
        mockMvc
            .perform(get("/v1/identity/groups?search=group&role=ACTIVITI_USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("salesgroup")));
    }

    @Test
    public void should_returnGroups_when_searchByRole() throws Exception {
        mockMvc
            .perform(get("/v1/identity/groups?role=ACTIVITI_USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("salesgroup")));
    }

}
