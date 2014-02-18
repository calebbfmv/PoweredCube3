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

package net.jselby.pc.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

import java.util.Collection;
import java.util.List;

/**
 * Created by James on 2/1/14.
 */
public class BukkitHelpMap implements HelpMap {
    @Override
    public HelpTopic getHelpTopic(String s) {
        return null;
    }

    @Override
    public Collection<HelpTopic> getHelpTopics() {
        return null;
    }

    @Override
    public void addTopic(HelpTopic helpTopic) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void registerHelpTopicFactory(Class<?> aClass, HelpTopicFactory<?> helpTopicFactory) {

    }

    @Override
    public List<String> getIgnoredPlugins() {
        return null;
    }
}
