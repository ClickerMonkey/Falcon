package org.magnos.net.examples.scream;

import java.util.Scanner;

import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.Pipeline;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.tcp.TcpPipeline;

public class ScreamClient 
{

	public static void main(String[] args) 
	{
		Pipeline pipeline = new TcpPipeline();
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setClientListener(new DefaultClientListener() {
			public void onClientReceive(Client client, Object data) {
				System.out.println(data);
			}
		});
		
		Client client = pipeline.newClient();
		client.connect("localhost", 8998);
		
		// Press Ctrl+Z to stop client
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			client.send(in.nextLine());
		}
		
		pipeline.getSelector().stop();
	}
	
}
