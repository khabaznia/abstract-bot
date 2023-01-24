package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.enums.FieldType
import com.khabaznia.bots.core.flow.model.EditFlow
import com.khabaznia.bots.core.flow.strategy.FieldResolvingStrategy
import com.khabaznia.bots.core.meta.keyboard.impl.InlineButton
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.service.I18nService
import com.khabaznia.bots.core.trait.BaseRequests
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.meta.Emoji.*

@Slf4j
@Component
class EditSelectFieldKeyboardService implements BaseRequests {

    @Autowired
    private Map<FieldType, FieldResolvingStrategy> fieldResolvingStrategyMap
    @Autowired
    private FieldViewService fieldViewService
    @Autowired
    private I18nService i18nService

    InlineKeyboard getSelectedEntitiesKeyboard(Map<Object, Boolean> entities) {
        def keyboard = inlineKeyboard
        def flow = currentEditFlow
        if (currentFieldAnnotation.canCreateEntity())
            keyboard.button('button.edit.flow.add.new.entity', PLUS, get(CreateNewEntityFlowDto.class)
                    .successText('text.create.new.entity.success.message')
                    .entityClass(selectableFieldEntityClass)
                    .redirectParams([parentEditFlowId: flow.id.toString()])
                    .successPath(EDIT_SELECTABLE_FIELD_AFTER_CREATE))
                    .row()
        entities.entrySet().collate(3).eachWithIndex { fieldsRow, rowIndex ->
            fieldsRow.each {
                keyboard.addButton(collectSelectEntityButton(flow, it, rowIndex, entities.size()))
            }
            keyboard.row()
        }
        log.trace 'Selectable keyboard is created for {{}} entities', entities?.size()
        keyboard.row()
        keyboard.button('button.edit.flow.confirm.selected',
                LEFT_ARROW,
                SELECT_ENTITIES_CONFIRM,
                [editFlowId: flow.id.toString()])
    }

    private InlineButton collectSelectEntityButton(EditFlow flow, Map.Entry<Object, Boolean> entity,
                                                   Integer rowIndex, Integer entitiesListSize) {
        inlineButton.text(getSelectButtonText(entity.key, rowIndex, entitiesListSize))
                .callbackData(SELECT_ENTITY_COLLECTION_FIELD)
                .params([entityId: entity.key.id.toString(), editFlowId: flow.id.toString()])
                .emoji(entity.value ? CHECKED_MARK : '') as InlineButton
    }

    private String getSelectButtonText(Object entity, Integer rowIndex, Integer entitiesListSize) {
        def thresholdOfSymbolsForCurrentRow = 10 // default 10 sybmols for each button
        switch (((rowIndex + 1) * 3) - entitiesListSize) {
            case (1): thresholdOfSymbolsForCurrentRow = 14; break // 2 buttons in last row
            case (2): thresholdOfSymbolsForCurrentRow = 20; break // 1 button in last row
        }
        def selectableFieldEntityClass = selectableFieldEntityClass
        def idFieldText = fieldViewService.getIdFieldValue(selectableFieldEntityClass, Long.valueOf(entity.id))
                ?: i18nService.getFilledTemplate(getDefaultMessageOfIdField(selectableFieldEntityClass))
                ?: getEntityIdField(selectableFieldEntityClass).name
        idFieldText.length() > thresholdOfSymbolsForCurrentRow
                ? idFieldText.substring(0, thresholdOfSymbolsForCurrentRow - 3).concat('...')
                : idFieldText
    }
}
