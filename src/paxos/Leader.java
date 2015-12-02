
package paxos;
/* Distributed Systems Project #2 
 * Jeramey Tyler
 * John Sheehan
 * 
 * Leader Class - Handles leader elections
 */

public class Leader
{

    //Downstream nodes
    Node[] downstreamNodes = new Node[4];
    //Id of this node
    int id;
    //The current node
    Node current;
    //Id of leader node
    Node leader;

    //Leader constructor takes a list of all downstream neighbors and an integer
    //representing the id of this node
    public Leader(Node[] downstreamNodes, Node current)
    {
        //Initialize downstreamNodes and id
        this.downstreamNodes = downstreamNodes;
        this.current = current;

        //Create list of receivers and initiate election
        int[] receivers = new int[5];
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
                //TODO: Add check for this.id != leader.id
                if (this.leader != null)
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
                    int[] receivers = new int[5];
                    receivers[0] = this.id;
                    election(this.id, receivers);
                }
            }
        }
    }

    //Send election message downstream
    private void election(int highestId, int[] receivers)
    {
        for (Node downstreamNode : this.downstreamNodes)
        {
            try
            {
                //Initialize message values
                Message m = new Message();
                m.messageType = Constant.messageType.Election;
                m.sender = this.current.getNodeName();
                m.highestId = highestId;
                m.receivers = receivers;

                this.current.sendTCPMessage(downstreamNode, m);
                //If message is sent without throwing exception then node is live
                break;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            //TODO: Should we go ahead and call coordinator if no other node is alive?
        }
    }

    public void receiveElection(int highestId, int[] receivers)
    {
        if (receivers[0] == this.id)
        {
            leader = this.current;

            //Create list of receivers and initiate election
            receivers = new int[5];
            receivers[0] = this.id;

            coordinator(this.id, receivers);
        }
        else
        {
            //Add this id to list of receivers
            for (int i = 0; i < receivers.length; i++)
            {
                if (receivers[i] == 0)
                {
                    receivers[i] = this.id;
                    break;
                }
            }

            election(highestId, receivers);
        }
    }

    private void coordinator(int highestId, int[] receivers)
    {
        for (Node downstreamNode : this.downstreamNodes)
        {
            try
            {
                //Initialize message values
                Message m = new Message();
                m.messageType = Constant.messageType.Coordinator;
                m.sender = this.current.getNodeName();
                m.highestId = highestId;
                m.receivers = receivers;

                this.current.sendTCPMessage(downstreamNode, m);
                //If message is sent without throwing exception then node is live
                break;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            //TODO: Should we go ahead and call coordinator if no other node is alive?
        }
    }

    public void receiveCoordinator(int highestId, int[] receivers)
    {
        if(receivers[0] != this.id)
        {
            for(Node node: downstreamNodes)
            {
                //TODO
                //if(node.Id == highestId)
                {
                    this.leader = node;
                }
            }
            
            for(int i = 0; i < receivers.length; i++)
            {
                if(receivers[i] == 0)
                {
                    receivers[i] = this.id;
                    break;
                }
            }
            
            coordinator(highestId, receivers);
        }
    }
}
