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

import java.io.*;
import java.util.ArrayList;

/**
 * Created by James on 2/16/14.
 */
public class PoweredCubeWorldLoader {
    public static World loadWorld(String world) {
        try {
            File file = new File(world + ".world");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            World chunks = (World) in.readObject();
            in.close();
            return chunks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveWorld(World world) {
        try {
            File file = new File(world.getName() + ".world");
            if (file.exists()) {
                file.delete();
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(world);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean worldExists(String world) {
        return new File(world + ".world").exists();
    }
}
