/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Proposal Class - Models a Paxos Proposal
 */

package paxos;

import java.util.ArrayList;

public class Proposal {
	private int proposalNumber;
	private int numberAccepts = 0;
	private String status = "Active";
	
	// initially unset
	public int accNum = -1;
	public int accVal = -1;
	
	public int[][] dictionary;
	public ArrayList<EventRecord> log;
	
	public Proposal(int propNumber){
		setProposalNumber(propNumber);
	}

	public int getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(int proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public int getNumberAccepts() {
		return numberAccepts;
	}

	public void setNumberAccepts(int numberAccepts) {
		this.numberAccepts = numberAccepts;
	}
	
	public void addAcceptance(){
		numberAccepts++;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
