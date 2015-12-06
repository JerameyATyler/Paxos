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
    
    //Keep track of all the proposals
    static Proposal[] proposals = new Proposal[Constant.MAX_PROPOSALS];
    
   // Proposal[Constant.MAX_PROPOSALS] proposals=new Proposal[];
	
	public void initializeProposalNumber(){
	
	   // Proposer numbers need to be unique for all nodes
	   // Use node name to generate starting number
	   /* switch (node.getNodeName()){
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
	      } */
		
		  firstProposalNumber = 1;
	      nextProposalNumber = firstProposalNumber;
	   
	   }
	   
	// sends a Prepare message to all acceptors
	public void sendPrepare(Message messageReceived,ArrayList<Node> nodeList){
		
		// create a new unique proposal number
		Proposal proposal = new Proposal(nextProposalNumber);
		proposal.log = messageReceived.log;		
		
		// store this proposal in a proposal list
		proposals[nextProposalNumber-firstProposalNumber] = proposal;
		
		//System.out.printf("Proposal Number %d\n",proposals[nextProposalNumber-firstProposalNumber-1].getProposalNumber());
		
		// prepare message
        Message prepareMessage = new Message();
        prepareMessage.msg = "Prepare";
        prepareMessage.messageType = Constant.messageType.Prepare;
        prepareMessage.sender = node.getNodeName();
        prepareMessage.m = proposal.getProposalNumber();
        
        nextProposalNumber++;
		
	    try
            {
	    	//send the prepare message to all the Beatles
	    	Iterator<Node> iterator = nodeList.iterator();
	    	Node Beatle;
	    	while(iterator.hasNext())
	    	    {
	    		Beatle=iterator.next();
	    		node.sendUDPMessage(Beatle, prepareMessage);
	    		System.out.printf("Prepare message# %d sent to %s\n",prepareMessage.m,Beatle.getNodeName());
	    	    }
	    	
            }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
		
	}
	
	// Promise message received
	void promiseReceived(Message messageReceived, ArrayList<Node> nodeList){
		
		System.out.printf("Promise Message# %d Received from %s: %s\n",messageReceived.m,messageReceived.sender,messageReceived.msg);
		    
		proposals[messageReceived.m-firstProposalNumber].addAcceptance();
			
		// If message received by majority
		if ((proposals[messageReceived.m-firstProposalNumber].getNumberAccepts() > Constant.NUMBER_OF_NODES / 2) &&
			     proposals[messageReceived.m-firstProposalNumber].getStatus().equals("Active"))
		    {
			proposals[messageReceived.m-firstProposalNumber].accNum = messageReceived.accNum;
			proposals[messageReceived.m-firstProposalNumber].accVal = messageReceived.accVal;
			proposals[messageReceived.m-firstProposalNumber].setStatus("Accepted");
		    	
		    sendAccept(messageReceived, nodeList);
		    	
		    }
			
		}
	
	// Send Accept
	void sendAccept(Message messageReceived, ArrayList<Node> nodeList){
		
		// prepare message
        Message acceptMessage = new Message();
        acceptMessage.msg = "Accept";
        acceptMessage.messageType = Constant.messageType.Accept;
        acceptMessage.sender = node.getNodeName();
        acceptMessage.m = messageReceived.m;
        acceptMessage.log = proposals[messageReceived.m-firstProposalNumber].log;
		
	    try
            {
	    	
	    	//send the Accept message to all the Beatles
	    	Iterator<Node> iterator = nodeList.iterator();
	    	Node Beatle = iterator.next();
	    	while(iterator.hasNext())
	    	    {
	    		node.sendUDPMessage(Beatle, acceptMessage);
	    		System.out.printf("Accept message# %d sent to %s\n",messageReceived.m,Beatle.getNodeName());
	    		Beatle=iterator.next();
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
	void sendCommit(Message messageReceived, ArrayList<Node> nodeList){
		// prepare message
        Message commitMessage = new Message();
        commitMessage.msg = "Commit";
        commitMessage.messageType = Constant.messageType.Commit;
        commitMessage.sender = node.getNodeName();
        commitMessage.m = messageReceived.m;
        commitMessage.log = messageReceived.log;
        
	    try
            {
	    	//send the commit message to all the Beatles
	    	Iterator<Node> iterator = nodeList.iterator();
	    	Node Beatle = iterator.next();
	    	while(iterator.hasNext())
	    	    {
	    		node.sendUDPMessage(Beatle, commitMessage);
	    		System.out.printf("Commit message# %d sent to %s\n",messageReceived.m,Beatle.getNodeName());
	    		Beatle=iterator.next();
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
		System.out.printf("Ack Message# %d Received from %s: %s\n",messageReceived.m,messageReceived.sender,messageReceived.msg);
		
		proposals[messageReceived.m-firstProposalNumber].addAck();
		
		// If message received by majority
		if ((proposals[messageReceived.m-firstProposalNumber].getNumberAck() >Constant.NUMBER_OF_NODES / 2) &&
		     proposals[messageReceived.m-firstProposalNumber].getStatus().equals("Accepted"))
		    {
			proposals[messageReceived.m-firstProposalNumber].accNum = messageReceived.accNum;
			proposals[messageReceived.m-firstProposalNumber].accVal = messageReceived.accVal;
			proposals[messageReceived.m-firstProposalNumber].setStatus("Commit");
		    	
		    sendCommit(messageReceived, nodeList);
		    	
		    }
	}
}
