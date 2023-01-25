package com.khabaznia.bots.core.flow.util

import com.khabaznia.bots.core.controller.Constants
import com.khabaznia.bots.core.flow.dto.EditEntityFlowDto
import com.khabaznia.bots.core.flow.service.EditSelectFieldKeyboardService
import com.khabaznia.bots.core.meta.container.DefaultRequestContainer
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.request.impl.AbstractKeyboardMessage
import com.khabaznia.bots.core.meta.request.impl.AbstractMediaRequest
import com.khabaznia.bots.core.meta.request.impl.SendMessage
import com.khabaznia.bots.core.service.ChatService
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
import static org.apache.groovy.parser.antlr4.util.StringUtils.isEmpty

@Slf4j
@Component
class EditFlowMessages implements BaseRequests, Configurable {

    private static final String EDIT_FLOW_LANG_MENU_MESSAGE_LABEL = 'editFlowChooseLangMessage'
    private static final String EDIT_FLOW_CURRENT_VALUE_MESSAGE_LABEL = 'editFlowCurrentValueMessage'
    private static final String EDIT_FLOW_SELECT_ENTITIES_MESSAGE = 'selectEntitiesKeyboardMessage'

    @Autowired
    private DefaultRequestContainer requests
    @Autowired
    private MessageService messageService
    @Autowired
    private EditSelectFieldKeyboardService keyboardService
    @Autowired
    private ChatService chatService

    void editFlowEnterMessage(String text, Map<String, String> binding,
                              Closure<String> defaultMessageRetriever = { defaultEditFlowEnterMessage }) {
        def oldValueCanBeDeleted = isCurrentValueRemovable()
        def message = sendMessage.text(text ?: enterMessage ?: defaultMessageRetriever.call())
                .binding(binding)
                .delete() as SendMessage
        def keyboard = inlineKeyboard
                .button('button.back', LEFT_ARROW, EDIT_FIELD_CANCEL, currentEditFlow.params)
        if (oldValueCanBeDeleted)
            keyboard.button('button.edit.flow.clear.value', EDIT_FIELD_CLEAR_VALUE)

        requests << message.keyboard(keyboard)
    }

    void editFlowCurrentValueMessage(String currentValue) {
        requests << sendMessage.text(getEditFlowCurrentValueText(currentValue))
                .binding([value: currentValue])
                .label(chatService.setChatParam(EDIT_FLOW_CURRENT_VALUE_MESSAGE_LABEL))
    }

    void updateEditFlowCurrentValueMessage(String currentValue, String oldValue) {
        if (oldValue != currentValue) requests << editMessage.text(getEditFlowCurrentValueText(currentValue))
                .binding([value: currentValue])
                .label(chatService.getChatParam(EDIT_FLOW_CURRENT_VALUE_MESSAGE_LABEL))
    }

    void editFlowChooseLangMessage(String text, Map<String, String> binding) {
        requests << sendMessage.text(text ?: enterMessage ?: 'text.edit.flow.choose.lang')
                .binding(binding)
                .keyboard(setLocaleButtons(inlineKeyboard))
                .delete()
    }

    void updateEditFlowChooseLangMenu(String lang) {
        requests << sendMessage.text('text.edit.flow.chosen.lang')
                .binding([lang: Constants.LANG_CONTROLLER.LANG_EMOJI[lang]])
                .label(chatService.setChatParam(EDIT_FLOW_LANG_MENU_MESSAGE_LABEL))
                .delete()
    }

    void deleteEditFlowChooseLangMessage() {
        requests << deleteMessage.label(chatService.getChatParam(EDIT_FLOW_LANG_MENU_MESSAGE_LABEL))
    }

    void editBooleanFieldMenu(String text, Map<String, String> binding) {
        requests << sendMessage.text(text ?: enterMessage ?: 'text.edit.flow.choose.boolean.value')
                .binding(binding)
                .keyboard(inlineKeyboard
                        .button(CHECKED_MARK, EDIT_BOOLEAN_FIELD, [value: true.toString()])
                        .button(CROSS_MARK, EDIT_BOOLEAN_FIELD, [value: false.toString()]))
                .delete()
    }

    void editFlowSelectEntitiesMenu(String text, Map<String, String> binding, Map<Object, Boolean> entities) {
        requests << sendMessage.text(text ?: enterMessage ?: 'text.edit.flow.select.entities')
                .binding(binding)
                .label(chatService.setChatParam(EDIT_FLOW_SELECT_ENTITIES_MESSAGE))
                .keyboard(keyboardService.getSelectedEntitiesKeyboard(entities))
    }

    void updateSelectEntitiesMenu(Map<Object, Boolean> entities) {
        requests << editMessage
                .label(chatService.getChatParam(EDIT_FLOW_SELECT_ENTITIES_MESSAGE))
                .keyboard(keyboardService.getSelectedEntitiesKeyboard(entities))
    }

