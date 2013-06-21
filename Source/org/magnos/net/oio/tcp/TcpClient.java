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

package org.magnos.net.oio.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

import org.magnos.net.AbstractClient;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.Worker;
import org.magnos.net.oio.OioWorker;


/**
 * A client which uses the TCP protocol and OIO.
 * 
 * @author Philip Diffenderfer
 *
 */
public class TcpClient extends AbstractClient
{

	// The socket of the client.
	private Socket socket;
	
	// The stream to read input from the socket.
	private InputStream inputStream;
	
	// The stream to write output to the socket.
	private OutputStream outputStream;

	
	/**
	 * Instantiates a new TcpClient.
	 * 
	 * @param pipeline
	 * 		The pipeline the client should use.
	 * @param worker
	 * 		The worker handling the client.
	 * @param server
	 * 		The server which accepted the client, or null if the client was
	 * 		created by the pipeline.
	 */
	protected TcpClient(Pipeline pipeline, Worker worker, Server server) 
	{
		super(pipeline, worker, server);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doConnect(SocketAddress address) throws IOException
	{
		setSocket(new Socket());
		
		invokeOpen();
		
		socket.connect(address);
		
		invokeConnect();
		
		if (worker == null) {
			worker = pipeline.getSelector().getWorker();
		}
		((OioWorker)worker).setClient(this);
		
		return (server != null);
	}


	/**
	 * XXX
	 * @param socket
	 */
	protected void setSocket(Socket socket) throws IOException
	{
		this.socket = socket;
		this.inputStream = new BufferedInputStream(socket.getInputStream());
		this.outputStream = new BufferedOutputStream(socket.getOutputStream());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getSocket() 
	{
		return socket;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClose() throws IOException 
	{
		((OioWorker)worker).setClient(null);
		worker = null;
		socket.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onFlush() 
	{
		handleWrite();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRead() throws IOException 
	{
		// Continually read in bytes from the input stream...
		for (;;) {
			// Returns either -1 or the number of bytes read
			int read = input.drain(inputStream);
			// End of stream? close the connection
			if (read < 0) {
				close();
				break;
			}
			// If a message has finally been decoded break out. This
			// ensures each handleRead reads in one message at a time.
			if (decode() > 0) {
				break;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onWrite() throws IOException 
	{
		while (encode() > 0) {
			output.fill(outputStream);
			outputStream.flush();
		}

		if (output.isEmpty()) {
			if (isDisconnecting()) {
				synchronized (pending) {
					if (pending.size() == 0) {
						close();
					}
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onConnect() 
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocketAddress getAddress() 
	{
		return socket.getRemoteSocketAddress();
	}

}
