package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.dto.DeleteEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntitiesFlowKeyboardDto
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.trait.BaseRequests
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.meta.Emoji.*

@Slf4j
@Component
class EditFlowKeyboardService implements BaseRequests {

    InlineKeyboard getKeyboard(EditEntitiesFlowKeyboardDto dto) {
        def keyboard = inlineKeyboard
        addCreateNewEntityButton(dto, keyboard)
        mapEditEntitiesToKeyboardButtons(dto, keyboard)
        if (dto.backPath) keyboard.row().button('button.back', LEFT_ARROW, dto.backPath)
        keyboard
    }

    private void addCreateNewEntityButton(EditEntitiesFlowKeyboardDto dto, InlineKeyboard keyboard) {
        if (!dto.canCreateNewEntity) return
        keyboard.button('button.edit.flow.add.new.entity', PLUS, get(CreateNewEntityFlowDto.class)
                .successText(dto.createNewEntitySuccessMessage ?: 'text.create.new.entity.success.message')
                .entityClass(dto.entityClass)
                .redirectParams(dto.redirectParams)
                .entityFactory(dto.entityFactory)
                .successPath(dto.thisStepPath))
        keyboard.row()
    }

    private void mapEditEntitiesToKeyboardButtons(EditEntitiesFlowKeyboardDto dto, InlineKeyboard keyboard) {
        dto.entities.each {
            if (dto.canDeleteEntities)
                keyboard.button(CROSS_MARK, get(DeleteEntityFlowDto.class)
                        .successText(dto.deleteEntitySuccessMessage)
                        .entityToEdit(it)
                        .redirectParams(dto.redirectParams)
                        .successPath(dto.thisStepPath))
            def buttonName = dto.entityNameRetriever.apply(it)
                    ?: getDefaultMessageOfIdField(dto.entityClass)
                    ?: getEntityEditableIdFieldName(dto.entityClass)
            keyboard.button(buttonName, editEntityFlowDto
                    .entityId(it.id)
                    .entityClass(it.class)
                    .redirectParams(dto.redirectParams)
                    .backPath(dto.thisStepPath))
            keyboard.row()
        }
    }
}
