/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Proposer Class - Models a Paxos Proposer
 */

package paxos;

public class Proposer {
	
	// Proposer numbers need to be unique for all nodes
	private int nextProposalNumber = 0;

	// sends a Prepare message to all acceptors
	void sendPrepare(){
		nextProposalNumber++;		
		// send message
	}
	
	// Chooses an accepted value
	void choose(){
		
	}
	
	// Send Accept
	void sendAccept(){
		// send accept message to all acceptors
	}
	
	
	// Send Commit
	void sendCommit(){
		
	}
	
	void promiseReceived(Message messageReceived){
		
	}
	
	void ackReceived(Message messageReceived){
		
	}
}


