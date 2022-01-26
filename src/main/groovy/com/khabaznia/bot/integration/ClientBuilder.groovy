package com.khabaznia.bot.integration

import feign.Feign
import feign.Logger
import feign.Retryer
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.slf4j.Slf4jLogger
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

@Service
class ClientBuilder<T> {

    T getClient(Class<T> instance, @NonNull String rootUri) {
        return Feign.builder()
                .retryer(new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(1L), 3))
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(instance))
                .logLevel(Logger.Level.FULL)
                .target(instance, rootUri);
    }
}
