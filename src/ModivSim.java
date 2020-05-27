import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ModivSim {
	
	static ArrayList<Node> nodes; 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Node[] nodes;

		try  
		{  
			
			File f = new File("nodes.txt");    
			FileReader fReader = new FileReader(f);   
			BufferedReader bReader = new BufferedReader(fReader);   

			ArrayList<String> lines = new ArrayList<String>();  
			String currentLine;
			
			while((currentLine = bReader.readLine())!=null)  
				lines.add(currentLine);
				
			int length = lines.size();
			
			nodes = new Node[length];
			
			for(int i = 0; i < length; i++)
			{
				
				String line = lines.get(i);
				
				
				
				
				ArrayList<Integer> neighborsAdded = new ArrayList<Integer>();
				
				Hashtable<Integer, Integer> linkCost =  
			            new Hashtable<Integer, Integer>();
				Hashtable<Integer, Integer> linkBandwidth =  
			            new Hashtable<Integer, Integer>(); 
				
				int[][] distanceTable = new int[length][length];
				
				//System.out.println(length);
				
				line = line.replaceAll("[()]", "");
				String[] newLine = line.split(",");
				
				int x = 1;
				
				int nodeID = Integer.valueOf(newLine[0]);
				
				//System.out.println(newLine);
				//System.out.println(nodeID);
				
				for (int a = 0; a < length; a++)
					for (int y = 0; y < length; y++)
							distanceTable[a][y] = 999;
				
				while(x < newLine.length)
				{
					
					int neighborID = Integer.valueOf(newLine[x]);
					int cost = Integer.valueOf(newLine[x+1]);
					int bandwidth = Integer.valueOf(newLine[x+2]);
					
					linkCost.put(neighborID, cost);
					linkBandwidth.put(neighborID, bandwidth);
					//System.out.println(nodeID + " " + neighborID);
					
					distanceTable[neighborID][neighborID] = cost;
					distanceTable[nodeID][nodeID] = 0;
					distanceTable[nodeID][neighborID] = cost * 2;
					
					x = x + 3;
					
				}
				
				Node node = new Node(nodeID, linkCost, linkBandwidth, distanceTable);
				nodes[i] = node;				
				
			}
			for(int m = 0; m < length; m++)
				for(int n = 0; n < length; n++)
					System.out.println(nodes[0].distanceTable[m][n]);

			fReader.close();    

		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  

	}

}
