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

import org.magnos.io.bytes.ByteReader;
import org.magnos.io.bytes.ByteWriter;
import org.magnos.io.bytes.ByteReader.ReadCallback;
import org.magnos.io.bytes.ByteWriter.WriteCallback;

public class ChatEvent implements ReadCallback<ChatEvent>, WriteCallback<ChatEvent>
{
	public enum Type {
		Signon, Signoff, JoinRoom, LeaveRoom, Message, GetRooms, GetUsers;
	}
	
	public Type type;
	public String user;
	public String message;
	public String room;
	
	public ChatEvent() {
	}
	
	public ChatEvent(Type type) {
		this(type, null, null, null);
	}
	
	public ChatEvent(Type type, String user) {
		this(type, user, null, null);
	}
	
	public ChatEvent(Type type, String user, String message) {
		this(type, user, message, null);
	}
	
	public ChatEvent(Type type, String user, String message, String room) {
		this.type = type;
		this.user = user;
		this.message = message;
		this.room = room;
	}
	
	public String display() {
		switch (type) {
		case Signon:
			return String.format("%s has signed on.", user);
		case Signoff:
			return String.format("%s has signed off.", user);
		case JoinRoom:
			return String.format("%s has joined %s.", user, room);
		case LeaveRoom:
			return String.format("%s has left %s.", user, room);
		case Message:
			return String.format("%s: %s", user, message);
		}
		return "";
	}
	
	
	@Override
	public ChatEvent read(ByteReader reader) {
		ChatEvent evt = new ChatEvent();
		evt.type = reader.getEnum(Type.class);
		evt.user = reader.getString();
		evt.message = reader.getString();
		evt.room = reader.getString();
		return (reader.isValid() ? evt : null);
	}

	@Override
	public void write(ByteWriter writer, ChatEvent evt) {
		writer.putEnum(evt.type);
		writer.putString(evt.user);
		writer.putString(evt.message);
		writer.putString(evt.room);
	}
	
	@Override
	public String toString() {
		return display();
	}
	
}
