import java.util.ArrayList;

public class Flow {
    String label;
    int source;
    int destination;
    int flowSize;
    int remainingSize;
    boolean flowing;
    int bottleneck;
    ArrayList<Integer> path;
    boolean flowed;

    public Flow(String label, int source, int destination, int flowSize){
        this.label = label;
        this.source = source;
        this.destination = destination;
        this.flowSize = flowSize;
        remainingSize = flowSize;
        this.flowing = false;
        this.path = new ArrayList<Integer>();
        this.flowed = false;


    }
}
