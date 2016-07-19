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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author techplex
 */
public class JSCraftPlugin extends JavaPlugin implements Listener {
	private Logger log;
	
	private UserManager um;
	private WebAPI api;
	
	

	@Override
	public void onEnable() {
		log = getLogger();
		getServer().getPluginManager().registerEvents(this, this);
		
		um = new UserManager(this);
		int port = 7070;
		api = new WebAPI(port, um);
		try {
			log.info("Starting WebAPI server on port:"+port);
			api.start();
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error when starting API Server", ex);
		}
//		this.getDataFolder().mkdir();
		
	}
	
	@EventHandler
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		um.addUser(event.getUniqueId());
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {
	}
	
	/**
	 * @note I wonder if there was a reason getClassLoader() had protected access
	 * @return 
	 */
	public ClassLoader getClsLdr() {
		return getClassLoader();
	}
}
