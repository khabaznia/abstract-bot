package com.khabaznia.bots.core.flow.controller

import com.khabaznia.bots.core.flow.enums.MediaType
import com.khabaznia.bots.core.routing.annotation.BotController
import com.khabaznia.bots.core.routing.annotation.BotRequest
import com.khabaznia.bots.core.routing.annotation.Input
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.COMMON.*
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.EDIT_FIELD_ENTER
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.EDIT_FIELD_VALIDATION_FAILED

@Slf4j
@Component
@BotController
class EditMediaFieldController extends AbstractEditFlowController {

    @BotRequest(path = IMAGE_CONTROLLER, after = EDIT_FIELD_ENTER)
    String editImageField(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.IMAGE)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = AUDIO_CONTROLLER, after = EDIT_FIELD_ENTER)
    String editAudioField(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.AUDIO)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = VIDEO_CONTROLLER, after = EDIT_FIELD_ENTER)
    String editVideoField(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.VIDEO)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = ANIMATION_CONTROLLER, after = EDIT_FIELD_ENTER)
    String editAnimationField(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.ANIMATION)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = DOCUMENT_CONTROLLER, after = EDIT_FIELD_ENTER)
    String editDocumentField(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.DOCUMENT)
        editFieldInternal(inputMedia)
    }

    // AFTER VALIDATION CONTROLLERS

    @BotRequest(path = IMAGE_CONTROLLER, after = EDIT_FIELD_VALIDATION_FAILED)
    String editImageFieldAfterValidation(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.IMAGE)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = AUDIO_CONTROLLER, after = EDIT_FIELD_VALIDATION_FAILED)
    String editAudioFieldAfterValidation(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.AUDIO)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = VIDEO_CONTROLLER, after = EDIT_FIELD_VALIDATION_FAILED)
    String editVideoFieldAfterValidation(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.VIDEO)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = ANIMATION_CONTROLLER, after = EDIT_FIELD_VALIDATION_FAILED)
    String editAnimationFieldAfterValidation(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.ANIMATION)
        editFieldInternal(inputMedia)
    }

    @BotRequest(path = DOCUMENT_CONTROLLER, after = EDIT_FIELD_VALIDATION_FAILED)
    String editDocumentFieldAfterValidation(@Input(media = true) String inputMedia) {
        editFlowService.setInputMediaType(MediaType.DOCUMENT)
        editFieldInternal(inputMedia)
    }
}
