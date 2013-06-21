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

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.magnos.net.AbstractServer;
import org.magnos.net.Acceptor;
import org.magnos.net.Client;
import org.magnos.net.Pipeline;
import org.magnos.net.Selector;
import org.magnos.net.nio.NioRegisterTask;
import org.magnos.net.nio.NioWorker;


/**
 * 
 * @author Philip Diffenderfer
 *
 */
public class TcpServer extends AbstractServer 
{
	
	protected ServerSocketChannel server;
	protected SelectionKey key;
	
	protected TcpServer(Selector selector, Pipeline pipeline, Acceptor acceptor) 
	{
		super(selector, pipeline, acceptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doBind(SocketAddress localAddress) throws IOException 
	{
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		address = localAddress;
		
		invokeStart();
		
		server.socket().bind(localAddress);
		
		invokeBind(localAddress);
		
		NioRegisterTask task = new NioRegisterTask(server, SelectionKey.OP_ACCEPT, this);
		task.setHandler((NioWorker)getAcceptor());
		
		key = task.sync();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleAccept() 
	{
		if (!state.equals(Accepting)) {
			return;
		}
		
		try {
			TcpClient client = (TcpClient)pipeline.newClient(this);
			if (clientListener != null) {
				client.addListener(clientListener);
			}
			
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			
			client.setSocket(channel);
			client.state().set(Client.Connecting);

			client.initialize();
			
			invokeAccept(client);
			
			client.invokeOpen();
			
			client.state().set(Client.Connected);
			
			NioRegisterTask task = new NioRegisterTask(channel, SelectionKey.OP_READ, client);
			task.setHandler((NioWorker)client.getWorker());
			client.setKey( task.sync() );
			
			client.invokeConnect();
		}
		catch (IOException e) {
			invokeError(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose() throws IOException
	{
		server.close();	
		key.cancel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServerSocketChannel getSocket() 
	{
		return server;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleConnect() 
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRead() 
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleWrite() 
	{
	}


	
}