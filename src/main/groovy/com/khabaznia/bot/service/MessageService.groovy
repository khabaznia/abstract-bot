package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.model.Button
import com.khabaznia.bot.model.Keyboard
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.repository.ButtonRepository
import com.khabaznia.bot.repository.EncryptedPathRepository
import com.khabaznia.bot.repository.KeyboardRepository
import com.khabaznia.bot.repository.MessageRepository

import com.khabaznia.bot.trait.Configured
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDateTime

import static com.khabaznia.bot.core.Constants.DELETE_MESSAGES_WEEKS_COUNT

@Slf4j
@Service
class MessageService implements Configured {

    @Autowired
    private MessageRepository messageRepository
    @Autowired
    private ButtonRepository buttonRepository
    @Autowired
    private KeyboardRepository keyboardRepository
    @Autowired
    private EncryptedPathRepository encryptedPathRepository

    List<Message> getMessagesForTypeAndChat(MessageType type, String chatCode) {
        def resultList = messageRepository.findByTypeAndChatCode(type, chatCode)
        log.trace "List for type: {}", resultList
        resultList
    }

    void removeMessagesOfType(MessageType type) {
        messageRepository.findByTypeAndChatCode(type, SessionUtil.currentChat.code)
                .each { messageRepository.delete(it) }
    }

    Message saveMessage(Message message) {
        if (message.label && messageRepository.existsByLabel(message.label)) {
            def existingMessage = getMessageForLabel(message.label)
            messageRepository.deleteById(existingMessage.getCode())
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

    Message getMessageForCode(Long code) {
        messageRepository.findById(code).orElse(null)
    }

    Message getMessageForLabel(String label) {
        messageRepository.findByLabel(label)
    }

    Message getMessageForMessageId(Integer messageId) {
        messageRepository.findByMessageId(messageId)
    }

    void removeMessageForCode(Long code) {
        messageRepository.deleteById(code)
    }

    void removeButton(Button button) {
        button.setKeyboard(null)
        buttonRepository.saveAndFlush(button)
        buttonRepository.delete(button)
    }

    private void deleteRelatedPaths(String buttonOrMessageCode) {
        def relatedPaths = encryptedPathRepository.findByValueContaining(buttonOrMessageCode)
        encryptedPathRepository.deleteAll(relatedPaths)
    }

    Button getButton(String id) {
        buttonRepository.getById(id)
    }

    Button saveButton(Button button) {
        buttonRepository.saveAndFlush(button)
    }

    Keyboard getKeyboard(Long id) {
        keyboardRepository.getById(id)
    }

    Integer deleteExpiredMessages() {
        def messages = messageRepository.findAllWithUpdateDateTimeBefore(expirationDate)
        log.info 'Deleting {} expired messages', messages?.size()
        messageRepository.deleteAll messages
        messages?.size()
    }

    Integer deleteOrphanedKeyboards() {
        def keyboards = keyboardRepository.findAllOrphaned()
        log.info'Deleting {} orphaned messages', keyboards?.size()
        keyboardRepository.deleteAll keyboards
        keyboards?.size()
    }

    private Date getExpirationDate() {
        LocalDateTime.now().minusWeeks(getConfig(DELETE_MESSAGES_WEEKS_COUNT) as long).toDate()
    }
}
