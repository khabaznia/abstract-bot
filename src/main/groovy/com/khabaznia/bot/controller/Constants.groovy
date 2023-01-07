package com.khabaznia.bot.controller

import static com.khabaznia.bot.controller.Constants.ADMIN_CONTROLLER.SET_LOGGING
import static com.khabaznia.bot.controller.Constants.LANG_CONTROLLER.CHANGE_LANG_AS_COMMAND
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
        public static final String UPDATE_MESSAGE = 'updateMessage'
        public static final String UPDATE_ID = 'updateId'
        public static final String IS_UPDATE_PROCESSED = 'isUpdateProcessed'
    }

    static final class COMMON {
        public static final String DEFAULT = '/default'
        public static final String IMAGE_CONTROLLER = '/process_image'
        public static final String AUDIO_CONTROLLER = '/process_audio'
        public static final String VIDEO_CONTROLLER = '/process_video'
        public static final String ANIMATION_CONTROLLER = '/process_animation'
        public static final String DOCUMENT_CONTROLLER = '/process_document'
        public static final String ANY_STRING = '/any_string'
        public static final String START = '/start'
        public static final String TO_MAIN = 'path.main'
        public static final String DELETE_REPLY_KEYBOARD = '/deleteReplyKeyboard'
        public static final String SETTINGS = 'path.settings.display'
        public static final String GENERAL_SUBSCRIPTION_NOTIFY_ALL = 'path.general.notify.all'
    }

    static final class CONFIRMATION_CONTROLLER {
        public static final String CONFIRMATION_ACTION = '/confirmation_action'
        public static final String CONFIRMATION_MENU = '/confirmation_menu'
    }

    static final class GROUP_CHATS_ACTIONS_CONTROLLER {
        public static final String BOT_CHAT_MEMBER_STATUS_UPDATED = '/bot_chat_member_status_updated'
        public static final String USER_CHAT_MEMBER_STATUS_UPDATED = '/user_chat_member_status_updated'
        public static final String PROCESS_JOIN_REQUEST = '/process_join_request'
        public static final String PROCESS_SERVICE_MESSAGE = '/process_service_messages'
        public static final String PROCESS_GROUP_CHAT_CREATED = '/process_group_chat_created'
        public static final String PROCESS_NEW_CHAT_MEMBERS = '/process_new_chat_members'
        public static final String PROCESS_USER_LEFT_CHAT = '/process_user_left_chat'
        public static final String PROCESS_MIGRATE_TO_CHAT_ID = '/process_migrate_to_chat_id'
    }

    static final class LANG_CONTROLLER {
        static final Map<String, String> LANG_EMOJI = [en: LANG_EN, ru: LANG_RU, uk: LANG_UK]

        public static final String DISPLAY_CHANGE_LANG = 'path.lang.settings.display'
        public static final String CHANGE_LANG = 'path.change.lang'
        public static final String CHANGE_LANG_AS_COMMAND = '/change_lang'
        public static final String CHANGE_LANG_RU = 'path.change.lang.ru'
        public static final String CHANGE_LANG_EN = 'path.change.lang.en'
        public static final String CHANGE_LANG_UK = 'path.change.lang.uk'
    }

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
        public static final def langCommand = [(CHANGE_LANG_AS_COMMAND): 'path.lang.settings.display']

        public static final def adminAllChatsCommands = [(SET_LOGGING)      : 'command.set.logging']
    }
}
