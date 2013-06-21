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

package org.magnos.net.nio.tcp;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.magnos.net.Client;
import org.magnos.net.ClientListener;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.DefaultServerListener;
import org.magnos.net.Pipeline;
import org.magnos.net.Protocol;
import org.magnos.net.Server;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.NioSelector;
import org.magnos.net.nio.tcp.TcpPipeline;
import org.magnos.test.BaseTest;


public class TestTcpPipeline extends BaseTest 
{
	
	private NioSelector selector;
	private Pipeline pipe;
	private Protocol<String> protocol;
	
	@Before
	public void testBefore() {
		selector = new NioSelector();
		selector.start();
		
		protocol = new StringProtocol();
		
		pipe = new TcpPipeline(selector);
		pipe.setDefaultProtocol(String.class);
		pipe.addProtocol(protocol);
		pipe.setClientListener(new DefaultClientListener());
		pipe.setServerListener(new DefaultServerListener());
	}
	
	@After
	public void testAfter() {
		selector.stop();
	}

	@Test
	public void testDefault()
	{
		broadcast();
		assertNotNull( pipe.getBufferFactory() );

		assertSame( protocol, pipe.getProtocol(String.class) );
	
		assertSame( selector, pipe.getSelector() );
		
		assertSame( String.class, pipe.getDefaultProtocol() );
	}
	
	@Test
	public void testListen()
	{
		broadcast();
		SocketAddress address = new InetSocketAddress(3874); 
		
		Server server = pipe.newServer();
		server.listen(address);
		
		assertNotNull( server.getAcceptor() );
		assertSame( address, server.getAddress() );
		assertSame( pipe, server.getPipeline() );
		assertNotNull( server.getClientListener() );
		assertNotNull( server.getSocket() );
		assertNotNull( server.getListeners().get(0) );
		assertFalse( server.isClosed() );
		
		server.close();
		assertTrue( server.isClosed() );
	}
	
	@Test
	public void testConnect()
	{
		broadcast();
		SocketAddress address = new InetSocketAddress(3874); 
		
		Server server = pipe.newServer();
		server.listen(address);
		sleep(500);
		Client client = pipe.newClient();
		client.connect(address);
		sleep(100);
		client.close();
		sleep(100);
		server.close();
	}
	
	@Test
	public void testFewClients()
	{
		broadcast();

		final int CLIENTS = 10;
		final SocketAddress address = new InetSocketAddress(3874); 

		Server server = pipe.newServer();
		server.setClientListener(new ClientListener() {
			public void onClientReceive(Client client, Object data) {
				client.send(data);
			}
			public void onClientConnect(Client client) { }
			public void onClientSend(Client client, Object data) { }
			public void onClientWrite(Client client) { }
			public void onClientRead(Client client) { }
			public void onClientError(Client client, Exception e) { }
			public void onClientClose(Client client) { }
			public void onClientDiscard(Client client, Object data) { }
			public void onClientDisconnect(Client client) { }
			public boolean onClientOpen(Client client) { return true; }
		});
		server.listen(address);
		
		final AtomicInteger sent = new AtomicInteger();
		
		GroupTask.initialize(CLIENTS);
		GroupTask.add(new Runnable() {
			public void run() {
				Client client = pipe.newClient();
				client.addListener(new ClientListener() {
					public void onClientReceive(Client client, Object data) {
						String msg = (String)data;
						if (msg.length() > 1) {
							client.send(msg.substring(1));
							sent.incrementAndGet();
						}
						else {
							client.close();
						}
					}
					public void onClientConnect(Client client) { }
					public void onClientSend(Client client, Object data) { }
					public void onClientWrite(Client client) { }
					public void onClientRead(Client client) { }
					public void onClientError(Client client, Exception e) { }
					public void onClientClose(Client client) { }
					public void onClientDiscard(Client client, Object data) { }
					public void onClientDisconnect(Client client) { }
					public boolean onClientOpen(Client client) { return true; }
				});
				client.connect(address);
				client.send("Hello world, for each character in this string a message will be echoed back.");
			}
		}, CLIENTS);
		GroupTask.execute();
		
		sleep(1000);
		
		System.out.println(sent.get());
	}
	
}
