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

package org.magnos.net.examples.echo.nio;

import java.util.Scanner;
import java.util.Set;

import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.DefaultServerListener;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.tcp.TcpPipeline;


public class TestTcpServer extends DefaultClientListener
{
	
	public static void main(String[] args) 
	{
		// Create the pipeline to use TCP and NIO
		Pipeline pipeline = new TcpPipeline();
		
		// Set the default protocol and add it.
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setServerListener(new DefaultServerListener());
		pipeline.setClientListener( new DefaultClientListener() {
			// Sends each message to all connected clients.
			@Override
			public void onClientReceive(Client client, Object data) 
			{
				// Also echo out the message to the command line.
				super.onClientReceive(client, data);
				
				// Send to all clients.
				Set<Client> clients = client.getServer().getClients();
				for (Client c : clients) {
					c.send(data);
				}
			}
		});
		
		// Start listening on port 34567.
		Server server = pipeline.newServer();
		server.listen(34567);
		
		// Wait for a newline to stop the server.
		new Scanner(System.in).nextLine();
		
		// Stop the server and close all client connections.
		pipeline.getSelector().stop();
	}
	
	
}
