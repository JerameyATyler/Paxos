/* Distributed Systems Project #1 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Node Class - Implements a node which can send and listen for messages from other nodes
 */

package message;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

public class Node {
    
	private String nodeName;  // may be used later
    private String ipAddress;
    private int portNum;    
    
    // Node constructor takes name i.e. "John", "Paul", "Geroge", "Ringo"
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
