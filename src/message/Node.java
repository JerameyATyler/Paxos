/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Node Class - Implements a node which can send and listen for messages from other nodes
 */

package message;

import java.io.*;
import java.net.*;

public class Node {
    
	private String nodeName;  // may be used later
    private String ipAddress;
    private int portNum;    
    
    // Node constructor takes name i.e. "John", "Paul", "Geroge", "Ringo", "Walrus"
    // IP Address and Port Number for communication
    public Node(String name, String ip, int port){
    	nodeName = name;
    	ipAddress = ip;
    	portNum = port;
    }

    // sends the message Message to the specified node Node
 	public void sendMessage(Node node, Message message) throws IOException{
 	         
             Socket socket = new Socket(node.getIpAddress(),node.getPortNum());    
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             out.writeObject(message);
             out.flush();
             socket.close();
    }
 	
 	// sends a message via UDP
 	public void sendMessageUDP(Node node, Message message) throws IOException{
         
 		DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] sendData = new byte[1024];
      
        sendData = message.msg.getBytes();
        DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, InetAddress.getByName(node.getIpAddress()), node.getPortNum());
        serverSocket.send(sendPacket);
        serverSocket.close();
    }
         
 	// wait in an infinite loop to receive messages
	void listenForUDPMessages(Calendar cal) throws IOException{
    	 
		// Start listening
		ServerSocket listener = new ServerSocket(portNum);
    	 
		// Debug purposes
		//System.out.printf("%s is listening...",nodeName);
		
		// Run in an infinite loop.  Call a handler each time a message is received
		try {
            while (true) {
                //new MessageReceiver(listener.accept(),getPortNum()).start();
            
          		try {
          			DatagramSocket serverSocket = new DatagramSocket(9876);
                    byte[] receiveData = new byte[1024];
                    //byte[] sendData = new byte[1024];
          			
          			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String( receivePacket.getData());
                    System.out.println("RECEIVED: " + sentence);
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    
                    //String capitalizedSentence = sentence.toUpperCase();
                    //sendData = capitalizedSentence.getBytes();
                    //DatagramPacket sendPacket =
                    //new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    //serverSocket.send(sendPacket);
          						
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
	
	// wait in an infinite loop to receive messages
	void listenForMessages(Calendar cal) throws IOException{
	    	 
	// Start listening
	ServerSocket listener = new ServerSocket(portNum);
	    	 
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

	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

}
