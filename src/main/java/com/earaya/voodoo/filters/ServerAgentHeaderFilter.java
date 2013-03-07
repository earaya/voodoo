package com.earaya.voodoo.filters;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class ServerAgentHeaderFilter implements ContainerResponseFilter {

	public static final String SERVER_AGENT_HEADER = "Server";
	private final ServerInfo serverInfo;
	
	@Inject
	public ServerAgentHeaderFilter(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		response.getHttpHeaders().add(SERVER_AGENT_HEADER, serverInfo.getServerIdentifier());
		return response;
	}

}
