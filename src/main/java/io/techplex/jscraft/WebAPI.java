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
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nanohttpd.NanoHTTPD;
import org.nanohttpd.NanoHTTPD.Response.Status;

/**
 *
 * @author techplex
 */
public class WebAPI extends NanoHTTPD {
	
	private UserManager um;
	public static final String MIME_JSON = "application/json";
	public static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	
	public WebAPI(int port, UserManager um) {
		super(port);
		this.um = um;
	}
	@Override
	public Response serve(IHTTPSession session){
		
		switch (session.getMethod()) {
			case GET:
				//get all users and their engines
				if (session.getUri().equals("/")) {
					HashMap<UUID, HashMap<UUID,Engine> > map = um.getUsers();
					JSONObject resp = new JSONObject(map);
					return newFixedLengthResponse(Status.OK, MIME_JSON, resp.toString());
				}
				
				//show the headerst the request was made with
				if (session.getUri().equals("/headers")) {
					String json = new JSONObject(session.getHeaders()).toString();
					return newFixedLengthResponse(Status.OK, MIME_JSON, json);
				}
				
				//get the engines for a spesific user
				if (Pattern.matches("/"+REGEX_UUID, session.getUri())) {
					String uri = session.getUri();
					uri = uri.substring(1);
					UUID user = UUID.fromString(uri);
					HashMap<UUID,Engine>  map = um.getUsers().get(user);
					JSONObject resp = new JSONObject(map);
					return newFixedLengthResponse(Status.OK, MIME_JSON, resp.toString());
				}

				return newFixedLengthResponse("Get\n"+session.getUri());
			case POST:
				
				if(session.getUri().equals("create")) {
					//user uuid
					//script name
					//optional script to start
				}
				
				//execute js code
				Pattern p = Pattern.compile("/code/("+REGEX_UUID+")");
				Matcher m = p.matcher(session.getUri());
				if(m.matches()) {
					
					
					String cl = session.getHeaders().get("content-length");
					int length = Integer.parseInt(cl);

					String body = convertStreamToString(session.getInputStream(), length);
					UUID engineuuid = UUID.fromString(m.group(1));
					Engine e = um.getEngine(engineuuid);
					if (e == null) return newFixedLengthResponse("Post\n"+session.getUri()+"\nInvalid Engine UUID");
					e.eval(body);
					return newFixedLengthResponse(body);
				}
					
					return newFixedLengthResponse("Post\n"+session.getUri());
			case PUT:
				return newFixedLengthResponse("Put");
			case DELETE:
				return newFixedLengthResponse("Delete");
			default:
				return newFixedLengthResponse("Bad Request");
				//return newFixedLengthResponse(Status.BAD_REQUEST, "application/json", "{\"status\":\"Unsupported Method");
		}
	}
	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
	
	private String convertStreamToString(java.io.InputStream is, int length) {
		StringBuilder sb = new StringBuilder();
		try {
			for(int i=0; i<length; i++) {
				sb.append((char)is.read());
			}
		} catch (IOException ex) {
			Logger.getLogger(WebAPI.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sb.toString();
	}
	
}
