package com.khabaznia.bots.core.meta.response.impl

import com.khabaznia.bots.core.meta.response.BaseResponse
import com.khabaznia.bots.core.meta.response.dto.ChatMember
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class ChatMemberResponse extends BaseResponse {

    ChatMember chatMember
}
