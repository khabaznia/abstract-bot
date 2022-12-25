package com.khabaznia.bot.meta.request.impl

import com.khabaznia.bot.meta.object.BotCommand
import com.khabaznia.bot.meta.object.BotCommandScope
import com.khabaznia.bot.meta.request.BaseRequest
import com.khabaznia.bot.meta.response.impl.BooleanResponse
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@ToString(includeSuper = true, includeNames = true)
@Component(value = 'setMyCommands')
@Scope('prototype')
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@TupleConstructor(includeSuperFields = true)
class SetMyCommands extends BaseRequest<BooleanResponse> {

    String chatId = MOCK_CHAT_ID
    List<BotCommand> commands
    BotCommandScope scope
    String languageCode
}
