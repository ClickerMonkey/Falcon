/* 
 * NOTICE OF LICENSE
 * 
 * This source file is subject to the Open Software License (OSL 3.0) that is 
 * bundled with this package in the file LICENSE.txt. It is also available 
 * through the world-wide-web at http://opensource.org/licenses/osl-3.0.php
 * If you did not receive a copy of the license and are unable to obtain it 
 * through the world-wide-web, please send an email to pdiffenderfer@gmail.com 
 * so we can send you a copy immediately. If you use any of this software please
 * notify me via my website or email, your feedback is much appreciated. 
 * 
 * @copyright   Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * @license     http://opensource.org/licenses/osl-3.0.php
 * 				Open Software License (OSL 3.0)
 */

package org.magnos.net.oio;

import java.util.Set;

import org.magnos.net.Acceptor;
import org.magnos.net.Server;
import org.magnos.task.TaskService;
import org.magnos.util.ConcurrentSet;
import org.magnos.util.NonNullRef;



public class OioAcceptor extends TaskService implements Acceptor
{

	private final NonNullRef<Server> serverRef;
	private final Set<Server> servers;
	
	public OioAcceptor() 
	{
		
		this.serverRef = new NonNullRef<Server>();
		this.servers = new ConcurrentSet<Server>();
		
		release.getBlockers().add(serverRef);
		
		start();
	}
	
	@Override
	public void awake()
	{
		super.awake();
		if (serverRef.has()) {
			serverRef.get().close();
			serverRef.set(null);
		}
	}
	
	public void setServer(Server server)
	{
		serverRef.set(server);
	}
	
	public boolean hasServer() {
		return serverRef.has();
	}
	
	@Override
	protected void onExecute()
	{
		Server server = null;
		if (release.enter()) {
			try {
				server = serverRef.get();	
			}
			finally {
				release.exit();
			}
		}
		
		if (server != null) {
			if (release.enter()) {
				try {
					handleServer(server);
				}
				finally {
					release.exit();	
				}
			}
		}
	}
	
	private void handleServer(Server server)
	{
		if (!server.isClosed()) {
			if (release.enter()) {
				try {
					server.handleAccept();		
				}
				finally {
					release.exit();
				}
			}
		}
		if (server.isClosed()) {
			serverRef.set(null);
		}
	}

	@Override
	public Set<Server> getServers() {
		return servers;
	}

	@Override
	public void free() {
		stop();
	}

	@Override
	public boolean isReusable() {
		return false;
	}

	@Override
	public boolean isUnused() {
		return !serverRef.has();
	}
	
}
