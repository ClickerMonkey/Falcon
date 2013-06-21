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

package org.magnos.net.examples.chat;

import javax.swing.JOptionPane;

import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.Pipeline;
import org.magnos.net.examples.chat.ChatEvent.Type;
import org.magnos.net.nio.udp.UdpPipeline;



public class ChatClient extends DefaultClientListener 
{

	public static void main(String[] args) 
	{
		ChatClient chatClient = new ChatClient();

		Pipeline pipeline = new UdpPipeline();
		pipeline.setDefaultProtocol(ChatEvent.class);
		pipeline.addProtocol(new ChatProtocol());
		pipeline.setClientListener(chatClient);
		
		Client client = pipeline.newClient();
		client.connect("127.0.0.1", 8998);
		chatClient.start( client );
	}
	
	private ChatUser user;
	private ChatWindow window;
	
	public void start(Client end) 
	{
		user = new ChatUser(end);
		window = new ChatWindow(user);
		window.setVisible(true);
		user.send(new ChatEvent(Type.Signon, user.name));
		user.send(new ChatEvent(Type.JoinRoom, user.name, null, "Room#1"));
	}
	
	@Override
	public void onClientReceive(Client client, Object data) 
	{
		super.onClientReceive(client, data);
		
		window.display((ChatEvent)data);
	}
	
	@Override
	public void onClientClose(Client client)
	{
		super.onClientClose(client);
		
		JOptionPane.showMessageDialog(window, "Good bye");
		
		window.dispose();

		System.exit(0);
	}

}
