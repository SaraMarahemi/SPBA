
package Utility;

/**
 *
 * @author Sarah
 */
import GraphHandling.Graph;
import BarabasiAlbert.ParallelGeneration.ParallelBarabasiAlbert;
import BarabasiAlbert.SequentialGeneration.SequentialBarabasiAlbert;
import GraphHandling.Generator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
public class InitializeBarabasiAlbertGenerators {
    
    public static final int V = 10000;
    public static final int m = 1;
    public static final int K = 3;
    public static final int n = 1000;

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, Exception
    {    
        System.out.println("K : "+K);
        System.out.println("V : "+V);
        //Generate First Graph
        Graph generatedFirstGraph = generateFirstGraph();
        ArrayList<Integer> P = necessaryArrayGenerator.makeRandomNumbers(generatedFirstGraph.getEdgeCount(), n, m);
        //SequentialBarabasiAlbert
        long startTime = System.nanoTime();
        SequentialBarabasiAlbert sequentialGenerator = new SequentialBarabasiAlbert(generatedFirstGraph, P , n*K , m);
        sequentialGenerator.makeSequentialBarabasiAlbert();
        System.out.println("Total time for sequential: "+ ((System.nanoTime()- startTime)/10^9) );
        //ParallelBarabasiAlbert 
        startTime = System.nanoTime();
        ParallelBarabasiAlbert parallelGenerator = new ParallelBarabasiAlbert(generatedFirstGraph,,P , n , K , m);
        parallelGenerator.makeParallelBarabasiAlbert();
        System.out.println("Total time for parallel: "+ ((System.nanoTime()- startTime)/10^9) );
        parallelGenerator.CloseExecutor();
        
    }
    
    //A complete or random or Barabasi-Albert Graph is needed at first to start the development
    //The first Same graph will be delivered to both parallel and Sequential Generator
    private static Graph generateFirstGraph()
    {
        return Generator.genearteBarabasiAlbertGraph(V);
    }
}
