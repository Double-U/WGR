/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.access.entity;

import java.util.List;
import java.util.UUID;
import net.wgr.core.dao.AutoGenerated;
import net.wgr.core.data.LazyQuery;
import net.wgr.core.data.Matcher;
import org.apache.cassandra.thrift.Column;

/**
 * A user entity class. 
 * @created Jul 24, 2011
 * @author double-u
 */
public class User extends net.wgr.core.dao.Object {

    @AutoGenerated
    private UUID id;
    private String userName, firstName, lastName, emailAddress, password;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public String getPasswordHash() {
        return password;
    }
    
    public void setPasswordHash(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public UUID getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getColumnFamily() {
        return "identities";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }

    public static User getByUserName(String userName) {
        User u = new User();
        LazyQuery q = new LazyQuery(u.getColumnFamily(), LazyQuery.Strategy.FIND_ONE);
        q.addMatcher(new UserNameMatcher(userName));
        q.run();

        if (q.getResults().size() > 0) {
            u.getFromColumns(q.getResults().values().iterator().next());
            return u;
        } else {
            return null;
        }
    }

    protected static class UserNameMatcher implements Matcher {
        
        protected String targetUserName;
        
        public UserNameMatcher(String target) {
            this.targetUserName = target;
        }

        @Override
        public boolean match(List<Column> columns) {
            for (Column c : columns) {
                if (new String(c.getName()).equals("userName") && new String(c.getValue()).equals(targetUserName)) {
                    return true;
                }
            }
            return false;
        }
    }
}
