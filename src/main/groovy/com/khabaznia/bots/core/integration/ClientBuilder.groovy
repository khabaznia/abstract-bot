package com.khabaznia.bots.core.integration

import com.khabaznia.bots.core.trait.Configurable
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.slf4j.Slf4jLogger
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

import static com.khabaznia.bots.core.routing.Constants.*

@Service
class ClientBuilder<T> implements Configurable{

    T getClient(Class<T> instance, @NonNull String rootUri) {
        Feign.builder()
                .retryer(retryer)
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(instance))
                .logLevel(Logger.Level.FULL)
                .target(instance, rootUri)
    }

    private Retryer.Default getRetryer() {
        new Retryer.Default(getLongConfig(INTEGRATION_RETYER_PERIOD),
                TimeUnit.SECONDS.toMillis(getLongConfig(INTEGRATION_RETYER_MAX_PERIOD)),
                getIntConfig(INTEGRATION_RETYER_MAX_ATTEMPTS))
    }
}
