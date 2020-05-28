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

	private static void initNodes(ArrayList<Node> nodes, Hashtable<Pair, Integer> links) throws IOException {
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
				Pair p = new Pair(nodeID, neighborID);
				links.put(p, 0);
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


	private static void loadFlow(ArrayList<Flow> flows)throws IOException  {


		File f = new File("flow.txt");
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


			line = line.replaceAll("[()]", "");
			line = line.replaceAll(" ", "");
			String[] newLine = line.split(",");

			String flowName = (String) newLine[0];
			int source = Integer.valueOf(newLine[1]);
			int destination = Integer.valueOf(newLine[2]);
			int flowSize = Integer.valueOf(newLine[3]);

			Flow fl = new Flow(flowName, source, destination, flowSize);
			flows.add(fl);

		}
		fReader.close();


	}

	public static ArrayList<Integer> findPath(ArrayList<Node> nodes, int src, int dest, Hashtable<Pair, Integer> links){
		ArrayList<Integer> result = new ArrayList<Integer>();
		int node = src;


		while(true){
			Hashtable<Integer, Pair<Integer, Integer>> fwd = ModivSimu.getNode(nodes, node).getForwardingTable();
			Pair<Integer, Integer> choices = fwd.get(dest);
			int firstC = (int) choices.fst;
			int secondC = (int) choices.snd;
			if(links.get(Pair.of(node, firstC)) == 0){
				result.add(firstC);
				node = firstC;
			}
			else if(links.get(Pair.of(node, secondC)) == 0){
				result.add(secondC);
				node = secondC;
			}else return null;

			if(result.get(result.size()-1)==dest){
				return result;
			}
		}
	}

	public static int findBottleneck(ArrayList<Node> nodes, Node src, ArrayList<Integer> path){

		int minimum = src.getLinkBandwidth().get(path.get(0));
		for(int i=0;i<path.size()-1;i++){
			Node iniNode = getNode(nodes, i);
			int width = iniNode.getLinkBandwidth().get(i+1);
			if (minimum > width) minimum = width;
		}
		return minimum;


	}

	public static void replaceAsBusy(ArrayList<Node> nodes,int src, ArrayList<Integer> path, Hashtable<Pair, Integer> links){
		links.replace(Pair.of(src, path.get(0)), 1);
		links.replace(Pair.of( path.get(0), src), 1);
		for(int i=0;i<path.size()-1;i++){
			links.replace(Pair.of(i, path.get(i+1)), 1);
			links.replace(Pair.of(path.get(i+1), i), 1);

		}

	}
	public static void replaceAsEmpty(ArrayList<Node> nodes,int src, ArrayList<Integer> path, Hashtable<Pair, Integer> links){
		links.replace(Pair.of(path.get(0), src), 0);
		links.replace(Pair.of(src, path.get(0)), 0);
		for(int i=0;i<path.size()-1;i++){
			links.replace(Pair.of(i, path.get(i+1)), 0);
			links.replace(Pair.of(path.get(i+1), i), 0);

		}

	}

	private static void flowSimulation(ArrayList<Node> nodes, ArrayList<Flow> flows, Hashtable<Pair, Integer> links) {
		boolean flowed = false;
		int flowCounter = 0;
		while(!flowed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(Flow fl : flows) {
				if (fl.flowed) {
					continue;
				} else {
					Node src = ModivSimu.getNode(nodes, fl.source);
					Node dest = ModivSimu.getNode(nodes, fl.destination);
					ArrayList<Integer> path = findPath(nodes, src.getNodeID(), dest.getNodeID(), links);

					if (fl.flowing) {
						fl.remainingSize -= fl.bottleneck;
						System.out.println(fl.label + ": " + fl.source + "->" + fl.path.get(0) + "->" +  fl.destination + " bottleneck: " + fl.bottleneck + " rem: " + fl.remainingSize );
						if (fl.remainingSize <= 0) {
							fl.flowed = true;
							flowCounter += 1;
							replaceAsEmpty(nodes, src.getNodeID(), fl.path, links);
						}
					} else {
						if (path != null) {
							int bottleNeck = findBottleneck(nodes, src, path);
							fl.bottleneck = bottleNeck;
							fl.path = path;
							replaceAsBusy(nodes, src.getNodeID(), path, links);

							fl.flowing = true;
						} else {
							System.out.println(fl.label + " is on queue");
						}


					}


				}

			}
			System.out.println("==================================================");
			if(flows.size()==flowCounter){
				flowed = true;
				System.out.println("Flow finished");
			}
		}


	}


	public static void main(String[] args) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Flow> flows = new ArrayList<Flow>();
		Hashtable<Pair, Integer> links = new Hashtable<Pair, Integer>();
		try{  
			initNodes(nodes, links);
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



		try{
			loadFlow(flows);
			System.out.println(flows);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		flowSimulation(nodes,flows, links);


	}

}
