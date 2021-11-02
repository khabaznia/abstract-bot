package com.khabaznia.bot.security.authentication

import com.khabaznia.bot.model.Chat
import com.khabaznia.bot.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import static com.khabaznia.bot.security.Constants.*

class BotUserPrincipal implements UserDetails {

    User fromUser
    Chat fromChat

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        [new SimpleGrantedAuthority(fromChat.role.toString()), new SimpleGrantedAuthority(fromUser.role.toString())]
    }

    @Override
    String getPassword() {
        fromChat.code + CHAT_ID_DELIMITER + fromUser.code
    }

    @Override
    String getUsername() {
        fromChat.code + CHAT_ID_DELIMITER + fromUser.code
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return true
    }
}
