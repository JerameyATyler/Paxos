
package paxos;

import java.util.HashMap;

/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Leader Class - Handles leader elections
 */
public class Leader
{

    //Downstream nodes keys in order
    String[] downstreamNodes;
    //Map of nodes
    HashMap<String, Node> nodeMap;
    //Id of this node
    String id;
    //The current node
    Node current;
    //Id of leader node
    Node leader;

    //Leader constructor takes a list of all downstream neighbors and an integer
    //representing the id of this node
    public Leader(String[] downstreamNodes, Node current,
                  HashMap<String, Node> nodeMap)
    {
        //Initialize downstreamNodes and id
        this.downstreamNodes = downstreamNodes;
        this.current = current;
        this.nodeMap = nodeMap;
        
        this.id = this.current.getNodeName();
        //Create list of receivers and initiate election
        String[] receivers = new String[5];
        receivers[0] = this.id;
        election(this.id, receivers);

        //Start heartbeat
        Runnable backGroundRunnable = new Runnable()
        {
            public void run()
            {
                try
                {
                    heartbeat();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        Thread heartbeatListener = new Thread(backGroundRunnable);
        heartbeatListener.start();

    }

    //Ping leader periodically to determine if it is still alive
    private void heartbeat()
    {
        //Continuously loop and check for leader liveness
        while (true)
        {
            try
            {
                Thread.sleep(10000);
                if (this.leader != null && !this.id.equals(this.leader.
                        getNodeName()))
                {

                    Message m = new Message();
                    m.messageType = Constant.messageType.Heartbeat;
                    m.sender = this.current.getNodeName();

                    this.current.sendTCPMessage(leader, m);
                }
            }
            catch (Exception ex)
            {
                if (ex instanceof java.net.SocketException)
                {
                    //Create list of receivers and initiate election
                    String[] receivers = new String[5];
                    receivers[0] = this.id;
                    election(this.id, receivers);
                }
            }
        }
    }

    //Send election message downstream
    private void election(String highestId, String[] receivers)
    {
        for (String downstreamNode : this.downstreamNodes)
        {
            try
            {
                //Initialize message values
                Message m = new Message();
                m.messageType = Constant.messageType.Election;
                m.sender = this.current.getNodeName();
                m.highestId = highestId;
                m.receivers = receivers;

                this.current.sendTCPMessage(nodeMap.get(downstreamNode), m);
                //If message is sent without throwing exception then node is live
                break;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void receiveElection(String highestId, String[] receivers)
    {
        if (receivers[0].equals(this.id))
        {
            leader = this.current;

            //Create list of receivers and initiate election
            receivers = new String[5];
            receivers[0] = this.id;

            coordinator(this.id, receivers);
        }
        else
        {
            //Add this id to list of receivers
            for (int i = 0; i < receivers.length; i++)
            {
                if (receivers[i] == null)
                {
                    receivers[i] = this.id;
                    break;
                }
            }
            if(this.id.compareTo(highestId) < 0)
            {
                highestId = this.id;
            }

            election(highestId, receivers);
        }
    }

    private void coordinator(String highestId, String[] receivers)
    {
        for (String downstreamNode : this.downstreamNodes)
        {
            try
            {
                //Initialize message values
                Message m = new Message();
                m.messageType = Constant.messageType.Coordinator;
                m.sender = this.current.getNodeName();
                m.highestId = highestId;
                m.receivers = receivers;

                this.current.sendTCPMessage(nodeMap.get(downstreamNode), m);
                //If message is sent without throwing exception then node is live
                break;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void receiveCoordinator(String highestId, String[] receivers)
    {
        if (!receivers[0].equals(this.id))
        {
            this.leader = nodeMap.get(highestId);
            for (int i = 0; i < receivers.length; i++)
            {
                if (receivers[i] == null)
                {
                    receivers[i] = this.id;
                    break;
                }
            }

            coordinator(highestId, receivers);
        }
    }
}
