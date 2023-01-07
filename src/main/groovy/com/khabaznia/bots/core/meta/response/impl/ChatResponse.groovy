package com.khabaznia.bots.core.meta.response.impl

import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.meta.response.dto.Chat
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class ChatResponse extends BaseResponse {

    Chat chat
}
