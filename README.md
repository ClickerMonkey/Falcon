falcon
======

A fast Java networking library that has a unified API over various networking technologies: TCP, UDP, blocking (OIO), and non-blocking (NIO).

*Disclaimer: this is a work in progress, although should operate fine as is. Plans for refactoring and heavier testing are in the works*

**Features**
- Either OIO (java.io) or NIO (java.nio) can be used. The blocking-nature of the underlying library is hidden.
- Either TCP or UDP can be used, the underlying transport protocol is hidden.
- This is an entirely event-driven framework, only the client initiates events, the server reacts and can respond or notify other clients however it likes.
- This is protocol independent, any protocol (like HTTP, FTP, etc) can be written for it.
- Entirely thread-safe

**Documentation**
- [JavaDoc](http://gh.magnos.org/?r=http://clickermonkey.github.com/falcon/)

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
- [falcon-1.0.jar](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/falcon/blob/master/build/falcon-1.0.jar?raw=true)
- [falcon-src-1.0.jar](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/falcon/blob/master/build/falcon-src-1.0.jar?raw=true) *- includes source code*
- [falcon-all-1.0.jar](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/falcon/blob/master/build/falcon-1.0.jar?raw=true) *- includes all dependencies*
- [falcon-all-src-1.0.jar](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/falcon/blob/master/build/falcon-src-1.0.jar?raw=true) *- includes all dependencies and source code*

**Dependencies**
- [taskaroo](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/taskaroo)
- [surfice](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/surfice)
- [zource](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/zource)
- [buffero](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/buffero)
- [curity](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/curity)
- [testility](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/testility) *for unit tests*

**Testing Examples**
- [Examples/org/magnos/net/examples](http://gh.magnos.org/?r=https://github.com/ClickerMonkey/falcon/tree/master/Examples/org/magnos/net/examples)
