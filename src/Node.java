import java.util.Hashtable;
import java.util.List;

public class Node implements Runnable{
	
	private int nodeID;
	private Hashtable<Integer, Integer> linkCost =  
            new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> linkBandwidth =  
            new Hashtable<Integer, Integer>(); 
	int[][] distanceTable;
	private int bottleneckBandwidthTable[];
	
	public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth)
	{
		
		this.nodeID = nodeID;
		this.linkCost = linkCost;
		this.linkBandwidth = linkBandwidth;
		
		
	}
	
	public void run()
	{
		
		
		
	}
	
	public void receiveUpdate(Message m)
	{
		int sender = m.getSenderID();
		
		for(int i = 0; i < distanceTable.length; i++)
			for(int j = 0; j < distanceTable.length; j++)
				if(distanceTable[i][sender] > m.getDistanceTable()[i][j] + linkCost.get(sender))
					distanceTable[i][sender] = m.getDistanceTable()[i][j] + linkCost.get(sender);
	}
	
	public boolean sendUpdate()
	{
		return false;
		
	}
	
	public Hashtable<Integer, Integer> getForwardingTable()
	{
		Hashtable<Integer, Integer> forwardingTable =  
	            new Hashtable<Integer, Integer>(); 
		
		int length = distanceTable[0].length;
		for (int i = 0; i < length; i++)
			for(int j = 0; j < length; j++)
				System.out.println("");
				
		return forwardingTable;
	}
	
	
	

}
