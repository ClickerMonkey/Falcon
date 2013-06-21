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
 * An adapter is anything which can read, write, accept, or connect. This is
 * used by workers and acceptors to process clients and servers.
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Adapter 
{
	
	/**
	 * Notifies the server/client that it can now read from its socket. This 
	 * should not be invoked outside of the server/clients worker/acceptor.
	 */
	public void handleRead();
	
	/**
	 * Notifies the server/client that it can now write to its socket. This 
	 * should not be invoked outside of the server/clients worker/acceptor.
	 */
	public void handleWrite();
	
	/**
	 * Notifies the server/client it can now accept clients on its socket. This 
	 * should not be invoked outside of the server/clients worker/acceptor.
	 */
	public void handleAccept();
	
	/**
	 * Notifies the server/client it can now complete its pending connection.  
	 * This should not be invoked outside of the server/clients worker/acceptor.
	 */
	public void handleConnect();
	
}
