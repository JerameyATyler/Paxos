/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Node Class - Implements a node which can send and listen for messages from other nodes via TCP and UDP
 */

package paxos;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Node {
    
	private String nodeName;  // may be used later
    private String ipAddress;
    private int tcpPortNum;
    private int udpPortNum;
    private ArrayList<Node> nodeList=new ArrayList<Node>();
    
    // Added for Paxos Implementation
    // Each node acts as proposer, acceptor and learner;
    public Proposer proposer = new Proposer();
    public Acceptor acceptor = new Acceptor();
    public Learner learner;
    
    private boolean isLeader = false;  // indicates this node is acting as Paxos leader (defaults to false)
    
    // Node constructor takes name i.e. "John", "Paul", "Geroge", "Ringo", "Walrus"
    // IP Address and Port Number for communication
    // Set leader = true to make this node the leader
    public Node(String name, String ip, int tcpport, int udpport, boolean leader){
    	setNodeName(name);
    	ipAddress = ip;
    	tcpPortNum = tcpport;
    	udpPortNum = udpport;
    	isLeader = leader;
    	proposer.node = this;
    	acceptor.node = this;
    }

    // Store a list of all other nodes
    public void setNodeList(ArrayList<Node> calendarNodeList){
    	nodeList = calendarNodeList;
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
         
 	    try {
 	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
 	      
 	        ObjectOutputStream oos = new ObjectOutputStream(baos);
 	      
 	        oos.writeObject(message);
 	        oos.flush();
 	     
 	        // serialize the message for transmission
 	        byte[] Buf= baos.toByteArray();

 	        int number = Buf.length;;
 	        byte[] data = new byte[4];

 	        for (int i = 0; i < 4; ++i) {
 	           int shift = i << 3; // i * 8
 	           data[3-i] = (byte)((number & (0xff << shift)) >>> shift);
 	        }

 	      // Open a socket to send message
 	      DatagramSocket socket = new DatagramSocket();
 	      InetAddress client = InetAddress.getByName(node.getIpAddress());
 	      
 	      // Send the size of the packet first
 	      DatagramPacket packet = new DatagramPacket(data, 4, client, udpPortNum);
 	      socket.send(packet);

 	      // Send the contents of the message next
 	      packet = new DatagramPacket(Buf, Buf.length, client, udpPortNum);
 	      socket.send(packet);
 	      
 	      socket.close();
 	      
 	      System.out.println("DONE SENDING");
 	      
 	    } catch(Exception e) {
 	        e.printStackTrace();
 	    }
    }
         
 	// wait in an infinite loop to receive messages over UDP
	void listenForUDPMessages(Calendar cal) throws IOException{
    
		DatagramSocket socket = new DatagramSocket(udpPortNum);
 
		// Run in an infinite loop.  Call a handler each time a message is received
		try {
   
            while(true)
               {
                  
            	try {
            	      // read in the length of the message
            		  byte[] data = new byte[4];
            	      DatagramPacket packet = new DatagramPacket(data, data.length);
            	      socket.receive(packet);
            	      int length = 0;
            	      for (int i = 0; i < 4; ++i) {
            	          length |= (data[3-i] & 0xff) << (i << 3);
            	      }

            	      // Read in the message
            	      byte[] buffer = new byte[length];
            	      packet = new DatagramPacket(buffer, buffer.length );
            	      socket.receive(packet);
            	      ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
            	      ObjectInputStream oos = new ObjectInputStream(baos);
            	      Message messageReceived = (Message)oos.readObject();
            	      
            	      // Put paxos message handlers here
            	      switch (messageReceived.messageType){ 
            	      
            	         case Prepare:
            	            {
            	    	    System.out.println("Prepare");
            	    	    acceptor.prepareReceived(messageReceived, nodeList);
            	    	    break;
            	            }
            	      
            	         case Promise:
        	                {
        	    	        System.out.println("Promise");
        	    	        proposer.promiseReceived(messageReceived, nodeList);
        	    	        break;
        	                }
        	                
            	         case Accept:
            	         	{
            	        	System.out.println("Accept");
            	        	acceptor.acceptReceived(messageReceived, nodeList);
            	        	break;
            	         	}
            	      
            	         case Ack:
            	         	{
            	        	System.out.println("Ack");
            	        	proposer.ackReceived(messageReceived, nodeList);
            	        	break;
            	         	}
            	      
            	         case Commit:
            	         	{
            	        	System.out.println("Commit");
            	        	acceptor.acceptReceived(messageReceived, nodeList);
            	        	break;
            	         	}
            	          
            	      } // end switch paxos message handler
            	      
            	      // Used for debug purposes
            	      System.out.printf("Received from: %s",messageReceived.msg);
            	      
            	    } catch(Exception e) {
            	        e.printStackTrace();
            	    }
               }	          
             } 
		
		finally {
            socket.close();
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

	// Determine if this node is paxos leader
	public boolean isLeader() {
		return isLeader;
	}

	// Make this node the paxos leader
	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

}
