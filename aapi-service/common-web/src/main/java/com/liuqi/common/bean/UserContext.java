package com.liuqi.common.bean;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 用户信息上下文
 */
@Data
public class UserContext implements UserDetails {
    private String tenantId;

    private String userId;

    private String username;

    private String nickname;

    private String avatar;

    private Boolean isSuperAdmin;

    private String password;

    private Boolean isExpired = false;
    private Boolean isLocked = false;
    private Boolean isEnabled = true;

    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
