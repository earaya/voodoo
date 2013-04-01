package com.earaya.voodoosample;

import com.earaya.voodoo.auth.rest.AuthenticationException;
import com.earaya.voodoo.auth.rest.Authenticator;
import com.earaya.voodoo.auth.rest.basic.BasicCredentials;
import com.google.common.base.Optional;

public class SecretAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if ("secret".equals(credentials.getPassword())) {
            return Optional.of(new User());
        }
        return Optional.absent();
    }
}