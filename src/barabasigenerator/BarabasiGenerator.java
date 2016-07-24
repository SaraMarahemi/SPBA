
package barabasigenerator;

import BarabasiAlbert.ParallelGeneration.ParallelBarabasiAlbert;
import BarabasiAlbert.SequentialGeneration.SequentialBarabasiAlbert;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author Sarah
 */
public class BarabasiGenerator {

    public static final int V = 1000;
    public static final int m = 1;
    public static final int K = 5;
    public static final int n = 500;
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
         System.out.println("K : "+K);
        System.out.println("V : "+V);
        //Time
        long startTime = System.nanoTime();    
        //Generate First Graph
        Graph generatedFirstGraph = generateFirstGraph();
        //Time
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("1 : Time to generate first graph : "+estimatedTime+" ns");
        //Necessary Change
        Graph FirstGraph = new SingleGraph("FirstGraphForParallel");
        Graph FirstGraphS = new SingleGraph("FirstGraphForSequential");
        changeFirstGraph(generatedFirstGraph, FirstGraph, FirstGraphS);
        //necessaryArrayGenerator
        necessaryArrayGenerator P_D_Generator = new necessaryArrayGenerator(K,FirstGraphS);
        int[] D = P_D_Generator.makeDArrayFromFirstGraph();
        int[] P = P_D_Generator.makeRandomNumbers();
        
        
        //SequentialBarabasiAlbert
        SequentialBarabasiAlbert sequentialGenerator = new SequentialBarabasiAlbert(FirstGraphS,D,P);
        Graph NewSequentialGraph = sequentialGenerator.makeSequentialBarabasiAlbert(K);
        
        //ParallelBarabasiAlbert 
        ParallelBarabasiAlbert parallelGenerator = new ParallelBarabasiAlbert(FirstGraph,D,P);
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
                
                Graph newP = new SingleGraph("newParallel");
                Graph newS = new SingleGraph("newSequential");
                makeGraph(NewParallelGraph, NewSequentialGraph, newP, newS);
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
    private static void changeFirstGraph(Graph generatedFirstGraph , Graph FirstForParallel , Graph FirstForSequential)
    {
        
        Graph temp = new SingleGraph("TempGraph");;
        for(Node node : generatedFirstGraph)
        {
            for(Edge edge : node.getEachEdge() )
            {
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();
                String node0ID = node0.getId();
                String node1ID = node1.getId();
                if(temp.getNode(node0ID) == null)
                {
                    temp.addNode(node0ID);
                    FirstForParallel.addNode(String.valueOf(Integer.parseInt(node0ID)+1));
                    FirstForSequential.addNode(String.valueOf(Integer.parseInt(node0ID)+1));
                }
                if(temp.getNode(node1ID) == null)
                {
                    temp.addNode(node1ID);
                    FirstForParallel.addNode(String.valueOf(Integer.parseInt(node1ID)+1));
                    FirstForSequential.addNode(String.valueOf(Integer.parseInt(node1ID)+1));
                }
                if(temp.getEdge(node0ID.concat("_").concat(node1ID)) == null)
                {
                    temp.addEdge(node0ID.concat("_").concat(node1ID), node0ID, node1ID);
                    FirstForParallel.addEdge((String.valueOf(Integer.parseInt(node0ID)+1)).concat("_").concat(String.valueOf(Integer.parseInt(node1ID)+1)), String.valueOf(Integer.parseInt(node0ID)+1), String.valueOf(Integer.parseInt(node1ID)+1));
                    FirstForSequential.addEdge((String.valueOf(Integer.parseInt(node0ID)+1)).concat("_").concat(String.valueOf(Integer.parseInt(node1ID)+1)), String.valueOf(Integer.parseInt(node0ID)+1), String.valueOf(Integer.parseInt(node1ID)+1));
                }
            }
        }
            
    }
    private static void makeGraph(Graph NewParallelGraph , Graph NewSequentialGraph , Graph newP , Graph newS)
    {
        for(Node node : NewParallelGraph)
        {
            for(Edge edge : node.getEachEdge() )
            {
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();
                String node0ID = node0.getId();
                String node1ID = node1.getId();
                if(newP.getNode(node0ID) == null)
                {
                    newP.addNode(node0ID);
                }
                if(newP.getNode(node1ID) == null)
                {
                    newP.addNode(node1ID);
                }
                if(newP.getEdge(node0ID.concat("_").concat(node1ID)) == null)
                {
                    newP.addEdge(node0ID.concat("_").concat(node1ID), node0ID, node1ID);
                }
            }
        }
        for(Node node : NewSequentialGraph)
        {
            for(Edge edge : node.getEachEdge() )
            {
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();
                String node0ID = node0.getId();
                String node1ID = node1.getId();
                if(newS.getNode(node0ID) == null)
                {
                    newS.addNode(node0ID);
                }
                if(newS.getNode(node1ID) == null)
                {
                    newS.addNode(node1ID);
                }
                if(newS.getEdge(node0ID.concat("_").concat(node1ID)) == null)
                {
                    newS.addEdge(node0ID.concat("_").concat(node1ID), node0ID, node1ID);
                }
            }
        }
        
    }
}
