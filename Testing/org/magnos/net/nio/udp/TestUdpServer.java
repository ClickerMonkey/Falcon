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

package org.magnos.net.nio.udp;

import java.net.SocketAddress;
import java.util.Scanner;

import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.ServerListener;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.udp.UdpPipeline;


public class TestUdpServer extends DefaultClientListener implements ServerListener
{

	public static void main(String[] args)
	{
		TestUdpServer listener = new TestUdpServer();
		
		Pipeline pipeline = new UdpPipeline();
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setClientListener(listener);
		pipeline.setServerListener(listener);
		
		Server server = pipeline.newServer();
		server.listen(26524);
		
		new Scanner(System.in).nextLine();
		
		pipeline.getSelector().stop();
	}

	@Override
	public void onServerAccept(Server server, Client client) {
		System.out.format("Server %s accepted client %s\n", server.getAddress(), client.getAddress());
	}

	@Override
	public void onServerBind(Server server, SocketAddress address) {
		System.out.format("Server bound to %s\n", address);
	}

	@Override
	public void onServerError(Server server, Exception e) {
		System.err.print("Server Error: ");
		e.printStackTrace();
	}

	@Override
	public void onServerStart(Server server) {
		System.out.format("Server started on %s\n", server.getAddress());
	}

	@Override
	public void onServerStop(Server server) {
		System.out.format("Server stopped on %s\n", server.getAddress());
	}
	
	@Override
	public void onClientReceive(Client client, Object data) {
		super.onClientReceive(client, data);
		client.send(data);
	}
	
}
