package com.khabaznia.bots.common

import static com.khabaznia.bots.common.Constants.ADMIN_CONTROLLER.SET_LOGGING

final class Constants {

    static final class ADMIN_CONTROLLER {
        public static final String ADMIN_START = '/admin_start'
        public static final String ADMIN_TO_MAIN = '/admin_main'
        public static final String FEATURES_LIST = 'path.features.list'
        public static final String SWITCH_FEATURE = '/switch_feature'
        public static final String SET_LOGGING = '/set_logging'
    }

    static final class USER_CONTROLLER {
        public static final String USER_START = '/user_start'
        public static final String USER_TO_MAIN = '/user_main'
    }

    static final class MAPPINGS {
        public static final def adminAllChatsCommands = [(SET_LOGGING): 'command.set.logging']
    }
}
