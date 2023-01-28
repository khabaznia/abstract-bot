package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.exception.BotServiceException
import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.enums.MediaType
import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.model.EditFlowRepository
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import javax.transaction.Transactional

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.util.BotSession.currentChat
import static com.khabaznia.bots.core.util.BotSession.setRedirectParams

@Slf4j
@Service
@Transactional
class EditFlowService {

    @Autowired
    private ApplicationContext context
    @Autowired
    private EditFlowRepository editFlowRepository
    @Autowired
    private UserService userService
    @Autowired
    private Map<FieldType, FieldProcessingStrategy> fieldProcessingStrategiesMap
    @Autowired
    private EditFlowEntityService entityService

    void saveEditFlowModel(EditFieldFlowDto editFieldFlowDto) {
        def editFlow = new EditFlow(entityClassName: editFieldFlowDto.entityClass.name,
                entityId: editFieldFlowDto.entityId,
                fieldName: editFieldFlowDto.fieldName,
                enterText: editFieldFlowDto.enterText,
                enterTextBinding: editFieldFlowDto.enterTextBinding,
                successMessage: editFieldFlowDto.successText,
                successPath: editFieldFlowDto.successPath,
                params: editFieldFlowDto.redirectParams,
                entityFactory: editFieldFlowDto.entityFactory,
                fieldSelectionStrategy: editFieldFlowDto.fieldSelectionStrategy)
        fieldProcessingStrategiesMap[getFieldType(getClass(editFlow), editFlow.fieldName)].prepare(editFlow)
        saveEditFlowInternal(editFlow)
    }

    void sendEnterMessage(boolean isNew) {
        log.trace 'Sending enter message'
        fieldProcessingStrategy.sendEnterMessages(currentEditFlow, isNew)
    }

    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        log.trace 'Sending success message for flow {}', editFlow.id
        fieldProcessingStrategy.sendSuccessMessages(editFlow, clear)
    }

    Long updateEntityWithInput(String input) {
        def entity = entityService.getEntity()
        def editFlow = currentEditFlow
        fieldProcessingStrategy.validate(editFlow, input)
        fieldProcessingStrategy.updateEntity(entity, input, editFlow)
        entityService.saveEntity(entity)
        log.info 'Entity successfully updated'
        entity.id
    }

    void postProcess(EditFlow editFlow, Long entityId = null) {
        def params = editFlow.params
        if (entityId) params.put('entityId', entityId.toString())
        setRedirectParams(params)
        currentChat.editFlow.childFlow
                ? deleteOldChildFlow()
                : deleteOldChatFlow(currentChat)
    }

    void setFieldLang(String lang) {
        def editFlow = currentEditFlow
        editFlow.setLang(lang)
        editFlowRepository.saveAndFlush(editFlow)
        log.debug 'Lang for edit flow {{}} is updated -> {}', editFlow.id, lang
    }

    void setInputMediaType(MediaType mediaType) {
        def editFlow = currentEditFlow
        editFlow.setInputMediaType(mediaType)
        log.debug 'Media type for edit flow {{}} is updated -> {}', editFlow.id, mediaType.toString()
        editFlowRepository.saveAndFlush(editFlow)
    }

    void selectEntityWithId(String entityId) {
        def longEntityId = Long.valueOf(entityId)
        def currentFlow = currentEditFlow
        if (currentFlow.selectedIds.contains(longEntityId)) {
            currentFlow.selectedIds.remove(longEntityId)
            log.debug 'Entity id {} was unselected', entityId
        } else {
            log.debug 'Entity id {} was selected', entityId
            currentFlow.selectedIds.add(longEntityId)
        }
        editFlowRepository.saveAndFlush(currentEditFlow)
    }

    private void saveEditFlowInternal(EditFlow editFlow) {
        log.debug 'Saving filled editFlow {}', editFlow
        def parentFlowId = editFlow.params?.get('parentEditFlowId')
        parentFlowId
                ? createNewChildFlow(editFlow, parentFlowId)
                : createNewChatFlow(editFlow)
    }

    private void createNewChatFlow(EditFlow editFlow) {
        def chat = currentChat
        deleteOldChatFlow(chat)
        editFlowRepository.saveAndFlush(editFlow)
        chat.editFlow = editFlow
        log.trace 'Edit flow saved to chat {}', chat.code
        userService.updateChat(chat)
    }

    private void createNewChildFlow(EditFlow editFlow, String parentFlowId) {
        def parentFlow = currentChat.editFlow
        if (parentFlow.id.toString() != parentFlowId)
            throw new BotServiceException('Provided parent editFlow is not same as current')
        parentFlow.setChildFlow(editFlow)
        log.debug 'Edit flow saved as child flow for parent flow with id {}', parentFlowId
        editFlowRepository.saveAndFlush(parentFlow)
    }

    private void deleteOldChildFlow() {
        def childFlow = currentEditFlow
        currentChat.editFlow.childFlow = null
        editFlowRepository.saveAndFlush(currentChat.editFlow)
        editFlowRepository.delete(childFlow)
        log.trace 'Old child flow was deleted'
    }

    private void deleteOldChatFlow(Chat chat) {
        def oldFlow = chat.editFlow
        if (oldFlow) {
            chat.editFlow = null
            userService.updateChat(chat)
            editFlowRepository.delete(oldFlow)
            log.trace 'Old flow {{}} was deleted', oldFlow.id
        }
    }

    private FieldProcessingStrategy getFieldProcessingStrategy() {
        fieldProcessingStrategiesMap[currentEditFlow.type]
    }
}
