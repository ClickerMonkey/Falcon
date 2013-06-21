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
	
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.magnos.net.AbstractClient;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.Worker;
import org.magnos.net.nio.NioInterestTask;
import org.magnos.net.nio.NioRegisterTask;
import org.magnos.net.nio.NioWorker;


/**
 * A client for UDP using NIO.
 * 
 * @author Philip Diffenderfer
 *
 */
public class UdpClient extends AbstractClient
{

	// The channel of communication with the udp server.
	private DatagramChannel channel;

	// The key registered to the client.
	private SelectionKey key;
	
	// The address the client is connected to.
	private SocketAddress address;

	/**
	 * Instantiates a new UdpClient.
	 * 
	 * @param pipeline
	 * 		The pipeline the client should use.
	 * @param worker
	 * 		The worker handling the client.
	 * @param server
	 * 		The server which accepted the client, or null if the client was
	 * 		created by the pipeline.
	 */
	protected UdpClient(Pipeline pipeline, Worker worker, Server server) 
	{
		super(pipeline, worker, server);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doConnect(SocketAddress address) throws IOException
	{
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
		
		setAddress(address);
		
		invokeOpen();
		
		channel.connect(address);
		
		invokeConnect();
		
		NioRegisterTask task = new NioRegisterTask(channel, SelectionKey.OP_READ, this);
		task.setHandler((NioWorker)getWorker());
		
		key = task.sync();
		
		return (server != null);
	}

	/**
	 * Internally sets this client and ensures the input and output buffers for 
	 * this client are sized enough to receive entire packets without loosing
	 * discarded data.
	 * 
	 * @param channel
	 * 		The channel of the client.
	 * @throws IOException
	 * 		An error occurred retrieving the channels socket.
	 */
	protected void setSocket(DatagramChannel channel) throws IOException
	{
		this.channel = channel;
		this.input.pad(channel.socket().getReceiveBufferSize());
		this.output.pad(channel.socket().getSendBufferSize());
	}

	/**
	 * Internally sets the selection key of the client.
	 * 
	 * @param key
	 * 		The selection key of the client.
	 */
	protected void setKey(SelectionKey key) 
	{
		this.key = key;
	}

	/**
	 * Internally sets the address of the client.
	 * 
	 * @param address
	 * 		The address of the client.
	 */
	protected void setAddress(SocketAddress address) 
	{
		this.address = address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatagramChannel getSocket() 
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
		if (!input.isEmpty()) {
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
	protected void onConnect()  
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocketAddress getAddress() 
	{
		return address;
	}

}