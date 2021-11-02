package com.khabaznia.bot.service

import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.request.BaseResponse
import com.khabaznia.bot.service.sender.Sender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject

@Service
class MethodExecutionService {

    @Autowired
    Sender sender

    BaseResponse execute(BaseRequest request) {
        BotApiObject telegramResponse = sender.execute(request)
        return new BaseResponse(response: telegramResponse)
    }
}
