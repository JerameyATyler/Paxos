package message;

import java.io.Serializable;
public class Appointment implements Comparable, Serializable{
	private static final long serialVersionUID = 1L;
    int creator;
    String name;
    boolean[] attendees = new boolean[4];
    int startHour;
    int startMinute;
    int duration;
    int day;
    int eventNumber;

    public Appointment(int creator, String name, boolean[] attendees, 
            int startHour, int startMinute, int duration, int day, 
            int eventNumber) {
        this.creator = creator;
        this.name = name;
        this.attendees = attendees;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.duration = duration;
        this.day = day;
        this.eventNumber = eventNumber;
    }

    @Override
    public int compareTo(Object t) { 
        Appointment apt = (Appointment)t;
        if(this.day < apt.day)
        {
            return 1;
        }
        else if(this.day > apt.day)
        {
            return -1;
        }
        else if(this.startHour < apt.startHour)
        {
            return 1;
        }
        else if(this.startHour > apt.startHour)
        {
            return -1;
        }
        else if(this.startMinute < apt.startMinute)
        {
            return 1;
        }
        else if(this.startMinute > apt.startMinute)
        {
            return -1;
        }
        else
        {
            return 0;
        }

    }
    
    @Override
    public String toString()
    {
        String str = "";
        str += this.creator + " ";
        str += this.name + " ";
        for(int i = 0; i < attendees.length; i++)
        {
            if(attendees[i])
            {
                str += i + ",";
            }
        }
        str += " ";
        str += this.startHour + " ";
        str += this.startMinute + " ";
        str += this.duration + " ";
        str += this.day + " ";
        str += this.eventNumber;
        
        return str;
    }
}
