package com.khabaznia.bots.example.stub.dto

import groovy.transform.ToString

@ToString
class StubResponseDto {

    Integer count
    List<StubResponseEntryDto> entries
}
