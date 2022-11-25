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

package io.dingodb.net.netty.api;

import io.dingodb.common.annotation.ApiDeclaration;
import io.dingodb.common.auth.Certificate;
import io.dingodb.common.codec.ProtostuffCodec;
import io.dingodb.net.Message;
import io.dingodb.net.error.ApiTerminateException;
import io.dingodb.net.netty.Channel;
import io.dingodb.net.netty.Constant;
import io.dingodb.net.service.AuthService;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static io.dingodb.net.Message.API_ERROR;

public interface AuthProxyApi {

    AuthProxyApi INSTANCE = new AuthProxyApi() {};

    Iterable<AuthService.Provider> serviceProviders = ServiceLoader.load(AuthService.Provider.class);

    /**
     * Authentication, throw exception if failed.
     * @param authentication certificate
     */
    @ApiDeclaration(name = Constant.AUTH)
    default Map<String, Object[]> auth(Channel channel, Map<String, ?> authentication) {
        AuthService service = null;
        try {
            Map<String, Object[]> result = new HashMap<>();
            boolean authed = false;
            for (AuthService.Provider authServiceProvider : serviceProviders) {
                service = authServiceProvider.get();
                Certificate certificate = (Certificate) service.auth(authentication.get(service.tag()));
                if (certificate.getCode() == 100) {
                    authed = true;
                }
                Object[] ret = new Object[2];
                ret[0] = certificate;
                result.put(service.tag(), ret);
            }
            if (!authed) {
                throw new Exception("auth failed , authentication:" + authentication.toString());
            }
            return result;
        } catch (Exception e) {
            channel.send(new Message(API_ERROR, ProtostuffCodec.write(e)), true);
            throw new ApiTerminateException(
                "Auth failed from [%s], message: %s",
                channel.remoteLocation().url(), e.getMessage()
            );
        }
    }

}
