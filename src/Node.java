import java.util.Hashtable;
import java.util.List;

public class Node implements Runnable{
	
	private int nodeID;
	private Hashtable<Integer, Integer> linkCost =  
            new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> linkBandwidth =  
            new Hashtable<Integer, Integer>(); 
	List<Integer>[] distanceTable;
	private int bottleneckBandwidthTable[];
	
	public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth, List<Integer>[] distanceTable)
	{
		
		this.nodeID = nodeID;
		this.linkCost = linkCost;
		this.linkBandwidth = linkBandwidth;
		this.distanceTable = distanceTable;
		
	}
	
	public void run()
	{
		
		
		
	}
	
	public void receiveUpdate(Message m)
	{
		
		for(int i = 0; i < distanceTable.length; i++)
		{
			
			if(distanceTable[i].get(m.getSenderID()) > m.getDistanceTable()
			
		}
		
	}
	
	public boolean sendUpdate()
	{
		return false;
		
	}
	
	public Hashtable<Integer, Integer> getForwardingTable()
	{
		return linkBandwidth;
		
	}
	
	
	

}
