/*
 * Copyright 2021 DataCanvas
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

package io.dingodb.verify.auth;

import com.google.auto.service.AutoService;
import io.dingodb.common.auth.Authentication;
import io.dingodb.common.auth.Certificate;
import io.dingodb.common.environment.ExecutionEnvironment;
import io.dingodb.net.service.AuthService;
import io.dingodb.verify.token.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

@Slf4j
public class TokenAuthService implements AuthService<Authentication>  {

    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    public TokenAuth tokenAuth;

    Iterable<TokenAuth.Provider> serviceProviders = ServiceLoader.load(TokenAuth.Provider.class);

    public TokenAuthService() {
        try {
            for (TokenAuth.Provider tokenAuthProvider : serviceProviders) {
                TokenAuth tokenAuth = tokenAuthProvider.get();
                log.info("tokenAuth:" + tokenAuth + "token toke:" + tokenAuth.getRole() + ", role:" + env.getRole());
                if (tokenAuth.getRole() == env.getRole()) {
                    this.tokenAuth = tokenAuth;
                }
            }
        } catch (NoSuchElementException e) {
            this.tokenAuth = null;
        }
    }

    private static final AuthService INSTANCE = new TokenAuthService();

    @AutoService(AuthService.Provider.class)
    public static class TokenAuthServiceProvider implements AuthService.Provider {

        @Override
        public <C> AuthService<C> get() {
            return INSTANCE;
        }
    }

    public String getInnerAuthToken() {
        String token = TokenManager.INSTANCE.createInnerToken();
        return token;
    }

    private Map<String, Object> verifyToken(String token) {
        Map<String, Object> claims = TokenManager.INSTANCE.certificateToken(token);
        return claims;
    }

    @Override
    public String tag() {
        return "token";
    }

    @Override
    public Authentication createAuthentication() {
        String token = null;
        if (tokenAuth == null) {
            token =  getInnerAuthToken();
        } else {
            token = tokenAuth.getAuthToken();
        }
        if (StringUtils.isNotBlank(token)) {
            Authentication authentication = Authentication.builder().token(token).role(env.getRole()).build();
            return authentication;
        } else {
            return null;
        }
    }

    @Override
    public Object auth(Authentication authentication) throws Exception {
        String token = authentication.getToken();
        Map<String, Object> clientInfo = verifyToken(token);
        if (clientInfo == null) {
            throw new Exception("auth token error");
        }
        String host = (String) clientInfo.get("host");
        String user = (String) clientInfo.get("user");
        if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(host) && tokenAuth != null) {
            authentication.setUsername(user);
            authentication.setHost(host);
            tokenAuth.cachePrivileges(authentication);
        }
        log.info("token auth is null:" + (tokenAuth == null) + ", authentication:" + authentication);
        return Certificate.builder().code(100).build();
    }
}
