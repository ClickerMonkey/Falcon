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

import org.magnos.net.Client;
import org.magnos.net.Worker;
import org.magnos.task.TaskService;
import org.magnos.util.ConcurrentSet;
import org.magnos.util.NonNullRef;



public class OioWorker extends TaskService implements Worker
{

	private final NonNullRef<Client> clientRef;
	private final Set<Client> clients;
	
	public OioWorker() 
	{
		super(false);
		
		this.clientRef = new NonNullRef<Client>();
		this.clients = new ConcurrentSet<Client>();
		
		release.getBlockers().add(clientRef);
		
		start();
	}
	
	@Override
	public void awake()
	{
		super.awake();
		if (clientRef.has()) {
			clientRef.get().close();
			clientRef.set(null);
		}
	}
	
	public void setClient(Client client)
	{
		clientRef.set(client);
	}
	
	public boolean hasClient() {
		return clientRef.has();
	}
	
	@Override
	protected void onExecute()
	{
		Client client = null;
		if (release.enter()) {
			try {
				client = clientRef.get();
			}
			finally {
				release.exit();
			}
		}
		
		if (client != null) {
			if (release.enter()) {
				try {
					handleClient(client);
				}
				finally {
					release.exit();	
				}
			}
		}
	}
	
	private void handleClient(Client client)
	{
		if (!client.isClosed()) {
			if (release.enter()) {
				try {
					client.handleRead();	
				}
				finally {
					release.exit();
				}
			}
		}
		if (client.isClosed()) {
			clientRef.set(null);
		}
	}
	
	@Override
	public Set<Client> getClients() {
		return clients;
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
		return !clientRef.has();
	}
	
	
	
}
