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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author techplex
 */
public class Engine {
	private JavaPlugin plugin;
	private String name;
	private final UUID id;
	private final ScriptEngine eng;
	
	public Engine(JavaPlugin plugin, String name) {
		this(plugin, name, "");
	}
	
	public Engine(JavaPlugin plugin, String name, String code) {
		this.plugin = plugin;
		this.name = name;
		this.id = UUID.randomUUID();
		this.eng = getNewScriptEngine(code);
	}

	/**
	 * Get the name of the Engine
	 * @return Engine name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the Engine
	 * @param name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the uuid of the engine.
	 * @return engine UUID
	 */
	public UUID getUUID() {
		return id;
	}
	
	/**
	 * Run some code in the engine
	 * @param code 
	 */
	public void eval(String code) {
		try {
			eng.eval(code);
		} catch (ScriptException ex) {
			plugin.getLogger().log(Level.SEVERE, "Script Exception", ex);
		}
	}
	
	/**
	 * Get a new script engine and run the provided code
	 * @param code	code to run at startup
	 * @return		the scriptengine
	 */
	private ScriptEngine getNewScriptEngine(String code) {
		ScriptEngine eng = factory();

		try {
			eng.eval(code);
		} catch (ScriptException ex) {
			plugin.getLogger().log(Level.SEVERE, "Script Exception", ex);
		}
		return eng;
	}
	
	/**
	 * Create a script engine
	 * @return 
	 */
	private ScriptEngine factory() {
		final String NO_JAVASCRIPT_MESSAGE = "No JavaScript Engine available. Plugin will not work without Javascript.";
		
		Thread currentThread = Thread.currentThread();
		ClassLoader previousClassLoader = currentThread.getContextClassLoader();
		try {
			plugin.getLogger().info("starting factory");
			if ( ! ( plugin instanceof JSCraftPlugin)) {
				throw new ClassCastException("Class can't be cast");
			}
			currentThread.setContextClassLoader(((JSCraftPlugin)plugin).getClsLdr());
			//@todo filter the classes users can access
//			NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
//
//			ScriptEngine engine = factory.getScriptEngine();

			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
		
			
			String jscraft_lib_dir = String.join(File.separator, plugin.getDataFolder().toString(), "js", "lib");
			File jscraft_lib = new File(jscraft_lib_dir);
			File bootjs = new File(jscraft_lib + File.separator + "boot.js");
			if ( ! bootjs.exists()) {
				plugin.getLogger().severe("boot.js missing. Unable to initialize script runtime properly."+bootjs.getAbsolutePath());
			} else {
				Invocable inv = (Invocable) engine;
				engine.eval(new FileReader(bootjs));
				inv.invokeFunction("__boot", plugin, engine);
			}
			
			if (engine == null) {
				System.out.println(NO_JAVASCRIPT_MESSAGE);
			} else {
				return engine;
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Exception", e);
//			e.printStackTrace();
			plugin.getLogger().info("Stack Trace");
		} finally {
			currentThread.setContextClassLoader(previousClassLoader);
		}
		plugin.getLogger().info("null");
		return null;
	}

	
}
