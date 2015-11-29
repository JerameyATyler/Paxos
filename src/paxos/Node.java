/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Node Class - Implements a node which can send and listen for messages from other nodes via TCP and UDP
 */

package paxos;

import java.io.*;
import java.net.*;

public class Node {
    
	private String nodeName;  // may be used later
    private String ipAddress;
    private int tcpPortNum;
    private int udpPortNum;
    
    // Added for Paxos Implementation
    // Each node acts as proposer, acceptor and learner;
    private Proposer proposer;
    private Acceptor acceptor;
    private Learner learner;
    
    // Node constructor takes name i.e. "John", "Paul", "Geroge", "Ringo", "Walrus"
    // IP Address and Port Number for communication
    public Node(String name, String ip, int tcpport, int udpport){
    	nodeName = name;
    	ipAddress = ip;
    	tcpPortNum = tcpport;
    	udpPortNum = udpport;
    }

    // sends the message Message over TCP to the specified node Node
 	public void sendTCPMessage(Node node, Message message) throws IOException{
 	         
             Socket socket = new Socket(node.getIpAddress(),tcpPortNum);    
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             out.writeObject(message);
             out.flush();
             socket.close();
    }
 	
 	// sends a message via UDP
 	public void sendUDPMessage(Node node, Message message) throws IOException{
         
 		DatagramSocket clientSocket = new DatagramSocket();
 		byte[] sendData = new byte[Constant.UDP_MAX_PACKET_SIZE];
      
        sendData = message.msg.getBytes();
        DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, InetAddress.getByName(node.getIpAddress()), udpPortNum);
        clientSocket.send(sendPacket);
        clientSocket.close();
    }
         
 	// wait in an infinite loop to receive messages over UDP
	void listenForUDPMessages(Calendar cal) throws IOException{
    
		DatagramSocket serverSocket = new DatagramSocket(udpPortNum);
        byte[] receiveData = new byte[Constant.UDP_MAX_PACKET_SIZE];
		
		// Run in an infinite loop.  Call a handler each time a message is received
		try {
   
            while(true)
               {
                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                  serverSocket.receive(receivePacket);
                  String sentence = new String( receivePacket.getData());
                  System.out.println("RECEIVED: " + sentence);
               }	          
             } 
		
		finally {
            serverSocket.close();
        }
		
	}
	
	// wait in an infinite loop to receive messages over TCP
	void listenForTCPMessages(Calendar cal) throws IOException{
	    	 
	// Start listening
	ServerSocket listener = new ServerSocket(tcpPortNum);
	    	 
			// Debug purposes
			//System.out.printf("%s is listening...",nodeName);
			
			// Run in an infinite loop.  Call a handler each time a message is received
			try {
	            while (true) {
	                //new MessageReceiver(listener.accept(),getPortNum()).start();
	            
	          		try {
	          			
	          			Socket clientSocket = listener.accept();
	          		    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
	          		    Message messageReceived;
	          			try {
	          				messageReceived = (Message) in.readObject();
	          					
	          				// Used for Debugging Purposes
	          				//System.out.printf("Message was %s",messageReceived.msg);
	          				
	          			    // PUT MESSAGE HANDLING CODE HERE
	          				// use messageReceived
	          				cal.receive(messageReceived);
	          			
	          			} catch (ClassNotFoundException e) {
	          					e.printStackTrace();
	          				}
	          			clientSocket.close();
	          			} catch (IOException e) {
	          			e.printStackTrace();
	          		}
	            	          	
	            }
	        } finally {
	            listener.close();
	        } 
		}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
