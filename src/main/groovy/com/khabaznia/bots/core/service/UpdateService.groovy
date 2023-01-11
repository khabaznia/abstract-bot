package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.enums.UpdateType
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

import static com.khabaznia.bots.core.routing.Constants.*
import static com.khabaznia.bots.core.security.Constants.CHAT_ID_DELIMITER
import static com.khabaznia.bots.core.service.DeepLinkingPathService.getDeepLinkingPath
import static com.khabaznia.bots.core.util.HTMLParsingUtil.mapHTMLParsableSymbols

@Slf4j
@Service
class UpdateService {

    public static final List<UpdateType> ONE_VARIANT_CONTROLLER_GROUP =
            [UpdateType.UNDEFINED_MESSAGE, UpdateType.IMAGE, UpdateType.VIDEO, UpdateType.AUDIO, UpdateType.ANIMATION, UpdateType.DOCUMENT]

    @Autowired
    private PathCryptService pathCryptService

    static String getChatInfoFromUpdate(Update update) {
        def message = getMessage(update)
        if (message) {
            return message?.chat?.id + CHAT_ID_DELIMITER + getUserFromUpdate(update).id
        } else {
            return update.myChatMember?.chat?.id + CHAT_ID_DELIMITER + update.myChatMember?.from?.id
        }
    }

    static Chat getApiChatFromUpdate(Update update) {
        getMessage(update)?.chat ?: (update.chatMember ?: update.myChatMember).chat
    }

    String getMappedMessageText(Update update) {
        mapHTMLParsableSymbols(getMessageFromUpdate(update)).strip()
    }

    String getMessageFromUpdate(Update update) {
        def message = update.hasCallbackQuery()
                ? update?.callbackQuery?.data
                : getMessage(update)?.text
        message = getDeepLinkingPath(message)
        pathCryptService.getDecryptedPath(message) ?: message ?: ''
    }

    Map<String, String> getParametersFromUpdate(Update update) {
        def message = getMessageFromUpdate(update)
        message?.contains(PARAMETERS_PREFIX) ? getParametersFromMessage(message) : [:]
    }

    static UpdateType getUpdateType(Update update) {
        if (update.hasMyChatMember()) return UpdateType.BOT_CHAT_MEMBER_UPDATED
        if (update.hasChatMember()) return UpdateType.USER_CHAT_MEMBER_UPDATED
        if (update.hasChatJoinRequest()) return UpdateType.CHAT_JOIN_REQUEST
        def message = getMessage(update)
        if (message.migrateToChatId) return UpdateType.MIGRATE_TO_CHAT_ID
        if (message.groupchatCreated) return UpdateType.GROUP_CHAT_CREATED
        if (!message.hasText() && message.newChatMembers) return UpdateType.NEW_CHAT_MEMBERS
        if (message.leftChatMember) return UpdateType.USER_LEFT_CHAT
        if (isServiceMessage(message)) return UpdateType.CHAT_SERVICE_MESSAGES
        def result = message.hasText() ? UpdateType.UNDEFINED_MESSAGE : UpdateType.UNDEFINED
        if (message.hasAudio()) return UpdateType.AUDIO
        if (message.hasVideo()) return UpdateType.VIDEO
        if (message.hasPhoto()) return UpdateType.IMAGE
        if (message.hasAnimation()) return UpdateType.IMAGE
        if (message.hasDocument()) return UpdateType.DOCUMENT
        result
    }

    static String getFileId(Update update) {
        def type = getUpdateType(update)
        def message = getMessage(update)
        switch (type) {
            case UpdateType.DOCUMENT: return message?.document?.fileId
            case UpdateType.VIDEO: return message?.video?.fileId
            case UpdateType.AUDIO: return message?.audio?.fileId
            case UpdateType.IMAGE: return message?.photo[0]?.fileId
        }
        return null
    }

    static Message getMessage(Update update) {
        if (update.hasMessage()) return update?.message
        if (update.hasCallbackQuery()) return update?.callbackQuery?.message
        if (update.hasEditedMessage()) return update?.editedMessage
        return null
    }

    static User getUserFromUpdate(Update update) {
        if (update.hasMessage()) return update?.message?.from
        if (update.hasCallbackQuery()) return update?.callbackQuery?.from
        if (update.hasEditedMessage()) return update?.editedMessage?.from
        return null
    }

    static Map<String, String> getParametersFromMessage(String message) {
        message?.tokenize(PARAMETERS_PREFIX)[1]?.tokenize(PARAMETERS_DELIMITER)
                ?.collectEntries { [it.tokenize(PARAMETER_KEY_VALUE_DELIMITER)[0], it.tokenize(PARAMETER_KEY_VALUE_DELIMITER)[1]] }
    }

    private static boolean isServiceMessage(Message message) {
        !message.hasText() &&
                (message.deleteChatPhoto ||
                        message.pinnedMessage ||
                        message.superGroupCreated ||
                        message.newChatPhoto ||
                        message.newChatTitle)
    }
}
