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

import java.io.Serializable;
import java.util.Random;

/**
 * Created by James on 2/16/14.
 */
public class Seed implements Serializable {
    public int alt1;
    public int alt2;
    public int alt3;
    public int alt4;
    public long seed;

    public Seed(long seed) {
        this.seed = seed;
        Random r = new Random(seed);
        alt1 = r.nextInt(20000);
        alt2 = r.nextInt(20000);
        alt3 = r.nextInt(20000);
        alt4 = r.nextInt(20000);
    }
}
