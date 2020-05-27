import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Node implements Runnable{
	
	private int nodeID;
	private Hashtable<Integer, Integer> linkCost =  
            new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> linkBandwidth =  
            new Hashtable<Integer, Integer>(); 
	private Hashtable<Integer, Hashtable<Integer, Integer>> distanceTable = 
			new Hashtable<Integer, Hashtable<Integer, Integer>>(0);
	private int bottleneckBandwidthTable[];
	
	public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth)
	{
		
		this.nodeID = nodeID;
		this.linkCost = linkCost;
		this.linkBandwidth = linkBandwidth;
		
		for(int i  = 0; i < 10; i ++)
		{
			
			Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
			distanceTable.put(i, table);
			
		}
		
		Set<Integer> keys = linkCost.keySet();
        for(Integer key: keys){
            distanceTable.get(key).put(key, linkCost.get(key));
        }
		
		
	}
	
	public void run()
	{
		
		
		
	}
	
	public void receiveUpdate(Message m)
	{
		int sender = m.getSenderID();
		
		Set<Integer> keys = m.getDistanceTable().keySet();
        for(Integer key: keys){
        	Set<Integer> keys2 = m.getDistanceTable().get(key).keySet();
            for(Integer key2: keys2){
            	int cost = m.getDistanceTable().get(key).get(key2) + linkCost.get(sender);
            	if(distanceTable.contains(key))
            	{
            		if(distanceTable.get(key).contains(key2))
            		{
            			if(distanceTable.get(key).get(key2) > cost)
            				distanceTable.get(key).put(key2, cost);
            		}
            		else
            		{
            			distanceTable.get(key).put(key2, cost);
            		}
            	}
            }  		
        }
		
	}
	
	public boolean sendUpdate()
	{
		return false;
		
	}
	
	public Hashtable<Integer, Integer> getForwardingTable()
	{
		Hashtable<Integer, Integer> forwardingTable =  
	            new Hashtable<Integer, Integer>(); 
		
		Set<Integer> keys = this.distanceTable.keySet();
        for(Integer key: keys)
        {
        	int min = 999;
        	int target = 999;
        	Set<Integer> keys2 = this.distanceTable.get(key).keySet();
        	for(Integer key2: keys2)
        	{
        		if(this.distanceTable.get(key).get(key2) < min)
        		{
        			min = distanceTable.get(key).get(key2);
        			target = key2;
        		}
        	}
        	if(target != 999)
        		forwardingTable.put(key, target);
        }
				
		return forwardingTable;
	}
	
	
	

}
