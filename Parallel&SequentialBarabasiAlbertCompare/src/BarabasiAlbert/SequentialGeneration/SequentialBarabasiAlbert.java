
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
    
    public Graph makeSequentialBarabasiAlbert(int i) throws InterruptedException, ExecutionException, IOException{
        
        K=i;
        List ResultForFindTargetNodesSequentially = new LinkedList<>();
        getResultsOfAddingKNodesToGraph(ResultForFindTargetNodesSequentially );
        //ShowUpdatedGraph(ResultForFindTargetNodesParallel);  
        Graph newGraph = UpdatedGraph(ResultForFindTargetNodesSequentially );
        return newGraph;
    }
    
    private void getResultsOfAddingKNodesToGraph(List SequentialResult ) throws InterruptedException, ExecutionException, IOException
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
    
    private Graph UpdatedGraph(List Result) throws IOException{
       
        Graph NewGraph = new SingleGraph("ParallelNewGraph");
        
        for(Node node : graph)
        {
            for(Edge edge : node.getEachEdge() )
            {
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();
                
                if(NewGraph.getNode(String.valueOf(node0.getIndex()+1)) == null)
                {
                    NewGraph.addNode(String.valueOf(node0.getIndex()+1));
                }
                
                if(NewGraph.getNode(String.valueOf(node1.getIndex()+1)) == null)
                {
                    NewGraph.addNode(String.valueOf(node1.getIndex()+1));
                }
                if(NewGraph.getEdge(String.valueOf(node0.getIndex()+1).concat(String.valueOf(node1.getIndex()+1))) == null)
                    NewGraph.addEdge(String.valueOf(node0.getIndex()+1).concat(String.valueOf(node1.getIndex()+1)),String.valueOf(node0.getIndex()+1), String.valueOf(node1.getIndex()+1));          
            }
        }
 
        for(int j=0; j<Result.size() ; j++)
        {
            nodePair np = (nodePair) Result.get(j);   
            NewGraph.addNode(String.valueOf(np.key()));
            NewGraph.addEdge(String.valueOf(np.key()).concat(String.valueOf(np.value())), String.valueOf(np.key()), String.valueOf(np.value()));
        }
        return NewGraph;
              
    }
    
    
    //In Comment
    private void ShowUpdatedGraph(List Result) throws IOException{
        
        String styleSheet =
            "node {" +
            "	fill-color: black;" +
            "}" +
            "node.marked {" +
            "	fill-color: red;" +
            "}";
        Graph FirstGraph = new SingleGraph("FirstGraph");
        Graph NewGraph = new SingleGraph("NewGraph");
        NewGraph.addAttribute("ui.stylesheet", styleSheet);
        for(Node node : graph)
        {
            for(Edge edge : node.getEachEdge() )
            {
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();
                
                if(NewGraph.getNode(String.valueOf(node0.getIndex()+1)) == null)
                {
                    NewGraph.addNode(String.valueOf(node0.getIndex()+1));
                    NewGraph.getNode(String.valueOf(node0.getIndex()+1)).addAttribute("ui.label", String.valueOf(node0.getIndex()+1));
                }
                
                if(NewGraph.getNode(String.valueOf(node1.getIndex()+1)) == null)
                {
                    NewGraph.addNode(String.valueOf(node1.getIndex()+1));
                    NewGraph.getNode(String.valueOf(node1.getIndex()+1)).addAttribute("ui.label", String.valueOf(node1.getIndex()+1));
                }
                if(NewGraph.getEdge(String.valueOf(node0.getIndex()+1).concat(String.valueOf(node1.getIndex()+1))) == null)
                    NewGraph.addEdge(String.valueOf(node0.getIndex()+1).concat(String.valueOf(node1.getIndex()+1)),String.valueOf(node0.getIndex()+1), String.valueOf(node1.getIndex()+1));
                //First Graph
                if(FirstGraph.getNode(String.valueOf(node0.getIndex()+1)) == null)
                {
                    FirstGraph.addNode(String.valueOf(node0.getIndex()+1));
                    FirstGraph.getNode(String.valueOf(node0.getIndex()+1)).addAttribute("ui.label", String.valueOf(node0.getIndex()+1));
                }
                if(FirstGraph.getNode(String.valueOf(node1.getIndex()+1)) == null)
                {
                    FirstGraph.addNode(String.valueOf(node1.getIndex()+1));
                    FirstGraph.getNode(String.valueOf(node1.getIndex()+1)).addAttribute("ui.label", String.valueOf(node1.getIndex()+1));
                }
                if(FirstGraph.getEdge(String.valueOf(node0.getIndex()+1).concat(String.valueOf(node1.getIndex()+1))) == null)
                    FirstGraph.addEdge(String.valueOf(node0.getIndex()+1).concat(String.valueOf(node1.getIndex()+1)),String.valueOf(node0.getIndex()+1), String.valueOf(node1.getIndex()+1));
            }
        }
        FirstGraph.display();
        for(int j=0; j<Result.size() ; j++)
        {
            nodePair np = (nodePair) Result.get(j);   
            NewGraph.addNode(String.valueOf(np.key()));
            NewGraph.getNode(String.valueOf(np.key())).addAttribute("ui.label", String.valueOf(np.key()));
            NewGraph.addEdge(String.valueOf(np.key()).concat(String.valueOf(np.value())), String.valueOf(np.key()), String.valueOf(np.value()));
            NewGraph.getNode(String.valueOf(np.key())).setAttribute("ui.class", "marked");
        }
        NewGraph.display();
              
    }
}
    
   