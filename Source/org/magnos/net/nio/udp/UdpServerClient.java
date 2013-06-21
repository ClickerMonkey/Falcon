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
import java.nio.ByteBuffer;

import org.magnos.net.AbstractClient;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;


/**
 * A simulated client for UDP using NIO.
 * 
 * @author Philip Diffenderfer
 *
 */
public class UdpServerClient extends AbstractClient 
{

	// The server handling all I/O for this simulated client.
	private UdpServer server;
	
	// The address of the client.
	private SocketAddress address;
	
	
	/**
	 * Instantiates a new UdpServerClient.
	 * 
	 * @param pipeline
	 * 		The pipeline the client should use.
	 * @param server
	 * 		The server which accepted the client, or null if the client was
	 * 		created by the pipeline.
	 */
	protected UdpServerClient(Pipeline pipeline, Server server) 
	{
		// No worker, the server handles reading and writing.
		super(pipeline, null, server);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doConnect(SocketAddress address) throws IOException 
	{
		throw new IOException();
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
	 * Internally sets the server of the client.
	 * 
	 * @param server
	 * 		The server which handles all reading and writing for this client.
	 */
	protected void setServer(UdpServer server) 
	{ 
		this.server = server;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onFlush() 
	{
		if (state.equals(Connected)) {
			invokeWrite();
			
			while (encode() > 0) 
			{
				if (output.size() > 0)
				{
					ByteBuffer data = bufferFactory.allocate(output.size());
					data.put(output.getReader(0)); // TODO
					server.send(new UdpPacket(this, data));
					output.clear();	
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClose() 
	{
		server.unregister(this);
	}
	
	/**
	 * Reads the packet into this clients input.
	 * 
	 * @param packet
	 * 		The packet to read in.
	 */
	protected void read(ByteBuffer packet)
	{
		if (state.equals(Connected)) 
		{
			input.drain(packet);
			invokeRead();
			if (!input.isEmpty() && state.equals(Connected)) {
				decode();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocketAddress getAddress() 
	{
		return address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getSocket() 
	{
		// The servers socket is this clients socket technically.
		return server.getSocket();
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
	protected void onRead() 
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onWrite() 
	{
	}

}
