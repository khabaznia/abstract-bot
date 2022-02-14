package com.khabaznia.bot.integration.api

import com.khabaznia.bot.integration.dto.StubRequestDto
import com.khabaznia.bot.integration.dto.StubResponseDto
import feign.QueryMap
import feign.RequestLine

interface StubApi {

    @RequestLine("GET /random")
    StubResponseDto random()

    @RequestLine("GET /entries")
    StubResponseDto entries(@QueryMap StubRequestDto request)
}
