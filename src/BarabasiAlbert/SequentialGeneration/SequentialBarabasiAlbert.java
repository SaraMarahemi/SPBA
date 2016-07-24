
package BarabasiAlbert.SequentialGeneration;

/**
 *
 * @author Sarah
 */

import barabasigenerator.WriteFile;
import barabasigenerator.nodePair;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.graph.*;


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
        WriteFile w5 = new WriteFile("SUpdatedGraph.txt",false);
        w5.writeToFile("", false);
    }
    
    public Graph makeSequentialBarabasiAlbert(int i) throws IOException {
        
        K=i;
        //Target nodes list
        List SR = new LinkedList<>();
        SequentialResult = SR;
        getResultsOfAddingKNodesToGraph();
        Graph newGraph = UpdatedGraph();
        return newGraph;
        
    }
    
    private void getResultsOfAddingKNodesToGraph( ) throws IOException 
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
                    dataResult.writeToFile(f.concat(String.valueOf(V+i)).concat(",").concat(String.valueOf(j)).concat(")") , true);
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
    
    private Graph UpdatedGraph() throws IOException
    {
        //log
        String f="(";
        WriteFile dataTargetNodes = new WriteFile( "SUpdatedGraph.txt" , true );
        
        int index = SequentialResult.indexOf(null);
        int bound = SequentialResult.size();
        if (index != -1)
            bound = index;
        
        for (int i=0; i<bound ; i++) 
        {
            nodePair np = (nodePair) SequentialResult.get(i);           
            String node0ID = String.valueOf(np.key());
            String node1ID = String.valueOf(np.value());
            if(graph.getNode(node0ID) == null)
            {
                graph.addNode(node0ID);
            }
            if(graph.getNode(node1ID) == null)
            {
                graph.addNode(node1ID);
            }
            if(graph.getEdge(node0ID.concat("_").concat(node1ID)) == null)
            {
                 graph.addEdge(node0ID.concat("_").concat(node1ID), node0ID, node1ID);
                 dataTargetNodes.writeToFile(f.concat(node0ID).concat(",").concat(node1ID).concat(")"), true);
            }
                
        }
        return graph;
    }
    
}
    
    
   