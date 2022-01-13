package com.khabaznia.bot.trait

import com.khabaznia.bot.model.Config
import com.khabaznia.bot.repository.ConfigRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import static com.khabaznia.bot.core.Constants.CONFIGS_DELIMITER
import static com.khabaznia.bot.core.Constants.CONFIG_KEYS_PREFIX

@Slf4j
trait Configurable {

    @Autowired
    private Environment env
    @Autowired
    private ConfigRepository configRepository

    String getConfig(final String key) {
        getConfigParam(key)
    }

    Collection<String> getConfigs(final String key) {
        getConfigParam(key)?.split(CONFIGS_DELIMITER)?.collect()
    }

    Boolean isEnabled(final String key) {
        Boolean.valueOf(getConfigParam(key))
    }

    private String getConfigParam(final String key) {
        log.trace 'Get config param for key -> {}', key
        configRepository.existsById(key)
                ? configRepository.getById(key).value
                : getPropertyFromFileAndSave(key)
    }

    private String getPropertyFromFileAndSave(final String key) {
        log.trace 'Not found in database. Try to resolve key {} from properties file', key
        def property = new Config(key: key, value: env.getProperty(key), isSwitchable: false, name: CONFIG_KEYS_PREFIX + key)
        configRepository.save(property)
        property.value
    }
}