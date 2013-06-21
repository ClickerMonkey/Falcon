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

import org.magnos.io.buffer.BufferFactoryBinary;
import org.magnos.net.AbstractPipeline;
import org.magnos.net.Client;
import org.magnos.net.Server;
import org.magnos.net.oio.OioAcceptor;
import org.magnos.net.oio.OioSelector;
import org.magnos.net.oio.OioWorker;


/**
 * A pipeline which uses the TCP protocol and OIO.
 * 
 * @author Philip Diffenderfer
 *
 */
public class TcpPipeline extends AbstractPipeline 
{

	/**
	 * Instantiates a new TcpPipeline. This method of instantiating should only
	 * be done if the application has a single pipeline OR the pipelines are
	 * based off of different technologies (NIO, OIO). The other method is
	 * preferred, this method is to keep single pipeline development simpler.
	 */
	public TcpPipeline()
	{
		this(new OioSelector());
	}

	/**
	 * Instantiates a TcpPipeline.
	 * 
	 * @param selector
	 * 		The selector for the pipeline providing workers and acceptors.
	 */
	public TcpPipeline(OioSelector selector) 
	{
		super(selector);
		this.setBufferFactory(new BufferFactoryBinary(8, 16));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Server newServer()
	{
		selector.start();
		
		OioAcceptor accepter = (OioAcceptor)selector.getAcceptor();
		accepter.start();
		
		Server server = new TcpServer(selector, this, accepter);

		if (clientListener != null) {
			server.setClientListener(clientListener);
		}
		if (serverListener != null) {
			server.addListener(serverListener);
		}
		
		return server;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Client newClient(Server server)
	{
		selector.start();
		
		OioWorker worker = (OioWorker)selector.getWorker();
		worker.start();
		
		Client client = new TcpClient(this, worker, server);

		if (clientListener != null) {
			client.addListener(clientListener);
		}
		
		return client;
	}
	
}
