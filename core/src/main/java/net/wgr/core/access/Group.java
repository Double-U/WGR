/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.access;

import java.util.List;
import java.util.UUID;

/**
 * 
 * @created Jul 24, 2011
 * @author double-u
 */
public class Group extends net.wgr.core.dao.Object {
    private UUID id;
    
    private List<UUID> users;
    private String name;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getUsers() {
        return users;
    }

    public void setUsers(List<UUID> users) {
        this.users = users;
    }

    @Override
    public String getColumnFamily() {
        return "groups";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }
}
