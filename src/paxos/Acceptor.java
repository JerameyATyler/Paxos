/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Acceptor Class - Models a Paxos Acceptor (and also the Learner)
 */

package paxos;

import java.util.ArrayList;
import java.util.Iterator;

//import java.io.IOException;

public class Acceptor {
	
	// Node which contains this proposer
	public Node node;
    
	private int maxPrepare = 0; //largest proposal number for which it has responded to a prepare message (initially 0)
	private int accNum = -1;    //largest proposal number of proposal it has accepted (initially -1 for null)
	private int accVal = -1;    //value of proposal numbered accNum (initially -1 for null) 
	
	// Obtain largest proposal number for which it has responded to a prepare message
	public int getMaxPrepare() {
		return maxPrepare;
	}
	
	// Set largest proposal number for which it has responded to a prepare message
	public void setMaxPrepare(int maxPrepare) {
		this.maxPrepare = maxPrepare;
	}
	
	// Get largest proposal which has been accepted
	public int getAccNum() {
		return accNum;
	}
	
	// Set largest proposal which has been accepted
	public void setAccNum(int accNum) {
		this.accNum = accNum;
	}
	
	// Get value of proposal numbered accNum (initially null) 
	public int getAccVal() {
		return accVal;
	}
	
	// Set value of proposal numbered accNum
	public void setAccVal(int accVal) {
		this.accVal = accVal;
	}
	
	// send Ack to proposer
	public void sendAck(){
		
	}

	public void prepareReceived(Message messageReceived, ArrayList<Node> nodeList){
	    System.out.printf("Prepare Message Received from: %s\n",messageReceived.sender.getNodeName(), messageReceived.msg);
	    if (messageReceived.m > maxPrepare)
	        {
	    	maxPrepare = messageReceived.m;
	    	
	    	// prepare reply
	        Message promiseMessage = new Message();
	        promiseMessage.msg = "HELLO FROM ACCEPTOR!";
	        promiseMessage.messageType = Constant.messageType.Promise;
	        promiseMessage.sender = node;
	        promiseMessage.accNum = accNum;
	        promiseMessage.accVal = accVal;
			
	        // send promise response to all the Beatles
		    try
	            {
		    	
		    	//send the message to all the Beatles
		    	Iterator<Node> iterator = nodeList.iterator();
		    	while(iterator.hasNext())
		    	    {
		    		node.sendUDPMessage(iterator.next(), promiseMessage);
		    	    }
	            }
	        catch (Exception ex)
	        {
	        	ex.printStackTrace();
	        }
	        }
	}
	
	public void acceptReceived(Message messageReceived, ArrayList<Node> nodeList){
		 System.out.printf("Accept Message Received from %s: %s\n",messageReceived.sender.getNodeName(),messageReceived.msg);
	}
	
    public void commitReceived(Message messageReceived, ArrayList<Node> nodeList){
    	 System.out.printf("Commit Message Received from %s: %s\n",messageReceived.sender.getNodeName(),messageReceived.msg);
	}
	
}
