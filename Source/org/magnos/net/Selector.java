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

import org.magnos.resource.ResourcePool;

/**
 * A selector assists in proving and managing pools of acceptors and workers. 
 * Each I/O technology (java.io and java.nio) will have their own selector since
 * NIO can take advantage of event based I/O and OIO requires a thread per 
 * socket (client and server).
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Selector 
{
	
	/**
	 * Returns the pool of workers for this selector.
	 * 
	 * @return
	 * 		The reference to the pool of workers.
	 */
	public ResourcePool<Worker> getWorkers();
	
	/**
	 * Takes a worker from the pool of workers which is ready to handle a 
	 * client.
	 * 
	 * @return
	 * 		The reference to the worker which is ready to handle a client.
	 */
	public Worker getWorker();
	
	/**
	 * Returns the pool of acceptors for this selector.
	 * 
	 * @return
	 * 		The reference to the pool of acceptors.
	 */
	public ResourcePool<Acceptor> getAcceptors();
	
	/**
	 * Takes an acceptor from the pool of workers which is ready to handle a 
	 * server.
	 * 
	 * @return
	 * 		The reference to the acceptor which is ready to handle a server.
	 */
	public Acceptor getAcceptor();
	
	/**
	 * Start the pool of acceptors and workers if it has not been started 
	 * already. A pool must be started to handle connecting clients or binding 
	 * servers. However this will automatically be invoked by pipelines each 
	 * time a client is trying to connect or a server is trying to bind to an 
	 * address.
	 */
	public void start();
	
	/**
	 * Stops the pools of acceptors and workers.
	 */
	public void stop();
	
}
