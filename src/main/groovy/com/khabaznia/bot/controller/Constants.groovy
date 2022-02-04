package com.khabaznia.bot.controller

import static com.khabaznia.bot.meta.Emoji.LANG_EN
import static com.khabaznia.bot.meta.Emoji.LANG_RU
import static com.khabaznia.bot.meta.Emoji.LANG_UK

final class Constants {

    static final class BUTTON_PARAMETERS {
        public static final String MESSAGE_UID = 'messageUid'
        public static final String ONE_TIME_KEYBOARD = 'oneTime'
        public static final String BUTTON_ID = 'buttonId'
        public static final String UNLIMITED_CALL = 'unlimitedCall'
    }

    static final class SESSION_ATTRIBUTES {
        public static final String UPDATE_MESSAGE_ATTR = 'updateMessage'
        public static final String UPDATE_ID_ATTR = 'updateId'
        public static final String IS_UPDATE_PROCESSED_ATTR = 'isUpdateProcessed'
    }

    static final class COMMON {
        static final String DEFAULT = '/default'
        static final String IMAGE_CONTROLLER = '/process_image'
        static final String AUDIO_CONTROLLER = '/process_audio'
        static final String VIDEO_CONTROLLER = '/process_video'
        static final String ANY_STRING = '/any_string'
        static final String START = '/start'
        static final String TO_MAIN = 'path.main'
        static final String SETTINGS = 'path.settings.display'
    }

    static final class LANG_CONTROLLER {
        static final Map<String, String> LANG_EMOJI = [en: LANG_EN, ru: LANG_RU, uk: LANG_UK]

        static final String DISPLAY_CHANGE_LANG = 'path.lang.settings.display'
        static final String CHANGE_LANG = 'path.change.lang'
        static final String CHANGE_LANG_RU = 'path.change.lang.ru'
        static final String CHANGE_LANG_EN = 'path.change.lang.en'
        static final String CHANGE_LANG_UK = 'path.change.lang.uk'
    }

    static final class ADMIN_CONTROLLER {
        static final String ADMIN_START = '/admin_start'
        static final String ADMIN_TO_MAIN = '/admin_main'
        static final String FEATURES_LIST = 'path.features.list'
        static final String SWITCH_FEATURE = '/switch_feature'
    }

    static final class USER_CONTROLLER {
        static final String USER_START = '/user_start'
        static final String USER_TO_MAIN = '/user_main'
    }

    static final class EXAMPLE_CONTROLLER {
        static final String MODIFIABLE_INLINE_KEYBOARD = 'path.example.action.one'
        static final String EDITING_MESSAGES = 'path.example.action_two'
        static final String INTEGRATION_TESTS_KEYBOARD = 'path.example.get.inline'
        static final String EXAMPLE = 'path.example'
        static final String NEXT = '/next'
        static final String AFTER_NEXT = '/after_next'
        static final String YES_ACTION = '/yes'
        static final String NO_ACTION = '/no'
        static final String BACK_ACTION = '/back'
    }
}
