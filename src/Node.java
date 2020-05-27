import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Node implements Runnable{
	private int nodeID;
	
	private final int tableSize = 5;
	private final int windowSize = 300;
	private Hashtable<Integer, Integer> linkCost;
	private Hashtable<Integer, Integer> linkBandwidth;
	private int[][] distanceTable;
	private int[] bottleneckBandwidthTable;
	
	private volatile boolean isConverged = false; 
	private TextArea windowOut;
	
	public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth)
	{
		this.nodeID = nodeID;
		this.linkCost = linkCost;
		this.linkBandwidth = linkBandwidth;
		
		initDistTable();
		initBandwidthTable();
		createWindow();
		updateText();
	}
	
	private void initDistTable() {
		distanceTable = new int[tableSize][tableSize];
		for(int row = 0; row < tableSize; row++) {
			for(int col = 0; col < tableSize; col++) {
				if(row == nodeID && linkCost.containsKey(col)) {
					distanceTable[row][col] = linkCost.get(col);
				} else if (col == nodeID && linkCost.containsKey(row)) {
					distanceTable[row][col] = linkCost.get(row);
				} else {
					distanceTable[row][col] = 999;					
				}
			}
		}
	}
	
	private void initBandwidthTable() {
		bottleneckBandwidthTable = new int[tableSize];
		for(int i = 0; i < tableSize; i++) {
			if(linkBandwidth.containsKey(i)) {
				bottleneckBandwidthTable[i] = linkBandwidth.get(i);
			} else {
				bottleneckBandwidthTable[i] = -1;
			}
		}
	}

	public void run()
	{
		sendUpdate();
		System.out.println("Sent update");
	}
	
//	public void receiveUpdate(Message m)
//	{
//		int sender = m.getSenderID();
//		
//		Set<Integer> keys = m.getDistanceTable().keySet();
//        for(Integer key: keys){
//        	Set<Integer> keys2 = m.getDistanceTable().get(key).keySet();
//            for(Integer key2: keys2){
//            	int cost = m.getDistanceTable().get(key).get(key2) + linkCost.get(sender);
//            	if(distanceTable.contains(key))
//            	{
//            		if(distanceTable.get(key).contains(key2))
//            		{
//            			if(distanceTable.get(key).get(key2) > cost)
//            				distanceTable.get(key).put(key2, cost);
//            		}
//            		else
//            		{
//            			distanceTable.get(key).put(key2, cost);
//            		}
//            	}
//            }  		
//        }
//		
//	}
	
	public boolean sendUpdate()
	{
		return false;
	}
	
//	public Hashtable<Integer, List<Integer>> getForwardingTable()
//	{
//		Hashtable<Integer, List<Integer>> forwardingTable =  
//	            new Hashtable<Integer, List<Integer>>(); 
//		
//		Set<Integer> keys = this.distanceTable.keySet();
//        for(Integer key: keys)
//        {
//        	int min = 999;
//        	int min2 = 999;
//        	int target = 999;
//        	int target2 = 999;
//        	Set<Integer> keys2 = this.distanceTable.get(key).keySet();
//        	for(Integer key2: keys2)
//        	{
//        		int cost = this.distanceTable.get(key).get(key2);
//        		if(cost < min2)
//        		{
//        			if(cost < min)
//        			{
//        				min2 = min;
//        				target2 = target;
//        				min = cost;
//            			target = key2;
//        			}
//        			else
//        			{
//        				min2 = cost;
//        				target2 = key2;
//        			}
//        			
//        		}
//        	}
//        	if(target != 999 || target2 != 999)
//        	{
//        		List<Integer> targetList= new ArrayList<Integer>();
//        		
//        		//add example
//        		targetList.add(target);
//        		targetList.add(target2);
//        		
//        		forwardingTable.put(key, targetList);
//        	}
//        }
//				
//		return forwardingTable;
//	}
	
	private void createWindow() {
		Frame outFrame = new Frame("Node " + nodeID);
		windowOut = new TextArea(tableSize*2 + 4, tableSize*2 + 4);
		outFrame.add(windowOut);
		outFrame.setSize(windowSize, windowSize);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int heightInWindows = (int)screenSize.getHeight() / windowSize;
		int row = nodeID % heightInWindows;
		int col = nodeID / heightInWindows;
		int x = windowSize*col;
		int y = windowSize*row;
		outFrame.setLocation(x, y);
		outFrame.setVisible(true);
	}
	
	private void updateText() {
//		String str = (Arrays.deepToString(distanceTable).replace("], ", "]\n"));
		String str = "";
		str += "Distance table\n\t";
		for(int col = 0; col < tableSize; col++){
        	str += col + "\t";   
        }
		str += "\n";
		for(int row = 0; row < tableSize; row++){
			str += row + "\t";
	        for(int col = 0; col < tableSize; col++){
	        	str += distanceTable[row][col] + "\t";   
	        }
	        str += "\n";
	    } 
		windowOut.setText(str);
	}
	
	public boolean isConverged() {
		return isConverged;
	}
}
