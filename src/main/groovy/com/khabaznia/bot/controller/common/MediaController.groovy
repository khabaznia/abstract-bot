package com.khabaznia.bot.controller.common

import com.khabaznia.bot.controller.AbstractBotController
import com.khabaznia.bot.core.annotation.BotController
import com.khabaznia.bot.core.annotation.BotRequest
import com.khabaznia.bot.meta.request.impl.AbstractMediaRequest
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bot.controller.Constants.COMMON.*
import static com.khabaznia.bot.controller.Constants.COMMON.ANIMATION_CONTROLLER
import static com.khabaznia.bot.service.UpdateService.*

@Slf4j
@Component
@BotController
class MediaController extends AbstractBotController {

    @BotRequest(path = IMAGE_CONTROLLER, enableDuplicateRequests = true)
    processPhoto() {
        sendMessage.text('text.good.photo')
        fillMediaRequest(sendPhoto)
        log.debug 'Processing image (nope)'
    }

    @BotRequest(path = VIDEO_CONTROLLER, enableDuplicateRequests = true)
    processVideo() {
        sendMessage.text('text.good.video')
        fillMediaRequest(sendVideo)
        log.debug 'Processing video (nope)'
    }

    @BotRequest(path = AUDIO_CONTROLLER, enableDuplicateRequests = true)
    processAudio() {
        sendMessage.text('text.good.audio')
        fillMediaRequest(sendAudio)
        log.debug 'Processing audio (nope)'
    }

    @BotRequest(path = ANIMATION_CONTROLLER, enableDuplicateRequests = true)
    processAnimation() {
        sendMessage.text('text.good.animation')
        fillMediaRequest(sendAnimation)
        log.debug 'Processing document (nope)'
    }

    @BotRequest(path = DOCUMENT_CONTROLLER, enableDuplicateRequests = true)
    processDocument() {
        sendMessage.text('text.good.document')
        fillMediaRequest(sendDocument)
        log.debug 'Processing document (nope)'
    }

    protected void fillMediaRequest(AbstractMediaRequest request) {
        request.fileIdentifier(getFileId(update))
                .text('text.admin.media.from.chat')
                .binding([chatId: SessionUtil.currentChat.code])
                .chatId(adminChatId)
    }
}
