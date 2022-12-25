package com.khabaznia.example.stub

import com.khabaznia.bot.integration.ClientBuilder
import com.khabaznia.example.stub.api.StubApi
import com.khabaznia.example.stub.dto.StubRequestDto
import com.khabaznia.example.stub.dto.StubResponseEntryDto
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
