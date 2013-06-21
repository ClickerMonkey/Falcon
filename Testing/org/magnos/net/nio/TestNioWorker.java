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

package org.magnos.net.nio;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.Test;
import org.magnos.net.Acceptor;
import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.DefaultServerListener;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.Worker;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.NioSelector;
import org.magnos.net.nio.NioWorker;
import org.magnos.net.nio.tcp.TcpPipeline;
import org.magnos.test.BaseTest;


public class TestNioWorker extends BaseTest 
{

	@Test
	public void testDefault() throws IOException
	{
		NioWorker w = new NioWorker();
		
		assertTrue( w.isRunning() );
		assertNotNull( w.getSelector() );
		assertTrue( w.getSelector().isOpen() );
		assertTrue( w.isUnused() );
		assertTrue( w.isReusable() );
		assertEquals( 0, w.getClients().size() );
		assertEquals( 0, w.getServers().size() );
		
		w.stop();
	}
	
	@Test
	public void testServers()
	{
		broadcast();
		
		NioSelector selector = new NioSelector();
		selector.start();
		
		Pipeline pipeline = new TcpPipeline(selector);
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setClientListener(new DefaultClientListener());
		pipeline.setServerListener(new DefaultServerListener());

		Acceptor accept = selector.getAcceptor();
		
		assertEquals( 0, accept.getServers().size() );
		
		Server server = pipeline.newServer();
		server.listen(8998);
		
		assertEquals( 1, accept.getServers().size() );
		assertTrue( accept.getServers().contains(server) );
		
		server.close();

		assertEquals( 0, accept.getServers().size() );
		assertFalse( accept.getServers().contains(server) );
		
		selector.stop();
	}
	
	@Test
	public void testClients()
	{
		broadcast();
		
		NioSelector selector = new NioSelector();
		selector.start();
		
		Pipeline pipeline = new TcpPipeline(selector);
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setClientListener(new DefaultClientListener());
		pipeline.setServerListener(new DefaultServerListener());

		Worker work = selector.getWorker();
		
		assertEquals( 0, work.getClients().size() );
		
		SocketAddress address = new InetSocketAddress(8998);
		Server server = pipeline.newServer();
		server.listen(address);
		
		Client client = pipeline.newClient();
		client.connect(address);

		sleep(1000);
		
		// the client and server side connections exist over one worker
		assertEquals( 2, work.getClients().size() );
		assertTrue( work.getClients().contains(client) );
		
		sleep(500);
		
		client.close();

		sleep(500);
		
		assertEquals( 0, work.getClients().size() );
		assertFalse( work.getClients().contains(client) );
		
		server.close();
		selector.stop();
	}
	
}
