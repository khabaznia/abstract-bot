package com.khabaznia.bots.core.flow.service

import com.khabaznia.bots.core.flow.dto.CreateNewEntityFlowDto
import com.khabaznia.bots.core.flow.dto.DeleteEntityFlowDto
import com.khabaznia.bots.core.flow.dto.EditEntitiesFlowKeyboardDto
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.trait.BaseRequests
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.validation.ConstraintViolationException

import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getDefaultMessageOfIdField
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.getEntityEditableIdFieldName
import static com.khabaznia.bots.core.meta.Emoji.*
import static javax.validation.Validation.buildDefaultValidatorFactory

/**
 * Intended to generate keyboard buttons from {@link EditEntitiesFlowKeyboardDto}
 */
@Slf4j
@Component
class EditFlowKeyboardService implements BaseRequests {

    @Autowired
    private FieldViewService fieldViewService

    /**
     * Generates and adds buttons for operations on entities that defined in dto entity class
     *
     * @param keyboard that should be added buttons to
     * @param dto {@link EditEntitiesFlowKeyboardDto} that contains all parameters to generation flow keybaord
     * @return same keyboard with buttons
     */
    InlineKeyboard addButtons(InlineKeyboard keyboard, EditEntitiesFlowKeyboardDto dto) {
        validate(dto)
        log.debug 'Generating keyboard for edit entities dto: {}', dto
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
        def columnsNumber = dto.canDeleteEntities ? 1 : dto.entitiesInRow ?: 1
        dto.entities.collate(columnsNumber).each { row ->
            row.each { entity ->
                if (dto.canDeleteEntities)
                    keyboard.button(CROSS_MARK, get(DeleteEntityFlowDto.class)
                            .successText(dto.deleteEntitySuccessMessage)
                            .entityToEdit(entity)
                            .redirectParams(dto.redirectParams)
                            .successPath(dto.thisStepPath))
                def buttonName = dto.entityNameRetriever?.apply(entity)
                        ?: fieldViewService.getIdFieldValue(entity.class, entity.id)
                        ?: getDefaultMessageOfIdField(dto.entityClass)
                        ?: getEntityEditableIdFieldName(dto.entityClass)
                keyboard.button(buttonName, editEntityFlowDto
                        .entityId(entity.id)
                        .fieldsInRow(dto.fieldsInRow)
                        .entityClass(entity.class)
                        .redirectParams(dto.redirectParams)
                        .backPath(dto.thisStepPath))
            }
            keyboard.row()
        }
    }

    private static void validate(EditEntitiesFlowKeyboardDto dto) {
        def constraints = buildDefaultValidatorFactory().getValidator().validate(dto)
        if (!constraints.isEmpty()) throw new ConstraintViolationException(constraints)
    }
}
