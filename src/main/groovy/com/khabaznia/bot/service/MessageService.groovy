package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.model.Button
import com.khabaznia.bot.model.Keyboard
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.repository.ButtonRepository
import com.khabaznia.bot.repository.KeyboardRepository
import com.khabaznia.bot.repository.MessageRepository
import com.khabaznia.bot.repository.RowRepository
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
    private RowRepository rowRepository

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
        log.trace "Saving message: {}", message
        messageRepository.saveAndFlush(message)
    }

    Keyboard saveKeyboard(Keyboard keyboard) {
        keyboardRepository.saveAndFlush(keyboard)
    }

    Message getMessageForCode(Long code) {
        messageRepository.findById(code).orElse(null)
    }

    void removeMessageForCode(Long code) {
        messageRepository.deleteById(code)
    }

    void removeButton(String id) {
        buttonRepository.deleteById(id)
    }

    Button getButton(String id) {
        buttonRepository.getById(id)
    }

    Button saveButton(Button button) {
        buttonRepository.save(button)
    }

    Keyboard getKeyboard(Long id) {
        keyboardRepository.getById(id)
    }

    void deleteExpiredMessages() {
        def messages = messageRepository.findAllWithUpdateDateTimeAfter(expirationDate)
        log.trace 'Deleting {} expired encrypted messages', messages?.size()
        messageRepository.deleteAll messages
    }

    private Date getExpirationDate() {
        LocalDateTime.now().minusWeeks(getConfig(DELETE_MESSAGES_WEEKS_COUNT) as long).toDate()
    }
}
