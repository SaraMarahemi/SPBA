
package Utility;

import GraphHandling.Graph;
import GraphHandling.Node;
import java.util.Random;

/**
 *
 * @author Sarah
 */
public class necessaryArrayGenerator {
    int[] P;
    int[] D;
    Graph graph;
    int V;
    int K;
    public necessaryArrayGenerator( int K , Graph GeneratedGraph)
    {
        V = GeneratedGraph.getNodeCount();
        this.K = K;
        //D generation
        D = new int[V+2]; //D array 
        //P generation
        P = new int[K+1]; //Random numbers array start with 1
        //Graph
        graph = GeneratedGraph;
    }
    
    public Graph getGraph()
    {
        return graph;
    }
    
    public void setGraph(Graph newGraph)
    {
        graph = newGraph;
        V = newGraph.getNodeCount();
        D = new int[V+2];
    }
    
    public int[] makeRandomNumbers( )
    {
        int FirstArrayLastIndex = D[V+1]-1;
        Random randomGenerator = new Random();
        for(int i=1 ; i<=K ; i++)
        {
            P[i] = randomGenerator.nextInt( (FirstArrayLastIndex+(i-1)*3)-1 )+1; 
        }    
        return P;
    }
    
    public int[] makeDArrayFromFirstGraph()
    {
       
        int[] Degree = new int[graph.getNodeCount()+1];
        int j=1;
        for (Node node : graph.getVertexList()) {
            Degree[j] = node.getAdjacentVertex().size();
            j++;      
        }
        
        int base=1;
        D[1] = base;
        for(int k=1; k<=graph.getNodeCount() ; k++)
        {
            D[k+1] = base+Degree[k]+1;
            base = D[k+1];
        }
        return D;
    }
    
}
