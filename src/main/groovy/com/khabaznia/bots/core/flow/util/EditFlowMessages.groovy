package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.controller.Constants
import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.meta.container.DefaultRequestContainer
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.request.impl.SendMessage
import com.khabaznia.bots.core.service.MessageService
import com.khabaznia.bots.core.trait.BaseRequests
import com.khabaznia.bots.core.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.*
import static com.khabaznia.bots.core.flow.util.EditableParsingUtil.*
import static com.khabaznia.bots.core.flow.util.FlowConversionUtil.*
import static com.khabaznia.bots.core.meta.Emoji.*
import static com.khabaznia.bots.core.routing.Constants.AVAILABLE_LOCALES
import static com.khabaznia.bots.core.util.SessionUtil.currentChat
import static org.apache.groovy.parser.antlr4.util.StringUtils.isEmpty

@Slf4j
@Component
class EditFlowMessages implements BaseRequests, Configurable{

    private static final String EDIT_FLOW_UPDATE_MESSAGE = 'editFlowChooseLangMessage'
    private static final String EDIT_FLOW_CURRENT_VALUE_MESSAGE = 'editFlowCurrentValueMessage'

    @Autowired
    private DefaultRequestContainer requests
    @Autowired
    private MessageService messageService

    void editFlowEnterMessage(String text, Map<String, String> binding) {
        def message = sendMessage.text(text ?: enterMessage ?: defaultEditFlowEnterMessage)
                .binding(binding)
                .delete() as SendMessage
        if (isValueClearingEnabled() && !isEmpty(currentEditFlow.oldValue?.strip())) {
            message.keyboard(inlineKeyboard.button('button.edit.flow.clear.value', EDIT_FIELD_CLEAR_VALUE))
        }
        requests << message
    }

    void editFlowCurrentValueMessage(String currentValue) {
        requests << sendMessage.text(getEditFlowCurrentValueText(currentValue))
                .binding([value: currentValue])
                .label(currentChat.code.concat(EDIT_FLOW_CURRENT_VALUE_MESSAGE))
    }

    void updateEditFlowCurrentValueMessage(String currentValue, String oldValue) {
        if (oldValue != currentValue)
            requests << editMessage.text(getEditFlowCurrentValueText(currentValue))
                    .binding([value: currentValue])
                    .label(currentChat.code.concat(EDIT_FLOW_CURRENT_VALUE_MESSAGE))
                    .delete()
    }

    void editFlowChooseLangMessage() {
        requests << sendMessage.text('text.edit.flow.choose.lang')
                .keyboard(setLocaleButtons(inlineKeyboard))
                .delete()
    }

    void updateEditFlowChooseLangMenu(String lang) {
        requests << sendMessage.text('text.edit.flow.chosen.lang')
                .binding([lang: Constants.LANG_CONTROLLER.LANG_EMOJI[lang]])
                .label(currentChat.code.concat(EDIT_FLOW_UPDATE_MESSAGE))
                .delete()
    }

    void deleteEditFlowChooseLangMessage() {
        requests << deleteMessage.label(currentChat.code.concat(EDIT_FLOW_UPDATE_MESSAGE))
    }

    void editBooleanFieldMenu() {
        requests << sendMessage.text('text.edit.flow.choose.boolean.value')
                .keyboard(inlineKeyboard
                        .button(CHECKED_MARK, EDIT_BOOLEAN_FIELD, [value: true.toString()])
                        .button(CROSS_MARK, EDIT_BOOLEAN_FIELD, [value: false.toString()]))
                .delete()
    }

    void editFlowSuccessMessage(String text, boolean clear = false) {
        requests << sendMessage
                .text(text ?: (clear
                        ? 'text.edit.flow.cleared.message'
                        : 'text.edit.flow.success.message'))
                .delete()
    }

    void deleteEntitySuccessMessage(String text) {
        requests << sendMessage.text(text ?: 'text.delete.entity.success.message')
                .delete()
    }

    void editFlowEntityFieldsSelectMessage(Map<String, String> fields, EditEntityFlowDto editEntityFlowDto) {
        InlineKeyboard keyboard = mapEditEntityFieldsToKeyboardButtons(fields, editEntityFlowDto)
        keyboard.row().button('button.back', LEFT_ARROW, editEntityFlowDto.backPath)
        requests << sendMessage.text(editEntityFlowDto.enterText ?: 'text.edit.flow.select.field.to.edit')
                .binding(editEntityFlowDto.enterTextBinding)
                .keyboard(keyboard)
                .delete()
    }

    private InlineKeyboard setLocaleButtons(InlineKeyboard inlineKeyboard) {
        getConfigs(AVAILABLE_LOCALES)
                .collectEntries { [(it): Constants.LANG_CONTROLLER.LANG_EMOJI[it]] }
                .each {
                    inlineKeyboard.button(it.value.toString(),
                            EDIT_LOCALIZED_FIELD_MENU,
                            [lang: it.key.toString()])
                }
        inlineKeyboard
    }

    private InlineKeyboard mapEditEntityFieldsToKeyboardButtons(Map<String, String> fields, EditEntityFlowDto editEntityFlowDto) {
        def keyboard = inlineKeyboard
        fields.each {
            keyboard.button(it.value, editFieldFlowDto
                    .fieldName(it.key)
                    .entityId(editEntityFlowDto.entityId)
                    .entityClass(editEntityFlowDto.entityClass)
                    .redirectParams(getPrefixMappedParams(getEditFLowDtoParams(editEntityFlowDto), FLOW_PARAM_PREFIX))
                    .successPath(EDIT_ENTITY_ENTER))
        }
        keyboard
    }

    private static String getEditFlowCurrentValueText(String currentValue) {
        !isEmpty(currentValue?.strip()) ? 'text.edit.flow.current.value' : 'text.edit.flow.no.current.value'
    }

    private static String getDefaultEditFlowEnterMessage() {
        isValueClearingEnabled() ? 'text.edit.flow.enter.new.value.or.clear' : 'text.edit.flow.enter.new.value'
    }
}
