package com.khabaznia.bots.core.trait

import com.khabaznia.bots.core.dto.ConfirmationFlowDto
import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.meta.keyboard.impl.*
import com.khabaznia.bots.core.meta.object.BotCommand
import com.khabaznia.bots.core.meta.object.BotCommandScope
import com.khabaznia.bots.core.meta.request.impl.*
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@Slf4j
trait BaseRequests {

    @Autowired
    ApplicationContext context
    @Autowired
    UserService userService

    public <T> T get(Class<T> beanClass) {
        context.getBean(beanClass) as T
    }

    // Requests

    SendMessage getSendMessage() {
        context.getBean 'sendMessage'
    }

    EditMessage getEditMessage() {
        context.getBean 'editMessage'
    }

    SendPhoto getSendPhoto() {
        context.getBean 'sendPhoto'
    }

    SendAnimation getSendAnimation() {
        context.getBean 'sendAnimation'
    }

    SendVideo getSendVideo() {
        context.getBean 'sendVideo'
    }

    SendAudio getSendAudio() {
        context.getBean 'sendAudio'
    }

    SendDocument getSendDocument() {
        context.getBean 'sendDocument'
    }

    DeleteMessage getDeleteMessage() {
        context.getBean 'deleteMessage'
    }

    PinMessage getPinMessage() {
        context.getBean 'pinMessage'
    }

    UnpinMessage getUnpinMessage() {
        context.getBean 'unpinMessage'
    }

    UnpinAllMessages getUnpinAllMessages() {
        context.getBean 'unpinAllMessages'
    }

    SetChatTitle getSetChatTitle() {
        context.getBean 'setChatTitle'
    }

    SetChatPhoto getSetChatPhoto() {
        context.getBean 'setChatPhoto'
    }

    LeaveChat getLeaveChat() {
        context.getBean 'leaveChat'
    }

    BanChatMember getBanChatMember() {
        context.getBean 'batChatMember'
    }

    GetChat getGetChat() {
        context.getBean 'getChat'
    }

    SendChatAction getSendChatAction() {
        context.getBean 'sendChatAction'
    }

    SetChatPermissions getSetChatPermissions() {
        context.getBean 'setChatPermissions'
    }

    SetMyCommands getSetMyCommands() {
        context.getBean 'setMyCommands'
    }

    SendLocation getSendLocation() {
        context.getBean 'sendLocation'
    }


    // OTHER OBJECTS

    InlineKeyboard getInlineKeyboard() {
        context.getBean 'inlineKeyboard'
    }

    ReplyKeyboard getReplyKeyboard() {
        context.getBean 'replyKeyboard'
    }

    ReplyKeyboardRemove getReplyKeyboardRemove() {
        context.getBean 'replyKeyboardRemove'
    }

    InlineButton getInlineButton() {
        context.getBean('inlineButton')
    }

    ReplyButton getReplyButton() {
        context.getBean('replyButton')
    }

    ConfirmationFlowDto getConfirmationFlowDto() {
        context.getBean 'confirmationFlowDto'
    }

    BotCommand getBotCommand() {
        context.getBean 'botCommand'
    }

    BotCommandScope getBotCommandScope() {
        context.getBean 'botCommandScope'
    }

    // Ofter used method/data

    String getAdminChatId() {
        userService.getUserForRole(UserRole.ADMIN)?.code
    }
}
