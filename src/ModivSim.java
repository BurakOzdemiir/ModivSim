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
				
				int nodeID = line.charAt(0);
				
				ArrayList<Integer> neighborsAdded = new ArrayList<Integer>();
				
				Hashtable<Integer, Integer> linkCost =  
			            new Hashtable<Integer, Integer>();
				Hashtable<Integer, Integer> linkBandwidth =  
			            new Hashtable<Integer, Integer>(); 
				
				List<Integer>[] distanceTable = new List[length]; 
				
				
				line = line.replaceAll("[()]", "");
				String[] newLine = line.split(",");
				
				int x = 1;
				
				while(x < newLine.length)
				{
					
					int neighborID = Integer.valueOf(newLine[x]);
					int cost = Integer.valueOf(newLine[x+1]);
					int bandwidth = Integer.valueOf(newLine[x+2]);
					
					neighborsAdded.add(neighborID);
					
					linkCost.put(neighborID, cost);
					linkBandwidth.put(neighborID, bandwidth);
					
					distanceTable[neighborID] = new ArrayList<>();
					distanceTable[neighborID].add(neighborID, cost);
					
					x = x + 3;
					
				}
				
				for(int j = 0; j < length; i++) // initializing indirect neighbor costs to infinity
					if(!neighborsAdded.contains(j))
					{
						distanceTable[j] = new ArrayList<>();
						distanceTable[j].add(j, 999);
					}
				
				Node node = new Node(nodeID, linkCost, linkBandwidth, distanceTable);
				nodes[i] = node;				
				
			}

			fReader.close();    

		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  

	}

}
