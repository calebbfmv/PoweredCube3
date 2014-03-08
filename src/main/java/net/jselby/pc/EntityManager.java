/*
 * PoweredCube3
 * Copyright (C) 2014 James
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jselby.pc;

import net.jselby.pc.network.Client;
import net.jselby.pc.world.Location;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A basic entity container. This is usually stored within the world, and it stores stuff like ItemStacks, and so on.
 * This needs major changes - its just a placeholder for a future system.
 */
public class EntityManager implements Serializable {
    public final static double TRIGGER_RANGE = 2;
    private final ArrayList<Entity> entities = new ArrayList<Entity>();

    public void tick() {
        // Make sure players aren't near these entities
        // Check each player
        synchronized (entities) {
            for (Client c : PoweredCube.getInstance().clients) {
                for (Entity entity : entities.toArray(new Entity[entities.size()])) {
                    if (new Location(c.x, c.y, c.z).distance(new Location(entity.x, entity.y, entity.z)) < TRIGGER_RANGE) {
                        entity.onApproach(c);
                    }
                }
            }
        }
    }

    /**
     * Registers a entity with the manager, if it already exists, it will be added again.
     * @param ent The entity to add
     */
    public void addEntity(Entity ent) {
        synchronized (entities) {
            entities.add(ent);
        }
    }

    /**
     * Removes a entity from the manager. If the entity doesn't exist, nothing happens
     * @param ent The entity to remove
     */
    public void removeEntity(Entity ent) {
        synchronized (entities) {
            entities.remove(ent);
        }
    }

    /**
     * Returns weather the entity has already been registered.
     * @param ent The entity to check
     * @return If the entity has been registered
     */
    public boolean containsEntity(Entity ent) {
        synchronized (entities) {
            return entities.contains(ent);
        }
    }
}
