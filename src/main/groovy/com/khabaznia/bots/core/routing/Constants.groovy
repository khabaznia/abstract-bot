package com.khabaznia.bots.core.routing

class Constants {
    public static final String PREVIOUS_PATH_DELIMITER = '|'
    public static final String PARAMETERS_PREFIX = '?'
    public static final String PARAMETERS_DELIMITER = ';'
    public static final String PARAMETER_KEY_VALUE_DELIMITER = '='
    public static final String ENCRYPTED_PATH_SALT = '/e2918cr'
    public static final String SPECIFIC_ROLE_DELIMITER = '_'
    public static final String BOT_NAME_DELIMITER = '@'

    public static final String CONFIGS_DELIMITER = ','
    public static final String CONFIG_KEYS_PREFIX = 'name.'
    public static final String SWITCHABLE_CONFIG_KEYS_PREFIX = 'switchable.'
    public static final String ENV_ONLY_CONFIG_KEYS_PREFIX = 'env.only.'

    public static final String DEFAULT_LOCALE = 'env.only.locale.default'
    public static final String AVAILABLE_LOCALES = 'env.only.locale.available'
    public static final String ADMIN_CHAT_ID = 'env.only.bot.admin.chat.id'
    public static final String EXPIRED_MESSAGES_IN_DAYS_COUNT = 'env.only.queue.expired.paths.in.days.count'
    public static final String CHAT_INACTIVE_MINUTES = 'env.only.queue.chat.inactive.minutes'
    public static final String BLOCK_DUPLICATE_REQUESTS = 'env.only.block.duplicate.requests'
    public static final String RESTRICTED_MODE = 'env.only.bot.restricted.mode'
    public static final String RESTRICTED_MODE_USERS = 'env.only.bot.restricted.mode.users'
    public static final String REQUESTS_DELAY_LIMIT_IN_SINGLE_CHAT = 'env.only.queue.requests.delay.limit.in.single.chat'
    public static final String EXECUTING_IN_QUEUE_ONLY_FOR_GROUP_CHATS = 'env.only.queue.only.for.group.chat'
    public static final String COUNT_OF_RETRIES_FOR_TELEGRAM_API_REQUESTS = 'env.only.integration.telegram.count.of.retries'
    public static final String CHECK_MESSAGES_IN_LOGGING_CHAT = 'env.only.check.messages.in.logging.chat'
    public static final String IGNORE_CHAT_ACTIONS_FOR_GROUPS = 'env.only.ignore.chat.actions.for.group'

    public static final String DEBUG_LOGGING = 'switchable.config.debug.logging.enabled'
    public static final String DUPLICATE_WARN_TO_ADMIN = 'switchable.config.duplicate.warn.logging.to.admin'
    public static final String EXECUTE_REQUESTS_IN_QUEUE = 'switchable.config.execute.requests.in.queue'
    public static final String DELETE_PREVIOUS_INLINE_KEYBOARDS = 'switchable.config.delete.previous.inline.keyboards'
    public static final String USE_ONLY_DEFAULT_LANGUAGE = 'switchable.config.use.only.default.language'
    public static final String DEFAULT_LANGUAGE_FOR_GROUPS = 'switchable.config.default.language.for.groups'

    public static final String INTEGRATION_RETYER_PERIOD = 'env.only.integration.retryer.period'
    public static final String INTEGRATION_RETYER_MAX_ATTEMPTS = 'env.only.integration.retryer.max.attempts'
    public static final String INTEGRATION_RETYER_MAX_PERIOD = 'env.only.integration.retryer.max.period'

    public static final String BOT_NAME = 'env.only.bot.name'
}
