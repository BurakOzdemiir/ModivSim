import java.util.Hashtable;

import com.sun.tools.javac.util.Pair;

public class Message {
	
	public int senderID;
	public int receiverID;
	public Hashtable<Integer, Pair<Integer, Integer>> costTable;
	
	public Message(int senderID, int receiverID, Hashtable<Integer, Pair<Integer, Integer>> costTable)
	{
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.costTable = costTable;
	}
}
