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
		createEngine(userId, "testEngine", "");
	}
	
	/**
	 * Create an engine and provide a users program to execute
	 * @param user	Which user the engine will belong to
	 * @param name	the name of the engine, analogus to program name
	 * @param js	The user's program.
	 * @return 
	 */
	public UUID createEngine(UUID user, String name, String js) {
		Engine engine = new Engine(plugin, name, js);

		users.get(user).put(engine.getUUID(), engine);

		return engine.getUUID();
	}

	public HashMap<UUID, HashMap<UUID,Engine> > getUsers() {
		return users;
	}

	public HashMap<UUID,Engine> getEnginesForUser(UUID id) {
		return users.get(id);
	}
	
}
