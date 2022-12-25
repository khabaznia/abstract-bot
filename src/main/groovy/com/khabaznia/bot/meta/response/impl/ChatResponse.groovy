package com.khabaznia.bot.meta.response.impl

import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.dto.Chat
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class ChatResponse extends BaseResponse {

    Chat chat
}
