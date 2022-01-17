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

    AtomicLong nextId

    @Autowired
    MessageRepository messageRepository

    @PostConstruct
    void initIdGenerator(){
        nextId = new AtomicLong()
    }

    List<Message> getMessagesForType(MessageType type) {
        def resultList = messageRepository.findByAndPaymentId(type, SessionUtil.currentChat.code)
        log.trace "List for type: {}", resultList
        resultList
    }

    void removeMessagesOfType(MessageType type) {
        messageRepository.findByAndPaymentId(type, SessionUtil.currentChat.code)
                .each { messageRepository.delete(it) }
    }

    void saveMessage(Message message, Long code) {
        message.setCode(code)
        log.trace "Saving message: {}", message
        messageRepository.save(message)
    }

    Long getUniqueCode() {
        long value = nextId.getAndIncrement()
        while (messageRepository.findById(value)) {
            value = nextId.getAndIncrement()
        }
        log.trace "Unique id for message: {}", value
        value
    }
}
