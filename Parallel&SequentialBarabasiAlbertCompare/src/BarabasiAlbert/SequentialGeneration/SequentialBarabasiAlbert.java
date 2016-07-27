package BarabasiAlbert.SequentialGeneration;

/**
 *
 * @author Sarah
 */
import GraphHandling.Graph;
import GraphHandling.Node;
import Utility.WriteFile;
import Utility.necessaryArrayGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class SequentialBarabasiAlbert {
    
    //Graph
    Graph graph; 
    int V; //Numbers of nodes in first graph
    int m; //Number of the Edges each new node bring
    int n; //Number of nodes to get attached
    ArrayList<Integer> P;
    
    public SequentialBarabasiAlbert(Graph firstGraph , ArrayList<Integer> P , int n , int m) throws IOException {
        
        //Graph
        graph = firstGraph;
        V = firstGraph.getNodeCount();
        this.P = P;
        this.n = n;
        this.m = m;
    }
    
    public Graph getGraph()
    {
        return graph;
    }
    
    public void makeSequentialBarabasiAlbert() throws InterruptedException, ExecutionException, IOException, Exception
    {
        System.out.println("**Check Sequential Mode**");
        evolveGraph(n);
    }
    
    private void evolveGraph(int timeSteps) throws IOException{

        for (int i = 0; i < timeSteps; i++) 
            evolveGraph();
    }
    
    private void evolveGraph() throws IOException 
    { 
        // generate and store the new edges; don't add them to the graph
        // yet because we don't want to bias the degree calculations
        // (all new edges in a timestep should be added in parallel and simultaniosult)
        ArrayList<Node> selectedNodes = new ArrayList<>();
        
        for(int edgesAddedInThisStep=0; edgesAddedInThisStep < m; edgesAddedInThisStep++)
        {
            boolean found = false;
            while (found == false)
            {
                int randomDecider = P.remove(0);
                Node selectedNodeToConnect = null;
                for(Node node: graph.getVertexList())
                {
                    randomDecider -= node.getAdjacentVertex().size();
                    if(randomDecider <=0 )
                        selectedNodeToConnect = node;
                }
                if(selectedNodes.contains(selectedNodeToConnect) == false)
                {
                    selectedNodes.add(selectedNodeToConnect);
                    found = true;
                }
            }
        }
        WriteFile dataResult = new WriteFile( "SequentialBarabasiAlbertResult.txt" , true );
        Node newNodeIndex = graph.addVertex();
        for (Node newEdgeEnd : selectedNodes)
        {
            graph.addEdge(newEdgeEnd, newNodeIndex);
            dataResult.writeToFile("("+newEdgeEnd.getIndex()+","+selectedNodes+")" , true);       
        }
    }
    
}