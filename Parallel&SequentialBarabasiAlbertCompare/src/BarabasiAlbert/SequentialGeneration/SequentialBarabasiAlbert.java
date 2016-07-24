package BarabasiAlbert.SequentialGeneration;

/**
 *
 * @author Sarah
 */
import GraphHandling.Graph;
import Utility.WriteFile;
import Utility.nodePair;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;


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
    TreeMap<Integer, Integer> SequentialResult;
    
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
        SequentialResult = new TreeMap<>();
    }
    
    public Graph makeSequentialBarabasiAlbert(int i) throws InterruptedException, ExecutionException, IOException{
        
        K=i;
        initialize();
        getResultsOfAddingKNodesToGraph();
        updateGraph();
        return graph;
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
    
    private void checkSequentialMode( TreeMap<Integer , Integer> Result) throws IOException
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
                    Result.put(V+i, j);
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
    
    private void updateGraph() throws IOException
    {
        Iterator<Map.Entry<Integer,Integer>> iterator = SequentialResult.entrySet().iterator();
        while (iterator.hasNext()) 
        {
            Map.Entry<Integer,Integer> entry = iterator.next();
            graph.addEdge(graph.addVertex(entry.getKey()), graph.addVertex(entry.getValue()));
        }
    }
    
}