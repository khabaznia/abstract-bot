package com.khabaznia.bot.controller

final class Constants {

    static final class BUTTON_PARAMETERS {
        public static final String MESSAGE_CODE = 'messageCode'
        public static final String ONE_TIME_KEYBOARD = 'oneTime'
        public static final String BUTTON_ID = 'buttonId'
    }

    static final class COMMON {
        static final String DEFAULT = '/default'
        static final String START = '/start'
        static final String MENU = '/menu'
    }

    static final class LANG_CONTROLLER {
        static final String DISPLAY_LANG = '/display'
        static final String CHANGE_LANG = '/change'

    }

    static final class SETTINGS_CONTROLLER {
        static final String CHANGE_LANG_SETTING = '/settings_display'
        static final String BACK_BUTTON = '/settings_back'
    }

    static final class ADMIN_CONTROLLER {
        static final String ADMIN_START = '/admin_start'
        static final String FEATURES_LIST = '/features'
        static final String SWITCH_FEATURE = '/switch'
    }

    static final class USER_CONTROLLER {
        static final String USER_START = '/user_start'
    }

    static final class EXAMPLE_CONTROLLER {
        static final String ACTION_ONE = 'path.action.one'
        static final String ACTION_TWO = 'path.action_two'
        static final String GET_INLINE = 'path.get.inline'
        static final String REPLY_KEYBOARD = '/get_reply'
        static final String NEXT = '/next'
        static final String YES_ACTION = '/yes'
        static final String NO_ACTION = '/no'
        static final String BACK_ACTION = '/back'
    }
}
