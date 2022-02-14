package com.khabaznia.bot.integration

import com.khabaznia.bot.integration.api.StubApi
import com.khabaznia.bot.integration.dto.StubRequestDto
import com.khabaznia.bot.integration.dto.StubResponseEntryDto
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class StubService {

    public static final String PUBLIC_API = 'https://api.publicapis.org'

    @Autowired
    private ClientBuilder<StubApi> clientBuilder

    StubResponseEntryDto random() {
        def stubApi = clientBuilder.getClient(StubApi.class, PUBLIC_API)
        def result = stubApi.random()
        log.trace 'Result from api -> {}', result
        result?.entries[0]
    }

    Integer entries(String query) {
        def stubApi = clientBuilder.getClient(StubApi.class, PUBLIC_API)
        def request = new StubRequestDto(category: query)
        def result = stubApi.entries(request)
        log.trace 'Entries result from api -> {}', result
        result?.count
    }
}
