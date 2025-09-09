package com.benchpress200.photique.auth.filter.result;

import com.benchpress200.photique.user.domain.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@RequiredArgsConstructor
public class UserAuthenticationResult implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> this.role);

        return collection;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public static UserAuthenticationResult from(final User user) {
        return UserAuthenticationResult.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .password(user.getPassword())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
