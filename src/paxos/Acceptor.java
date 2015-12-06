/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Acceptor Class - Models a Paxos Acceptor (and also the Learner)
 */

package paxos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

//import java.io.IOException;

public class Acceptor {
	
	// Node which contains this proposer
	public Node node;
    
	private int maxPrepare = 0; //largest proposal number for which it has responded to a prepare message (initially 0)
	private int accNum = -1;    //largest proposal number of proposal it has accepted (initially -1 for null)
	private int accVal = -1;    //value of proposal numbered accNum (initially -1 for null) 
	
    //Store the list of event records that are part of the log as the accepted value
    ArrayList<EventRecord> accLog = new ArrayList();
	
	// Obtain largest proposal number for which it has responded to a prepare message
	public int getMaxPrepare() {
		return maxPrepare;
	}
	
	// Set largest proposal number for which it has responded to a prepare message
	public void setMaxPrepare(int maxPrepare) {
		this.maxPrepare = maxPrepare;
                this.writeMaxPrepare();
	}
	
	// Get largest proposal which has been accepted
	public int getAccNum() {
		return accNum;
	}
	
	// Set largest proposal which has been accepted
	public void setAccNum(int accNum) {
		this.accNum = accNum;
                this.writeAcc();
	}
	
	// Get value of proposal numbered accNum (initially null) 
	public int getAccVal() {
		return accVal;
	}
	
	// Set value of proposal numbered accNum
	public void setAccVal(int accVal) {
		this.accVal = accVal;
	}
	
	
    // Prepare message received from Proposer
	public void prepareReceived(Message messageReceived, ArrayList<Node> nodeList){
	    System.out.printf("Prepare Message Received from %s: %s\n",messageReceived.sender, messageReceived.msg);
	    if (messageReceived.m > maxPrepare)
	        {
	    	maxPrepare = messageReceived.m;
	    	this.writeMaxPrepare();
                
	    	//if this node becomes leader set nextProposalNumber to m+1
	    	if (node.proposer.getNextProposalNumber() <= maxPrepare)
	    		node.proposer.setNextProposalNumber(maxPrepare+1);
	    	
	    	// prepare reply
	        Message promiseMessage = new Message();
	        promiseMessage.msg = "Promise";
	        promiseMessage.messageType = Constant.messageType.Promise;
	        promiseMessage.sender = node.getNodeName();
	        promiseMessage.m = messageReceived.m;
	        promiseMessage.accNum = accNum;
	        promiseMessage.accVal = accVal;
	        
	        // send promise response to Proposer
	        try
            {
	    	//send the promise message to the Proposer
	    	Iterator<Node> iterator = nodeList.iterator();
	    	Node Beatle;
	    	while(iterator.hasNext())
	    	    {
	    		Beatle=iterator.next();
	    		
	    		if (Beatle.getNodeName().equals(messageReceived.sender))
	    		   {
	    		   node.sendUDPMessage(Beatle, promiseMessage);
	    		   System.out.printf("Promise message# %d sent to %s:\n",promiseMessage.m,Beatle.getNodeName());
	    		   }
	    	    }
	    	
            }
	        catch (Exception ex)
	        {
	        	ex.printStackTrace();
	        }
	        }
	}
	
	public void acceptReceived(Message messageReceived, ArrayList<Node> nodeList){
		 System.out.printf("Accept Message# %d Received from %s: %s\n",messageReceived.m, messageReceived.sender,messageReceived.msg);
		 
		 // Record Accept
		 if (messageReceived.m >= maxPrepare)
		    {  
		    accNum = messageReceived.m;
                    this.writeAcc();
		    accVal = messageReceived.v;
		    accLog = messageReceived.log;
			
		    // send back Ack
			sendAck(messageReceived,nodeList);
		    }
		
	}
	
    public void commitReceived(Message messageReceived, ArrayList<Node> nodeList){
    	 System.out.printf("Commit Message# %d Received from %s: %s\n",messageReceived.m,messageReceived.sender,messageReceived.msg);
	}
	
 // send Ack to proposer
 	public void sendAck(Message messageReceived, ArrayList<Node> nodeList){
 		
 		// send ack response to Proposer
 		// prepare reply
         Message ackMessage = new Message();
         ackMessage.msg = "Ack";
         ackMessage.messageType = Constant.messageType.Ack;
         ackMessage.sender = node.getNodeName();
         ackMessage.accNum = accNum;
         ackMessage.accVal = accVal;
         ackMessage.m = messageReceived.m;
         ackMessage.log = messageReceived.log;
 		
         // send ack message to Proposer
	        try
         {
	    	Iterator<Node> iterator = nodeList.iterator();
	    	Node Beatle;
	    	while(iterator.hasNext())
	    	    {
	    		Beatle=iterator.next();
	    		
	    		if (Beatle.getNodeName().equals(messageReceived.sender))
	    		   {
	    		   node.sendUDPMessage(Beatle, ackMessage);
	    		   System.out.printf("Ack message# %d sent to %s:\n",ackMessage.m,Beatle.getNodeName());
	    		   }
	    	    }
         }
         catch (Exception ex)
         {
         	ex.printStackTrace();
         }
 		
 	}

    public void initializeAcc()
    {
        if (new File("Acc.txt").isFile())
        {
            File file = new File("Acc.txt");
            try (BufferedReader br
                    = new BufferedReader(new FileReader(file)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    accNum = Integer.parseInt(line);
                }
            }
            catch (Exception ex)
            {

            }
        }
        if (new File("Max.txt").isFile())
        {
            File file = new File("Max.txt");
            try (BufferedReader br
                    = new BufferedReader(new FileReader(file)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    maxPrepare = Integer.parseInt(line);
                }
            }
            catch (Exception ex)
            {

            }
        }
    }

    private void writeMaxPrepare()
    {
        try (PrintWriter writer = new PrintWriter("Max.txt", "UTF-8"))
        {
            writer.println(this.maxPrepare);
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println("lol");
        }
    }
    
    private void writeAcc()
    {
        try (PrintWriter writer = new PrintWriter("Acc.txt", "UTF-8"))
        {
            writer.println(this.accNum);
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println("lol");
        }
    }
}