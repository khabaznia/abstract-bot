package com.khabaznia.bot.service

import com.khabaznia.bot.model.EncryptedPath
import com.khabaznia.bot.repository.EncryptedPathRepository
import com.khabaznia.bot.trait.Configured
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDateTime

import static com.khabaznia.bot.core.Constants.ENCRYPTED_PATH_PREFIX
import static com.khabaznia.bot.core.Constants.DELETE_PATHS_WEEKS_COUNT

@Slf4j
@Service
class PathCryptService implements Configured {

    @Autowired
    EncryptedPathRepository repository

    static boolean isEncrypted(final String path) {
        path?.startsWith(ENCRYPTED_PATH_PREFIX)
    }

    String encryptPath(final String path) {
        def key = ENCRYPTED_PATH_PREFIX + path.md5()
        repository.existsById(key)
                ? key
                : repository.save(new EncryptedPath(key: key, value: path)).key
    }

    String decryptPath(final String path) {
        log.trace 'Decryption of path -> {}', {}
        def encryptedPath = repository.findById(path)
        encryptedPath.ifPresent { repository.save(it) }
        encryptedPath.get().value
    }

    void deleteExpiredPaths() {
        def paths = repository.findAllWithUpdateDateTimeAfter(expirationDate)
        log.trace 'Deleting {} expired encrypted paths', paths?.size()
        repository.deleteAll paths
    }

    private Date getExpirationDate() {
        LocalDateTime.now().minusWeeks(getConfig(DELETE_PATHS_WEEKS_COUNT) as long).toDate()
    }
}
