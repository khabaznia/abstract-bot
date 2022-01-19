package com.khabaznia.bot.service

import com.khabaznia.bot.enums.MessageType
import com.khabaznia.bot.model.Message
import com.khabaznia.bot.repository.MessageRepository
import com.khabaznia.bot.util.SessionUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.util.concurrent.atomic.AtomicLong

@Slf4j
@Service
class MessageService {

    @Autowired
    MessageRepository messageRepository

    List<Message> getMessagesForTypeAndChat(MessageType type, String chatCode) {
        def resultList = messageRepository.findByTypeAndChatCode(type, chatCode)
        log.trace "List for type: {}", resultList
        resultList
    }

    void removeMessagesOfType(MessageType type) {
        messageRepository.findByTypeAndChatCode(type, SessionUtil.currentChat.code)
                .each { messageRepository.delete(it) }
    }

    void saveMessage(Message message) {
        log.trace "Saving message: {}", message
        messageRepository.save(message)
    }

    Message getEmptyMessage() {
        log.trace "Saving empty message: {}"
        messageRepository.save(new Message(text: 'placeholder', messageId: 1))
    }

    Message getMessageForCode (Long code){
        messageRepository.findById(code).orElse(null)
    }

    void removeMessageForCode (Long code){
        messageRepository.deleteById(code)
    }
}
