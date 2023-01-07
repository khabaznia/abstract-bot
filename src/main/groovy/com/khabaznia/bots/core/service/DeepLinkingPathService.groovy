package com.khabaznia.bots.core.service

import com.khabaznia.bots.core.trait.Configurable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.COMMON.START
import static com.khabaznia.bots.core.routing.Constants.BOT_NAME
import static com.khabaznia.bots.core.routing.Constants.ENCRYPTED_PATH_SALT

@Slf4j
@Component
class DeepLinkingPathService implements Configurable {

    @Autowired
    private PathCryptService pathCryptService

    private static final String DEEP_LINKING_PARAMS_DELIMITER = ' '

    static String getDeepLinkingPath(String initialPath) {
        initialPath?.startsWith(START.concat(DEEP_LINKING_PARAMS_DELIMITER))
                ? ENCRYPTED_PATH_SALT.concat(initialPath.tokenize(DEEP_LINKING_PARAMS_DELIMITER)[1])
                : initialPath
    }

    String generateDeepLinkPath(String path, Map<String, String> params) {
        def deepLinkPathPrefix = "https://t.me/${getConfig(BOT_NAME)}?${START.substring(1)}="
        def finalPath = path.addParams(params)
        def resultLink = deepLinkPathPrefix.concat getEncryptedPathWithoutSalt(finalPath)
        log.debug 'Generated deep link for path: {} -> {}', finalPath, resultLink
        resultLink
    }

    private String getEncryptedPathWithoutSalt(String path) {
        pathCryptService.encryptPath(path).substring(ENCRYPTED_PATH_SALT.size())
    }
}
