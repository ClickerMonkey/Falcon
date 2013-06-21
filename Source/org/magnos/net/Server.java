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

import java.net.SocketAddress;
import java.util.List;
import java.util.Set;

import org.magnos.util.ConcurrentSet;
import org.magnos.util.State;




/**
 * A server is created by a pipeline and listens on a port to accept clients.
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Server extends Adapter
{
	
	/**
	 * The server is in an accepting state.
	 */
	public static final int Accepting = State.create(0);
	
	/**
	 * The server is currently trying to disconnect. In this state the server
	 * will not accept any more clients.
	 */
	public static final int Disconnecting = State.create(1);
	
	/**
	 * The server is closed and no longer binded to the port it was listening to
	 * and cannot accept clients.
	 */
	public static final int Closed = State.create(2);
	
	/**
	 * Listens to the provided port.
	 * 
	 * @param port
	 * 		The port to listen to.
	 * @return True if the server is now listening on this port.
	 */
	public boolean listen(int port);
	
	/**
	 * Listens on the provided SocketAddress.
	 * 
	 * @param address
	 * 		The address to listen on.
	 * @return True if the server is now listening on this address.
	 */
	public boolean listen(SocketAddress address);
	
	/**
	 * The pipeline that created the server.
	 * 
	 * @return
	 * 		The reference to the pipeline.
	 */
	public Pipeline getPipeline();
	
	/**
	 * Returns the acceptor that handles this server.
	 * 
	 * @return
	 * 		The reference to the acceptor that handles this server.
	 */
	public Acceptor getAcceptor();
	
	/**
	 * The address the server is binded on and listening to.
	 * 
	 * @return
	 * 		The reference to the address the server is listening on.		
	 */
	public SocketAddress getAddress();
	
	/**
	 * The Socket of the server, This may be many different things according
	 * to the server implementation.
	 * 
	 * @return
	 * 		The reference to the socket object the server is accepting on.
	 */
	public Object getSocket();
	
	/**
	 * Returns the current state of this server. This is controlled internally 
	 * and should not be modified externally.
	 * 
	 * @return
	 * 		The reference to the server's state.
	 */
	public State state();
	
	/**
	 * Returns whether this server has been closed.
	 * 
	 * @return
	 * 		True if the server is closed, otherwise false.
	 */
	public boolean isClosed();
	
	/**
	 * Closes this server immediately. This will close all clients connected to
	 * this server, deallocate all associated resources, and close any open 
	 * sockets.
	 */
	public void close();
	
	/**
	 * Returns whether this server is currently disconnecting. If this server
	 * is disconnecting the server will close once all clients have successfully
	 * disconnected. When disconnecting a server will not accept any more 
	 * clients.
	 * 
	 * @return
	 * 		True if the server is disconnecting, otherwise false.
	 */
	public boolean isDisconnecting();
	
	/**
	 * Disconnects this server by telling to to disconnect all clients and to 
	 * cease accepting any new clients.
	 */
	public void disconnect();
	
	/**
	 * Returns the set of open clients accepted by this server. This set should
	 * not be modified and is not thread-safe and should only be used for 
	 * iterative purposes. 
	 * 
	 * @return
	 * 		The reference to the set of clients connected to this server.
	 * @see ConcurrentSet
	 */
	public Set<Client> getClients();
	
	/**
	 * Adds a listener to this server.
	 * 
	 * @param listener
	 * 		The listener to add to this server.
	 */
	public void addListener(ServerListener listener);
	
	/**
	 * Returns the list of listeners of events on this server. Listeners can be
	 * directly added to this list.
	 *  
	 * @return
	 * 		The reference to the list of listeners.
	 */
	public List<ServerListener> getListeners();
	
	/**
	 * Returns the default ClientListener to add to accepted clients. If this is
	 * null then no ClientListener will be added to the client.
	 * 
	 * @return
	 * 		The reference to the default ClientListener.
	 */
	public ClientListener getClientListener();
	
	/**
	 * Sets the default ClientListener to add to accepted clients. If this is
	 * null then no ClientListener will be added to the client.
	 * 
	 * @param listener
	 * 		The new default ClientListener.
	 */
	public void setClientListener(ClientListener listener);
	
	/**
	 * Returns the selector the server uses to create workers for accepted 
	 * clients.
	 * 
	 * @return
	 * 		The reference to the selector used by the server.
	 */
	public Selector getSelector();
	
	/**
	 * Sets the selector the server uses to create workers for accepted clients.
	 * 
	 * @param selector
	 * 		The selector to use to create workers for accepted clients.
	 */
	public void setSelector(Selector selector);
	
}