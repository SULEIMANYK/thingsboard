/**
 * Copyright © 2016-2023 The Thingsboard Authors
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
package org.thingsboard.monitoring.client;

import lombok.RequiredArgsConstructor;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.stereotype.Component;
import org.thingsboard.monitoring.config.WsConfig;
import org.thingsboard.monitoring.data.Latencies;
import org.thingsboard.monitoring.service.MonitoringReporter;
import org.thingsboard.monitoring.util.TbStopWatch;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class WsClientFactory {

    private final WsConfig wsConfig;
    private final MonitoringReporter monitoringReporter;
    private final TbStopWatch stopWatch;

    public WsClient createClient(String accessToken) throws Exception {
        URI uri = new URI(wsConfig.getBaseUrl() + "/api/ws/plugins/telemetry?token=" + accessToken);
        stopWatch.start();
        WsClient wsClient = new WsClient(uri, wsConfig.getRequestTimeoutMs());
        if (wsConfig.getBaseUrl().startsWith("wss")) {
            SSLContextBuilder builder = SSLContexts.custom();
            builder.loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true);
            wsClient.setSocketFactory(builder.build().getSocketFactory());
        }
        boolean connected = wsClient.connectBlocking(wsConfig.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);
        if (!connected) {
            throw new IllegalStateException("Failed to establish WS session");
        }
        monitoringReporter.reportLatency(Latencies.WS_CONNECT, stopWatch.getTime());
        return wsClient;
    }

}
