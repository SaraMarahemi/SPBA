
package Utility;

/**
 *
 * @author Sarah
 */
import BarabasiAlbert.ParallelGeneration.ParallelBarabasiAlbert;
import BarabasiAlbert.SequentialGeneration.SequentialBarabasiAlbert;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
public class InitializeBarabasiAlbertGenerators {
    
    public static final int V = 1000;
    public static final int m = 1;
    public static final int K = 5;
    public static final int n = 100;

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException{
        
        System.out.println("K : "+K);
        System.out.println("V : "+V);
        //Time
        long startTime = System.nanoTime();    
        //Generate First Graph
        Graph generatedFirstGraph = generateFirstGraph();
        //Time
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("1 : Time to generate first graph : "+estimatedTime+" ns");
        //necessaryArrayGenerator
        necessaryArrayGenerator P_D_Generator = new necessaryArrayGenerator(K,generatedFirstGraph);
        int[] D = P_D_Generator.makeDArrayFromFirstGraph();
        int[] P = P_D_Generator.makeRandomNumbers();
        
        //SequentialBarabasiAlbert
        SequentialBarabasiAlbert sequentialGenerator = new SequentialBarabasiAlbert(generatedFirstGraph,D,P);
        Graph NewSequentialGraph = sequentialGenerator.makeSequentialBarabasiAlbert(K);
        
        //ParallelBarabasiAlbert 
        ParallelBarabasiAlbert parallelGenerator = new ParallelBarabasiAlbert(generatedFirstGraph,D,P);
        Graph NewParallelGraph = parallelGenerator.makeParallelBarabasiAlbert(K);
        int Scounter = 0;
        int Fcounter = 0;
        if(sequentialGenerator.getTempTime() > parallelGenerator.getTempTime())
        {
            System.out.println("$$ ---- SUCCESSFUL ---- $$");
            Scounter ++;
        }
        else
        {
            System.out.println("$$ ---- FAILED ---- $$");
            Fcounter ++;
        }
        
        for(int i=1 ; i<(n) ; i++)
        {
            
            P_D_Generator.setGraph(NewSequentialGraph);
            D = P_D_Generator.makeDArrayFromFirstGraph();
            P = P_D_Generator.makeRandomNumbers();
            //Sequential
                //Time
                startTime = System.nanoTime();
            sequentialGenerator.setGraph(NewSequentialGraph);
            sequentialGenerator.setD(D);
            sequentialGenerator.setP(P);
            NewSequentialGraph = sequentialGenerator.makeSequentialBarabasiAlbert(K);
                //Time
                estimatedTime = System.nanoTime() - startTime;
                sequentialGenerator.addToTotalTime(estimatedTime);
            //Parallel
                //Time
                startTime = System.nanoTime();
            parallelGenerator.setGraph(NewParallelGraph);
            parallelGenerator.setD(D);
            parallelGenerator.setP(P);
            NewParallelGraph = parallelGenerator.makeParallelBarabasiAlbert(K);
                //Time
                estimatedTime = System.nanoTime() - startTime;
                parallelGenerator.addToTotalTime(estimatedTime);
                
                if(sequentialGenerator.getTempTime() > parallelGenerator.getTempTime())
                {
                    System.out.println("$$ ---- SUCCESSFUL ---- $$");
                    Scounter ++;
                }
                else
                {
                    System.out.println("$$ ---- FAILED ---- $$");
                    Fcounter ++;
                }
        }
        System.out.println("Sequential Total Time : "+sequentialGenerator.getTotalTime()+" ns");
        System.out.println("**Parallel Total Time : "+parallelGenerator.getTotalTime()+" ns");
        System.out.println("Successful : "+Scounter);
        System.out.println("Failed : "+Fcounter);
        parallelGenerator.CloseExecutor();
        
    }
    
    //A complete or random or Barabasi-Albert Graph is needed at first to start the development
    //The first Same graph will be delivered to both parallel and Sequential Generator
    private static Graph generateFirstGraph()
    {
        Graph graph = new SingleGraph("Barabàsi-Albert");
        Generator gen = new BarabasiAlbertGenerator(m,true);
        gen.addSink(graph); 
        gen.begin();
        for(int i=0; i<V-2; i++) // Generate V-2 nodes
        {
            gen.nextEvents();
        }
        gen.end();
        return graph;
    }
    
}
