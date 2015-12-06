/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Calendar Class - Models the Calendar on a particular node
 */

package paxos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Calendar
{
    // Only uncomment ONE of these entries based on this AWS instance
    //Node node = new Node(Constant.NODE_JOHN,Constant.JOHN_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER, true, true);
    //Node node = new Node(Constant.NODE_PAUL,Constant.PAUL_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER,false, true);
    Node node = new Node(Constant.NODE_GEORGE,Constant.GEORGE_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER,false, true);
    //Node node = new Node(Constant.NODE_RINGO,Constant.RINGO_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER,false, true);
    //Node node = new Node(Constant.NODE_WALRUS,Constant.WALRUS_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER,false, true);
	
    //Create all node instances - Initially John is leader
    Node John = new Node(Constant.NODE_JOHN,Constant.JOHN_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER, true, false);
    Node Paul = new Node(Constant.NODE_PAUL,Constant.PAUL_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER, false, false);
    Node George = new Node(Constant.NODE_GEORGE,Constant.GEORGE_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER, false, false);
    Node Ringo = new Node(Constant.NODE_RINGO,Constant.RINGO_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER, false, false);
    Node Walrus = new Node(Constant.NODE_WALRUS,Constant.WALRUS_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER, false, false);
    
    //Add a list of the nodes so each node knows how to communicate with the rest
    ArrayList<Node> nodeList=new ArrayList<Node>();
 
    ArrayList<Node>[] nodes
    = (ArrayList<Node>[]) new ArrayList[Constant.NUMBER_OF_NODES];
    
    //Appointments for all users that are known by this user. 
    //The user's appointments correspond to appointments[userNumber]
    ArrayList<Appointment>[] appointments
            = (ArrayList<Appointment>[]) new ArrayList[Constant.NUMBER_OF_NODES];

    //The index of appointments that corresponds to this user
    private int userNumber = 0;

    //The current event number
    private int eventNumber = 0;

    //The dictionary of what this user knows about the knowledge of the 
    //other users
    int[][] dictionary = new int[Constant.NUMBER_OF_NODES][Constant.NUMBER_OF_NODES];

    //The list of event records that is used to maintain the log
    ArrayList<EventRecord> log = new ArrayList();

    
        //Store Leader Node (Defaults to John)
        Node Leader = John;
    public static void main(String[] args)
    {
        Calendar cal = new Calendar();
        //cal.test();
        
        cal.runCalendar();
    }

    /**
     * Starts the calendar
     */
    public void runCalendar()
    {
    	
        //Initialize the appointments lists
        
        //Check if log exists. If so recreate it
        if(new File("log.txt").isFile())
        {
            this.reconstructLog();
        }
        else
        {
            for (int i = 0; i < this.appointments.length; i++)
            {
                this.appointments[i] = new ArrayList();
            }
        }
        
        //Initialize the node list
        nodeList.add(John);
        nodeList.add(Paul);
        nodeList.add(George);
        nodeList.add(Ringo);
        nodeList.add(Walrus);
        
        //Each node stores a list of all other nodes
        John.setNodeList(nodeList);
        Paul.setNodeList(nodeList);
        George.setNodeList(nodeList);
        Ringo.setNodeList(nodeList);
        Walrus.setNodeList(nodeList);
        
        //Save the Node List
        node.setNodeList(nodeList);

        //Listen in the background for TCP messages
        Runnable backGroundRunnable = new Runnable(){
        	public void run(){
        		try{
        			node.listenForTCPMessages(Calendar.this);
        		}catch(IOException e){
        			e.printStackTrace();
        		}
        	}
        };
        Thread messageListener = new Thread(backGroundRunnable);
        messageListener.start();

        //Listen in the background for UDP messages
        Runnable backGroundUDPRunnable = new Runnable(){
        	public void run(){
        		try{
        			node.listenForUDPMessages(Calendar.this);
        		}catch(IOException e){
        			e.printStackTrace();
        		}
        	}
        };
        Thread messageUDPListener = new Thread(backGroundUDPRunnable);
        messageUDPListener.start();
        
        findLeader();
        
        //Send the Leader a recover message to obtain latest version of the log
        Message recoverMessage = new Message();
        recoverMessage.msg = "Recover";
        recoverMessage.messageType = Constant.messageType.Recover;
        recoverMessage.sender = node.getNodeName();
          
	    try
            {
	    	//send the commit message to the Leader
	    	node.sendUDPMessage(Leader, recoverMessage);
	    	//System.out.printf("Recover message# sent to %s\n",Leader.getNodeName());
            }
        
	    catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        
        boolean cont = true;
        
        // Display Calendar User Interface
        while (cont)
        {
        	findLeader();
        	
 		    System.out.printf("Node %s is the Leader\n\n",Leader.getNodeName());
        	
            String in = "";
            //Prompt user for input
            while (cont)
            {
                System.out.printf("Welcome to %s's calendar. Enter the number "
                        + "corresponding to what you want to do: \n",node.getNodeName());
                System.out.println("1) Create appointment");
                System.out.println("2) Delete appointment");
                //System.out.println("3) Edit appointment");
                System.out.println("3) Print appointments");
                System.out.println("4) Exit");

                Scanner s = new Scanner(System.in);
                in = s.nextLine();
                cont = !this.isValidInt(in);
            }
            
            cont = true;

            int input = Integer.parseInt(in);
                    
            //Invoke appropriate functions from user input
            switch (input)
            {
                case 1:
                    Appointment apt = this.createAppointment();
                    if (this.isValidAppointment(apt))
                    {
                        this.appointments[this.userNumber]
                                .add(apt);
                        this.dictionary[this.userNumber][this.userNumber]
                                = this.eventNumber;

                        EventRecord eR = this.
                                createEventRecord(eventType.Create, apt);
                        
                        for(int i = 0; i < apt.attendees.length; i++)
                        {
                        	if(apt.attendees[i] && i != this.userNumber)
                        	{
                        		EventRecord e = createEventRecord(eventType.Create, i, apt);
                        		this.log.add(e);
                        	}
                        }
                        
                        this.log.add(eR);
                        this.writeLog();
                        this.reconstructAppointmentList();
                        Message m = new Message();
                        m.dictionary = this.dictionary;
                        m.apt = apt;
                        m.eventType = 1;
                       
                        //Initiate paxos protocol to add appointment      	       
                        System.out.println("Request sent to add this appointment to the Calendars...\n");
                        
                        // Initiate message
                        Message initiateMessage = new Message();
                        initiateMessage.msg = "Initiate";
                        initiateMessage.messageType = Constant.messageType.Initiate;
                        initiateMessage.sender = node.getNodeName();
                        initiateMessage.log = this.log;
                		
                	    try
                            {
                	    	node.sendUDPMessage(Leader, initiateMessage);
                	    	}
                        catch (Exception ex)
                            {
                            ex.printStackTrace();
                            } 
                    }
                    else
                    {
                        System.out.println("Warning: The appointment entered "
                                + "conflicts with another appointment!");
                    }
                    break;
                case 2:
                    this.deleteAppointment();
                    
                    //Initiate paxos protocol to delete appointment      	       
                    System.out.println("Request sent to delete this appointment from the Calendars...\n");
                    
                    // Initiate message
                    Message initiateMessage = new Message();
                    initiateMessage.msg = "Initiate";
                    initiateMessage.messageType = Constant.messageType.Initiate;
                    initiateMessage.sender = node.getNodeName();
                    initiateMessage.log = this.log;
            		
            	    try
                        {
            	    	node.sendUDPMessage(Leader, initiateMessage);
            	    	}
                    catch (Exception ex)
                        {
                        ex.printStackTrace();
                        } 
                    
                    break;
                case 3:
                    this.printAppointment();
                    break;
                case 4:
                    cont = false;
                    break;
            }
        }
        System.exit(0);
    }

    //Appointment creation initiated by the user
    private Appointment createAppointment()
    {
        String name;
        boolean[] attendees;
        int startHour;
        int startMinute;
        int duration;
        int day;

        String prompt = "Enter the name of the appointment: ";
        System.out.println(prompt);
        name = new Scanner(System.in).nextLine();
        name = name.replace(' ', '_');
        prompt = "Enter the day using an integer with Sunday starting at 1: ";
        day = getInt(1, 7, prompt);

        prompt = "Enter the starting hour in 24-hour format: ";
        startHour = getInt(0, 23, prompt);

        prompt = "Enter the starting minute: ";
        startMinute = getInt(0, 59, prompt);

        prompt = "Enter the duration in 15 minute increments: ";
        duration = getMinute(prompt, startHour, startMinute);

        attendees = getAttendees();

        Appointment appt = new Appointment(userNumber, name, attendees,
                                           startHour, startMinute, duration, day,
                                           ++this.eventNumber);
        
        return appt;
    }

    //Validate and insert or reject incoming appointment
    private void createAppointment(Message m)
    {
        //Set eventNumber to the max between current eventNumber and incoming 
        //eventNumber
        this.eventNumber = Math.max(
                m.dictionary[this.userNumber][this.userNumber], ++this.eventNumber);

        if (this.isValidAppointment(m.apt))
        {
            this.appointments[m.apt.creator].add(m.apt);
            boolean[] attendees = new boolean[Constant.NUMBER_OF_NODES];
            System.arraycopy(m.apt.attendees, 0, attendees, 0, attendees.length);

            Appointment newApppointment = new Appointment(m.apt.creator,
                                                          m.apt.name,
                                                          attendees,
                                                          m.apt.startHour,
                                                          m.apt.startMinute,
                                                          m.apt.duration,
                                                          m.apt.day,
                                                          ++this.eventNumber);
            this.appointments[this.userNumber].add(newApppointment);
            this.dictionary[this.userNumber][this.userNumber] = this.eventNumber;

            this.writeLog();
            this.reconstructAppointmentList();
        }
        else
        {
            this.conflictingIncomingAppointment(m);
        }
    }

    //Handle incoming appointment with a conflict
    private void conflictingIncomingAppointment(Message m)
    {
        m.apt.attendees[this.userNumber] = false;
        this.appointments[m.apt.creator].add(m.apt);
        this.dictionary[this.userNumber][this.userNumber] = ++this.eventNumber;
        
        EventRecord e = this.createEventRecord(eventType.Delete, m.apt);
        this.log.add(e);
        
        
        for(int i = 0; i < this.appointments.length; i++)
        {
        	ArrayList<Appointment> aptToRemove = new ArrayList();
        for(Appointment apt: this.appointments[i])
        {
        	if(apt.name.equals(m.apt.name))
        	{
        		aptToRemove.add(apt);
        		if(apt.attendees[i])
        		{
        			EventRecord eR = this.createEventRecord(eventType.Create, m.apt);
        			this.log.add(eR);
        		}
        	}        	
        }
        
        for(Appointment apt: aptToRemove)
        {
        	this.appointments[this.userNumber].remove(apt);
        }
        }
        m.eventType = 3;
        this.send(m);
        this.writeLog();
        this.reconstructAppointmentList();
    }

    //Edit an existing appointment
    private void editAppointment()
    {
        String prompt = "Enter the number of the appointment you wish to "
                + "edit:\n";
        ArrayList<Appointment> apts = appointments[userNumber];
        for (int i = 1; i <= apts.size(); i++)
        {
            prompt += i + ")" + apts.get(i - 1).name + "\n";
        }
        System.out.println(apts.size() + ") Exit");

        int aptIndex = this.getInt(1, apts.size(), prompt) - 1;

        boolean[] usersToNotify = new boolean[Constant.NUMBER_OF_NODES];
        for (int i = 0; i < usersToNotify.length; i++)
        {
            if (apts.get(aptIndex).attendees[i])
            {
                usersToNotify[i] = true;
            }
        }

        prompt = "Enter the number of the appointment property you wish to "
                + "edit:\n";
        prompt += "1) Name\n";
        prompt += "2) Day\n";
        prompt += "3) Starting Hour\n";
        prompt += "4) Starting Minute\n";
        prompt += "5) Duration\n";
        prompt += "6) Attendees\n";

        int prop = this.getInt(1, 6, prompt);

        Scanner s = new Scanner(System.in);
        switch (prop)
        {
            case 1:
            {
                prompt = "Enter the name of the appointment: ";
                System.out.println(prompt);
                apts.get(aptIndex).name = s.nextLine().replace(' ', '_');
                break;
            }
            case 2:
            {
                prompt = "Enter the day using an integer with Sunday starting "
                        + "at 1: ";
                apts.get(aptIndex).day = getInt(1, 7, prompt);
                break;
            }
            case 3:
            {
                prompt = "Enter the starting hour in 24-hour format: ";
                apts.get(aptIndex).startHour = getInt(0, 23, prompt);
                break;
            }
            case 4:
            {
                prompt = "Enter the starting minute: ";
                apts.get(aptIndex).startMinute = getInt(0, 59, prompt);
                break;
            }
            case 5:
            {
                prompt = "Enter the duration in 15 minute increments: ";
                apts.get(aptIndex).duration = getMinute(prompt, apts.get(
                                                        aptIndex).startHour,
                                                        apts.get(aptIndex).startMinute);
                break;
            }
            case 6:
            {
                apts.get(aptIndex).attendees = this.getAttendees();

                for (int i = 0; i < usersToNotify.length; i++)
                {
                    if (apts.get(aptIndex).attendees[i])
                    {
                        usersToNotify[i] = true;
                    }
                }
                break;
            }
        }
        apts.get(aptIndex).eventNumber = ++this.eventNumber;
        this.dictionary[userNumber][userNumber] = this.eventNumber;
        EventRecord e = this.createEventRecord(eventType.Edit, apts.
                                               get(aptIndex));
        this.log.add(e);
        this.writeLog();
        this.reconstructAppointmentList();
        Message m = new Message();
        m.apt = apts.get(aptIndex);
        m.eventType = 3;
        m.dictionary = this.dictionary;
        m.log = this.log;
        this.send(m);
        this.send(m, usersToNotify);
    }

    //Determine if an appointment is valid based on the knowledge the user 
    //currently has
    private boolean isValidAppointment(Appointment apt)
    {
        for (int i = 0; i < apt.attendees.length; i++)
        {
            if (apt.attendees[i])
            {
                for (Appointment appointment : this.appointments[i])
                {
                	if(!appointment.name.equals(apt.name))
                	{
                    if (appointment.day == apt.day)
                    {
                        //Find starting and ending times of appointments to be
                        //compared in minutes from midnight.
                        int startTime1 = 60 * appointment.startHour
                                + appointment.startMinute;
                        int endTime1 = 60 * appointment.startHour
                                + appointment.startMinute
                                + appointment.duration;
                        int startTime2 = 60 * apt.startHour
                                + apt.startMinute;
                        int endTime2 = 60 * apt.startHour + apt.startMinute
                                + apt.duration;
                        //If appointment 1 starts before appointment 2
                        if (startTime1 < startTime2)
                        {
                            //If appointment 1 ends after appointment 2
                            if (endTime1 > startTime2)
                            {
                                return false;
                            }
                        }
                        //If appointment 1 starts after appointment 2
                        else if (startTime1 > startTime2)
                        {
                            //If appointment 1 starts before appointment 2 ends
                            if (endTime2 > startTime1)
                            {
                                return false;
                            }
                        }
                        //If the two appointments start at the same time.
                        else
                        {
                            return false;
                        }
                    }
                	}
                }
            }
        }

        return true;
    }

    //Delete one of the user's existing appointments
    private void deleteAppointment()
    {
        boolean cont = true;
        String in = "";
        while (cont)
        {

            System.out.println("Enter the number of the appointment "
                    + "you wish to delete:");
            for (int i = 1; i < appointments[userNumber].size() + 1; i++)
            {
                System.out.println(i + ") "
                        + appointments[userNumber].get(i - 1).name);
            }
            System.out.println("0) to cancel");

            Scanner s = new Scanner(System.in);
            in = s.nextLine();

            cont = !isValidInt(in);
        }

        int option = Short.parseShort(in) - 1;
        if (option > 0)
        {
            Appointment toRemove = appointments[userNumber].get(option);
            EventRecord e = this.createEventRecord(eventType.Delete, toRemove);
            Message m = new Message();
            appointments[userNumber].remove(toRemove);
            this.dictionary[userNumber][userNumber] = ++this.eventNumber; 
            this.log.add(e);
            m.dictionary = this.dictionary;
            m.log = this.log;
            m.apt = toRemove;
            m.eventType = 2;
            //this.pruneLog();
            this.reconstructAppointmentList();
           // this.send(m);           
        }
    }

    //Prompt the user for an integer and verify it is within a set bounds
    private int getInt(int lowerBound, int upperBound, String prompt)
    {
        Scanner scanner = new Scanner(System.in);
        String in = "";
        boolean isValid = false;
        while (!isValid)
        {
            System.out.println(prompt);
            in = scanner.nextLine();
            if (isValidInt(in) && lowerBound <= Integer.parseInt(in)
                    && Integer.parseInt(in) <= upperBound)
            {
                isValid = true;
            }
            else
            {
                System.out.println("Enter an integer between " + lowerBound
                        + " and " + upperBound + ".");
            }
        }
        return Integer.parseInt(in);
    }

    //Prompt the user for the duration of an appointment and verify that it
    //is an increment of 15 and does not extend into the next day
    private int getMinute(String prompt, int startHour, int startMinute)
    {
        Scanner scanner = new Scanner(System.in);
        String in = "";
        boolean isValid = false;
        while (!isValid)
        {
            System.out.println(prompt);
            in = scanner.nextLine();
            if (isValidInt(in))
            {
                int minutes = Integer.parseInt(in);
                if (minutes % 15 == 0 && minutes <= ((24 - startHour) * 60)
                        - startMinute)
                {
                    isValid = true;
                }
                else
                {
                    System.out.println("Duration must be in 15 minute "
                            + "increments and cannot extend unto the next "
                            + "day.");
                }
            }
        }
        return Integer.parseInt(in);
    }

    //Verifies that user input is an integer
    private boolean isValidInt(String in)
    {
        int iIn;
        try
        {
            iIn = Integer.parseInt(in);

        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    //Prompt the user for who will be attending an appointment and validate the
    //input
    private boolean[] getAttendees()
    {
        String prompt = "Enter the numbers of the attendess separated by "
                + "commas:\n1)John\n2)Paul\n3)George\n4)Ringo\n5)Walrus";
        boolean cont = true;
        boolean[] attendees = new boolean[Constant.NUMBER_OF_NODES];
        switch(this.node.getNodeName())
        {
            case "John":
            {
                this.userNumber = 0;
                break;
            }
            case "Paul":
            {
                this.userNumber = 1;
                break;
            }
            case "George":
            {
                this.userNumber = 2;
                break;
            }
            case "Ringo":
            {
                this.userNumber = 3;
                break;
            }
            case "Walrus":
            {
                this.userNumber = 4;
                break;
            }
            
        }
        attendees[this.userNumber] = true;
        attendees[userNumber] = true;
        while (cont)
        {
            cont = false;
            Scanner scanner = new Scanner(System.in);
            System.out.println(prompt);
            String in = scanner.nextLine();
            String[] attendeesStr = in.split(",");
            for (String s : attendeesStr)
            {
                try
                {
                    s = s.trim();
                    int num = Short.parseShort(s);
                    attendees[num - 1] = true;
                }
                catch (Exception ex)
                {
                    System.out.println("One or more input was incorrect!");
                    attendees = new boolean[Constant.NUMBER_OF_NODES];
                    cont = true;
                    break;
                }
            }
        }
        return attendees;
    }

    //Prompt the user for whose appointments they would like to see then print
    //them
    private void printAppointment()
    {
        int aptIndex = this.userNumber;
        boolean cont = true;
        while (cont)
        {
            System.out.println("Enter the user number whose appointments "
                    + "you wish to see:");
            System.out.println("1) John");
            System.out.println("2) Paul");
            System.out.println("3) George");
            System.out.println("4) Ringo");
            System.out.println("5) Walrus");

            Scanner s = new Scanner(System.in);
            String aptIndexStr = s.nextLine();

            if (this.isValidInt(aptIndexStr))
            {
                aptIndex = Integer.parseInt(aptIndexStr);
                if (aptIndex >= 1 && aptIndex <= Constant.NUMBER_OF_NODES)
                {
                    aptIndex -= 1;
                    cont = false;
                }
                else
                {
                    System.out.println("Warning: User number entered is "
                            + "invalid!");
                }
            }
            else
            {
                System.out.println("Warning: User number entered is "
                        + "invalid!");
            }
        }
        if (appointments[aptIndex].size() > 0)
        {
            for (Appointment appt : appointments[aptIndex])
            {
                String day;
                switch (appt.day)
                {
                    case 1:
                        day = "Sunday";
                        break;
                    case 2:
                        day = "Monday";
                        break;
                    case 3:
                        day = "Tuesday";
                        break;
                    case 4:
                        day = "Wednesday";
                        break;
                    case 5:
                        day = "Thursday";
                        break;
                    case 6:
                        day = "Friday";
                        break;
                    case 7:
                        day = "Saturday";
                        break;
                    default:
                        day = "Smurfsday";
                }
                String attendees = "";
                String[] attendeesStr =
                {
                    "John", "Paul", "George", "Ringo", "Walrus"
                };
                for (int i = 0; i < appt.attendees.length; i++)
                {
                    if (appt.attendees[i] && !attendeesStr[i].equals(this.node.getNodeName()))
                    {
                        attendees += attendeesStr[i] + " ";
                    }
                    else if (appt.attendees[i] 
                            && attendeesStr[i].equals(this.node.getNodeName()))
                    {
                        attendees += "you ";
                    }
                }
                System.out.println("\n" + appt.name + " starts on " + day + " at:");
                
                System.out.println(appt.startHour + ":" + appt.startMinute);
                System.out.println("and lasts for " + appt.duration
                        + " minutes.");
                System.out.println("Attendees are: " + attendees);
            }
        }
        else
        {
            System.out.println("User does not have any appointments");
        }
        System.out.println();
    }

    //Receive message from another user. Union logs and dictionaries. Insert 
    //necessary appointments into the appointments lists.
    public void receive(Message m)
    {
        this.unionDictionary(m.dictionary);
        this.unionLog(m.log);
        switch(m.eventType)
        {
        case 1:
        {
            this.createAppointment(m);
        	break;
        }
        case 2:
        {
        	break;
        }
        case 3:
        {
        	break;
        }
        }
        this.writeLog();
        this.reconstructAppointmentList();
    }

    //Send message to appropriate users
    private void send(Message m)
    {

        m.dictionary = this.dictionary;
        for (int i = 0; i < m.apt.attendees.length; i++)
        {
            if (m.apt.attendees[i] && i != this.userNumber)
            {
                m.log = this.fetchEventRecords(i);
                switch (i)
                {
                
                //Node node = new Node(Constant.NODE_JOHN,Constant.JOHN_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER);
                //Node node = new Node(Constant.NODE_PAUL,Constant.PAUL_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER);
                //Node node = new Node(Constant.NODE_GEORGE,Constant.GEORGE_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER);
                //Node node = new Node(Constant.NODE_RINGO,Constant.RINGO_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER);
                //Node node = new Node(Constant.NODE_WALRUS,Constant.WALRUS_IP,Constant.TCP_PORT_NUMBER, Constant.UDP_PORT_NUMBER);
                
                    case 0:
                    {
                        Node receiver = John;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 1:
                    {
                        Node receiver = Paul;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 2:
                    {
                        Node receiver = George;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 3:
                    {
                        Node receiver = Ringo;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 4:
                    {
                        Node receiver = Walrus;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    //Reconstruct the local log from the stable log
    private void reconstructLog()
    {
        ArrayList<EventRecord> newLog = new ArrayList();

        File file = new File("log.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                EventRecord eR = this.createEventRecord(line);
                newLog.add(eR);
            }
        }
        catch (Exception ex)
        {

        }
        this.log = newLog;
        this.reconstructDictionary();
        this.reconstructAppointmentList();
    }

    //Reconstruct dictionary from stable log
    private void reconstructDictionary()
    {
        int[][] tempDictionary = new int[Constant.NUMBER_OF_NODES][Constant.NUMBER_OF_NODES];
        for (EventRecord eR : this.log)
        {
            int creatorNumber = Integer.parseInt(eR.parameters.split(" ")[0]);
            tempDictionary[eR.userNumber][creatorNumber]
                    = Math.max(eR.eventNumber,
                               tempDictionary[eR.userNumber][creatorNumber]);
        }
        this.dictionary = tempDictionary;
    }

    //Write the log to disk
    public void writeLog()
    {
        //this.pruneLog();
        try (PrintWriter writer = new PrintWriter("log.txt", "UTF-8"))
        {
            this.log.stream().
                    forEach((eR) ->
                    {
                        writer.println(eR);
            });
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println("lol");
        }
    }

    //Remove obsolete data from the log
    private void pruneLog()
    {
        ArrayList<EventRecord> recordsToRemove = new ArrayList();
        for (int i = this.log.size() - 1; i >= 0; i--)
        {
            EventRecord e = this.log.get(i);
            //If the event Type is a delete
            if (e.eventType == 2)
            {
                String name = e.parameters.split(" ")[1];
                //Check if the event names are the same. If so check if the
                //event numbers are different. If the event numbers are the 
                //same check if the user numbers are the same. If the name is
                //the same and either the event number or user number differs
                //delete
                this.log.stream().
                        forEach((log1) ->
                        {
                            String name2 = log1.parameters.split(" ")[1];
                            int eNum2 = log1.eventNumber;
                            int uNum2 = log1.userNumber;
                            if (name.equals(name2) && (e.eventNumber != eNum2
                                                       || e.userNumber != uNum2))
                            {
                                recordsToRemove.add(log1);
                                if (!recordsToRemove.contains(e))
                                {
                                    recordsToRemove.add(e);
                                }
                            }
                });
            }
        }

        recordsToRemove.stream().
                forEach((eR) ->
                {
                    this.log.remove(eR);
        });
    }

    //Union the user's log with the log received from another user
    private void unionLog(ArrayList<EventRecord> log)
    {
        for (EventRecord e : log)
        {
            boolean add = true;
            for (EventRecord f : this.log)
            {
                if (e.compareTo(f) == 0)
                {
                    add = false;
                }
            }
            if (add)
            {
                this.log.add(e);
            }
        }
    }

    //Union the user's dictionary with the dictionary received from another user
    private void unionDictionary(int[][] dictionary)
    {
        for (int i = 0; i < dictionary.length; i++)
        {
            for (int j = 0; j < dictionary[i].length; j++)
            {
                this.dictionary[i][j] = Math.max(dictionary[i][j],
                                                 this.dictionary[i][j]);
            }
        }
    }

    //Create and event record 
    private EventRecord createEventRecord(eventType e, Appointment apt)
    {
        int eNumber;
        int eType;
        String parameters;

        switch (e)
        {
            case Create:
            {
                eType = 1;
                eNumber = apt.eventNumber;
                parameters = apt.toString();

                break;
            }
            case Delete:
            {
                eType = 2;
                eNumber = this.eventNumber;
                parameters = apt.toString();

                break;
            }
            case Edit:
            {
                eType = 3;
                eNumber = apt.eventNumber;
                parameters = apt.toString();

                break;
            }
            default:
            {
                eNumber = 0;
                eType = 0;
                parameters = "";
            }
        }

        return new EventRecord(eNumber, eType, userNumber, parameters);
    }
    
  //Create and event record 
    private EventRecord createEventRecord(eventType e,int uNumber, Appointment apt)
    {
        int eNumber;
        int eType;
        String parameters;

        switch (e)
        {
            case Create:
            {
                eType = 1;
                eNumber = apt.eventNumber;
                parameters = apt.toString();

                break;
            }
            case Delete:
            {
                eType = 2;
                eNumber = this.eventNumber;
                parameters = apt.name;

                break;
            }
            case Edit:
            {
                eType = 3;
                eNumber = apt.eventNumber;
                parameters = apt.toString();

                break;
            }
            default:
            {
                eNumber = 0;
                eType = 0;
                parameters = "";
            }
        }

        return new EventRecord(eNumber, eType, uNumber, parameters);
    }

    //Create an event record from an event record string retreived from 
    //stable log
    private EventRecord createEventRecord(String eString)
    {
        int uNumber = Integer.parseInt(eString.split("/")[0]);
        int eNumber = Integer.parseInt(eString.split("/")[1]);
        int eventType = Integer.parseInt(eString.split("/")[2]);
        String params = eString.split("/")[3];

        return new EventRecord(eNumber, eventType, uNumber, params);
    }

    private void test()
    {
        for (int i = 0; i < this.appointments.length; i++)
        {
            this.appointments[i] = new ArrayList();
        }
        this.reconstructLog();
        this.pruneLog();
    }

    //Recreate appointments lists from log
    public void reconstructAppointmentList()
    {
    	ArrayList<Appointment>[] tempAppointments
        = (ArrayList<Appointment>[]) new ArrayList[Constant.NUMBER_OF_NODES];
    	
    	for (int i = 0; i < tempAppointments.length; i++)
        {
            tempAppointments[i] = new ArrayList();
        }
    	
        for (EventRecord e : this.log)
        {
            String[] params = e.parameters.split(" ");
            int creator = Integer.parseInt(params[0]);
            String name = params[1];
            String[] attendeesStrings = params[2].split(",");
            boolean[] attendees = new boolean[Constant.NUMBER_OF_NODES];
            for (String at : attendeesStrings)
            {
                int i = Integer.parseInt(at);
                attendees[i] = true;
                
            }
            int startHour = Integer.parseInt(params[3]);
            int startMinute = Integer.parseInt(params[4]);
            int duration = Integer.parseInt(params[5]);
            int day = Integer.parseInt(params[6]);
            int eNumber = Integer.parseInt(params[7]);

            Appointment apt = new Appointment(creator, name, attendees,
                                              startHour, startMinute, duration,
                                              day, eNumber);
            for (String at : attendeesStrings)
            {
                int i = Integer.parseInt(at);
                
                if(tempAppointments[i].size() == 0)
                {
                	tempAppointments[i].add(apt);
                }
                else{
                	boolean add = true;
                for(Appointment a: tempAppointments[i])
                {
                	if(a.name.equals(apt.name))
                	{
                        add = false;               		
                	}
                }
                if(add)
                tempAppointments[i].add(apt);
                }
                
            }
        }
        this.appointments = tempAppointments;
    }

    private ArrayList<EventRecord> fetchEventRecords(int uNumber)
    {
        ArrayList<EventRecord> eRs = new ArrayList();

        this.log.stream().
                filter((e)
                        -> (e.eventNumber > dictionary[uNumber][e.userNumber])).
                map((e) ->
                {
                    eRs.add(e);
                    return e;
        }).
                forEach((e) ->
                {
                    dictionary[uNumber][e.userNumber] = e.eventNumber;
        });

        return eRs;

    }

    private void send(Message m, boolean[] usersToNotify)
    {
    	
        for (int i = 0; i < usersToNotify.length; i++)
        {
            if (m.apt.attendees[i] && i != this.userNumber)
            {
                m.log = this.fetchEventRecords(i);
                switch (i)
                {
                    case 0:
                    {
                        Node receiver = John;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 1:
                    {
                        Node receiver = Paul;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 2:
                    {
                        Node receiver = George;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 3:
                    {
                        Node receiver = Ringo;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                    case 4:
                    {
                        Node receiver = Walrus;
                        try
                        {
                            this.node.sendTCPMessage(receiver, m);
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
    
    public void findLeader()
    {
    	//Find the Leader
    	Iterator<Node> iterator = nodeList.iterator();
    	Node Beatle = iterator.next();
    	
    	while(iterator.hasNext())
    	    {
    		if (Beatle.isLeader())
    		   {	
    		   //System.out.printf("Node %s is the Leader\n\n",Beatle.getNodeName());
               Leader = Beatle;
    		   }
    		   
    		Beatle=iterator.next();
    	    }
    }

    public void setLeader(Node leader)
    {
        this.Leader = leader;
        this.nodeList.stream().
                forEach((beatle) ->
        {
            if(beatle.getNodeName().equals(leader.getNodeName()))
            {
                beatle.setLeader(true);
            }
            else
            {
                beatle.setLeader(false);
            }
        });
    }

    //Enum for the the different event types
    private enum eventType
    {

        Create, Delete, Edit
    }

}
