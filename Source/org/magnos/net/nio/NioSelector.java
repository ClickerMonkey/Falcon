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

import org.magnos.net.AbstractSelector;

/**
 * The selector which creates workers and acceptors for NIO pipelines.
 * 
 * @author Philip Diffenderfer
 *
 */
public class NioSelector extends AbstractSelector
{

	/**
	 * Instantiates a new NioSelector.
	 */
	public NioSelector() 
	{
		super(new NioAcceptorFactory(), new NioWorkerFactory());
		
		// Start with no accepting thread, no need for an accepting thread if
		// we're a client.
		acceptorPool.setMinCapacity(0);
		acceptorPool.setMaxCapacity(1);
		
		// Start with atleast one worker, servers and clients need atleast one.
		workerPool.setCapacity(1);
	}
	
}