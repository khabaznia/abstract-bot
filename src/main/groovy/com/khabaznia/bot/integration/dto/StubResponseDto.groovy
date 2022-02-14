package com.khabaznia.bot.integration.dto

import groovy.transform.ToString

@ToString
class StubResponseDto {

    Integer count
    List<StubResponseEntryDto> entries
}
