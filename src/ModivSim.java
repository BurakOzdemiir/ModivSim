import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ModivSim { 
	
	private static void initNodes(ArrayList<Node> nodes) throws IOException {
		File f = new File("nodes.txt");    
		FileReader fReader = new FileReader(f);   
		BufferedReader bReader = new BufferedReader(fReader);   

		ArrayList<String> lines = new ArrayList<String>();  
		String currentLine;
		
		while((currentLine = bReader.readLine())!=null)  
			lines.add(currentLine);
			
		int length = lines.size();		
		for(int i = 0; i < length; i++)
		{
			String line = lines.get(i);
			
			Hashtable<Integer, Integer> linkCost =  
		            new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> linkBandwidth =  
		            new Hashtable<Integer, Integer>(); 
			
			line = line.replaceAll("[()]", "");
			String[] newLine = line.split(",");
			
			int x = 1;
			
			int nodeID = Integer.valueOf(newLine[0]);
			while(x < newLine.length)
			{
				int neighborID = Integer.valueOf(newLine[x]);
				int cost = Integer.valueOf(newLine[x+1]);
				int bandwidth = Integer.valueOf(newLine[x+2]);
				
				linkCost.put(neighborID, cost);
				linkBandwidth.put(neighborID, bandwidth);
				x = x + 3;
			}
			Node node = new Node(nodeID, linkCost, linkBandwidth);
			nodes.add(node);	
		}
		fReader.close();    
	}
	
	private static TextArea createSimWindow() {
		Frame outFrame = new Frame("Simulator");
		TextArea windowOut = new TextArea(1, 30);
		outFrame.add(windowOut);
		outFrame.setSize(100, 100);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		outFrame.setLocation((int)screenSize.getWidth()/2, (int)screenSize.getHeight()/2);
		outFrame.setVisible(true);
		return windowOut;
	}
	
	private static void waitForConvergence(ArrayList<Node> nodes, TextArea simText) {
		boolean converged = false;
		int counter = 0;
		while(!converged) {
			try {
				simText.setText("Round: " + counter);
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			converged = true;
			for (Node node : nodes) {
				if(!node.isConverged) {
					converged = false;
					break;
				}
			}
		}
		simText.setText("Simulation over at round: " + counter);
	}

	public static void main(String[] args) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		try{  
			initNodes(nodes);
		}  
		catch(IOException e){  
			e.printStackTrace();  
		}
		for(Node n : nodes) {
			ArrayList<BlockingQueue<Message>> inboxes = new ArrayList<>();
			for(int id : n.neighborIDs) {
				inboxes.add(nodes.get(id).msgQ);
			}
			n.setupNeighborMessaging(inboxes);
		}
		TextArea simText = createSimWindow();
		ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(nodes.size());
		for (Node node : nodes) {
			scheduler.scheduleWithFixedDelay(node, 0, 1, TimeUnit.SECONDS);
		}
		System.out.println("Scheduling start");
		waitForConvergence(nodes, simText);
		scheduler.shutdown();
	}
}
