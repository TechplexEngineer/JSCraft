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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
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
				//get a list of all users and their engines
				//GET /script/ BODY: {<USERUUID>: {<ENGINEUUID>:{js:"", xml:"", name:"", uuid:""} } }
				if (session.getUri().equals("/script")) {
					HashMap<UUID, HashMap<UUID,Engine> > map = um.getUsers();
					JSONObject resp = new JSONObject(map);
					return newFixedLengthResponse(Status.OK, MIME_JSON, resp.toString());
				}

				//Read existing Script and BlockXML by Engine UUID
				//GET /script/<EngineUUID> BODY: {js:"<JavaScript>", xml:"<xml>"}
				Pattern p = Pattern.compile("/script/(" + REGEX_UUID + ")");
				Matcher m = p.matcher(session.getUri());
				if (m.matches()) {

					UUID engineuuid = UUID.fromString(m.group(1));
					Engine eng = um.getEngine(engineuuid);
					if (eng == null) {
						return newFixedLengthResponse("Post\n" + session.getUri() + "\nInvalid Engine UUID");
					}
					JSONObject ret = new JSONObject();
					ret.append("js", eng.getCode());
					ret.append("xml", eng.getXml());

					return newFixedLengthResponse(ret.toString());
				}

				//Start existing by Engine UUID
				//GET /script/start/<EngineUUID>
				p = Pattern.compile("/script/start/(" + REGEX_UUID + ")");
				m = p.matcher(session.getUri());
				if (m.matches()) {
					UUID engineuuid = UUID.fromString(m.group(1));
					Engine eng = um.getEngine(engineuuid);
					if (eng == null) {
						return newFixedLengthResponse("Post\n" + session.getUri() + "\nInvalid Engine UUID");
					}
					eng.Start();
					return newFixedLengthResponse("Engine Started");
				}

				//Stop existing by Engine UUID
				//GET /script/stop/<EngineUUID>
				p = Pattern.compile("/script/stop/(" + REGEX_UUID + ")");
				m = p.matcher(session.getUri());
				if (m.matches()) {
					UUID engineuuid = UUID.fromString(m.group(1));
					Engine eng = um.getEngine(engineuuid);
					if (eng == null) {
						return newFixedLengthResponse("Post\n" + session.getUri() + "\nInvalid Engine UUID");
					}
					eng.Stop();
					return newFixedLengthResponse("Engine Stopped");
				}

				return newFixedLengthResponse("Get\n"+session.getUri());
			case POST:

				//Add new Script for User
				//POST /script/new/<UserUUID> BODY: {name:"", js:"<JavaScript>", xml:"<xml>", start:true} //start is optional others required
				p = Pattern.compile("/script/new/(" + REGEX_UUID + ")");
				m = p.matcher(session.getUri());
				if (m.matches()) {

					try {
						JSONTokener tokener = new JSONTokener(session.getInputStream());
						JSONObject body = new JSONObject(tokener);

						if (!body.has("js"))
							return newFixedLengthResponse("Missing 'js' field");
						if (!body.has("xml"))
							return newFixedLengthResponse("Missing 'xml' field");
						if (!body.has("name"))
							return newFixedLengthResponse("Missing 'name' field");

						Boolean start;
						if (!body.has("start"))
							start = false; //if missing optional start field, default to false.
						else
							start = body.getBoolean("start");

						UUID userUUID = UUID.fromString(m.group(1));
						String name = body.getString("name");
						String js = body.getString("js");
						String xml = body.getString("xml");

						UUID engineUUID = um.createEngine(userUUID, name, js, xml, start);

						return newFixedLengthResponse(engineUUID.toString());
					} catch (JSONException ex) {
						return newFixedLengthResponse("Got Invalid JSON"); //@todo make this error better.
					}
				}

				//Update existing Script and BlockXML by Engine UUID
				//POST /script/<EngineUUID>  BODY: {name:"", js:"<JavaScript>", xml:"<xml>", start:true} //start, name are optional others require
				p = Pattern.compile("/script/(" + REGEX_UUID + ")");
				m = p.matcher(session.getUri());
				if (m.matches()) {

					UUID engineuuid = UUID.fromString(m.group(1));
					Engine eng = um.getEngine(engineuuid);
					if (eng == null) {
						return newFixedLengthResponse("Post\n" + session.getUri() + "\nInvalid Engine UUID");
					}

					try {
						JSONTokener tokener = new JSONTokener(session.getInputStream());
						JSONObject body = new JSONObject(tokener);

						if (!body.has("js"))
							return newFixedLengthResponse("Missing 'js' field");
						if (!body.has("xml"))
							return newFixedLengthResponse("Missing 'xml' field");

						String name;
						if (!body.has("name"))
							name = eng.getName(); //if missing optional name field, default to previous name.
						else
							name = body.getString("name");

						Boolean start;
						if (!body.has("start"))
							start = false; //if missing optional start field, default to false.
						else
							start = body.getBoolean("start");



						String js = body.getString("js");
						String xml = body.getString("xml");

						eng.Stop();
						eng.setName(name);
						eng.setCode(js, xml);

						if (start) {
							eng.Start();
						}

						return newFixedLengthResponse(eng.getUUID().toString());
					} catch (JSONException ex) {
						return newFixedLengthResponse("Got Invalid JSON"); //@todo make this error better.
					}
				}

				return newFixedLengthResponse("Post\n"+session.getUri());
			case DELETE:
				//Delete existing by Engine UUID
				//DELETE /script/<EngineUUID>
				if (Pattern.matches("/script/"+REGEX_UUID, session.getUri())) {
					String uri = session.getUri();
					uri = uri.substring(1);
					UUID engine = UUID.fromString(uri);
					boolean removed = um.removeEngine(engine);

					return newFixedLengthResponse(Status.OK, MIME_JSON, ""+removed);
				}
				return newFixedLengthResponse("Delete");

			default:
				return newFixedLengthResponse("Bad Request");

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
