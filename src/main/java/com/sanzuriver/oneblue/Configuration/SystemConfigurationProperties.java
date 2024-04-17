package com.sanzuriver.oneblue.Configuration;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@ConfigurationProperties(prefix = "oneblue")
@Data
@RefreshScope
public class SystemConfigurationProperties {
    private String username;
    private String password;
    private String MusicFolderPath;
    private WebDav webDav;
    @Data
    @RefreshScope
    public static class WebDav{
        private String url;
        private String username;
        private String password;
    }
    @Bean
    @RefreshScope
    public HttpClient httpClient() {
        return HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    }

    @Bean
    @RefreshScope
    public HttpClientContext httpClientContext() {
        URI uri = URI.create(webDav.getUrl());
        HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort());

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials upc = new UsernamePasswordCredentials(webDav.getUsername(), webDav.getPassword());
        provider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), upc);

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(provider);
        context.setAuthCache(authCache);

        return context;
    }
}
