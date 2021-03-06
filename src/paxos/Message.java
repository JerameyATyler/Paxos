/* Distributed Systems Project #2
 * Jeramey Tyler
 * John Sheehan
 * 
 * Message Class - Stores a message which can be sent and received by a node
 */

package paxos;

import java.util.ArrayList;

import java.io.Serializable;

public class Message implements Serializable {

	int[][] dictionary;
	ArrayList<EventRecord> log;    //the entire log is the value for consensus
	private static final long serialVersionUID = 1L;
	// Added for testing purposes
	String msg;
	Appointment apt;
	int eventType;
	Constant.messageType messageType;
	String sender;
	int m; 			// paxos message values
	int accVal;    
	int accNum;
	int v;   // used for testing purposes
        //Properties for leader election
        String[] receivers;
        String highestId;
}