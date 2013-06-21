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

import java.nio.channels.SelectionKey;

import org.magnos.task.Task;


/**
 * The task to set the interest of a given SelectionKey.
 * 
 * @author Philip Diffenderfer
 *
 */
public class NioInterestTask extends Task<SelectionKey> 
{
	
	// The key to change the interest of.
	private SelectionKey key;
	
	// The new interest of the key.
	private int interestOps;
	
	/**
	 * Instantiates a new NioInterestTask.
	 * 
	 * @param key
	 * 		The key to change the interest of.
	 * @param interestOps
	 * 		The new interest of the key.
	 */
	public NioInterestTask(SelectionKey key, int interestOps) 
	{
		this.key = key;
		this.interestOps = interestOps;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected SelectionKey execute() 
	{
		// Change the interest!
		key.interestOps(interestOps);
		
		// Return the key who's interest was changed.
		return key;
	}
	
}