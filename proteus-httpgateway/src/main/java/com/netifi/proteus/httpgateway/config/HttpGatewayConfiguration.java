/**
 * Copyright 2018 Netifi Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netifi.proteus.httpgateway.config;

import com.netifi.proteus.httpgateway.handler.HttpGatewayHandler;
import com.netifi.proteus.httpgateway.registry.FileSystemServiceRegistry;
import com.netifi.proteus.httpgateway.registry.ServiceRegistry;
import io.netifi.proteus.Proteus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class HttpGatewayConfiguration {

    @Bean
    public RouterFunction<ServerResponse> groupRoute(HttpGatewayHandler handler) {
        return route(POST("/{group}/{service}/{method}")
                .and(accept(MediaType.APPLICATION_JSON)), handler::handleGroup);
    }

    @Bean
    public RouterFunction<ServerResponse> destinationRoute(HttpGatewayHandler handler) {
        return route(POST("/{group}/{destination}/{service}/{method}")
                .and(accept(MediaType.APPLICATION_JSON)), handler::handleDestination);
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        return new FileSystemServiceRegistry();
    }

    @Bean
    public Proteus proteus(HttpGatewaySettings httpGatewaySettings, ProteusSettings proteusSettings) {
        return Proteus.builder()
                .group(httpGatewaySettings.getGroup())
                .host(proteusSettings.getBrokerHostname())
                .port(proteusSettings.getBrokerPort())
                .accessKey(proteusSettings.getAccessKey())
                .accessToken(proteusSettings.getAccessToken())
                .build();
    }
}
