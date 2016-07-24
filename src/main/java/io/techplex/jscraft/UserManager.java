/*
 * Copyright (C) 2016 techplex
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package io.techplex.jscraft;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author techplex
 */
public class UserManager {
	private final HashMap<UUID, HashMap<UUID,Engine> > users;
	private JavaPlugin plugin;
	
	public UserManager(JavaPlugin plugin) {
		this.plugin=plugin;
		users = new HashMap<>();
	}
	/**
	 * Add a user to the hashmap. Generally when they first login to the server.
	 * @param userId
	 */
	public void addUser(UUID userId) {
		users.put(userId, new HashMap<>());
		
		//@note testing:
		createEngine(userId, "testEngine", "", "", false);
	}
	
	/**
	 * Create an engine and provide a users program to execute
	 * @param user	Which user the engine will belong to
	 * @param name	the name of the engine, analogus to program name
	 * @param js	The user's program.
	 * @param xml	The blocklyXML describing the users blocks that generated the JS code
	 * @param start	True to start the JS code running, or false to store the JS and run it later.
	 * @return 
	 */
	public UUID createEngine(UUID user, String name, String js, String xml, boolean start) {
		Engine engine = new Engine(plugin, name, js, xml, start);

		users.get(user).put(engine.getUUID(), engine);

		return engine.getUUID();
	}

	public HashMap<UUID, HashMap<UUID,Engine> > getUsers() {
		return users;
	}

	public HashMap<UUID,Engine> getEnginesForUser(UUID id) {
		return users.get(id);
	}

	public Engine getEngine(UUID engineuuid) {
		for (Map.Entry<UUID, HashMap<UUID, Engine>> entry : users.entrySet()) {
			HashMap<UUID, Engine> value = entry.getValue();
			if(value.containsKey(engineuuid)) {
				return value.get(engineuuid);
			}	
		}
		return null;
	}
	
	public boolean removeEngine(UUID engineuuid) {
		for (Map.Entry<UUID, HashMap<UUID, Engine>> entry : users.entrySet()) {
			HashMap<UUID, Engine> value = entry.getValue();
			if(value.containsKey(engineuuid)) {
				return (value.remove(engineuuid) != null);
			}	
		}
		return false;
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	
	
}
