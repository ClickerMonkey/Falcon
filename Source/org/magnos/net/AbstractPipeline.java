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

package org.magnos.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.magnos.io.buffer.BufferFactory;
import org.magnos.util.ConcurrentSet;



/**
 * A basic implementation of a Pipeline.
 * 
 * @author Philip Diffenderfer
 *
 */
public abstract class AbstractPipeline implements Pipeline 
{
	
	// The map of protocols to their class type.
	protected final Map<Class<?>, Protocol<?>> protocols;
	
	// The set of servers started by this pipeline.
	protected final Set<Server> servers;
	
	// The set of clients connected to servers by this pipeline.
	protected final Set<Client> clients;
	
	// The default client listener for all clients.
	protected ClientListener clientListener;
	
	// The default server listener for all servers.
	protected ServerListener serverListener;
	
	// The factory which handles allocation and deallocated of ByteBuffers.
	protected BufferFactory bufferFactory;
	
	// The default (initial) protocol of a client.
	protected Class<?> defaultProtocol;
	
	// The selector used to get Acceptors and Workers.
	protected Selector selector;
	
	
	
	/**
	 * Instantiates an AbstractPipeline.
	 * 
	 * @param selector
	 * 		The selector the pipeline uses to get Acceptors and Workers.
	 */
	public AbstractPipeline(Selector selector) 
	{
		this.selector = selector;
		this.protocols = new HashMap<Class<?>, Protocol<?>>(); 
		this.servers = new ConcurrentSet<Server>();
		this.clients = new ConcurrentSet<Client>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Client newClient()
	{
		return newClient(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Server> getServers()
	{
		return servers;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Client> getClients()
	{
		return clients;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientListener getClientListener() 
	{
		return clientListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClientListener(ClientListener listener) 
	{
		this.clientListener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServerListener getServerListener() 
	{
		return serverListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setServerListener(ServerListener listener) 
	{
		this.serverListener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferFactory getBufferFactory() 
	{
		return bufferFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBufferFactory(BufferFactory factory) 
	{
		this.bufferFactory = factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Selector getSelector() 
	{
		return selector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelector(Selector selector) 
	{
		this.selector = selector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <D> void addProtocol(Protocol<D> protocol) 
	{
		protocols.put(protocol.getType(), protocol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <D> Protocol<D> getProtocol(Class<D> type) 
	{
		return (Protocol<D>)protocols.get(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaultProtocol(Class<?> type) 
	{
		this.defaultProtocol = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getDefaultProtocol() 
	{
		return defaultProtocol;
	}
	
}