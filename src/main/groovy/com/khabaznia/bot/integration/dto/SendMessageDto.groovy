package com.khabaznia.bot.integration.dto

import com.khabaznia.bot.enums.MessageFeature
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
