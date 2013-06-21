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

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.magnos.task.Task;


/**
 * The task to register a channel to its worker's selector with the given
 * interest and attachment.
 * 
 * @author Philip Diffenderfer
 *
 */
public class NioRegisterTask extends Task<SelectionKey> 
{
	
	// The channel to register.
	private SelectableChannel channel;
	
	// The initial interest of the channel.
	private int interestOps;
	
	// The attachment to the channel.
	private Object attachment;
	
	/**
	 * Instantiates a new NioRegisterTask.
	 * 
	 * @param channel
	 * 		The channel to register.
	 * @param interestOps
	 * 		The initial interest of the channel.
	 * @param attachment
	 * 		The attachment to the channel.
	 */
	public NioRegisterTask(SelectableChannel channel, int interestOps, Object attachment) 
	{
		this.channel = channel;
		this.interestOps = interestOps;
		this.attachment = attachment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected SelectionKey execute() 
	{
		SelectionKey key = null;
		try 
		{
			// Get the selector by assing the handler is an NioWorker. If it's
			// not then an exception will be thrown... this will occur only
			// if a user interferes with the task in some manor.
			Selector selector = ((NioWorker)getHandler()).getSelector();
			
			// Create the key by registering the channel.
			key = channel.register(selector, interestOps, attachment);	
		}
		catch (ClosedChannelException e) {
			// TODO
			e.printStackTrace();
		}
		
		// Return the key generated (if any).
		return key;
	}
	
}