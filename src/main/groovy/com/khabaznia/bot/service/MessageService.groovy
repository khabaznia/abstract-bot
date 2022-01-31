package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.model.Button
import com.khabaznia.bot.model.Keyboard
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.repository.ButtonRepository
import com.khabaznia.bot.repository.KeyboardRepository
import com.khabaznia.bot.repository.MessageRepository

import com.khabaznia.bot.trait.Configurable
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDateTime

import static com.khabaznia.bot.core.Constants.DELETE_MESSAGES_WEEKS_COUNT

@Slf4j
@Service
class MessageService implements Configurable {

    @Autowired
    private MessageRepository messageRepository
    @Autowired
    private ButtonRepository buttonRepository
    @Autowired
    private KeyboardRepository keyboardRepository

    Message saveMessage(Message message) {
        if (message.label && messageRepository.existsByLabel(message.label)) {
            def existingMessage = getMessage(message.label)
            messageRepository.deleteById(existingMessage.getUid())
        }
        log.trace "Saving message: {}", message
        if (message.keyboard) {
            def keyboard = message.keyboard
            keyboard.buttons.each {
                it.setKeyboard(keyboard)
            }
        }
        messageRepository.save(message)
    }

    Message getMessage(String uniqueId) {
        messageRepository.findById(uniqueId).orElse(messageRepository.findByLabel(uniqueId))
                ?: getByMessageId(uniqueId)
    }

    List<Message> getMessagesForTypeAndChat(MessageType type, String chatCode) {
        def resultList = messageRepository.findByTypeAndChatCode(type, chatCode)
        resultList
    }

    void removeMessagesOfType(MessageType type) {
        messageRepository.findByTypeAndChatCode(type, SessionUtil.currentChat.code)
                .each { messageRepository.delete(it) }
    }

    void removeMessageForUid(String code) {
        messageRepository.deleteById(code)
    }

    void removeMessage(String uniqueId) {
        removeMessageForUid(getMessage(uniqueId).uid)
    }

    Integer deleteExpiredMessages() {
        def messages = messageRepository.findAllWithUpdateDateTimeBefore(expirationDate)
        log.info 'Deleting {} expired messages', messages?.size()
        messageRepository.deleteAll messages
        messages?.size()
    }

    Keyboard getKeyboard(Long id) {
        keyboardRepository.getById(id)
    }

    Integer deleteOrphanedKeyboards() {
        def keyboards = keyboardRepository.findAllOrphaned()
        log.info 'Deleting {} orphaned messages', keyboards?.size()
        keyboardRepository.deleteAll keyboards
        keyboards?.size()
    }

    void removeButton(Button button) {
        button.setKeyboard(null)
        buttonRepository.saveAndFlush(button)
        buttonRepository.delete(button)
    }

    Button getButton(String id) {
        buttonRepository.getById(id)
    }

    Button saveButton(Button button) {
        buttonRepository.saveAndFlush(button)
    }

    private Message getByMessageId(String uniqueId) {
        try {
            return messageRepository.findByMessageId(Integer.parseInt(uniqueId))
        } catch (NumberFormatException e) {
            log.trace "Can't find message with message id - {}. Possibly it's uid or label, ant it's message was deleted", uniqueId
        }
        return null
    }

    private Date getExpirationDate() {
        LocalDateTime.now().minusWeeks(getLongConfig(DELETE_MESSAGES_WEEKS_COUNT)).toDate()
    }
}
