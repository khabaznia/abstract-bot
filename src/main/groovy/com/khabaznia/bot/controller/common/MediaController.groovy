package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.service.UpdateService.*

@Slf4j
@Component
@BotController
class MediaController extends AbstractBotController {

    @BotRequest(path = IMAGE_CONTROLLER, enableDuplicateRequests = true)
    String processPhoto() {
        sendMessage.text('message.good.photo')
        sendPhoto.photo(getPhotoId(update))
                .text('message.admin.media.from.chat')
                .binding([chatId: SessionUtil.currentChat.code])
                .chatId(adminChatId)
        log.debug 'Processing image (nope)'
        DEFAULT
    }

    @BotRequest(path = VIDEO_CONTROLLER, enableDuplicateRequests = true)
    String processVideo() {
        sendMessage.text('message.good.video')
        sendVideo.video(getVideoId(update))
                .text('message.admin.media.from.chat')
                .binding([chatId: SessionUtil.currentChat.code])
                .chatId(adminChatId)
        log.debug 'Processing video (nope)'
        DEFAULT
    }

    @BotRequest(path = AUDIO_CONTROLLER, enableDuplicateRequests = true)
    String processAudio() {
        sendMessage.text('message.good.audio')
        sendAudio.audio(getAudioId(update))
                .text('message.admin.media.from.chat')
                .binding([chatId: SessionUtil.currentChat.code])
                .chatId(adminChatId)
        log.debug 'Processing audio (nope)'
        DEFAULT
    }
}
