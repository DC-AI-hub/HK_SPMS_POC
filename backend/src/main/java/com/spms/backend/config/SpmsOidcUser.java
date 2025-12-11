package com.spms.backend.config;

import com.spms.backend.controller.dto.idm.DepartmentDTO;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;
import java.util.List;

public class SpmsOidcUser extends DefaultOidcUser {

    private UserModel authenticatedUser;

    public SpmsOidcUser(Collection<? extends GrantedAuthority> authorities,
                        OidcIdToken idToken, OidcUserInfo userInfo,
                        String nameAttributeKey,
                        UserModel authenticatedUser
                        ) {
        super(authorities, idToken, userInfo, nameAttributeKey);
        this.authenticatedUser = authenticatedUser;
    }

    public UserModel getAuthenticatedUser() {
        return authenticatedUser;
    }

}
