package com.keuin.blame.util;

import net.minecraft.entity.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityUtil {
    /**
     * Get passengers and passengers of passengers and so on. i.e. get the closure of 'riding' relation on given entity.
     * @param entity The entity to start from.
     * @return the closure. Note that the set contains the root entity.
     */
    public static Set<Entity> getEntityRidingRelationClosure(Entity entity) {
        Objects.requireNonNull(entity);
        Set<Entity> set = new HashSet<>();
        set.add(entity);
        bfs(entity, set);
        return Collections.unmodifiableSet(set);
    }

    private static void bfs(Entity entity, Set<Entity> set) {
        for (var sub : entity.getPassengerList()) {
            set.add(sub);
            bfs(sub, set);
        }
    }
}
