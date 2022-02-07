package com.khabaznia.bot.integration

import com.khabaznia.bot.trait.Configurable
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.slf4j.Slf4jLogger
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

import static com.khabaznia.bot.core.Constants.*

@Service
class ClientBuilder<T> implements Configurable{

    T getClient(Class<T> instance, @NonNull String rootUri) {
        return Feign.builder()
                .retryer(new Retryer.Default(getLongConfig(INTEGRATION_RETYER_PERIOD), TimeUnit.SECONDS.toMillis(getLongConfig(INTEGRATION_RETYER_MAX_PERIOD)), getIntConfig(INTEGRATION_RETYER_MAX_ATTEMPTS)))
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(instance))
                .logLevel(Logger.Level.FULL)
                .target(instance, rootUri);
    }
}
