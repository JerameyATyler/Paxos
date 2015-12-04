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

	// Track the first proposal number issued by this node
	private int firstProposalNumber;
	
	// Proposer numbers need to be unique for all nodes
	private int nextProposalNumber;
	
    //Store the list of event records that are part of the log as the accepted value
    ArrayList<EventRecord> accLog = new ArrayList();
    
    //Keep track of all the proposals
    Proposal[] proposals = new Proposal[Constant.MAX_PROPOSALS];
    
   // Proposal[Constant.MAX_PROPOSALS] proposals=new Proposal[];
	
	public void initializeProposalNumber(){
	
	   // Proposer numbers need to be unique for all nodes
	   // Use node name to generate starting number
	   switch (node.getNodeName()){
	      case "John":
	    	  firstProposalNumber = 1 * Constant.MAX_PROPOSALS;
	    	  break;
	      case "Paul":
	    	  firstProposalNumber = 2 * Constant.MAX_PROPOSALS;
	    	  break;
	      case "George":
	    	  firstProposalNumber = 3 * Constant.MAX_PROPOSALS;
	    	  break;
	      case "Ringo":
	    	  firstProposalNumber = 4 * Constant.MAX_PROPOSALS;
	    	  break;
	      case "Walrus":
	    	  firstProposalNumber = 5 * Constant.MAX_PROPOSALS;
	    	  break;
	      default:
	    	  firstProposalNumber = 6 * Constant.MAX_PROPOSALS;
	    	  break;
	      }

	      nextProposalNumber = firstProposalNumber;
	   
	   }
	   
	// sends a Prepare message to all acceptors
	public void sendPrepare(ArrayList<Node> nodeList){
		
		// create a new unique proposal number
		proposals[nextProposalNumber-firstProposalNumber] = new Proposal(nextProposalNumber);
		
		//System.out.printf("Proposal Number %d\n",proposals[nextProposalNumber-firstProposalNumber-1].getProposalNumber());
		
		// prepare message
        Message prepareMessage = new Message();
        prepareMessage.msg = "Prepare";
        prepareMessage.messageType = Constant.messageType.Prepare;
        prepareMessage.sender = node.getNodeName();
        prepareMessage.m = nextProposalNumber;
        
        nextProposalNumber++;
		
	    try
            {
	    	
	    	//send the prepare message to all the Beatles
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
	
	// Promise message received
	void promiseReceived(Message messageReceived, ArrayList<Node> nodeList){
		
		System.out.printf("Promise Message Received from %s: %s\n",messageReceived.sender,messageReceived.msg);
		    
		proposals[messageReceived.m-firstProposalNumber].addAcceptance();
			
		// If message received by majority
		if (proposals[messageReceived.m-firstProposalNumber].getNumberAccepts()>Constant.NUMBER_OF_NODES / 5)
		    {
			proposals[messageReceived.m-firstProposalNumber].accNum = messageReceived.accNum;
			proposals[messageReceived.m-firstProposalNumber].accVal = messageReceived.accVal;
			proposals[messageReceived.m-firstProposalNumber].setStatus("Accepted");
		    	
		    sendAccept(nodeList);
		    	
		    }
			
		}
	
	// Send Accept
	void sendAccept(ArrayList<Node> nodeList){
		
		// prepare message
        Message acceptMessage = new Message();
        acceptMessage.msg = "Accept";
        acceptMessage.messageType = Constant.messageType.Accept;
        acceptMessage.sender = node.getNodeName();
        acceptMessage.m = nextProposalNumber;
        acceptMessage.log = accLog;
		
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
	
	// Chooses an accepted value
		void choose(){
			
		}
	
	// Send Commit
	void sendCommit(ArrayList<Node> nodeList){
		// prepare message
        Message commitMessage = new Message();
        commitMessage.msg = "Commit";
        commitMessage.messageType = Constant.messageType.Accept;
        commitMessage.sender = node.getNodeName();
        commitMessage.m = nextProposalNumber;
        commitMessage.log = accLog;
		
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
	
	// Ack message received
	void ackReceived(Message messageReceived, ArrayList<Node> nodeList){
		System.out.printf("Ack Message Received from %s: %s\n",messageReceived.sender,messageReceived.msg);
		
		sendCommit(nodeList);
	}
}
