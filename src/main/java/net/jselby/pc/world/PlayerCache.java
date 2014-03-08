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

package net.jselby.pc.world;

import net.jselby.pc.PlayerInventory;
import net.jselby.pc.network.Slot;

import java.io.Serializable;

/**
 * Created by James on 2/16/14.
 */
public class PlayerCache implements Serializable {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public Slot[] inventory;
}
