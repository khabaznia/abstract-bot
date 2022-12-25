package com.khabaznia.example.stub.api

import com.khabaznia.example.stub.dto.StubRequestDto
import com.khabaznia.example.stub.dto.StubResponseDto
import feign.QueryMap
import feign.RequestLine

interface StubApi {

    @RequestLine("GET /random")
    StubResponseDto random()

    @RequestLine("GET /entries")
    StubResponseDto entries(@QueryMap StubRequestDto request)
}
