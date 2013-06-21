package org.magnos.net.examples.scream;

import java.util.Scanner;

import org.magnos.net.Client;
import org.magnos.net.DefaultClientListener;
import org.magnos.net.DefaultServerListener;
import org.magnos.net.Pipeline;
import org.magnos.net.Server;
import org.magnos.net.examples.StringProtocol;
import org.magnos.net.nio.tcp.TcpPipeline;

public class ScreamServer 
{

	public static void main(String[] args) 
	{
		Pipeline pipeline = new TcpPipeline();
		pipeline.setDefaultProtocol(String.class);
		pipeline.addProtocol(new StringProtocol());
		pipeline.setServerListener(new DefaultServerListener());

		Server server = pipeline.newServer();
		server.listen(8998);
		server.setClientListener(new DefaultClientListener() {
			public void onClientReceive(Client client, Object data) {
				for (Client other : client.getServer().getClients()) {
					other.send(data);
				}
			}
		});
		
		// Press ENTER to stop server
		new Scanner(System.in).nextLine();
		
		pipeline.getSelector().stop();
	}
	
}
