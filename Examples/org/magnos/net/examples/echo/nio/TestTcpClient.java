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

package org.magnos.net.examples.echo.nio;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

import org.magnos.net.Client;
import org.magnos.net.ClientListener;
import org.magnos.net.Pipeline;
import org.magnos.net.examples.ByteProtocol;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.tcp.TcpPipeline;



public class TestTcpClient implements ClientListener
{

	public static void main(String[] args) 
	{
		// Create the pipeline to use TCP and NIO
		Pipeline pipeline = new TcpPipeline();
		
		// Set the default protocol and add it.
		pipeline.setDefaultProtocol(byte[].class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.addProtocol(new ByteProtocol());
		pipeline.setClientListener(new TestTcpClient());

		// Connect to the server on 34567.
		Client client = pipeline.newClient();
		client.connect("www.philsprojects.net", 6078);
		
		// For every line typed send to the server.
		Scanner input = new Scanner(System.in);
		while (input.hasNextLine()) {
			client.send(input.nextLine().getBytes());
		}
		
		// Once input has stopped disconnect the client.
		pipeline.getSelector().stop();
	}

	private long timeOpen;
	private Queue<Long> timeQueue = new ArrayDeque<Long>();

	private double elapsed(long s, long e) {
		return (e - s) * 0.000000001;
	}
	
	@Override
	public boolean onClientOpen(Client client) {
		timeOpen = System.nanoTime();
		return true;
	}

	@Override
	public void onClientConnect(Client client) {
		System.out.format("Client connected in %fs.\n", elapsed(timeOpen, System.nanoTime()));
	}

	@Override
	public void onClientDiscard(Client client, Object data) {
		System.out.format("Unknown data [%s] discarded.\n", data);
	}

	@Override
	public void onClientRead(Client client) {
		
	}

	@Override
	public void onClientReceive(Client client, Object data) {
		System.out.format("Response time %f\n", elapsed(timeQueue.poll(), System.nanoTime()));
	}

	@Override
	public void onClientWrite(Client client) {
		
	}


	@Override
	public void onClientSend(Client client, Object data) {
		timeQueue.offer(System.nanoTime());
	}

	@Override
	public void onClientError(Client client, Exception e) {
		System.err.println("Client error");
		e.printStackTrace();
	}

	@Override
	public void onClientDisconnect(Client client) {
		System.out.println("Disconnect requested.");
	}

	@Override
	public void onClientClose(Client client) {
		System.out.format("Client closed after being connected for %fs\n", elapsed(timeOpen, System.nanoTime()));
	}
	
}