    void deleteSelectEntitiesFieldMenu() {
        requests << deleteMessage.label(chatService.getChatParam(EDIT_FLOW_SELECT_ENTITIES_MESSAGE))
    }

    void editFlowErrorMessage() {
        requests << withCurrentReplyKeyboard(sendMessage
                .text('text.edit.flow.error.try.again'))
    }

    void editFlowSuccessMessage(String text, boolean clear = false) {
        requests << withCurrentReplyKeyboard(sendMessage
                .text(text ?: (clear
                        ? 'text.edit.flow.cleared.message'
                        : 'text.edit.flow.success.message')))
    }

    void editFieldCancelMessage() {
        requests << withCurrentReplyKeyboard(sendMessage.text('text.edit.field.cancel'))
    }

    void editFlowCurrentMediaMessage(String currentFileId, String beanName) {
        requests << (!isEmpty(currentFileId?.strip())
                ? (context.getBean(beanName) as AbstractMediaRequest)
                    .fileIdentifier(currentFileId)
                    .messageLabel(chatService.setChatParam(EDIT_FLOW_CURRENT_VALUE_MESSAGE_LABEL))
                    .text('text.edit.flow.media.field.current.value')
                    .delete()
                : sendMessage.text('text.edit.flow.no.media.is.assigned')
                    .label(chatService.setChatParam(EDIT_FLOW_CURRENT_VALUE_MESSAGE_LABEL))
                    .delete())
    }

    void mediaUpdatedSuccessMessage(String text, boolean clear = false) {
        requests << withCurrentReplyKeyboard(sendMessage
                .text(text ?: (clear ? 'text.edit.flow.file.was.removed' : 'text.edit.flow.file.was.updated')))
    }

    void updateEditFlowCurrentMediaMessage(String newFileId, String oldFileId) {
        if (newFileId != oldFileId) requests << deleteMessage
                .label(chatService.getChatParam(EDIT_FLOW_CURRENT_VALUE_MESSAGE_LABEL))
    }

    void selectedEntitiesSavedMessage() {
        requests << withCurrentReplyKeyboard(sendMessage
                .text('text.edit.flow.selected.values.persisted'))
    }

    void deleteEntitySuccessMessage(String text) {
        requests << sendMessage.text(text ?: 'text.delete.entity.success.message')
                .delete()
    }

    void entityViewMessage(String finalViewText) {
        if (finalViewText) requests << sendMessage.text(finalViewText).delete()
    }

    void editFlowEntityFieldsSelectMessage(Map<String, String> fields, EditEntityFlowDto editEntityFlowDto) {
        InlineKeyboard keyboard = mapEditEntityFieldsToKeyboardButtons(fields, editEntityFlowDto)
        keyboard.row().button('button.back', LEFT_ARROW, editEntityFlowDto.backPath)
        requests << sendMessage.text(editEntityFlowDto.enterText ?: 'text.edit.flow.select.field.to.edit')
                .binding(editEntityFlowDto.enterTextBinding)
                .keyboard(keyboard)
                .delete()
    }

    static String getDefaultEditFlowMediaEnterMessage() {
        isCurrentValueRemovable() ? 'text.edit.flow.send.me.new.media.or.clear' : 'text.edit.flow.send.me.new.media'
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
        fields.entrySet().collate(editEntityFlowDto.fieldsInRow ?: 3).each { fieldsRow ->
            fieldsRow.each {
                keyboard.button(it.value, editFieldFlowDto
                        .fieldName(it.key)
                        .entityId(editEntityFlowDto.entityId)
                        .entityClass(editEntityFlowDto.entityClass)
                        .redirectParams(getPrefixMappedParams(getEditFLowDtoParams(editEntityFlowDto), FLOW_PARAM_PREFIX))
                        .successPath(EDIT_ENTITY_ENTER))
            }
            keyboard.row()
        }
        keyboard
    }

    private AbstractKeyboardMessage withCurrentReplyKeyboard(AbstractKeyboardMessage message) {
        def currentReplyKeyboard = chatService.currentReplyKeyboard
        currentReplyKeyboard ? message.keyboard(currentReplyKeyboard) : message
    }

    private static String getEditFlowCurrentValueText(String currentValue) {
        !isEmpty(currentValue?.strip()) ? 'text.edit.flow.current.value' : 'text.edit.flow.no.current.value'
    }

    private static String getEditFlowCurrentMediaText(String currentValue) {
        !isEmpty(currentValue?.strip()) ? 'text.edit.flow.media.field.current.value' : 'text.edit.flow.no.media.is.assigned'
    }

    private static String getDefaultEditFlowEnterMessage() {
        isCurrentValueRemovable() ? 'text.edit.flow.enter.new.value.or.clear' : 'text.edit.flow.enter.new.value'
    }

    private static boolean isCurrentValueRemovable() {
        isValueClearingEnabled() && !isEmpty(currentEditFlow.oldValue?.strip())
    }
}
