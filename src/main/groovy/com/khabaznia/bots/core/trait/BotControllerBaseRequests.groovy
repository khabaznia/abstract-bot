package com.khabaznia.bots.core.trait

import com.khabaznia.bots.core.dto.ConfirmationFlowDto
import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.meta.container.DefaultRequestContainer
import com.khabaznia.bots.core.meta.keyboard.impl.InlineButton
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyButton
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyKeyboard
import com.khabaznia.bots.core.meta.keyboard.impl.ReplyKeyboardRemove
import com.khabaznia.bots.core.meta.object.BotCommand
import com.khabaznia.bots.core.meta.object.BotCommandScope
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.meta.request.impl.*
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@Slf4j
trait BotControllerBaseRequests {

    @Autowired
    ApplicationContext context
    @Autowired
    UserService userService
    @Autowired
    DefaultRequestContainer requests

    public <T extends BaseRequest> T get(Class<T> beanClass) {
        def message = context.getBean(beanClass) as T
        requests << message
        message
    }

    // Requests

    SendMessage getSendMessage() {
        def message = context.getBean 'sendMessage'
        requests << message
        message
    }

    EditMessage getEditMessage() {
        def message = context.getBean 'editMessage'
        requests << message
        message
    }

    SendPhoto getSendPhoto() {
        def message = context.getBean 'sendPhoto'
        requests << message
        message
    }

    SendAnimation getSendAnimation() {
        def message = context.getBean 'sendAnimation'
        requests << message
        message
    }

    SendVideo getSendVideo() {
        def message = context.getBean 'sendVideo'
        requests << message
        message
    }

    SendAudio getSendAudio() {
        def message = context.getBean 'sendAudio'
        requests << message
        message
    }

    SendDocument getSendDocument() {
        def message = context.getBean 'sendDocument'
        requests << message
        message
    }

    DeleteMessage getDeleteMessage() {
        def message = context.getBean 'deleteMessage'
        requests << message
        message
    }

    PinMessage getPinMessage() {
        context.getBean 'pinMessage'
    }

    UnpinMessage getUnpinMessage() {
        context.getBean 'unpinMessage'
    }

    UnpinAllMessages getUnpinAllMessages(){
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
        def message = context.getBean 'batChatMember'
        requests << message
        message
    }

    GetChat getGetChat() {
        context.getBean 'getChat'
    }

    SendChatAction getSendChatAction() {
        context.getBean 'sendChatAction'
    }

    SetMyCommands getSetMyCommands() {
        context.getBean 'setMyCommands'
    }

    SendLocation getSendLocation() {
        def message = context.getBean 'sendLocation'
        requests << message
        message
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

    void cleanRequests() {
        requests.clean()
    }

    List<BaseRequest> getRequestList() {
        requests.convertedRequests()
    }
}
