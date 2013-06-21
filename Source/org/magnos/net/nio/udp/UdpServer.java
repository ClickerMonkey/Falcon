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
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.magnos.io.buffer.BufferFactory;
import org.magnos.net.AbstractServer;
import org.magnos.net.Acceptor;
import org.magnos.net.Client;
import org.magnos.net.Pipeline;
import org.magnos.net.Selector;
import org.magnos.net.Server;
import org.magnos.net.nio.NioInterestTask;
import org.magnos.net.nio.NioRegisterTask;
import org.magnos.net.nio.NioWorker;


public class UdpServer extends AbstractServer
{

	private DatagramChannel channel;
	private SelectionKey key;
	private BufferFactory factory;
	private ByteBuffer input;
	private Queue<UdpPacket> packets;
	private Map<SocketAddress, UdpServerClient> clientMap;
	

	protected UdpServer(Selector selector, Pipeline pipeline, Acceptor acceptor) 
	{
		super(selector, pipeline, acceptor);
		this.clientMap = new ConcurrentHashMap<SocketAddress, UdpServerClient>();
		this.packets = new ArrayDeque<UdpPacket>();
		this.factory = pipeline.getBufferFactory();
	}


	@Override
	protected void doBind(SocketAddress address) throws IOException 
	{
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
		channel.socket().setReuseAddress(true);
		
		invokeStart();
		
		channel.socket().bind(address);
		
		invokeBind(address);
		
		NioRegisterTask task = new NioRegisterTask(channel, SelectionKey.OP_READ, this);
		task.setHandler((NioWorker)getAcceptor());
		
		key = task.sync();
		
		input = factory.allocate(channel.socket().getReceiveBufferSize());
	}

	@Override
	public void handleAccept() 
	{
	}

	@Override
	public void onClose() 
	{
		try {
			channel.close();
			key.cancel();
			for (UdpServerClient client : clientMap.values()) {
				client.close();
			}
		} catch (Exception e) {
			invokeError(e);
		}
	}

	@Override
	public DatagramChannel getSocket() 
	{
		return channel;
	}

	@Override
	public void handleConnect() 
	{
	}

	protected void send(UdpPacket packet) {
		synchronized (packets) {
			packets.offer(packet);
			
			NioInterestTask task = new NioInterestTask(key, SelectionKey.OP_WRITE);
			task.setHandler((NioWorker)acceptor);
			task.async();
		}
	}
	
	protected void unregister(UdpServerClient client) {
		clientMap.remove(client.getAddress());
	}
	
	@Override
	public void handleRead() 
	{
		try {
			if (state.equals(Server.Closed)) {
				return;
			}
			
			SocketAddress from = channel.receive(input);
			input.flip();
			
			if (!input.hasRemaining()) {
				return;
			}
			
			UdpServerClient client = clientMap.get(from);
			if (client == null) {
				client = new UdpServerClient(pipeline, this);
				if (clientListener != null) {
					client.getListeners().add(clientListener);	
				}
				client.setAddress(from);
				client.setServer(this);
				
				client.state().set(Client.Connecting);
				
				client.initialize();
				
				invokeAccept(client);
				
				client.invokeOpen();
				
				client.state().set(Client.Connected);
				client.invokeConnect();
				
				clientMap.put(from, client);
			}
			
			client.read(input);
			
			if (client.isDisconnecting()) {
				client.close();
			}
			
			input.clear();
		}
		catch (Exception e) {
			invokeError(e);
			close();
		}
	}

	@Override
	public void handleWrite() 
	{
		try {
			if (state.equals(Server.Closed)) {
				return;
			}
			for (;;) {
				UdpPacket packet = null;
				synchronized (packets) {
					packet = packets.poll();
				}
				if (packet == null) {
					break;
				}
				channel.send(packet.getData(), packet.getClient().getAddress());
			}
			key.interestOps(SelectionKey.OP_READ);
		}
		catch (Exception e) {
			invokeError(e);
			close();
		}
	}
	

}