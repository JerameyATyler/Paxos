/* Distributed Systems Project #2
 * Jeramey Tyler
 * John Sheehan
 * 
 * Message Class - Stores a message which can be sent and received by a node
 */

package message;

import java.util.ArrayList;

import java.io.Serializable;

public class Message implements Serializable {

	int[][] dictionary;
	ArrayList<EventRecord> log;
	private static final long serialVersionUID = 1L;
	// Added for testing purposes
	String msg;
	Appointment apt;
	int eventType;
}