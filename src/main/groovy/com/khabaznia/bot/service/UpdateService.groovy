package com.khabaznia.bot.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class UpdateService {

    String getChatInfoFromUpdate(Update update){
        return null
    }

    Map<String, String> getParametersFromUpdate(Update update){
        return null
    }
}
