import java.util.List;

public class Message {
	
	private int senderID;
	private int receiverID;
	private int linkBandwidth;
	private int linkCost;
	private List<Integer>[] distanceTable;
	
	public Message(int senderID, int receiverID, int linkBandwidth, int linkCost, List<Integer>[] distanceTable)
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
	
	public List<Integer>[] getDistanceTable()
	{
		return this.distanceTable;
	}
	
}
