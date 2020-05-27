import java.util.Hashtable;
import java.util.List;

public class Message {
	
	private int senderID;
	private int receiverID;
	private int linkBandwidth;
	private int linkCost;
	private Hashtable<Integer, Hashtable<Integer, Integer>> distanceTable = 
			new Hashtable<Integer, Hashtable<Integer, Integer>>(0);
	
	public Message(int senderID, int receiverID, int linkBandwidth, int linkCost, Hashtable<Integer, Hashtable<Integer, Integer>> distanceTable)
	{
		
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.linkBandwidth = linkBandwidth;
		this.linkCost = linkCost;
		this.distanceTable = distanceTable;
		
	}
	
	public int getSenderID() {
		return this.senderID;
	}
	
	public Hashtable<Integer, Hashtable<Integer, Integer>> getDistanceTable()
	{
		return this.distanceTable;
	}
	
}
