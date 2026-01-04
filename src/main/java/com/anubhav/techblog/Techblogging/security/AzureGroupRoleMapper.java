package com.anubhav.techblog.Techblogging.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class AzureGroupRoleMapper {

    // Azure GROUP OBJECT IDs → App Roles
    private static final Map<String, String> GROUP_ROLE_MAP = Map.of(
        "071ba6dc-85e9-4af1-ae50-f40d699564c8", "ROLE_ADMIN",
        "08d704ee-f071-49e2-8f6a-f5e39a9295b6",  "ROLE_USER"
    );

    public Set<String> mapGroupsToRoles(Collection<String> groupIds) {
        if (groupIds == null) return Set.of();

        Set<String> roles = new HashSet<>();

        for (String groupId : groupIds) {
            if (GROUP_ROLE_MAP.containsKey(groupId)) {
                roles.add(GROUP_ROLE_MAP.get(groupId));
            }
        }
        return roles;
    }
}