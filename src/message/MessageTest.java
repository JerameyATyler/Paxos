/* Distributed Systems Project #1 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Message Test Program
 */

package message;

import java.io.IOException;

public class MessageTest {

	static final int portNumber = 9096;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Comment out all but one on each AWS Instance
		Node John = new Node("John","54.191.5.185",portNumber);
		Node Paul = new Node("Paul","52.89.220.0",portNumber);
		Node George = new Node("George","52.89.113.240",portNumber);
		Node Ringo = new Node("Ringo","54.186.26.182",portNumber);
		
		// Comment this out for all nodes except Paul
		try {
			//Paul.listenForMessages();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		// Comment this out for all nodes except John
		Message testMessage = new Message();
		testMessage.msg = "Hello";
		
		try {
			John.sendMessage(Paul, testMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		
		Message message2 = new Message("Paul","I Say No");
		try {
			message2.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("Oh No Paul is Dead");
		
		Message message3 = new Message("Ringo","You say goodbye");
		try {
			message3.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message message4 = new Message("George","and I say hello");
		try {
			message4.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}

}
