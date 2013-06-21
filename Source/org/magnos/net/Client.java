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
import java.util.List;
import java.util.Queue;

import org.magnos.io.BufferStream;
import org.magnos.util.State;



/**
 * A client is created when a pipeline is requested to connect to some
 * server listening on some address OR when a server accepts a client.
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Client extends Adapter
{
	
	/**
	 * The client is not completely connected but is in the process. This exists
	 * for non-blocking connecting.
	 */
	public static final int Connecting = State.create(0);
	
	/**
	 * The client is connected and can read and write data.
	 */
	public static final int Connected = State.create(1);
	
	/**
	 * The client has been requested to close and the client is in the process.
	 * The client cannot read but it can write remaining data.
	 */
	public static final int Disconnecting = State.create(2);
	
	/**
	 * The client has been closed and it can no longer read or write.
	 */
	public static final int Closed = State.create(3);

	
	/** XXX
	 * Creates a Client connected to a server at the given address. If there is 
	 * an error creating the client, null will be returned. The default 
	 * ClientListener will be added to the client if non-null. This may be a 
	 * blocking or non-blocking invocation depending on the implementation.
	 * 
	 * @param address
	 * 		The address of the server to connect to.
	 * @return
	 * 		The reference to the newly created client, or null.
	 */
	public boolean connect(SocketAddress address);
	
	/** XXX
	 * Creates a Client connected to a server at the given address. If there is 
	 * an error creating the client, null will be returned. The default 
	 * ClientListener will be added to the client if non-null. This may be a 
	 * blocking or non-blocking invocation depending on the implementation.
	 * 
	 * @param host
	 * 		The host of the server to connect to.
	 * @param port
	 * 		The port of the server to connect to.
	 * @return
	 * 		The reference to the newly created client, or null.
	 */
	public boolean connect(String host, int port);
	
	
	/**
	 * The pipeline this client is communicating through. This is used by the
	 * client to retrieve protocols for serializing and un-serializing data.
	 * 
	 * @return
	 * 		The reference to this clients pipeline.
	 */
	public Pipeline getPipeline();
	
	/**
	 * Returns the server which accepted the client. This will be null if this
	 * client has connected to a server.
	 * 
	 * @return
	 * 		The reference to the server.
	 */
	public Server getServer();
	
	/**
	 * Returns the worker that handles this client.
	 * 
	 * @return
	 * 		The reference to the worker that handles this client.
	 */
	public Worker getWorker();
	
	/**
	 * The address of the client.
	 * 
	 * @return
	 * 		The reference to the address of the client.
	 */
	public SocketAddress getAddress();
	
	/**
	 * The Socket of the client. This may be many different things (including
	 * null for simulated clients).
	 * 
	 * @return
	 * 		The reference to the socket object the client is connected with.
	 */
	public Object getSocket();
	
	/**
	 * Returns the current state of this client. This is controlled internally 
	 * and should not be modified externally.
	 * 
	 * @return
	 * 		The reference to the client's state.
	 */
	public State state();
	
	/**
	 * Returns whether this client has been closed.
	 * 
	 * @return
	 * 		True if the client is closed, otherwise false.
	 */
	public boolean isClosed();
	
	/**
	 * Closes this client immediately. This may deallocate all associated 
	 * resources and close any open sockets.
	 */
	public void close();
	
	/**
	 * Sets whether this client will buffer its messages to send into a single
	 * chunk of data to be sent. A client can only buffer data when several
	 * objects exist in the pending queue, else the client never buffers data.
	 * 
	 * @param buffering
	 * 		True if the client should attempt to buffer objects together, 
	 * 		otherwise false.
	 */
	public void setBuffering(boolean buffering);
	
	/**
	 * Returns whether this client will buffers its messages to send into a 
	 * single chunk of data to be sent. A client can only buffer data when 
	 * several objects exist in the pending queue, else the client never buffers 
	 * data.
	 * 
	 * @return
	 * 		True if the client should attempt to buffer objects together, 
	 * 		otherwise false.
	 */
	public boolean isBuffering();
	
	/**
	 * Returns whether this client is currently disconnecting. If this client
	 * is disconnecting the client will close once all pending data is sent
	 * to the server.
	 * 
	 * @return
	 * 		True if the client is disconnecting, otherwise false.
	 */
	public boolean isDisconnecting();
	
	/**
	 * Disconnects this client by telling it to close once all pending data is
	 * sent to the server.
	 */
	public void disconnect();
	
	/**
	 * Adds a listener to this client.
	 * 
	 * @param listener
	 * 		The listener to add to this client.
	 */
	public void addListener(ClientListener listener);
	
	/**
	 * Returns the list of listeners of events on this client. Listeners can
	 * be directly added to this list.
	 * 
	 * @return
	 * 		The reference to the list of listeners.
	 */
	public List<ClientListener> getListeners();
	
	/**
	 * Returns the output stream of the client. This should not be directly
	 * accessed but if it is the flush method must be called on the stream
	 * in order to notify the client it has data to send.
	 * 
	 * @return
	 * 		The reference to the input BufferStream.
	 */
	public BufferStream getOutput();
	
	/**
	 * Returns the input stream of the client. This should not be directly 
	 * accessed but if it is the skip method must be called on the stream
	 * in order to ensure the read bytes are discarded.
	 * 
	 * @return
	 * 		The reference to the output BufferStream.
	 */
	public BufferStream getInput();
	
	/**
	 * Send the given data to the server this client is connected to. The
	 * data is not guaranteed to be sent immediately but it will be placed on
	 * a queue. The class of the given object will dictate which protocol is 
	 * used to serialize the object, if there is no protocol associated with
	 * the object then the object will be discarded.
	 * 
	 * @param data
	 * 		The data to send to the server.
	 */
	public void send(Object data);
	
	/**
	 * Returns the queue of the pending data to be sent to the server. This is
	 * not thread safe and the returned queue should not be modified.
	 * 
	 * @return
	 * 		The reference to the queue of pending data.
	 */
	public Queue<Object> pending();
	
	
	/**
	 * The current protocol of the client for reading. This can be changed at 
	 * any time but once its changed the data coming from the server will now
	 * be read with the new protocol.
	 * 
	 * @return
	 * 		The protocol type.
	 */
	public Class<?> getProtocol();
	
	/**
	 * Sets the current protocol of the client. When the server sends data the
	 * client will use the current protocol to parse that data. If the current
	 * protocol is not capable of reading the data on this client the input will
	 * grow with all data received until the client properly processes the data 
	 * in the input stream. This should be changed very carefully.
	 * 
	 * @param type
	 * 		The new protocol type.
	 */
	public void setProtocol(Class<?> type);
	
	
	
	/**
	 * Attaches any object to this client. The object to attach can be retrieved
	 * at any time but is not thread safe.
	 * 
	 * @param attachment
	 * 		The object to attach to this client.
	 */
	public void attach(Object attachment);
	
	/**
	 * Returns the last object attached to this client. This is not thread safe.
	 * 
	 * @return
	 * 		The object attached to this client.
	 */
	public Object attachment();
	
}