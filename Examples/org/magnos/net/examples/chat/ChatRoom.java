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

import java.util.HashSet;
import java.util.Set;


public class ChatRoom 
{

	public final String name;
	private Set<ChatUser> users;
	
	public ChatRoom(String name) {
		this.name = name;
		this.users = new HashSet<ChatUser>();
	}
	
	public void broadcast(ChatEvent event) {
		for (ChatUser user : users) {
			user.client.send(event);
		}
	}
	
	public void add(ChatUser user) {
		users.add(user);
	}
	
	public void remove(ChatUser user) {
		users.remove(user);
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public String toString() {
		return name;
	}
	
}
