package com.khabaznia.bot.trait

import com.khabaznia.bot.model.Config
import com.khabaznia.bot.repository.ConfigRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import static com.khabaznia.bot.core.Constants.CONFIGS_DELIMITER
import static com.khabaznia.bot.core.Constants.CONFIG_KEYS_PREFIX
import static com.khabaznia.bot.core.Constants.SWITCHABLE_CONFIG_KEYS_PREFIX
import static com.khabaznia.bot.core.Constants.ENV_ONLY_CONFIG_KEYS_PREFIX

@Slf4j
trait Configurable {

    @Autowired
    private Environment env
    @Autowired
    private ConfigRepository configRepository

    String getConfig(String key) {
        getConfigParam(key)
    }

    Long getLongConfig(String key) {
        Long.parseLong(getConfigParam(key))
    }

    Collection<String> getConfigs(String key) {
        getConfigParam(key)?.tokenize(CONFIGS_DELIMITER)
    }

    Boolean isEnabled(String key) {
        Boolean.valueOf(getConfigParam(key))
    }

    void setConfig(String key, String value) {
        def config = configRepository.getById(key)
        config.setValue(value)
        configRepository.save(config)
    }

    List<Config> getSwitchableConfigs() {
        configRepository.findByKeyStartsWith(SWITCHABLE_CONFIG_KEYS_PREFIX)
    }

    private String getConfigParam(String key) {
        key.startsWith(ENV_ONLY_CONFIG_KEYS_PREFIX)
                ? getEnvProperty(key)
                : (configRepository.existsById(key)
                    ? configRepository.getById(key).value
                    : getPropertyFromFileAndSave(key))
    }

    private String getPropertyFromFileAndSave(String key) {
        log.trace 'Not found in database. Try to resolve key {} from properties file', key
        def property = new Config(key: key, value: getEnvProperty(key), name: CONFIG_KEYS_PREFIX + key)
        configRepository.save(property)
        property.value
    }

    private String getEnvProperty(String key) {
        env.getProperty(key)
    }
}