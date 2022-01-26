package com.khabaznia.bot.integration.dto

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class StubResponseEntry {

    String API
    String Description
    String Auth
    String HTTPS
    String Cors
    String Category
}
