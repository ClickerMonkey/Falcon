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

import org.magnos.resource.ResourceFactory;
import org.magnos.resource.ResourcePool;

/**
 * An abstract implementation of a selector.
 * 
 * @author Philip Diffenderfer
 *
 */
public abstract class AbstractSelector implements Selector 
{

	// The pool of acceptors.
	protected final ResourcePool<Acceptor> acceptorPool;
	
	// The pool of workers.
	protected final ResourcePool<Worker> workerPool;
	
	
	/**
	 * Instantiates an AbstractSelector.
	 * 
	 * @param acceptors
	 * 		The pool of acceptors.
	 * @param workers
	 * 		The pool of workers.
	 */
	public AbstractSelector(ResourceFactory<Acceptor> acceptors, ResourceFactory<Worker> workers) 
	{ 
		this.acceptorPool = new ResourcePool<Acceptor>(acceptors);
		this.workerPool = new ResourcePool<Worker>(workers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourcePool<Acceptor> getAcceptors() 
	{
		return acceptorPool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourcePool<Worker> getWorkers() 
	{
		return workerPool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Worker getWorker() 
	{
		return workerPool.allocate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Acceptor getAcceptor() 
	{
		return acceptorPool.allocate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() 
	{
		acceptorPool.populate();
		workerPool.populate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() 
	{
		acceptorPool.empty();
		workerPool.empty();
	}

}
