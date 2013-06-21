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
 * A listener to the events of a client.
 *
 * @author Philip Diffenderfer
 *
 */
public interface ClientListener
{

	/**
	 * The client has been accepted or is connecting. This can be used to to
	 * tune the socket parameters before it binds to an address. This is invoked
	 * in the context of the Pipelines connect method.
	 * 
	 * @param client
	 * 		The client to initialize.
	 */
	public boolean onClientOpen(Client client);
	
	/**
	 * The client has successfully connected and is ready reading and writing. 
	 * This is invoked in the context of the Pipelines connect method or the
	 * workers thread if the connect was invoked in non-blocking mode.
	 * 
	 * @param client
	 * 		The client that has connected.
	 */
	public void onClientConnect(Client client);

	/**
	 * The client is aboue to read from its socket. This can be used to do 
	 * custom handling of sockets. This is invoked in the context of the Worker 
	 * thread.
	 * 
	 * @param client
	 * 		The client reading.
	 */
	public void onClientRead(Client client);
	
	/**
	 * The client has received an object which was decoded from its input. This 
	 * is invoked in the context of the Worker thread.
	 * 
	 * @param client
	 * 		The client that received the data.
	 * @param data
	 * 		The data received by the client.
	 */
	public void onClientReceive(Client client, Object data);

	/**
	 * The client is about to write to its socket. This can be used to do custom 
	 * handling of sockets. This is invoked in the context of the Worker thread.
	 * 
	 * @param client
	 * 		The client writing.
	 */
	public void onClientWrite(Client client);
	
	/**
	 * The client has sent an object - encoded it to its output and will send it
	 * soon. This is invoked in the context of the Worker thread.
	 *  
	 * @param client
	 * 		The client that sent the data.
	 * @param data
	 * 		The data sent by the client.
	 */
	public void onClientSend(Client client, Object data);

	/**
	 * The client has attempted to send an object that does not have a matching
	 * protocol in the clients pipeline to properly encode the data. This is 
	 * invoked in the context of the Worker thread.
	 * 
	 * @param client
	 * 		The client thats discarding the data.
	 * @param data
	 * 		The data that could not be encoded.
	 */
	public void onClientDiscard(Client client, Object data);
	
	/**
	 * An error has occurred with the client, this can be thrown from any of the
	 * above events.
	 * 
	 * @param client
	 * 		The client that threw the exception.
	 * @param e
	 * 		The excpetion thrown by performing some action on the client.
	 */
	public void onClientError(Client client, Exception e);
	
	/**
	 * The client has been requested to disconnect. When disconnecting the 
	 * client will refuse any more data to send and will not read from the 
	 * socket. Once all output has been flushed the client closes. This is 
	 * invoked in the context of the invoking thread.
	 * 
	 * @param client
	 * 		The client being disconnected.
	 */
	public void onClientDisconnect(Client client);
	
	/**
	 * The client has been closed. This may happen manually, from disconnecting,
	 * or because an error was thrown and had to close the client. This is 
	 * invoked in the context of the invoking thread or if the client is
	 * disconnecting its in the context of the Worker thread.
	 * 
	 * @param client
	 * 		The client that was closed.
	 */
	public void onClientClose(Client client);
	
}