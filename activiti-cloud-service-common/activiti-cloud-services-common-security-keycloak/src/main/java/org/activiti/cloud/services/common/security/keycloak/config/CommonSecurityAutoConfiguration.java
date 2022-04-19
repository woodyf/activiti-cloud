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
package org.activiti.cloud.services.common.security.keycloak.config;

import java.util.List;
import org.activiti.api.runtime.shared.security.PrincipalGroupsProvider;
import org.activiti.api.runtime.shared.security.PrincipalIdentityProvider;
import org.activiti.api.runtime.shared.security.PrincipalRolesProvider;
import org.activiti.api.runtime.shared.security.SecurityContextPrincipalProvider;
import org.activiti.api.runtime.shared.security.SecurityContextTokenProvider;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.cloud.security.authorization.AuthorizationConfigurer;
import org.activiti.cloud.security.authorization.EnableAuthorizationConfiguration;
import org.activiti.cloud.services.common.security.keycloak.KeycloakAccessTokenPrincipalGroupsProvider;
import org.activiti.cloud.services.common.security.keycloak.KeycloakAccessTokenPrincipalRolesProvider;
import org.activiti.cloud.services.common.security.keycloak.JwtAccessTokenProvider;
import org.activiti.cloud.services.common.security.keycloak.KeycloakAccessTokenValidator;
import org.activiti.cloud.services.common.security.keycloak.KeycloakPrincipalGroupsProviderChain;
import org.activiti.cloud.services.common.security.keycloak.KeycloakPrincipalIdentityProvider;
import org.activiti.cloud.services.common.security.keycloak.KeycloakPrincipalRolesProviderChain;
import org.activiti.cloud.services.common.security.keycloak.KeycloakSecurityContextPrincipalProvider;
import org.activiti.cloud.services.common.security.keycloak.KeycloakSecurityContextTokenProvider;
import org.activiti.cloud.services.common.security.keycloak.KeycloakSecurityManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableAuthorizationConfiguration
@ConditionalOnWebApplication
@ConditionalOnMissingBean(value = {SessionAuthenticationStrategy.class, SessionAuthenticationStrategy.class})
@PropertySource("classpath:keycloak-configuration.properties" )
public class CommonSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthorizationConfigurer authorizationConfigurer;
    @Value("${keycloak.resource}" )
    private String resource;
    @Value("${keycloak.use-resource-role-mappings:false}" )
    private boolean useResourceRoleMapping;

    @Autowired
    public CommonSecurityAutoConfiguration(AuthorizationConfigurer authorizationConfigurer) {
        this.authorizationConfigurer = authorizationConfigurer;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContextPrincipalProvider authenticatedPrincipalProvider() {
        return new KeycloakSecurityContextPrincipalProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAccessTokenProvider jwtAccessTokenProvider() {
        return new JwtAccessTokenProvider(resource, useResourceRoleMapping);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeycloakAccessTokenValidator keycloakAccessTokenValidator() {
        return new KeycloakAccessTokenValidator() {
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public PrincipalIdentityProvider principalIdentityProvider(JwtAccessTokenProvider keycloakAccessTokenProvider,
                                                               KeycloakAccessTokenValidator keycloakAccessTokenValidator) {
        return new KeycloakPrincipalIdentityProvider(keycloakAccessTokenProvider,
                                                     keycloakAccessTokenValidator);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public KeycloakAccessTokenPrincipalGroupsProvider keycloakAccessTokenPrincipalGroupsProvider(JwtAccessTokenProvider keycloakAccessTokenProvider,
                                                                                                 KeycloakAccessTokenValidator keycloakAccessTokenValidator) {
        return new KeycloakAccessTokenPrincipalGroupsProvider(keycloakAccessTokenProvider,
                                                              keycloakAccessTokenValidator);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public KeycloakAccessTokenPrincipalRolesProvider keycloakAccessTokenPrincipalRolesProvider(JwtAccessTokenProvider keycloakAccessTokenProvider,
                                                                                               KeycloakAccessTokenValidator keycloakAccessTokenValidator) {
        return new KeycloakAccessTokenPrincipalRolesProvider(keycloakAccessTokenProvider,
                                                             keycloakAccessTokenValidator);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeycloakPrincipalGroupsProviderChain principalGroupsProviderChain(List<PrincipalGroupsProvider> principalGroupsProviders) {
        return new KeycloakPrincipalGroupsProviderChain(principalGroupsProviders);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeycloakPrincipalRolesProviderChain principalRolesProviderChain(List<PrincipalRolesProvider> principalRolesProviders) {
        return new KeycloakPrincipalRolesProviderChain(principalRolesProviders);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityManager securityManager(SecurityContextPrincipalProvider authenticatedPrincipalProvider,
                                           PrincipalIdentityProvider principalIdentityProvider,
                                           KeycloakPrincipalGroupsProviderChain principalGroupsProvider,
                                           KeycloakPrincipalRolesProviderChain principalRolesProviderChain) {
        return new KeycloakSecurityManagerImpl(authenticatedPrincipalProvider,
                                               principalIdentityProvider,
                                               principalGroupsProvider,
                                               principalRolesProviderChain);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContextTokenProvider securityContextTokenProvider() {
        return new KeycloakSecurityContextTokenProvider();
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @ConditionalOnMissingBean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authorizationConfigurer.configure(http);
        http
            .authorizeRequests().anyRequest().permitAll()
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthorityConverter(jwtAccessTokenProvider()));
        return jwtAuthenticationConverter;
    }

}
