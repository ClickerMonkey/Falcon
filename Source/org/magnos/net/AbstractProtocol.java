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

/**
 * An abstract implementation of a Protocol that handles basic getters and 
 * setters.
 * 
 * @author Philip Diffenderfer
 *
 * @param <D>
 * 		The type of data thats being encoded and decoded.
 */
public abstract class AbstractProtocol<D> implements Protocol<D> 
{
	
	// The listener to the events of the protocol.
	protected ProtocolListener<D> listener;
	
	// The data type.
	protected final Class<D> type;

	/**
	 * Instantiates an AbstractProtocol with a default listener.
	 */
	public AbstractProtocol(Class<D> type)
	{
		this.type = type;
		this.listener = new ProtocolListenerAdapter<D>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<D> getType()
	{
		return type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProtocolListener<D> getListener() 
	{
		return listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setListener(ProtocolListener<D> listener) 
	{
		this.listener = listener;
	}
	
}
