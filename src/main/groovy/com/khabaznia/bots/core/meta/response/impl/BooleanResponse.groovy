package com.khabaznia.bots.core.meta.response.impl

import com.khabaznia.bots.core.meta.response.BaseResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString
@TupleConstructor(includeSuperFields = true)
class BooleanResponse extends BaseResponse {

    Boolean result
}
