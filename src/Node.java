import java.util.ArrayList;
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
	private Hashtable<Integer, Integer> bottleneckBandwidthTable;
	
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
        
        Set<Integer> bkeys = linkBandwidth.keySet();
        for(Integer key: bkeys){
            bottleneckBandwidthTable.put(key, linkBandwidth.get(key));
        }
		
		
	}
	
	public void run()
	{
		sendUpdate();
		System.out.println("Sent update");
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
	
	public Hashtable<Integer, List<Integer>> getForwardingTable()
	{
		Hashtable<Integer, List<Integer>> forwardingTable =  
	            new Hashtable<Integer, List<Integer>>(); 
		
		Set<Integer> keys = this.distanceTable.keySet();
        for(Integer key: keys)
        {
        	int min = 999;
        	int min2 = 999;
        	int target = 999;
        	int target2 = 999;
        	Set<Integer> keys2 = this.distanceTable.get(key).keySet();
        	for(Integer key2: keys2)
        	{
        		int cost = this.distanceTable.get(key).get(key2);
        		if(cost < min2)
        		{
        			if(cost < min)
        			{
        				min2 = min;
        				target2 = target;
        				min = cost;
            			target = key2;
        			}
        			else
        			{
        				min2 = cost;
        				target2 = key2;
        			}
        			
        		}
        	}
        	if(target != 999 || target2 != 999)
        	{
        		List<Integer> targetList= new ArrayList<Integer>();
        		
        		//add example
        		targetList.add(target);
        		targetList.add(target2);
        		
        		forwardingTable.put(key, targetList);
        	}
        }
				
		return forwardingTable;
	}
	

}
