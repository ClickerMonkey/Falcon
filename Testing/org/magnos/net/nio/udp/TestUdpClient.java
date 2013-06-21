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

package org.magnos.net.nio.udp;

import java.util.Scanner;

import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.Pipeline;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.udp.UdpPipeline;


public class TestUdpClient extends DefaultClientListener
{

	public static void main(String[] args) 
	{
		// Listens for client events.
		TestUdpClient listener = new TestUdpClient();
		
		// Creates a selector and initializes the pipeline.
		Pipeline pipeline = new UdpPipeline();
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setClientListener(listener);
		
		// Connect to the UDP server at the given address.
		Client client = pipeline.newClient();
		client.connect("127.0.0.1", 26524);
		
		// For each line of input, send to the server.
		Scanner input = new Scanner(System.in);
		while (input.hasNextLine()) {
			client.send(input.nextLine());
		}
		
		// Stop the selector
		pipeline.getSelector().stop();
	}

}
