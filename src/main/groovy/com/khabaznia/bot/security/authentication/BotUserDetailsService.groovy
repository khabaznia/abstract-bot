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
        new BotUserPrincipal(fromChat: userService.getChatForCode(code.split(CHAT_ID_DELIMITER)[0]),
                fromUser: userService.getUserForCode(code.split(CHAT_ID_DELIMITER)[1]))
    }
}
