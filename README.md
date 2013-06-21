falcon
======

A fast Java networking library that has a unified API over various networking technologies: TCP, UDP, blocking (OIO), and non-blocking (NIO).

**Example**

```java
// Create the pipe (tcp+nio), set the transmission protocol.
Pipeline pipe = new TcpPipeline(); // could be: nio.UdpPipeline, oio.TcpPipeline, oio.UdpPipeline
pipe.setDefaultProtocol(String.class);
pipe.addProtocol(new StringProtocol());

// Listen on port 9090 for clients
Server server = pipe.newServer();
server.setClientListener(new DefaultClientListener() {
    public void onClientReceive(Client client, Object data) {
        super.onClientReceive(client, data);
        // Get all clients connected to the server
        Set<Client> clients = client.getServer().getClients();
        // Send all clients the data this client sent.
        for (Client current : clients) {
            current.send(data);
        }
    }
});
server.listen(9090);

// Connect 2 clients
Client client1 = pipe.newClient();
client1.connect("127.0.0.1", 9090);
Client client2 = pipe.newClient();
client2.connect("127.0.0.1", 9090);

// Send myself and client2 a message
client1.send("Hello Tom");
// Send myself and client1 a message
client2.send("Hello Beth");

// Close the clients as soon as all data is sent
client1.disconnect();
client2.disconnect();

/** processing! (waiting for data to send to server) **/

// Finally the server is finished, close it.
server.close();
```

**Builds**

https://github.com/ClickerMonkey/falcon/tree/master/build

**Testing Examples**

https://github.com/ClickerMonkey/falcon/tree/master/Examples/org/magnos/net/examples
