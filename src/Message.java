import java.util.List;

public class Message {
	
	private int senderID;
	private int receiverID;
	private int linkBandwidth;
	private int linkCost;
	private int[][] distanceTable;
	
	public Message(int senderID, int receiverID, int linkBandwidth, int linkCost, int[][] distanceTable)
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
	
	public int[][] getDistanceTable()
	{
		return this.distanceTable;
	}
	
}
