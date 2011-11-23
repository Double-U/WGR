/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.session;

import com.twmacinta.util.MD5;
import java.util.Date;
import net.wgr.core.access.AuthenticationProvider;
import net.wgr.core.access.entity.User;
import net.wgr.core.data.DAObjectMatcher;
import net.wgr.core.data.LazyQuery;
import net.wgr.server.session.Session;

/**
 * 
 * @created Jul 24, 2011
 * @author double-u
 */
public class SimpleAuthenticationProvider extends AuthenticationProvider {

    private static SimpleAuthenticationProvider instance;

    public Ticket authenticate(final String user, String password) {
        MD5 md5 = new MD5();
        // I like things salty
        md5.Update(".0f]$e" + password + "5f8&i");
        final String hash = md5.asHex();
        LazyQuery<User> lq = new LazyQuery("identities", LazyQuery.Strategy.FIND_ONE);
        lq.addMatcher(new DAObjectMatcher<User>(User.class) {

            @Override
            public boolean match(User object) {
                return object.getUserName().equals(user) && object.getPasswordHash().equals(hash);
            }
        });
        lq.run();
        User result = lq.getResult().getValue();
        if (result != null) {
            return createTicket(new Date(), result);
        }
        return null;
    }

    public static boolean authenticateSession(String user, String password, Session s) {
        if (instance == null) {
            instance = new SimpleAuthenticationProvider();
        }
        if (s == null || user.isEmpty() || password.isEmpty()) {
            return false;
        }
        s.setTicket(instance.authenticate(user, password));
        return s.getTicket() != null;
    }
}
