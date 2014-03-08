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
import net.jselby.pc.world.World;

import java.io.Serializable;

/**
 * A object representing a in-game entity.
 *
 * @author j_selby
 */
public abstract class Entity implements Serializable {
    public int id;

    public double x;
    public double y;
    public double z;

    public short xSpeed;
    public short ySpeed;
    public short zSpeed;

    public float pitch;
    public float yaw;
    public World world;

    /**
     * When this entity is reached by a player (distance defined in the EntityManager class)
     * @param c The client that approached
     */
    public abstract void onApproach(Client c);

    /**
     * Sends a packet representing this entity to the supplied client.
     * @param c The client to add
     */
    public abstract void showToClient(Client c);
}
