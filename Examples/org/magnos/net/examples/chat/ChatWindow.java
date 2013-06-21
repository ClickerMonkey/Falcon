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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

public class ChatWindow extends JFrame implements WindowListener
{

	private final ChatUser user;
	private final JTextArea txtReceive;
	private final JTextArea txtSend;
	
	public ChatWindow(ChatUser chatUser) {
		user = chatUser;
		user.name = JOptionPane.showInputDialog("Username");
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(480, 200);
		setTitle("Chat Window");
		addWindowListener(this);
		setAlwaysOnTop(true);
		setLayout(new BorderLayout());
		
		txtReceive = new JTextArea();
		txtReceive.setEditable(false);
		txtReceive.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(txtReceive, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setAutoscrolls(true);
		add(scroll, BorderLayout.CENTER);
		
		txtSend = new JTextArea();
		txtSend.setPreferredSize(new Dimension(480, 20));
		txtSend.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) { }
			public void keyPressed(KeyEvent e) { 
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (e.isShiftDown()) {
						txtSend.append("\n");
						validate();
					}
					else {
						e.consume();
						send();
					}
				}
			}
			public void keyTyped(KeyEvent e) { }
		});
		add(txtSend, BorderLayout.PAGE_END);
		validate();
	}
	
	private void send() {
		String text = txtSend.getText().trim();
		txtSend.setText("");
		txtSend.setPreferredSize(new Dimension(480, 20));
		validate();
		
		if (text != null && text.length() > 0) {
			user.send(new ChatEvent(ChatEvent.Type.Message, user.name, text));	
		}
	}

	public void display(ChatEvent event) {
		txtReceive.append(event.display() + "\n");
		txtReceive.setCaretPosition(txtReceive.getText().length());
	}
	
	public void windowClosed(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		user.signOff();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
	}
	
}
