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

/**
 * A basic ServerListener which prints all events to stdout.
 * 
 * @author Philip Diffenderfer
 *
 */
public class DefaultServerListener implements ServerListener 
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServerAccept(Server server, Client client) 
	{
		System.out.format("Server %s accepted client %s\n", server.getAddress(), client.getAddress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServerError(Server server, Exception e) 
	{
		System.err.print("Server Error: ");
		e.printStackTrace();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServerBind(Server server, SocketAddress address) 
	{
		System.out.format("Server bound to %s\n", address);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServerStart(Server server) 
	{
		System.out.format("Server started on %s\n", server.getAddress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onServerStop(Server server) 
	{
		System.out.format("Server stopped on %s\n", server.getAddress());
	}

}
