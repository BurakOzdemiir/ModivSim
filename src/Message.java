import java.util.Hashtable;

import com.sun.tools.javac.util.Pair;

public class Message {
	
	public int senderID;
	public int receiverID;
	public Hashtable<Integer, Pair<Integer, Integer>> forwardTable;
	
	public Message(int senderID, int receiverID, Hashtable<Integer, Pair<Integer, Integer>> forwardTable)
	{
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.forwardTable = forwardTable;
	}
	
	public int getSenderID() {
		return this.senderID;
	}
}
