package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.meta.Emoji
import com.khabaznia.bots.core.meta.container.DefaultRequestContainer
import com.khabaznia.bots.core.meta.keyboard.impl.InlineKeyboard
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.trait.BaseRequests
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.EDIT_BOOLEAN_FIELD
import static com.khabaznia.bots.core.controller.Constants.EDIT_FIELD_CONTROLLER.EDIT_LOCALIZED_FIELD_MENU
import static com.khabaznia.bots.core.controller.Constants.LANG_CONTROLLER.LANG_EMOJI
import static com.khabaznia.bots.core.flow.service.EditFlowService.enterMessage
import static com.khabaznia.bots.core.routing.Constants.AVAILABLE_LOCALES
import static com.khabaznia.bots.core.util.SessionUtil.currentUser
import static org.apache.groovy.parser.antlr4.util.StringUtils.isEmpty

@Slf4j
@Component
class BotMessagesService implements BaseRequests, Loggable, Configurable {

    private static final String EDIT_FLOW_UPDATE_MESSAGE = 'editFlowChooseLangMessage'
    private static final String EDIT_FLOW_CURRENT_VALUE_MESSAGE = 'editFlowCurrentValueMessage'
    private static final Map<String, String> BOOLEAN_VALUES_MAPPING = [(Boolean.TRUE.toString()): Emoji.CHECKED_MARK, (Boolean.FALSE.toString()): Emoji.CROSS_MARK]
    @Autowired
    private DefaultRequestContainer requests

    BaseRequest sendExceptionMessage(String message, Map<String, String> binding) {
        sendMessage.text(message).binding(binding)
    }

    void editFlowEnterMessage(String text, Map<String, String> binding) {
        requests << sendMessage.text(text ?: enterMessage)
                .binding(binding)
                .delete()
    }

    void editFlowCurrentValueMessage(String currentValue, boolean isBooleanValue = false) {
        requests << sendMessage.text(getEditFlowCurrentValueText(currentValue))
                .binding([value: getEditFlowMappedBooleanValue(isBooleanValue, currentValue)])
                .label(currentUser.code.concat(EDIT_FLOW_CURRENT_VALUE_MESSAGE))
    }

    void updateEditFlowCurrentValueMessage(String currentValue, boolean isBooleanValue = false) {
        requests << editMessage.text(getEditFlowCurrentValueText(currentValue))
                .binding([value: getEditFlowMappedBooleanValue(isBooleanValue, currentValue)])
                .label(currentUser.code.concat(EDIT_FLOW_CURRENT_VALUE_MESSAGE))
                .delete()
    }

    void editFlowChooseLangMessage() {
        requests << sendMessage.text('text.edit.flow.choose.lang')
                .keyboard(setLocaleButtons(inlineKeyboard))
                .delete()
    }

    void updateEditFlowChooseLangMenu(String lang) {
        requests << sendMessage.text('text.edit.flow.chosen.lang')
                .binding([lang: LANG_EMOJI[lang]])
                .label(currentUser.code.concat(EDIT_FLOW_UPDATE_MESSAGE))
                .delete()

    }

    void deleteEditFlowChooseLangMessage() {
        requests << deleteMessage.label(currentUser.code.concat(EDIT_FLOW_UPDATE_MESSAGE))
    }

    void editBooleanFieldMenu() {
        requests << sendMessage.text('text.edit.flow.choose.boolean.value')
                .keyboard(inlineKeyboard
                        .button(Emoji.CHECKED_MARK, EDIT_BOOLEAN_FIELD, [value: true.toString()])
                        .button(Emoji.CROSS_MARK, EDIT_BOOLEAN_FIELD, [value: false.toString()]))
                .delete()
    }

    void editFlowSuccessMessage(String text) {
        requests << sendMessage.text(text ?: 'text.edit.flow.success.message')
                .delete()
    }

    private InlineKeyboard setLocaleButtons(InlineKeyboard inlineKeyboard) {
        getConfigs(AVAILABLE_LOCALES)
                .collectEntries { [(it): LANG_EMOJI[it]] }
                .each {
                    inlineKeyboard.button(
                            it.value.toString(),
                            EDIT_LOCALIZED_FIELD_MENU,
                            [lang: it.key.toString()])
                }
        inlineKeyboard
    }

    private static String getEditFlowCurrentValueText(String currentValue) {
        !isEmpty(currentValue?.strip()) ? 'text.edit.flow.current.value' : 'text.edit.flow.no.current.value'
    }

    private static String getEditFlowMappedBooleanValue(boolean isBooleanValue, String currentValue) {
        isBooleanValue ? BOOLEAN_VALUES_MAPPING.get(currentValue) : currentValue
    }
}
