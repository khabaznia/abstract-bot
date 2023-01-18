package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.dto.EditFieldFlowDto
import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.strategy.FieldProcessingStrategy
import com.khabaznia.bots.core.model.EditFlow
import com.khabaznia.bots.core.repository.EditFlowRepository
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

import javax.persistence.EntityManager
import javax.transaction.Transactional

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.util.SessionUtil.currentChat

@Slf4j
@Service
@Validated
@Transactional
class EditFlowService {

    @Autowired
    private EntityManager entityManager
    @Autowired
    private ApplicationContext context
    @Autowired
    private EditFlowRepository editFlowRepository
    @Autowired
    private UserService userService
    @Autowired
    private Map<FieldType, FieldProcessingStrategy> fieldProcessingStrategiesMap

    void saveEditFlowModel(EditFieldFlowDto editFieldFlowDto) {
        def editFlow = new EditFlow(
                entityClassName: editFieldFlowDto.entityClass.name,
                entityId: editFieldFlowDto.entityId,
                fieldName: editFieldFlowDto.fieldName,
                enterText: editFieldFlowDto.enterText,
                enterTextBinding: editFieldFlowDto.enterTextBinding,
                successMessage: editFieldFlowDto.successText,
                successPath: editFieldFlowDto.successPath,
                params: editFieldFlowDto.redirectParams)
        fieldProcessingStrategiesMap[getFieldType(getClass(editFlow), editFlow.fieldName)]
                .prepare(editFlow)
    }

    void sendEnterMessage(boolean isNew) {
        fieldProcessingStrategy.sendEnterMessages(currentEditFlow, isNew)
    }

    void setFieldLang(String lang) {
        def editFlow = currentEditFlow
        editFlow.setLang(lang)
        editFlowRepository.save(editFlow)
    }

    void updateEntityWithInput(String input) {
        fieldProcessingStrategy.validate(currentEditFlow, input)
        def entity = getFilledEntity(input)
        entityManager.persist(entity)
        entityManager.flush()
    }

    void sendSuccessMessages(EditFlow editFlow, boolean clear) {
        fieldProcessingStrategy.sendSuccessMessages(editFlow, clear)
    }

    void cleanUp() {
        deleteOldFlow()
    }

    private void deleteOldFlow() {
        def chat = currentChat
        def oldFlow = chat.editFlow
        if (oldFlow) {
            chat.editFlow = null
            userService.updateChat(chat)
            editFlowRepository.delete(oldFlow)
            entityManager.flush()
        }
    }

    private Object getFilledEntity(String input) {
        def editFlow = currentEditFlow
        def entityClass = getClass(editFlow)
        def entity = editFlow.entityId
                ? entityManager.find(entityClass, editFlow.entityId)
                : entityClass.getDeclaredConstructor().newInstance()
        fieldProcessingStrategy.updateEntity(entity, input, editFlow)
        entity
    }

    void deleteEntity(Class entityClass, Long entityId) {
        def entity = entityManager.find(entityClass, entityId)
        entityManager.remove(entity)
    }

    private FieldProcessingStrategy getFieldProcessingStrategy() {
        fieldProcessingStrategiesMap[currentEditFlow.type]
    }
}
