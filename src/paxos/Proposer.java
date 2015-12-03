/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Proposer Class - Models a Paxos Proposer
 */

package paxos;

import java.util.ArrayList;
import java.util.Iterator;

public class Proposer {
	
	// Node which contains this proposer
	public Node node;
	
	// Proposer numbers need to be unique for all nodes
	private int nextProposalNumber = 0;
	
	// sends a Prepare message to all acceptors
	public void sendPrepare(ArrayList<Node> nodeList){
		nextProposalNumber++;
				
		// prepare message
        Message prepareMessage = new Message();
        prepareMessage.msg = "HELLO BEATLES!";
        prepareMessage.messageType = Constant.messageType.Prepare;
        prepareMessage.sender = node.getNodeName();
        prepareMessage.m = nextProposalNumber;
		
	    try
            {
	    	
	    	//send the message to all the Beatles
	    	Iterator<Node> iterator = nodeList.iterator();
	    	while(iterator.hasNext())
	    	    {
	    		node.sendUDPMessage(iterator.next(), prepareMessage);
	    	    }
	    	
            }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
		
	}
	
	// Chooses an accepted value
	void choose(){
		
	}
	
	// Send Accept
	void sendAccept(ArrayList<Node> nodeList){
		
		// prepare message
        Message acceptMessage = new Message();
        acceptMessage.msg = "ACCEPT MESSAGE";
        acceptMessage.messageType = Constant.messageType.Accept;
        acceptMessage.sender = node.getNodeName();
        acceptMessage.m = nextProposalNumber;
		
	    try
            {
	    	
	    	//send the accept message to all the Beatles
	    	Iterator<Node> iterator = nodeList.iterator();
	    	while(iterator.hasNext())
	    	    {
	    		node.sendUDPMessage(iterator.next(), acceptMessage);
	    	    }
	    	
            }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
		// send accept message to all acceptors
	}
	
	
	// Send Commit
	void sendCommit(ArrayList<Node> nodeList){
		// prepare message
        Message commitMessage = new Message();
        commitMessage.msg = "COMMIT MESSAGE";
        commitMessage.messageType = Constant.messageType.Accept;
        commitMessage.sender = node.getNodeName();
        commitMessage.m = nextProposalNumber;
		
	    try
            {
	    	
	    	//send the accept message to all the Beatles
	    	Iterator<Node> iterator = nodeList.iterator();
	    	while(iterator.hasNext())
	    	    {
	    		node.sendUDPMessage(iterator.next(), commitMessage);
	    	    }
	    	
            }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
		// send accept message to all acceptors
		
	}
	
	// Promise message received
	void promiseReceived(Message messageReceived, ArrayList<Node> nodeList){
		System.out.printf("Promise Message Received from %s: %s\n",messageReceived.sender,messageReceived.msg);
	    
		// TODO: if message received by majority
		if (true == true)
	        {
	    	int accNum = messageReceived.accNum;
	    	int accVal = messageReceived.accVal;
	    	
	    	sendAccept(nodeList);
	    	
	        }
		
	}
	
	// Ack message received
	void ackReceived(Message messageReceived, ArrayList<Node> nodeList){
		System.out.printf("Ack Message Received from %s: %s\n",messageReceived.sender,messageReceived.msg);
		
		sendCommit(nodeList);
	}
}
