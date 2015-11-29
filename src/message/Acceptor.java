/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Acceptor Class - Models a Paxos Acceptor
 */

package message;

//import java.io.IOException;

public class Acceptor {
	
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

	public void listenForMessages(){
	
	}
    
}
