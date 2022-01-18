package com.khabaznia.bot.strategy

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.BaseResponse
import com.khabaznia.bot.service.ApiMethodService
import org.springframework.beans.factory.annotation.Autowired

abstract class RequestProcessingStrategy<Request extends BaseRequest, Response extends BaseResponse> {

    @Autowired
    ApiMethodService apiMethodService

    void beforeProcess(Request request){
        //default - no action
    }

    Response process(Request request){
        apiMethodService.execute(request) as Response
    }

    void afterProcess(Response response){
        //default - no action
    }
}