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

import java.util.Scanner;

import org.magnos.net.Client;
import org.magnos.net.Pipeline;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.udp.UdpPipeline;



public class TestUdpClient
{

	public static void main(String[] args) 
	{
		Pipeline pipeline = new UdpPipeline();
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());

		Client client = pipeline.newClient();
		client.connect("127.0.0.1", 9876);
		
		Scanner input = new Scanner(System.in);
		while (input.hasNextLine()) {
			client.send(input.nextLine());
		}
		
		pipeline.getSelector().stop();
	}
	
}
