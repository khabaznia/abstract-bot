package com.khabaznia.bots.common.util

import com.khabaznia.bots.core.enums.Scope
import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.service.BotCommandService
import com.khabaznia.bots.core.trait.BaseRequests
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.khabaznia.bots.common.Constants.MAPPINGS.adminAllChatsCommands

@Slf4j
@Component
class CommandsUtil implements BaseRequests {

    @Autowired
    private BotCommandService botCommandService

    List<BaseRequest> getAdminAllGroupChatsCommands() {
        botCommandService.convertToSetMyCommands([(botCommandScope.type(Scope.ALL_CHAT_ADMINISTRATORS)): adminAllChatsCommands])
    }
}
