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
import java.nio.channels.SocketChannel;

import org.magnos.net.AbstractClient;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.Worker;
import org.magnos.net.nio.NioInterestTask;
import org.magnos.net.nio.NioRegisterTask;
import org.magnos.net.nio.NioWorker;


/**
 * A client which uses the TCP protocol and NIO.
 * 
 * @author Philip Diffenderfer
 *
 */
public class TcpClient extends AbstractClient
{
	
	// The selectable channel registered to some selector to receive events.
	private SocketChannel channel;
	
	// The key registered to the client.
	private SelectionKey key;

	/**
	 * Instantiates a new TcpClient.
	 * 
	 * @param pipeline
	 * 		The pipeline the client should use.
	 * @param worker
	 * 		The worker handling the client.
	 * @param server
	 * 		The server which accepted the client, or null if the client was
	 * 		created by the pipeline.
	 */
	protected TcpClient(Pipeline pipeline, Worker worker, Server server) 
	{
		super(pipeline, worker, server);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doConnect(SocketAddress address) throws IOException
	{
		channel = SocketChannel.open();
		channel.configureBlocking(false);

		invokeOpen();
		
		channel.connect(address);
		
		NioRegisterTask task = new NioRegisterTask(channel, SelectionKey.OP_CONNECT, this);
		task.setHandler((NioWorker)getWorker());
		
		key = task.sync();
		
		return (server != null);
	}

	// XXX
	public void setSocket(SocketChannel channel) 
	{
		this.channel = channel;
	}
	
	// XXX
	public void setKey(SelectionKey key)
	{
		this.key = key;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocketChannel getSocket() 
	{
		return channel;
	}		
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClose() throws IOException
	{
		channel.close();
		key.cancel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onFlush() 
	{
		NioInterestTask task = new NioInterestTask(key, SelectionKey.OP_WRITE);
		task.setHandler((NioWorker)worker);
		task.async();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRead() throws IOException 
	{
		if (input.drain(channel) < 0) {
			close();
		}
		else if (!input.isEmpty()) {
			decode();	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onWrite() throws IOException
	{
		while (encode() > 0) {
			output.fill(channel);
		}
		
		if (output.isEmpty()) {
			if (isDisconnecting()) {
				synchronized (pending) {
					if (pending.size() == 0) {
						close();
					}
				}
			}
			else {
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onConnect() throws IOException  
	{
		channel.finishConnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocketAddress getAddress() 
	{
		return channel.socket().getRemoteSocketAddress();
	}

}