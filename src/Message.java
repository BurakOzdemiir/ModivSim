public class Message {
	
	public int senderID;
	public int receiverID;
	public int[] costTable;
	
	public Message(int senderID, int receiverID, int[] costTable)
	{
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.costTable = costTable;
	}
}
