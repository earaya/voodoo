package com.earaya.voodoo.sample;

import com.earaya.voodoo.auth.AuthenticationException;
import com.earaya.voodoo.auth.Authenticator;
import com.earaya.voodoo.auth.basic.BasicCredentials;
import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: earaya
 * Date: 3/8/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if ("secret".equals(credentials.getPassword())) {
            return Optional.of(new User());
        }
        return Optional.absent();
    }
}