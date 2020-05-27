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
				if(linkCost.containsKey(row) && row == col) {
					neighborIDs.add(row);
					distanceTable[row][row] = linkCost.get(row);
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
		isConverged = true;
		while(!msgQ.isEmpty()) {
			receiveUpdate(msgQ.poll());		
		}
		updateText();
	}
	
	public void receiveUpdate(Message m)
	{
		int sender = m.senderID;
		Hashtable<Integer, Pair<Integer, Integer>> costTable = m.costTable;
		for(int k : costTable.keySet()) {
			int newCost = linkCost.get(k) + costTable.get(k).fst;
			if(newCost < distanceTable[k][sender]) {
				distanceTable[k][sender] = newCost;
				isConverged = false;
			}
		}
	}
	
	public boolean sendUpdate()
	{
		if(isConverged) {
			return false;
		} else {
			for(int i = 0; i < neighborIDs.size(); i++) {
				BlockingQueue<Message> q = neighborInboxes.get(i);
				Message msg = new Message(nodeID, neighborIDs.get(i), getCostTable());
				q.add(msg);
			}
			return true;			
		}
	}
	

	
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
