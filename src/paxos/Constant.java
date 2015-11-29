/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Stores all constants used throughout the implementation
 */

package paxos;

interface Constant {
	
    /* Updated Node information as of 11/29/2015 */
    
	public static final String NODE_JOHN = "John";
	public static final String NODE_PAUL = "Paul";
	public static final String NODE_GEORGE = "George";			     
	public static final String NODE_RINGO = "Ringo";
	public static final String NODE_WALRUS = "Walrus"; 
	
    public static final String JOHN_IP = "52.33.135.248";
    public static final String PAUL_IP = "54.208.8.249";
    public static final String GEORGE_IP = "54.183.186.188";			     
    public static final String RINGO_IP = "52.19.207 .28";
    public static final String WALRUS_IP = "52.29.131.190"; 
    
    //Port number for all nodes
    static final int TCP_PORT_NUMBER = 9097;     // used for tcp leader election
    static final int UDP_PORT_NUMBER = 9877;     // used for udp atomic broadcast
    
    //Max UDP Packet Size
    static final int UDP_MAX_PACKET_SIZE = 1024; // max size may need to be increased 
    
    //Number of Nodes
    static final int NUMBER_OF_NODES = 5;        // five nodes in this paxos implementation
	    
}