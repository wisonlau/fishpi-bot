package io.dannio.fishpi;

import io.dannio.fishpi.properties.WebhookProperties;
import lombok.RequiredArgsConstructor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;
import org.telegram.telegrambots.updatesreceivers.DefaultExceptionMapper;
import org.telegram.telegrambots.updatesreceivers.RestApi;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * reimplements DefaultWebhook to set custom
 */
@RequiredArgsConstructor
@Component
public class CustomWebhook implements Webhook {

    private String keystoreServerFile;
    private String keystoreServerPwd;
    private String internalUrl;

    private RestApi restApi;

    private final WebhookProperties webhookProperties;

    private final ApplicationContext context;

    @PostConstruct
    public void setup() {
        this.restApi = new RestApi();
    }

    @Override
    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    @Override
    public void setKeyStore(String keyStore, String keyStorePassword) throws TelegramApiException {
        this.keystoreServerFile = keyStore;
        this.keystoreServerPwd = keyStorePassword;
        validateServerKeystoreFile(keyStore);
    }

    @Override
    public void registerWebhook(WebhookBot callback) {
        restApi.registerCallback(callback);
    }

    @Override
    public void startServer() throws TelegramApiException {
        ResourceConfig rc = new ResourceConfig();
        rc.register(restApi);
        context.getBeansWithAnnotation(Controller.class).values().forEach(rc::register);
        rc.register(JacksonFeature.class);
        rc.register(DefaultExceptionMapper.class);

        final HttpServer grizzlyServer;
        if (keystoreServerFile != null && keystoreServerPwd != null) {
            SSLContextConfigurator sslContext = new SSLContextConfigurator();

            // set up security context
            // contains server keypair
            sslContext.setKeyStoreFile(keystoreServerFile);
            sslContext.setKeyStorePass(keystoreServerPwd);

            grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(getBaseUri(), rc, true,
                    new SSLEngineConfigurator(sslContext).setClientMode(false).setNeedClientAuth(false));
        } else {
            grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(getBaseUri(), rc);
        }

        try {
            grizzlyServer.start();
        } catch (IOException e) {
            throw new TelegramApiException("Error starting webhook server", e);
        }
    }

    private URI getBaseUri() {
        return URI.create(internalUrl == null ? "http://0.0.0.0:" + webhookProperties.getPort() : internalUrl);
    }

    private static void validateServerKeystoreFile(String keyStore) throws TelegramApiException {
        File file = new File(keyStore);
        if (!file.exists() || !file.canRead()) {
            throw new TelegramApiException("Can't find or access server keystore file.");
        }
    }
}
