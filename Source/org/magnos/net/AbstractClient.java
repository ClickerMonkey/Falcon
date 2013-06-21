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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.magnos.io.BufferStream;
import org.magnos.io.BufferStreamListener;
import org.magnos.io.DynamicBufferStream;
import org.magnos.io.buffer.BufferFactory;
import org.magnos.io.bytes.ByteReader;
import org.magnos.io.bytes.ByteWriter;
import org.magnos.util.State;



/**
 * A basic implementation of a client. 
 * 
 * @author Philip Diffenderfer
 *
 */
public abstract class AbstractClient implements Client, BufferStreamListener 
{

	// The queue of pending data to send.
	protected final Queue<Object> pending;

	// The pipeline which created this client.
	protected final Pipeline pipeline;

	// An arbitrary state of the client.
	protected final State state;

	// The server which accepted this client if any.
	protected final Server server;

	// The worker which handles this client.
	protected Worker worker;


	// The BufferFactory this client uses to manage ByteBuffers.
	protected BufferFactory bufferFactory;
	
	// The input stream of the client.
	protected BufferStream input;

	// The output stream of the client.
	protected BufferStream output;

	// The listener to this clients events.
	protected final List<ClientListener> clientListeners;

	// The class of the current protocol.
	protected Class<?> currentProtocolClass;

	// The current protocol to use to decode data.
	protected Protocol<?> currentProtocol;

	// An attached object to this client.
	protected Object attachment;

	// Whether this client buffers transmissions upon write.
	protected boolean buffering;


	/**
	 * Instantiates an AbstractClient.
	 * 
	 * @param pipeline
	 * 		The pipeline which created the client.
	 * @param worker
	 * 		The worker which handles the client.
	 */
	protected AbstractClient(Pipeline pipeline, Worker worker, Server server) 
	{
		this.pipeline = pipeline;
		this.worker = worker;
		this.server = server;
		this.state = new State(Closed);
		this.bufferFactory = pipeline.getBufferFactory();
		this.clientListeners = new ArrayList<ClientListener>(1);
		this.pending = new ArrayDeque<Object>();
		this.setProtocol(pipeline.getDefaultProtocol());
	}

	/**
	 * Registers this client with its worker, server, and pipeline.
	 */
	protected void register()
	{
		if (worker != null) {
			worker.getClients().add(this);	
		}
		if (server != null) {
			server.getClients().add(this);
		}
		else {
			pipeline.getClients().add(this);
		}
	}

	/**
	 * Unregisters this client with its worker, server, and pipeline.
	 */
	private void unregister()
	{
		if (worker != null) {
			worker.getClients().remove(this);	
		}
		if (server != null) {
			server.getClients().remove(this);	
		}
		else  {
			pipeline.getClients().remove(this);
		}
	}

	/**
	 * Handles the request to write the data contained in the output stream
	 * to its destination.
	 */
	protected abstract void onFlush();

	/**
	 * Handles closing of the client. This will only be invoked once.
	 */
	protected abstract void onClose() throws IOException;

	/**
	 * Handles reading from the clients socket.
	 */
	protected abstract void onRead() throws IOException;

	/**
	 * Handles writing from the clients socket.
	 */
	protected abstract void onWrite() throws IOException;

	/**
	 * Handles connecting the clients socket if it hasn't connected yet. 
	 */
	protected abstract void onConnect() throws IOException;

	/**
	 * Handles connecting to the given address. 
	 */
	protected abstract boolean doConnect(SocketAddress address) throws IOException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean connect(SocketAddress address)
	{
		synchronized (state) 
		{
			// If currently connecting wait to be connected.
			if (state.equals(Connecting)) {
				state.waitFor(Connected);
			}
			// If disconnecting wait for it to close.
			if (state.equals(Disconnecting)) {
				state.waitFor(Closed);
			}
			// If closed, we can now connect.
			if (state.equals(Closed))
			{
				try {
					// Change state to connecting
					state.set(Connecting);

					// Initialize Input/Output and register
					initialize();
					
					// Attempt connection
					if ( doConnect(address) )
					{
						// Connect successful, notify.
						state.set(Connected);	
					}
				}
				catch (IOException e)
				{
					// Notify listeners of errors
					invokeError(e);
					
					// "Close" the connection on error
					close();
				}
			}
			
			// Only successful if the binded address equals the given address.
			return address.equals(getAddress());
		}
	}

