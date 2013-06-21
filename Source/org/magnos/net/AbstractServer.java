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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.magnos.util.ConcurrentSet;
import org.magnos.util.State;



/**
 * An abstract implementation of a Server.
 * 
 * @author Philip Diffenderfer
 *
 */
public abstract class AbstractServer implements Server 
{
	
	// The pipeline of the server.
	protected final Pipeline pipeline;
	
	// The set of clients this server has accepted and are still connected.
	protected final Set<Client> clients;
	
	// The state of the server.
	protected final State state;

	// The acceptor handling the server.
	protected Acceptor acceptor;
	
	// The address the server is bound to.
	protected SocketAddress address;
	
	// The selector the server uses to create workers for accepted clients.
	protected Selector selector;
	
	// The listener of events on the server.
	protected List<ServerListener> serverListeners;
	
	// The default listener of events for client created by the server.
	protected ClientListener clientListener;
	
	
	/**
	 * Instantiates a new AbstractServer.
	 * 
	 * @param acceptor
	 * 		The acceptor handling the server.
	 * @param selector
	 * 		The selector the server uses to create workers for accepted clients.
	 * @param pipeline
	 * 		The pipeline of the server.
	 */
	public AbstractServer(Selector selector, Pipeline pipeline, Acceptor acceptor) 
	{
		this.selector = selector;
		this.pipeline = pipeline;
		this.acceptor = acceptor;
		this.clients = new ConcurrentSet<Client>();
		this.state = new State(Closed);
		this.serverListeners = new ArrayList<ServerListener>(1);
		this.clientListener = pipeline.getClientListener();
	}


	/**
	 * Registers this client with its worker, server, and pipeline.
	 */
	private void register()
	{
		if (acceptor != null) {
			acceptor.getServers().add(this);	
		}
		pipeline.getServers().add(this);
	}
	
	/**
	 * Unregisters this client with its worker, server, and pipeline.
	 */
	private void unregister()
	{
		if (acceptor != null) {
			acceptor.getServers().remove(this);	
		}
		pipeline.getServers().remove(this);
	}

	/**
	 * Handles the closing of the server. This will only be invoked once.
	 */
	protected abstract void onClose() throws IOException;
	
	/**
	 * Handles the binding of the server to the address.
	 */
	protected abstract void doBind(SocketAddress address) throws IOException;

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean listen(int port)
	{
		return listen(new InetSocketAddress(port));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean listen(SocketAddress address)
	{
		synchronized (state) 
		{
			// If currently connecting wait to be connected.
			if (state.equals(Disconnecting)) {
				state.waitFor(Closed);
			}
			// If closed, we can now connect.
			if (state.equals(Closed))
			{
				try {
					// Attempt connection
					doBind(address);

					// Add client to server/pipeline sets.
					register();
					
					// Connect successful, notify.
					state.set(Accepting);
				}
				catch (IOException e)
				{
					// Notify listeners of errors
					invokeError(e);
					
					// "Close" the connection on error
					close();
				}
			}
			
			// Only successful if the binded address equals the given address.
			return address.equals(getAddress());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close()
	{
		synchronized (state) 
		{
			if (state.has(Accepting | Disconnecting)) 
			{
				state.set(Closed);
				
				for (Client client : clients) {
					client.close();
				}
				
				try {
					onClose();	
				}
				catch (IOException e) {
					invokeError(e);
				}

				invokeStop();
				
				unregister();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isClosed()
	{
		return state.equals(Closed);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Set<Client> getClients()
	{
		return clients;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final State state()
	{
		return state;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Acceptor getAcceptor()
	{
		return acceptor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Selector getSelector() 
	{
		return selector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setSelector(Selector selector) 
	{
		this.selector = selector;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(ServerListener listener)
	{
		serverListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ServerListener> getListeners()
	{
		return serverListeners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ClientListener getClientListener() 
	{
		return clientListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setClientListener(ClientListener listener) 
	{
		this.clientListener = listener;;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SocketAddress getAddress() 
	{
		return address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Pipeline getPipeline() 
	{
		return pipeline;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDisconnecting()
	{
		return state.equals(Disconnecting);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void disconnect()
	{
		state.cas(Accepting, Disconnecting);
	}

	/**
	 * Invokes start event to all listeners.
	 */
	public void invokeStart() 
	{
		for (ServerListener sl : serverListeners) {
			sl.onServerStart(this);
		}
	}

	/**
	 * Invokes bind event to all listeners.
	 */
	public void invokeBind(SocketAddress address) 
	{
		for (ServerListener sl : serverListeners) {
			sl.onServerBind(this, address);
		}
	}

	/**
	 * Invokes accept event to all listeners.
	 */
	public void invokeAccept(Client client) 
	{
		for (ServerListener sl : serverListeners) {
			sl.onServerAccept(this, client);
		}
	}

	/**
	 * Invokes error event to all listeners.
	 */
	public void invokeError(Exception e) 
	{
		for (ServerListener sl : serverListeners) {
			sl.onServerError(this, e);
		}
	}

	/**
	 * Invokes stop event to all listeners.
	 */
	public void invokeStop() 
	{
		for (ServerListener sl : serverListeners) {
			sl.onServerStop(this);
		}
	}
	
}