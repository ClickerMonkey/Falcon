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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import org.magnos.net.Acceptor;
import org.magnos.net.Adapter;
import org.magnos.net.Client;
import org.magnos.net.Server;
import org.magnos.net.Worker;
import org.magnos.task.Task;
import org.magnos.task.TaskService;
import org.magnos.util.ConcurrentSet;



/**
 * The NIO implementation of a Worker. 
 * 
 * @author Philip Diffenderfer
 *
 */
public class NioWorker extends TaskService implements Worker, Acceptor
{
	
	// The set of clients being handler by this worker.
	private final Set<Client> clients;
	
	// The set of servers being handled by this worker.
	private final Set<Server> servers;
	
	// The selector that handles the IO events of the clients and servers.
	private Selector selector;
	
	/**
	 * Instantiates a new NioWorker.
	 * 
	 * @throws IOException
	 * 		A selector could not be opened.
	 */
	public NioWorker() 
	{
		super(false);
		
		this.clients = new ConcurrentSet<Client>();
		this.servers = new ConcurrentSet<Server>();
		
		start();
	}
	
	/**
	 * Returns the selector of the worker which handles IO events on the clients
	 * and servers.
	 * 
	 * @return
	 * 		The reference to the selector.
	 */
	public Selector getSelector() 
	{
		return selector;
	}
	
	/**
	 * The service is already registered with the release, invoke the parents 
	 * awake and this services awake by nudging the selector from blocking.
	 */
	@Override
	public void awake()
	{
		super.awake();
		selector.wakeup();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addEvent(Task<?> task) 
	{
		boolean added = super.addEvent(task);
		// If a task is added, wake-up the event.
		if (added && selector != null) {
			selector.wakeup();
		}
		
		return added;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onExecute() 
	{
		int selected = 0;
		
		try {
			// Enter the blockable area if the service has been interrupted.
			if (release.enter()) {
				try {
					// Select all ready events...
					selected = selector.select();	
				}
				// If select throws an error (which will happen  its blocking
				// and this service has been requested to pause or stop).
				finally {
					release.exit();	
				}
			}
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
		
		if ( selected == 0 )
		{
			return;
		}
		
		// Only iterate the events if some were given (uninterrupted).
		Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
		
		// Iterate through each selected key in the set...
		while (keys.hasNext()) 
		{
			// Grab the next key and remove it from the set.
			SelectionKey key = keys.next();
			keys.remove();
			
			// If the key has been canceled, skip over it.
			if (!key.isValid()) {
				continue;
			}
			
			// The adapter that handles all events.
			Adapter adapter = (Adapter)key.attachment();
			
			// If the key is acceptable assume its attachment is a Server.
			if (key.isAcceptable()) {
				// Accept the connection, then continue processing keys.
				adapter.handleAccept();
			}
			else {
				// If the key is connectable, handle the connection.
				if (key.isConnectable()) {
					adapter.handleConnect();
				}
				// If the key is readable then handle reading, but only if
				// the key is still valid. The previous connect may have
				// canceled the key.
				if (key.isValid() && key.isReadable()) {
					adapter.handleRead();
				}
				// If the key is writable then handle writing, but only
				// if the key is still valid. The previous reading or
				// connecting may have canceled the key.
				if (key.isValid() && key.isWritable()) {
					adapter.handleWrite();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart() 
	{
		// If the selector has not been started...
		if (selector == null || !selector.isOpen())
		{
			try {
				selector = Selector.open();
			}
			catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStop() 
	{
		try {
			// Go through each key registered with the selector.
			for (SelectionKey key : selector.keys()) 
			{
				// Invoke the close method on the server or client to ensure its
				// sockets are closed, key is canceled, and listeners are
				// notified that it has been closed.
				Object obj = key.attachment();
				if (obj instanceof Server) {
					((Server)obj).close();
				}
				if (obj instanceof Client) {
					((Client)obj).close();
				}
			}
			// Close the selector.
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPause() 
	{
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume()
	{
		
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
	public Set<Server> getServers() 
	{
		return servers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReusable() 
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUnused() 
	{
		return clients.isEmpty() && servers.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void free() 
	{
		stop();
	}
	
}