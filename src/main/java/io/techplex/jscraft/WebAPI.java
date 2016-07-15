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

import org.nanohttpd.NanoHTTPD;
import org.nanohttpd.NanoHTTPD.Response.Status;

/**
 *
 * @author techplex
 */
public class WebAPI extends NanoHTTPD {
	
	public WebAPI(int port) {
		super(port);
	}
	@Override
	public Response serve(IHTTPSession session){
		
		switch (session.getMethod()) {
			case GET:
				
				return newFixedLengthResponse("Get\n"+session.getUri());
			case POST:
				return newFixedLengthResponse("Post");
			case PUT:
				return newFixedLengthResponse("Put");
			case DELETE:
				return newFixedLengthResponse("Delete");
			default:
				return newFixedLengthResponse("Bad Request");
				//return newFixedLengthResponse(Status.BAD_REQUEST, "application/json", "{\"status\":\"Unsupported Method");
		}
	}
	
}