	public void initialize()
	{
		// Instantiate the input and output streams
		input = new DynamicBufferStream(this, bufferFactory);
		output = new DynamicBufferStream(this, bufferFactory);

		// Add client to server/pipeline sets.
		register();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean connect(String host, int port)
	{
		return connect(new InetSocketAddress(host, port));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(ClientListener listener)
	{
		clientListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close()
	{
		synchronized (state) 
		{
			if (state.has(Connected | Disconnecting)) 
			{
				state.set(Closed);

				try {
					onClose();	
				}
				catch (IOException e) {
					invokeError(e);
				}

				invokeClose();

				output.free();
				input.free();

				unregister();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void handleRead()
	{
		try {
			// Only read the client if its connected.
			if (state.has(Connected)) 
			{
				invokeRead();
				// Double check the state incase the listener closed the client.
				if (state.has(Connected)) {
					onRead();	
				}
			}
		}
		catch (IOException e) {
			invokeError(e);
			disconnect();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void handleWrite()
	{
		try {
			// Only write the client if its connected
			if (state.has(Connected | Disconnecting)) {
				invokeWrite();
				// Double check the state incase the listener closed the client.
				if (state.has(Connected | Disconnecting)) {
					onWrite();	
				}
			}
		}
		catch (IOException e) {
			invokeError(e);
			close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void handleConnect()
	{
		try {
			synchronized (state) 
			{
				// Only finish the connection if its trying to connect.
				if (state.equals(Connecting)) 
				{
					onConnect();
					// Notify the listener and update the state.
					invokeConnect();
					state.set(Connected);
				}
			}
		}
		catch (IOException e) {
			invokeError(e);
			close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleAccept()
	{
		// Empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isClosed()
	{
		return state.equals(Closed);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDisconnecting() 
	{
		return state.equals(Disconnecting);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void disconnect()
	{
		if (state.cas(Connected, Disconnecting)) {
			invokeDisconnect();
			onFlush();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onBufferFlush(BufferStream stream) 
	{
		if (state.has(Connected)) {
			// Only invoke onWrite if the output buffer has been flushed and
			// contains data to write to the destination.
			if (stream == output && output.size() > 0) {
				onFlush();
			}	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setProtocol(Class<?> type) 
	{
		if (type == null) {
			throw new NullPointerException();
		}

		this.currentProtocolClass = type;
		this.currentProtocol = pipeline.getProtocol(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Class<?> getProtocol() 
	{
		return currentProtocolClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Pipeline getPipeline() 
	{
		return pipeline;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void send(Object data) 
	{
		if (data != null && state.has(Connected)) {
			synchronized (pending) {
				pending.add(data);	
			}
			onFlush();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Queue<Object> pending() 
	{
		return pending;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBuffering(boolean buffering)
	{
		this.buffering = buffering;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBuffering()
	{
		return buffering;
	}

	/**
	 * Decodes the input stream for objects, using the current protocol. If
	 * the input stream has been freed or not enough data exists in the input
	 * stream to fully decode an object 0 will be returned. For each non-null
	 * object decoded the client listener will be notified of a received object. 
	 * 
	 * @return
	 * 		The number of objects decoded from the input stream.
	 */
	protected int decode() 
	{
		Object data = null;
		int decoded = 0;
		// While data is decoded from input...
		while ((data = decode(currentProtocol)) != null) 
		{
			// Notify the client with the data received.
			invokeReceive(data);
			decoded++;
		}
		// The number of objects successfully decoded.
		return decoded;
	}

	/**
	 * Decodes a single object from the input stream and returns it. If the
	 * input stream has been freed or not enough data exists in the input stream
	 * to fully decode an object null will be returned.
	 * 
	 * @param <T>
	 * 		The object type being decoded.
	 * @param type
	 * 		The class of the object type being decoded.
	 * @return
	 * 		The decoded object or null if an object could not be decoded.
	 */
	private <T> Object decode(Protocol<T> protocol) 
	{
		// If input has been freed already we can't decode anything.
		if (input.isFree() || state.has(Disconnecting | Closed)) {
			return null;
		}

		Object data = null;
		try 
		{
			// Decode the input but wrap it with a reader first.
			data = protocol.decode(new ByteReader(input), this);
		}
		catch (Exception e) 
		{
			// An exception has occurred decoding the input stream.
			protocol.getListener().onDecodeError(protocol, input, this, e);	
		}

		// The object decoded.
		return data;
	}

	/**
	 * Encodes the output stream with all pending objects placing them in a
	 * the output to be sent as a single entity. If any data being encoded does
	 * not have an associated protocol a NullPointerException will be thrown.
	 * 
	 * @return
	 * 		The number of objects successfully encoded and placed in output.
	 */
	@SuppressWarnings("unchecked")
	protected int encode() 
	{
		Object data = null;
		int encoded = 0;
		do {
			// Take from queue with as few synchronization as possible.
			synchronized (pending) {
				data = pending.poll();
			}
			// If data to encode exists...
			if (data != null) {
				if (encode((Class<Object>)data.getClass(), data)) {
					encoded++;	
				}
				else {
					break;
				}
			}
			if (encoded > 0 && !buffering) {
				break;
			}
		} while (data != null);

		// The number of objects successfully encoded.
		return encoded;
	}

	/**
	 * Encodes a single object to the output stream. If the output stream has
	 * been freed this method will return immediately and the given data will
	 * not be encoded.
	 * 
	 * @param <T>
	 * 		The object type being encoded.
	 * @param type
	 * 		The class of the object being encoded.
	 * @param data
	 * 		The object to encode to the.
	 */
	private <T> boolean encode(Class<T> type, T data) 
	{
		// If output has been freed already we can't encode anything.
		if (output.isFree() || state.has(Disconnecting | Closed)) {
			return false;
		}

		// The protocol to use to encode data to the output stream. If this is
		// null it will cause an exception to be thrown which is desired.
		Protocol<T> protocol = pipeline.getProtocol(type);

		// Was there not a protocol?
		if (protocol == null) {
			invokeDiscard(data);
		}
		else {
			try {
				// Encode the object to output but wrap it with a writer first.
				protocol.encode(new ByteWriter(output), data, this);

				// Notify the client with the data sent (or encoded rather).
				invokeSend(data);
			}
			catch (Exception e) {
				// An exception has occurred encoding data to the output stream.
				protocol.getListener().onEncodeError(protocol, output, data, this, e);
			}
		}

		return (protocol != null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final State state()
	{
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Worker getWorker()
	{
		return worker;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Server getServer()
	{
		return server;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<ClientListener> getListeners() 
	{
		return clientListeners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BufferStream getInput() 
	{
		return input;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BufferStream getOutput() 
	{
		return output;
	}

	/**
	 * {@inheritDoc}
	 */
	public void attach(Object attachment) 
	{
		this.attachment = attachment;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object attachment() 
	{
		return attachment;
	}

	/**
	 * Invokes open event to all listeners.
	 */
	public void invokeOpen() 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientOpen(this);	
		}
	}

	/**
	 * Invokes connect event to all listeners.
	 */
	public void invokeConnect() 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientConnect(this);
		}
	}

	/**
	 * Invokes error event to all listeners.
	 */
	public void invokeError(Exception e) 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientError(this, e);
		}
	}

	/**
	 * Invokes read event to all listeners.
	 */
	public void invokeRead() 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientRead(this);
		}
	}

	/**
	 * Invokes receive event to all listeners.
	 */
	public void invokeReceive(Object data) 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientReceive(this, data);
		}
	}

	/**
	 * Invokes write event to all listeners.
	 */
	public void invokeWrite() 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientWrite(this);
		}
	}

	/**
	 * Invokes send event to all listeners.
	 */
	public void invokeSend(Object data) 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientSend(this, data);
		}
	}

	/**
	 * Invokes discard event to all listeners.
	 */
	public void invokeDiscard(Object data) 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientDiscard(this, data);
		}
	}

	/**
	 * Invokes disconnect event to all listeners.
	 */
	public void invokeDisconnect() 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientDisconnect(this);
		}
	}

	/**
	 * Invokes close event to all listeners.
	 */
	public void invokeClose() 
	{
		for (ClientListener cl : clientListeners) {
			cl.onClientClose(this);
		}
	}

}