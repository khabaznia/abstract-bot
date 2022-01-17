package com.khabaznia.bot.meta.response.impl

import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.model.Message
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class MessageResponse extends BaseResponse {

    Message result
}
