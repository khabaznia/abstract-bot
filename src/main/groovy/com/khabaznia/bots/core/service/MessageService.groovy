package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.enums.MessageFeature
import com.khabaznia.bots.core.model.Button
import com.khabaznia.bots.core.model.Keyboard
import com.khabaznia.bots.core.model.Message
import com.khabaznia.bots.core.repository.ButtonRepository
import com.khabaznia.bots.core.repository.KeyboardRepository
import com.khabaznia.bots.core.repository.MessageRepository
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

import java.time.LocalDateTime

import static com.khabaznia.bots.core.routing.Constants.EXPIRED_MESSAGES_IN_DAYS_COUNT

@Slf4j
@Service
class MessageService implements Configurable {

    @Autowired
    private MessageRepository messageRepository
    @Autowired
    private ButtonRepository buttonRepository
    @Autowired
    private KeyboardRepository keyboardRepository
    @Autowired
    private PathCryptService pathCryptService

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

    List<Message> getMessagesForUpdate(Integer updateId) {
        messageRepository.findAllUnsentByChatCode(SessionUtil.currentChat.code, updateId)
    }

    Message getMessage(String uniqueId) {
        messageRepository.findById(uniqueId).orElse(messageRepository.findByLabel(uniqueId))
                ?: getByMessageId(uniqueId)
    }

    List<Message> getMessagesForTypeExcludingUpdateId(MessageFeature type, String chatCode, Integer updateId) {
        messageRepository.findByTypeAndChatCodeThatNotOfUpdateId(type, chatCode, updateId)
    }

    void removeMessagesOfTypeExcludingUpdateId(MessageFeature type, Integer updateId) {
        messageRepository.findByTypeAndChatCodeThatNotOfUpdateId(type, SessionUtil.currentChat?.code, updateId)
                .each { removeMessageForUid(it.uid) }
    }

    void removeMessageForUid(String uid) {
        log.trace "Removing message for uid: {}", uid
        try {
            messageRepository.deleteById(uid)
            pathCryptService.deletePathsOfMessage(uid)
        } catch (EmptyResultDataAccessException e) {
            log.warn "Error during deleting message and related buttons. Seems, was deleted by another thread. Uid - {}", uid
        }
    }

    void removeMessage(String uniqueId) {
        def message = getMessage(uniqueId)
        if (message) {
            removeMessageForUid(message.uid)
        }
    }

    Integer removeExpiredMessages() {
        def messages = messageRepository.findAllWithUpdateDateTimeBefore(expirationDate)
        log.debug 'Deleting {} expired messages', messages?.size()
        messageRepository.deleteAll messages
        messages?.size()
    }

    Keyboard getKeyboard(Long id) {
        keyboardRepository.getById(id)
    }

    Integer removeOrphanedKeyboards() {
        def keyboards = keyboardRepository.findAllOrphaned()
        log.debug 'Deleting {} orphaned messages', keyboards?.size()
        keyboardRepository.deleteAll keyboards
        keyboards?.size()
    }

    void removeButton(Button button) {
        log.trace "Removing button {}", button.key
        button.setKeyboard(null)
        buttonRepository.saveAndFlush(button)
        buttonRepository.delete(button)
    }

    Button getButton(String id) {
        buttonRepository.getById(id)
    }

    Button saveButton(Button button) {
        log.trace "Saving button {}", button.key
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
        LocalDateTime.now().minusDays(getLongConfig(EXPIRED_MESSAGES_IN_DAYS_COUNT)).toDate()
    }
}
