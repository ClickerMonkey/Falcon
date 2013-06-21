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

import java.util.Set;

import org.magnos.io.buffer.BufferFactory;
import org.magnos.util.ConcurrentSet;



/**
 * A pipeline creates servers and clients and defines the protocols and default
 * listeners the servers and client utilize.
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Pipeline 
{
	
	/**
	 * Creates a new Server. If there is an error 
	 * creating the server, null will be returned. The default ServerListener 
	 * will be added to the server if non-null. 
	 *  
	 * @return
	 * 		The reference to the newly created server, or null.
	 */
	public Server newServer();
	
	/**
	 * Creates a new Client for connecting to servers with. If there is 
	 * an error creating the client, null will be returned. The default 
	 * ClientListener will be added to the client if non-null.
	 * 
	 * @return
	 * 		The reference to the newly created client, or null.
	 */
	public Client newClient();
	
	/**
	 * Creates a new Client for a server implementation. If there is 
	 * an error creating the client, null will be returned. The default 
	 * ClientListener will be added to the client if non-null.
	 * 
	 * @param server
	 * 		The server the client is for.
	 * @return
	 * 		The reference to the newly created client, or null.
	 */
	public Client newClient(Server server);
	
	/**
	 * Returns the set of open servers created by this pipeline. This set should
	 * not be modified and is not thread safe, it is only meant for iterating 
	 * the set of servers.
	 *  
	 * @return
	 * 		The reference to a set of open servers.
	 * @see ConcurrentSet
	 */
	public Set<Server> getServers();
	
	/**
	 * Returns the set of open clients created by this pipeline. This set should
	 * not be modified and is not thread safe, it is only meant for iterating
	 * the set of clients.
	 * 
	 * @return
	 * 		The reference to a set of open clients.
	 * @see ConcurrentSet
	 */
	public Set<Client> getClients();
	
	
	/**
	 * Returns the default protocol data type. This is the first protocol 
	 * clients of pipeline use to read data until its changed.
	 * 
	 * @return
	 * 		The reference to the default protocol.
	 */
	public Class<?> getDefaultProtocol();
	
	/**
	 * Sets the default protocol data type. This is the first protocol 
	 * clients of pipeline use to read data until its changed.
	 * 
	 * @param type
	 * 		The default protocol.
	 */
	public void setDefaultProtocol(Class<?> type);
	
	/**
	 * Adds the protocol to the pipeline. A protocol is mapped by a class type,
	 * when a client has to read data it queries its pipeline with its current
	 * protocol type and proceeds to decode read data. 
	 * 
	 * @param <D>
	 * 		The data type encoded and decoded by the protocol.
	 * @param protocol
	 * 		The protocol to use to encode and decode the given data type.
	 */
	public <D> void addProtocol(Protocol<D> protocol);

	/**
	 * Returns the selector used by the pipeline to allocate workers for clients
	 * and acceptors for servers. This is also used by the servers to create
	 * workers to handle clients unless changed explicitly for a given server.
	 * 
	 * @return
	 * 		The reference to the Selector.
	 */
	public Selector getSelector();
	
	/**
	 * Sets the selector used by the pipeline to allocate workers for clients
	 * and acceptors for servers. This is also used by the servers to create
	 * workers to handle clients unless changed explicitly for a given server.
	 * 
	 * @param selector
	 * 		The new selector of the pipeline.
	 */
	public void setSelector(Selector selector);
	
	/**
	 * Gets the protocol associated with the given class.
	 * 
	 * @param <D>
	 * 		The data type encoded and decoded by the protocol.
	 * @param type
	 * 		The class of the data type.
	 * @return
	 * 		The reference to the protocol which encodes and decodes the given
	 * 		data type.
	 */
	public <D> Protocol<D> getProtocol(Class<D> type);
	
	
	/**
	 * Returns the default ClientListener for clients created with this 
	 * pipeline. If this is non-null and a ClientListener is not specified on
	 * connect this will be added to the clients listener list.
	 * 
	 * @return
	 * 		The reference to the default ClientListener.
	 */
	public ClientListener getClientListener();
	
	/**
	 * Sets the default ClientListener for clients created with this pipeline. 
	 * If this is non-null and a ClientListener is not specified on connect this
	 * will be added to the client's listener list.
	 * 
	 * @param listener
	 * 		The default ClientListener.
	 */
	public void setClientListener(ClientListener listener);
	
	/**
	 * Returns the default ServerListener for servers created with this 
	 * pipeline. If this is non-null and a ServerListener is not specified on
	 * listen this will be added to the servers listener list.
	 * 
	 * @return
	 * 		The reference to the default ServerListener.
	 */
	public ServerListener getServerListener();
	
	/**
	 * Sets the default ServerListener for servers created with this pipeline. 
	 * If this is non-null and a ServerListener is not specified on listen this
	 * will be added to the server's listener list.
	 * 
	 * @param listener
	 * 		The default ServerListener.
	 */
	public void setServerListener(ServerListener listener);
	
	/**
	 * Returns the BufferFactory used by the clients and servers created by
	 * this pipeline.
	 * 
	 * @return
	 * 		The reference to a BufferFactory.
	 */
	public BufferFactory getBufferFactory();
	
	/**
	 * Sets the BufferFactory used by the clients and servers created by this 
	 * pipeline.
	 * 
	 * @param factory
	 * 		The new BufferFactory.
	 */
	public void setBufferFactory(BufferFactory factory);
	
}