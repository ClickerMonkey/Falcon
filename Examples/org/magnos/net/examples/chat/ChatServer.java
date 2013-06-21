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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.magnos.net.Client;
import org.magnos.net.ClientListener;
import org.magnos.net.DefaultServerListener;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.examples.chat.ChatEvent.Type;
import org.magnos.net.examples.chat.ChatUser.Status;
import org.magnos.net.nio.udp.UdpPipeline;



public class ChatServer extends DefaultServerListener implements ClientListener 
{
	// Tests:
	// 1. Start several clients
	// 2. Close clients first
	// 3. Close server first

	public static void main(String[] args) 
	{
		ChatServer chat = new ChatServer();

		Pipeline pipeline = new UdpPipeline();
		pipeline.setDefaultProtocol(ChatEvent.class);
		pipeline.addProtocol(new ChatProtocol());
		pipeline.setClientListener(chat);
		pipeline.setServerListener(chat);

		Server server = pipeline.newServer();
		server.listen(8998);
		
		new Scanner(System.in).nextLine();
		
		pipeline.getSelector().stop();
	}
	
	private Set<ChatUser> users = new HashSet<ChatUser>();
	private Map<String, ChatRoom> rooms = new HashMap<String, ChatRoom>();
	
	public ChatServer() {
		rooms.put("Room#1", new ChatRoom("Room#1"));
		rooms.put("Room#2", new ChatRoom("Room#2"));
	}
	
	@Override
	public void onClientConnect(Client end) {
//		System.out.format("Client at %s connected.\n", end.getAddress());
		users.add(new ChatUser(end));
	}

	@Override
	public void onClientReceive(Client end, Object data) {
		System.out.format("Client at %s sent [%s]\n", end.getAddress(), data);
		
		ChatEvent event = (ChatEvent)data;
		ChatUser user = (ChatUser)end.attachment();
		ChatRoom room = user.room;
		
		switch (event.type) {
		case LeaveRoom:
			user.leave();
			break;
		case JoinRoom:
			ChatRoom join = rooms.get(event.room); 
			user.room = join;
			join.add(user);
			break;
		case Signon:
			user.name = event.user;
			user.status = Status.Online;
			break;
		case Signoff:
			if (user.status == Status.Online) {
				logoff(user);
			}
			break;
		}

		if (room != null) {
			room.broadcast(event);
		}
		else {
			for (ChatUser other : users) {
				other.send(event);
			}
		}

		print(event.display());
	}

	@Override
	public void onClientClose(Client end) {
		ChatUser user = (ChatUser)end.attachment();
		if (user.status == Status.Online) {
			logoff(user);
		}
		users.remove(user);
		System.out.format("Client at %s disconnected.\n", end.getAddress());
	}
	
	private void logoff(ChatUser user) {
		user.leave();
		ChatEvent event = new ChatEvent(Type.Signoff, user.name);
		for (ChatUser other : users) {
			if (other != user) {
				other.send(event);
			}
		}
		users.remove(user);
		user.client.close();
		user.status = Status.Offline;
	}
	
	private void print(String s) {
		if (s != null && s.trim().length() > 0) {
			System.out.println(s);
		}
	}

	@Override
	public void onClientSend(Client end, Object data) {

	}

	@Override
	public void onClientError(Client end, Exception e) { 
		System.err.println("Client Error");
		e.printStackTrace();
	}
	
	@Override
	public void onClientRead(Client end) { 
		System.out.format("Client at %s read.\n", end.getAddress());
	}

	@Override
	public void onClientWrite(Client end) { 
		System.out.format("Client at %s written.\n", end.getAddress());		
	}

	@Override
	public void onClientDiscard(Client client, Object data) {
		System.out.format("Client at %s could not write [%s], it has been discarded.", client.getAddress(), data);
	}
	
	public void onClientDisconnect(Client client) {
		
	}
	
	public boolean onClientOpen(Client client) { 
		return true; 
	}


}
