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

import org.magnos.io.BufferStream;

/**
 * A basic ProtocolListener which prints all events to stdout.
 * 
 * @author Philip Diffenderfer
 *
 * @param <D>
 * 		The data encoded and decoded.
 */
public class ProtocolListenerAdapter<D> implements ProtocolListener<D> 
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDecodeError(Protocol<D> protocol, BufferStream in, Client client, Exception e) 
	{
		System.err.println("Decode Error: ");
		e.printStackTrace();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEncodeError(Protocol<D> protocol, BufferStream out, D data, Client client, Exception e) 
	{
		System.err.println("Encode Error: ");
		e.printStackTrace();
	}

}
