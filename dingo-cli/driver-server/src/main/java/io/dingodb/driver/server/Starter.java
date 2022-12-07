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

package io.dingodb.driver.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.dingodb.common.config.DingoConfiguration;
import io.dingodb.exec.Services;
import io.dingodb.net.NetService;
import io.dingodb.net.NetServiceProvider;
import io.dingodb.server.driver.DriverProxyServer;

import java.util.ServiceLoader;

public class Starter {

    @Parameter(names = "--help", help = true, order = 0)
    private boolean help;

    @Parameter(names = "--port", description = "Coordinator port.", order = 6)
    private Integer port = 8765;

    @Parameter(names = "--config", description = "Config file path.", order = 2)
    private String config;

    public static void main(String[] args) throws Exception {
        Starter starter = new Starter();
        JCommander commander = new JCommander(starter);
        commander.parse(args);
        starter.exec(commander);
    }

    public void exec(JCommander commander) throws Exception {
        if (help) {
            commander.usage();
            return;
        }
        DingoConfiguration.parse(this.config);
        NetService netService = ServiceLoader.load(NetServiceProvider.class).iterator().next().get();

        Services.initControlMsgService();
        Services.initReloadService();
        netService.listenPort(port);
        DingoConfiguration.instance().setPort(port);
        DriverProxyServer driverProxyServer = new DriverProxyServer();
    }
}
