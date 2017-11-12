package com.nemezis.server;

import com.nemezis.client.GeneralService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GeneralServiceImpl extends RemoteServiceServlet implements GeneralService {

	@Override
	public String greetServer(String name) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
}