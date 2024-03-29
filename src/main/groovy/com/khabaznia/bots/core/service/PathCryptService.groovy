package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.exception.BotException
import com.khabaznia.bots.core.model.EncryptedPath
import com.khabaznia.bots.core.repository.EncryptedPathRepository
import com.khabaznia.bots.core.trait.Configurable
import com.khabaznia.bots.core.trait.Loggable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDateTime

import static com.khabaznia.bots.core.routing.Constants.ENCRYPTED_PATH_SALT
import static com.khabaznia.bots.core.routing.Constants.EXPIRED_MESSAGES_IN_DAYS_COUNT

@Slf4j
@Service
class PathCryptService implements Configurable, Loggable {

    @Autowired
    private EncryptedPathRepository repository

    static boolean isEncrypted(String path) {
        path?.startsWith(ENCRYPTED_PATH_SALT)
    }

    String encryptPath(String path) {
        def key = ENCRYPTED_PATH_SALT + path.md5()
        repository.existsById(key)
                ? key
                : repository.save(new EncryptedPath(key: key, value: path)).key
    }

    String getDecryptedPath(String path) {
        if (!isEncrypted(path)) return path
        def encryptedPath = repository.findById(path)
        encryptedPath.ifPresent { repository.save(it) }
        encryptedPath.orElseThrow(
                () -> new BotException("Path ${path.bold()} can\'t be decrypted. \nSeems, it was deleted due to timeout")
        ).value
    }

    void deletePathsOfMessage(String messageId) {
        repository.findByValueContaining(messageId)
                .each { repository.delete(it) }
    }

    Integer removeExpiredPaths() {
        def paths = repository.findAllWithUpdateDateTimeBefore(expirationDate)
        repository.deleteAll paths
        paths?.size()
    }

    private Date getExpirationDate() {
        def expDate = LocalDateTime.now().minusDays(getLongConfig(EXPIRED_MESSAGES_IN_DAYS_COUNT)).toDate()
        expDate
    }
}
