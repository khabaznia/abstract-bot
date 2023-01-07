package com.khabaznia.bots.core.endpoint

import com.khabaznia.bots.core.enums.UserRole
import com.khabaznia.bots.core.integration.dto.ErrorResponseDto
import com.khabaznia.bots.core.integration.dto.SendMessageDto
import com.khabaznia.bots.core.meta.mapper.ApiDtoMapper
import com.khabaznia.bots.core.service.BotRequestService
import com.khabaznia.bots.core.service.UserService
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@Slf4j
@RestController
@RequestMapping(value = '/api')
@Tag(name = "Bot api")
class BotRestController {

    @Autowired
    private UserService userService
    @Autowired
    private ApiDtoMapper apiDtoMapper
    @Autowired
    private BotRequestService requestService

    @Operation(summary = "Send message", tags = "API")
    @ApiResponses(value = [
            @ApiResponse(description = "Request successfully send", responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Incorrect request",
                    content = [@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))]),
            @ApiResponse(responseCode = "403", description = "Authentication Failure"),
    ])
    @PostMapping("/sendMessage")
    ResponseEntity<Void> sendMessage(@RequestBody @Valid SendMessageDto messageDto) {
        messageDto.chatId = messageDto.chatId ?: adminChat
        log.info 'Send message from API - {}', messageDto
        requestService.execute(apiDtoMapper.toBotRequest(messageDto))
        ResponseEntity.ok().build()
    }

    protected String getAdminChat() {
        userService.getUserForRole(UserRole.ADMIN)?.code
    }
}
