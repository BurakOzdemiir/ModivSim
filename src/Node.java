import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sun.tools.javac.util.Pair;

public class Node implements Runnable{
	private int nodeID;
	public ArrayList<Integer> neighborIDs;
	
	public ArrayList<BlockingQueue<Message>> neighborInboxes;
	public BlockingQueue<Message> msgQ;
	
	private Hashtable<Integer, Integer> linkCost;
	private Hashtable<Integer, Integer> linkBandwidth;
	private int[][] distanceTable;
	private int[] bottleneckBandwidthTable;
	
	public volatile boolean isConverged = false; 
	private TextArea windowOut;
	
	private final int tableSize = 5;
	private final int windowSize = tableSize * 55;
	
	public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth)
	{
		this.nodeID = nodeID;
		this.linkCost = linkCost;
		this.linkBandwidth = linkBandwidth;
		
		neighborIDs = new ArrayList<>();
		msgQ = new LinkedBlockingQueue<>();
		initDistTable();
		initBandwidthTable();
		createWindow();
		updateText();
	}
	
	public void setupNeighborMessaging(ArrayList<BlockingQueue<Message>> inboxes) {
		neighborInboxes = inboxes;
	}

 	private void initDistTable() {
		distanceTable = new int[tableSize][tableSize];
		for(int row = 0; row < tableSize; row++) {
			for(int col = 0; col < tableSize; col++) {
				if(row == nodeID && linkCost.containsKey(col)) {
					neighborIDs.add(col);
					distanceTable[row][col] = linkCost.get(col);
				} else if (col == nodeID && linkCost.containsKey(row)) {
					distanceTable[row][col] = linkCost.get(row);
				} else {
					distanceTable[row][col] = 999;					
				}
			}
		}
	}
 	
 	public Hashtable<Integer, Pair<Integer, Integer>> getForwardingTable(){
 		Hashtable<Integer, Pair<Integer, Integer>> fwdTable = new Hashtable<>();
 		for(int i = 0; i < tableSize; i++) {
 			int[] bestID = {-1, -1};
 			int[] bestCosts = {999, 999};
 	 		for(int id : neighborIDs) {
 	 			int cost = linkCost.get(id) + distanceTable[i][id];
 	 			if(cost < bestCosts[0]){
 	 				bestCosts[1] = bestCosts[0];
 	 				bestID[1] = bestID[0];
 	 				bestCosts[0] = cost;
 	 				bestID[0] = id;
 	 			} else if(cost < bestCosts[1]){
 	 				bestCosts[1] = cost;
 	 				bestID[1] = id;
 	 			}
 	 		}
 	 		Pair<Integer, Integer> p = new Pair<Integer, Integer>(bestID[0], bestID[1]);
 			fwdTable.put(i, p);
 		}
 		return fwdTable;
 	}
 	
 	public Hashtable<Integer, Pair<Integer, Integer>> getCostTable(){
 		Hashtable<Integer, Pair<Integer, Integer>> costTable = new Hashtable<>();
 		for(int i = 0; i < tableSize; i++) {
 			int[] bestCosts = {999, 999};
 	 		for(int id : neighborIDs) {
 	 			int cost = linkCost.get(id) + distanceTable[i][id];
 	 			if(cost < bestCosts[0]){
 	 				bestCosts[1] = bestCosts[0];
 	 				bestCosts[0] = cost;
 	 			} else if(cost < bestCosts[1]){
 	 				bestCosts[1] = cost;
 	 			}
 	 		}
 	 		Pair<Integer, Integer> p = new Pair<Integer, Integer>(bestCosts[0], bestCosts[1]);
 	 		costTable.put(i, p);
 		}
 		return costTable;
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
		while(!msgQ.isEmpty()) {
			receiveUpdate(msgQ.poll());		
		}
	}
	
	public void receiveUpdate(Message m)
	{
		int sender = m.getSenderID();
		

        for(int row = 0; row < tableSize; row++){
        	for(int col = 0; col < tableSize; col++) {
        		int myCost = distanceTable[row][col];
//        		int incomingCost = incomingTable[row][col];
//        		distanceTable[row][col] = Math.min(myCost, incomingCost);
        	}
        }
        
		
	}
	
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
		int x = (windowSize*col) % (int)screenSize.getWidth();
		int y = windowSize*row;
		outFrame.setLocation(x, y);
		outFrame.setVisible(true);
	}
	
	private void updateText() {
		String str = "";
		str += "Distance table\n\t";
		for(int col = 0; col < tableSize; col++){
        	str += col + "\t";   
        }
		str += "\n";
		for(int row = 0; row < tableSize; row++){
			if(neighborIDs.contains(row)) {
				str += row + "\t";
		        for(int col = 0; col < tableSize; col++){
		        	str += distanceTable[row][col] + "\t";   
		        }
		        str += "\n";
			}
	    } 
		windowOut.setText(str);
	}
}
