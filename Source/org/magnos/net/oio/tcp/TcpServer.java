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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import org.magnos.net.AbstractServer;
import org.magnos.net.Acceptor;
import org.magnos.net.Client;
import org.magnos.net.Pipeline;
import org.magnos.net.Selector;
import org.magnos.net.oio.OioAcceptor;
import org.magnos.net.oio.OioWorker;


public class TcpServer extends AbstractServer
{

	private ServerSocket serverSocket;
	
	public TcpServer(Selector selector, Pipeline pipeline, Acceptor acceptor) 
	{
		super(selector, pipeline, acceptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doBind(SocketAddress address) throws IOException
	{
		serverSocket = new ServerSocket();
		
		invokeStart();
		
		serverSocket.bind(address);
		
		invokeBind(address);
		
		if (acceptor == null) {
			acceptor = selector.getAcceptor();
		}
		((OioAcceptor)acceptor).setServer(this);
	}
	
	protected void setSocket(ServerSocket socket) 
	{
		this.serverSocket = socket;
	}

	@Override
	public void handleAccept() 
	{
		if (!state.equals(Accepting)) {
			return;
		}
		
		try {

			Socket socket = serverSocket.accept();

			TcpClient client = (TcpClient)pipeline.newClient(this);
			if (clientListener != null) {
				client.getListeners().add(clientListener);	
			}
			
			client.setSocket(socket);	
			client.state().set(Client.Connecting);
			
			invokeAccept(client);
			
			client.invokeOpen();

			client.state().set(Client.Connected);
			
			((OioWorker)client.getWorker()).setClient(client);
			
			client.invokeConnect();
		}
		catch (IOException e) {
			invokeError(e);
		}
	}
	
	@Override
	public void onClose() throws IOException 
	{
		((OioAcceptor)acceptor).setServer(null);
		acceptor = null;
		serverSocket.close();
	}

	@Override
	public ServerSocket getSocket() 
	{
		return serverSocket;
	}
	
	@Override
	public void handleConnect() 
	{
	}
	
	@Override
	public void handleRead() 
	{
	}
	
	@Override
	public void handleWrite() 
	{
	}

}
