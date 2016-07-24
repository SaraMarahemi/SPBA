
package BarabasiAlbert.SequentialGeneration;

/**
 *
 * @author Sarah
 */
import Utility.WriteFile;
import Utility.nodePair;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class SequentialBarabasiAlbert {
    
    //Graph
        Graph graph; 
        int V; //Numbers of nodes in first graph
        int m; //Number of the Edges each new node bring
        int K; //Numbers of nodes which connect to first graph in parallel at each phase
    //time
        long startTime; // Declared for using as timers
        long estimatedTime;
        long totalTime;
        long temp;
    //D & P Array
        int[] D;
        int[] P;
    //Target nodes list
        List SequentialResult;
    
    public SequentialBarabasiAlbert(Graph firstGraph , int[] D , int[] P) throws IOException {
        
        //Graph
            graph = firstGraph;
            V = firstGraph.getNodeCount();
        //D & P Array
            this.D = D;
            this.P = P;
        //Log
            prepareLogFiles();
        //Time
            totalTime = 0;
        
    }
    
    public void setGraph(Graph newGraph)
    {
        graph = newGraph;
        V = newGraph.getNodeCount();
                
    }
    public Graph getGraph()
    {
        return graph;
    }
    
    public void setD(int[] D)
    {
        this.D = D;
    }
    public int[] getD()
    {
        return D;
    }
    
    public void setP(int[] P)
    {
        this.P = P;
    }
    public int[] getP()
    {
        return P;
    }
    
    public void addToTotalTime(long t)
    {
        totalTime += t;
    }
    public long getTotalTime()
    {
        return totalTime;
    }
    public long getTempTime()
    {
        return temp;
    }
    
    private void prepareLogFiles() throws IOException{
       
        WriteFile w4 = new WriteFile("SequentialBarabasiAlbertResult.txt",false);
        w4.writeToFile("", false);
       
    }
    
    private void initialize()
    {
        //Target nodes list
        List SR = new LinkedList<>();
        SequentialResult = SR;
    }
    
    public Graph makeSequentialBarabasiAlbert(int i) throws InterruptedException, ExecutionException, IOException{
        
        K=i;
        initialize();
        getResultsOfAddingKNodesToGraph();
        Graph newGraph = UpdatedGraph();
        return newGraph;
    }
    
    private void getResultsOfAddingKNodesToGraph( ) throws InterruptedException, ExecutionException, IOException
    { 
        //Time
        temp = 0;
        startTime = System.nanoTime();
        //check sequential mode
        System.out.println("**Check Sequential Mode**");
        checkSequentialMode(SequentialResult);
        //Time
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_1 : Time to get Result in sequential mode :"+estimatedTime+"ns");
    }
    
    private void checkSequentialMode( List Result) throws IOException
    {
        //Log
        String f = "(";
        WriteFile dataResult = new WriteFile( "SequentialBarabasiAlbertResult.txt" , true );
        int[] sD = new int[V+K+2];
        for(int i=1 ; i<=V+1 ; i++)
        {
            sD[i] = D[i];
        }
        
        int lastIndex = V+1;
        for(int i=1 ; i<=K ; i++)
        {
            for(int j=1 ; j<=lastIndex ; j++)
            {
                if(P[i]>=sD[j] && P[i]<sD[j+1])
                {
                    nodePair PairNode = new nodePair(V+i, j);
                    Result.add(PairNode);
                    dataResult.writeToFile(f.concat(String.valueOf(i)).concat(",").concat(String.valueOf(j)).concat(")") , true);
                    for(int h=j+1 ; h<=lastIndex ; h++)
                    {
                        sD[h]++;
                    }
                    lastIndex++;
                    sD[lastIndex] = sD[lastIndex-1]+2;
                    break;
                }
            }
        }
    }
    
    private Graph UpdatedGraph() throws IOException{
       
        Graph NewGraph = new SingleGraph("SequentialNewGraph");
        
        for(Node node : graph)
        {
            for(Edge edge : node.getEachEdge() )
            {
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();
                
                if(NewGraph.getNode(String.valueOf(node0.getIndex())) == null)
                {
                    NewGraph.addNode(String.valueOf(node0.getIndex()));
                }
                
                if(NewGraph.getNode(String.valueOf(node1.getIndex())) == null)
                {
                    NewGraph.addNode(String.valueOf(node1.getIndex()));
                }
                if(NewGraph.getEdge(String.valueOf(node0.getIndex()).concat(String.valueOf(node1.getIndex()))) == null)
                    NewGraph.addEdge(String.valueOf(node0.getIndex()).concat(String.valueOf(node1.getIndex())),String.valueOf(node0.getIndex()), String.valueOf(node1.getIndex()));          
            }
        }
        
        for(int j=0; j<SequentialResult.size() ; j++)
        {
            nodePair np = (nodePair) SequentialResult.get(j);   
            if(NewGraph.getNode(String.valueOf(np.key())) == null)
            {
                NewGraph.addNode(String.valueOf(np.key()));
            }
            if(NewGraph.getNode(String.valueOf(np.value())) == null)
            {
                NewGraph.addNode(String.valueOf(np.value()));
            }
            if(NewGraph.getEdge(String.valueOf(np.key()).concat("&").concat(String.valueOf(np.value()))) == null)
                NewGraph.addEdge(String.valueOf(np.key()).concat("&").concat(String.valueOf(np.value())), String.valueOf(np.key()), String.valueOf(np.value()));
        }
        return NewGraph;
              
    }
    
}
    
   