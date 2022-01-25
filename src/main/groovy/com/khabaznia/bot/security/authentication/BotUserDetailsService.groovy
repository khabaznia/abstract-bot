package com.khabaznia.bot.security.authentication

import com.khabaznia.bot.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

import static com.khabaznia.bot.security.Constants.*

@Component
class BotUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService

    @Override
    UserDetails loadUserByUsername(String code) throws UsernameNotFoundException {
        def user = userService.getUserForCode(code.tokenize(CHAT_ID_DELIMITER)[1])
        def chat = userService.getChatForCode(code.tokenize(CHAT_ID_DELIMITER)[0], code.tokenize(CHAT_ID_DELIMITER)[1]);
        new BotUserPrincipal(fromChat: chat, fromUser: user)
    }
}
