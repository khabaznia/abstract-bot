package com.khabaznia.bot.meta.response.impl

import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.meta.response.dto.ChatMember
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class ChatMemberResponse extends BaseResponse {

    ChatMember chatMember
}
