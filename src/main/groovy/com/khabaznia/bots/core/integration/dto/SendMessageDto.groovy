package com.khabaznia.bots.core.integration.dto

import com.khabaznia.bots.core.enums.MessageFeature
import groovy.transform.ToString

import javax.validation.constraints.NotBlank

@ToString
class SendMessageDto {

    String chatId
    @NotBlank(message = "text must not be blank")
    String text
    List<MessageFeature> features
    List<Map<String, String>> buttons
}
