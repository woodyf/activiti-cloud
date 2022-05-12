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
package org.activiti.cloud.services.common.security.keycloak.test;

import static org.activiti.cloud.services.common.security.keycloak.test.JwtSecurityContextTokenProviderTest.TOKEN_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.activiti.cloud.services.common.security.keycloak.KeycloakAccessTokenValidator;
import org.activiti.cloud.services.common.security.keycloak.config.JwtAdapter;
import org.activiti.cloud.services.common.security.keycloak.config.KeycloakJwtAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
public class KeycloakAccessTokenValidatorTest {

    private final KeycloakAccessTokenValidator validator = new KeycloakAccessTokenValidator();

    @Mock
    private JwtAdapter jwtAdapter;

    @Test
    public void should_validate_whenTokenIsValid() {
        // given
        Jwt accessToken = createAccessToken(true, false);

        when(jwtAdapter.getJwt()).thenReturn(accessToken);

        // when
        Boolean result = validator.isValid(jwtAdapter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void should_validate_whenTokenIsValidAndNotBeforeIsSetInThePast() {
        // given
        Jwt accessToken = createAccessToken(true, false);

        when(jwtAdapter.getJwt()).thenReturn(accessToken);

        // when
        Boolean result = validator.isValid(jwtAdapter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void should_notValidate_whenTokenIsValidAndNotBeforeIsSetInTheFuture() {
        // given
        Jwt accessToken = createAccessToken(true, true);

        when(jwtAdapter.getJwt()).thenReturn(accessToken);

        // when
        Boolean result = validator.isValid(jwtAdapter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void should_notValidate_whenTokenIsNotValid() {
        // given
        Jwt accessToken = createAccessToken(false, false);

        when(jwtAdapter.getJwt()).thenReturn(accessToken);

        // when
        Boolean result = validator.isValid(jwtAdapter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void should_thrownSecurityException_whenTokenIsNull() {
        // given
        KeycloakJwtAdapter keycloakJwtAdapter = new KeycloakJwtAdapter(null);

        // when
        Throwable result = catchThrowable(() -> {
            validator.isValid(keycloakJwtAdapter);
        });

        // then
        assertThat(result).isInstanceOf(SecurityException.class);
    }

    private Jwt createAccessToken(boolean valid, boolean notBefore) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("testHeaderName", "testHeaderValue");

        Map<String, Object> claims = new HashMap<>();
        claims.put("testClaimName", "testClaimValue");

        Instant issuedAt;
        Instant expiresAt;

        if (valid) {
            issuedAt = Instant.now();
            expiresAt = Instant.now().plusSeconds(10);
        } else {
            issuedAt = Instant.now().minusSeconds(60);
            expiresAt = Instant.now().minusSeconds(50);
        }

        if (notBefore) {
            claims.put("nbf", Instant.now().plusSeconds(10));
        } else {
            claims.put("nbf", Instant.now().minusSeconds(10));
        }

        return new Jwt(TOKEN_VALUE,
            issuedAt,
            expiresAt,
            headers,
            claims);
    }
}
