/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Appointment Class - Models an Event Record
 */

package paxos;

import java.io.Serializable;

public class EventRecord implements Comparable, Serializable
{
    int eventNumber;
    int eventType;
    int userNumber;
    String parameters;

    public EventRecord(int eventNumber, int eventType, int userNumber, 
                                                       String parameters)
    {
        this.eventNumber = eventNumber;
        this.eventType = eventType;
        this.userNumber = userNumber;
        this.parameters = parameters;
    }
    
    @Override
    public int compareTo(Object t)
    {
        if(this.eventNumber == ((EventRecord)t).eventNumber && 
                this.userNumber == ((EventRecord)t).userNumber)
        {
            return 0;
        }
        return -1;
    }
   
    @Override
    public String toString()
    {
        String str = "";
        
        str += userNumber + "/";
        str += eventNumber + "/";
        str += eventType + "/";
        str += parameters;
        
        return str;
    }
}
