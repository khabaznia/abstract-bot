package com.khabaznia.bots.core.security.authentication

import com.khabaznia.bots.core.model.Chat
import com.khabaznia.bots.core.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import static com.khabaznia.bots.core.security.Constants.CHAT_ID_DELIMITER

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
