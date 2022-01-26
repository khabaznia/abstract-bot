package com.khabaznia.bot.integration.api

import com.khabaznia.bot.integration.dto.StubRequest
import com.khabaznia.bot.integration.dto.StubResponse
import feign.QueryMap
import feign.RequestLine

interface StubApi {

    @RequestLine("GET /random")
    StubResponse random()

    @RequestLine("GET /entries")
    StubResponse entries(@QueryMap StubRequest request)
}
