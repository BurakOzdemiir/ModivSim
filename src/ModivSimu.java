import com.sun.tools.javac.util.Pair;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class ModivSimu {

	static Hashtable<Pair, Integer> dCosts =
			new Hashtable<Pair, Integer>();
	private static void initNodes(ArrayList<Node> nodes) throws IOException {
		File f = new File("nodesD.txt");
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
				int dynamic = 0;
				int neighborID = Integer.valueOf(newLine[x]);
				int cost;
				if(String.valueOf(newLine[x+1]).equals("x")){
					dynamic = 1;
					Random rand = new Random();
					cost = rand.nextInt(10) + 1;
				}else {
					cost = Integer.valueOf(newLine[x + 1]);
				}
				int bandwidth = Integer.valueOf(newLine[x+2]);
				
				linkCost.put(neighborID, cost);
				if (!(dynamic == 0)){
					Pair p = new Pair(nodeID, neighborID);
					dCosts.put(p, 1);
					System.out.println(p +":  " + 1);

				}
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
		outFrame.setSize(300, 100);
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
				counter++;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			converged = true;


			Random rand = new Random();
			boolean prob = rand.nextBoolean();
			Set<Pair> keys = dCosts.keySet();
			for (Pair p : keys){
				int first = (int) p.fst;
				int second = (int) p.snd;
				if(first>second){
					if (prob) {
						int newCost = rand.nextInt(10)+ 1;
						Node n1 = ModivSimu.getNode(nodes, first);
						Node n2 = ModivSimu.getNode(nodes, second);
						n1.changeLinkCost(second, newCost);
						n2.changeLinkCost(first, newCost);
					}
				}
			}
			for (Node node : nodes) {
				if(!node.isConverged) {
					converged = false;
				}
			}
		}
		simText.setText("Simulation over at round: " + counter);
	}

	public static Node getNode(ArrayList<Node> nodes, int id){
		for (Node node : nodes) {
			if (node.getNodeID() == id){
				return node;
			}
		}
		return null;
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
		waitForConvergence(nodes, simText);
		scheduler.shutdown();
	}
}
