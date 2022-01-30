package com.khabaznia.bot.core

class Constants {
    public static final String PREVIOUS_PATH_DELIMITER = '|'
    public static final String COMMANDS_DELIMITER = '/'
    public static final String PARAMETERS_PREFIX = '::'
    public static final String PARAMETERS_DELIMITER = ';'
    public static final String PARAMETER_KEY_VALUE_DELIMITER = '='
    public static final String ENCRYPTED_PATH_PREFIX = '/encrypted'

    public static final String CONFIGS_DELIMITER = ','
    public static final String CONFIG_KEYS_PREFIX = 'name.'
    public static final String SWITCHABLE_CONFIG_KEYS_PREFIX = 'switchable.'

    public static final String ADMIN_CHAT_ID = 'bot.admin.chat.id'
    public static final String LOGGING_CHAT_ID = 'bot.logging.chat.id'
    public static final String DEBUG_LOGGING = 'switchable.config.debug.logging.enabled'
    public static final String DUPLICATE_WARN_TO_ADMIN = 'switchable.config.duplicate.warn.logging.to.admin'
    public static final String BLOCK_DUPLICATE_REQUESTS = 'switchable.config.block.duplicate.requests'


    public static final String DELETE_MESSAGES_WEEKS_COUNT = 'delete.paths.weeks.count'
    public static final String DELETE_PREVIOUS_INLINE_KEYBOARDS = 'switchable.config.delete.previous.inline.keyboards'

    public static final String REQUESTS_LIMIT_PER_MINUTE_IN_SINGLE_CHAT = 'requests.limit.per.minute.in.single.chat'
    public static final String REQUESTS_DELAY_LIMIT_IN_SINGLE_CHAT = 'requests.delay.limit.in.single.chat'
    public static final String CHAT_INACTIVE_MINUTES = 'chat.inactive.minutes'

}
