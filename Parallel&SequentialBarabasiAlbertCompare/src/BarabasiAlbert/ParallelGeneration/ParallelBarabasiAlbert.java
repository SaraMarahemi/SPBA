
package BarabasiAlbert.ParallelGeneration;

/**
 *
 * @author Sarah
 */
import Utility.nodePair;
import Utility.WriteFile;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class ParallelBarabasiAlbert {
    
    //Graph
        Graph graph; 
        int V; //Numbers of nodes in first graph
        int m; 
        int K; //Numbers of nodes which connect to first graph in parallel
    //Threads Manager
        ExecutorService executor;
    //time
        long startTime;
        long estimatedTime;
        long totalTime;
        long temp;
    //D & P Array
        int[] D;
        int[] P;
    //Target nodes list
        List ParallelResult;
            
    public ParallelBarabasiAlbert(Graph firstGraph , int[] D , int[] P) throws IOException {
        
        //Graph
            graph = firstGraph;
            V = firstGraph.getNodeCount();
        //Threads Manager
            executor = Executors.newFixedThreadPool(4);
        //D & P Array
            this.D = D;
            this.P = P;
        //Log
            prepareLogFiles();
        //Time
            totalTime = 0;
        
    }
        
    public void CloseExecutor()
    {
        executor.shutdown();
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
       
        WriteFile w3 = new WriteFile("ParallelBarabasiAlbertResult.txt",false);
        w3.writeToFile("", false);
       
    }
    
    public Graph makeParallelBarabasiAlbert(int i) throws InterruptedException, ExecutionException, IOException{
        
        K=i;
        //Target nodes list
            List PR = new LinkedList<>();
            ParallelResult = PR;
        getResultsOfAddingKNodesToGraph();
        //ShowUpdatedGraph(ResultForFindTargetNodesParallel);
        Graph newGraph = UpdatedGraph(ParallelResult);
        return newGraph;
        
    }
    
    private void getResultsOfAddingKNodesToGraph() throws InterruptedException, ExecutionException, IOException
    {
        
        //Time
        temp = 0;
        startTime = System.nanoTime(); 
        //check parallel mode
        System.out.println("**Check Parallel Mode**");
        List[] candidateNodes = new List[K+1];//start array with 1
        findCandidateNodesByThreads(candidateNodes);
        //Time 
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_2-1 : Time to findCandidateNodesByThreads :"+estimatedTime+"ns");     
        
        //Time
        startTime = System.nanoTime(); 
        //A Array generation
        int[] A = new int[K+1];
        makeAnalysisRandomNumbersArray(A);
        //Time
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_2-2: Time to makeAnalysisRandomNumbersArray :"+estimatedTime+"ns");
        startTime = System.nanoTime(); 
        //Find Target Nodes parallel
        findTargetNodesByThread(candidateNodes,A);
        //Time
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_2-3 : Time to findTargetNodesByThread :"+estimatedTime+"ns");
        System.out.println("4_2 : Time to get result in parallel :"+temp+"ns");
        
        
    }
    
    private void makeAnalysisRandomNumbersArray(int[] A)
    {
        for(int i=1; i<=K ; i++)
            A[i]=0;
        for(int i=1; i<P.length ; i++)
        {
            for(int j = i+1; j<P.length ; j++)
            {
                if(P[j]>P[i])
                {
                    A[j]++;
                }
            }
        }
    }
    
    private int countTargetNodesFromThis(int Z , List Result){
        
        int numberOfTargetsFromThis=0;
        for(int j=0; j<Result.size() ; j++)
        {
            nodePair np = (nodePair) Result.get(j);
            if(np.value() >= Z)
            {
                numberOfTargetsFromThis++;
            }
        }
        return numberOfTargetsFromThis;
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
    
    private void findTargetNodesByThread(List[] R,int[] A) throws IOException, InterruptedException, ExecutionException
    {
        //Log
        String f="(";
        WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        nodePair np;
        int FirstArrayLastIndex = (graph.getEdgeCount()*2)+V;
        for(int i=1; i<=K ; i++)
        {
            if(R[i].size() == 1)
            {
                np = (nodePair) R[i].get(0);
                nodePair PairNode = new nodePair(V+i, np.key());
                ParallelResult.add(PairNode);
                dataTargetNodes.writeToFile(f.concat(String.valueOf(i).concat(",").concat(String.valueOf(np.key())).concat(")")), true);
            }
            else
            {
                if(P[i]> FirstArrayLastIndex)
                {
                    int TargetNumber;
                    TargetNumber= findTargetNodeAmongCandidateNodesByThread(R[i], ParallelResult);
                    nodePair PairNode = new nodePair(V+i, TargetNumber);
                    ParallelResult.add(PairNode);
                    dataTargetNodes.writeToFile(f.concat(String.valueOf(i).concat(",").concat(String.valueOf(TargetNumber)).concat(")")), true);
                }
                else
                {
                    np = (nodePair) R[i].get(0);
                    int Z = np.key();
                    int T = np.value();
                    int c=0;
                    if(A[i]<= T)
                    {
                        nodePair PairNode = new nodePair(V+i, Z);
                        ParallelResult.add(PairNode);
                        dataTargetNodes.writeToFile(f.concat(String.valueOf(i).concat(",").concat(String.valueOf(Z)).concat(")")), true);
                        continue;
                    }
                    //A[i]-=T;
                    int j=1;
                    while(j<R[i].size())
                    {
                        np = (nodePair) R[i].get(j);
                        Z = np.key();
                        T = np.value();
                        c=countOfOne(ParallelResult , Z);
                        if(A[i]<=T+c)
                        {
                            nodePair PairNode = new nodePair(V+i, Z);
                            ParallelResult.add(PairNode);
                            dataTargetNodes.writeToFile(f.concat(String.valueOf(i).concat(",").concat(String.valueOf(Z)).concat(")")), true);
                            break;
                        }
                        else
                        {
                         //   A[i]-=(T);
                        }
                        j++;
                    }
                }
            }
        }
    }
    
    private int countOfOne(List Result , int Z)
    {
        int numberOfTargetsFromThis=0;
        for(int j=0; j<Result.size() ; j++)
        {
            nodePair np = (nodePair) Result.get(j);
            if(np.value() == Z)
            {
                numberOfTargetsFromThis++;
            }
        }
        return numberOfTargetsFromThis;
    }
    
    private int findTargetNodeAmongCandidateNodesByThread(List R , List Result ) throws InterruptedException, ExecutionException
    {
        int numThreads = R.size();
        Set<Future<List>> set = new HashSet<>();
        for(int j=0 ; j<R.size() ; j++){
            nodePair np = (nodePair) R.get(j);
            Callable<List> callable = new Task2(np , Result);
            Future<List> future = executor.submit(callable);
            set.add(future);
        }
        List pR;
        int result;
        for(Future<List> future : set)
        {
          pR = future.get();
          if((boolean)pR.get(0) == true)
          {
              return ((int)pR.get(1));
          }
           
        }
      //  executor.shutdown(); //always reclaim resources
       // executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        nodePair np = (nodePair) R.get(R.size()-1);
        return np.key();
    }
    
    private final class Task2 implements Callable<List> {
        
        nodePair np;
        int c;
        Task2(nodePair np , List Result){
          this.np = np;
          c = countTargetNodesFromThis(np.key() , Result);
        }
        
        @Override public List call() throws Exception {
            
          List R = CheckThisCandidateNode(np , c);
          return R;
        }
    }
    private List CheckThisCandidateNode(nodePair np , int c)
    {
        List R = new LinkedList<>();
        
        if(np.value()>0)
        {
            if(c>=np.value())
            {
                R.add(true);
                R.add(np.key());
            }
            else
            {
                R.add(false);
                R.add(np.key());
            }
        }
        else if(np.value()==0)
        {
            if(c==np.value())
            {
                R.add(true);
                R.add(np.key());
            }
            else
            {
                R.add(false);
                R.add(np.key());
            }
        }
        else //np.value()<0
        {
            if(c==0)
            {
                R.add(true);
                R.add(np.key());
            }
            else
            {
                R.add(false);
                R.add(np.key());
            }
        }
        
        return R;
    }
   
    private void findCandidateNodesByThreads(List[] R ) throws InterruptedException, ExecutionException
    {
        
        List pR;
        Set<Future<List>> set = new HashSet<>();
        for(int j=1 ; j<=K ; j++){
           Callable<List> callable = new Task(j,P[j],D);
           Future<List> future = executor.submit(callable);
           set.add(future);
        }
        for(Future<List> future : set)
        {
            pR = future.get();
            R[((int)pR.get(0))] = (List) pR.get(1);
        }
      //  executor.shutdown(); //always reclaim resources
    }
    
    private final class Task implements Callable<List> {
        
        private final int i;
        private final int Pi;
        int[] pD;
        
        Task(int i , int Pi , int[] D){
          this.i=i;
          this.Pi=Pi;
          pD = D;
        }
        
        @Override public List call() throws Exception {
            
            List R;
            R = findCandidateNodesInParallel(i,Pi,pD );
          List pR = new LinkedList<>();
          pR.add(i);
          pR.add(R);
          return pR;
    }
  }
    
    private List findCandidateNodesInParallel(int i,int Pi,int[] pD ){
        
        List R = new LinkedList<>();
        int firstArrayLastIndex = pD[V+1]-1;
        int currentArrayLastIndex = firstArrayLastIndex + (i-1)*3;
        if(Pi == currentArrayLastIndex)
        {
            nodePair np = new nodePair(V+i-1, 0);
            R.add(np);
            return R;
        }
        if(Pi == currentArrayLastIndex-1)
        {
            nodePair np = new nodePair(V+i-1, 1);
            R.add(np);
            return R;
        }
        if(Pi<=firstArrayLastIndex)
        {
            int Z = find(Pi,pD);
            int T = Pi-pD[Z];
            nodePair np = new nodePair(Z,T);
            R.add(np);
            int counter = i-1-T;
            int x = 0;
            while(counter>0 && Z>1)
            {
                x = pD[Z]-pD[Z-1];
                counter-=x;
                Z=Z-1;
                T=Pi-pD[Z];
                np = new nodePair(Z,T);
                R.add(np);
            }
        }
        else
        {
            int F = currentArrayLastIndex-Pi;
            int I = (F/2)+1;
            int J = (F+1)/3;
            //Candidate Nodes are V+i-J-1 to V+i-I
            int y , candidateNode;
            J++;
            int counter = 1;
            int extraDegree = 0;
            while(J<=I)
            {
                candidateNode = V+i-J;
                if(candidateNode <1) break;
                if((i-J) < 0 && counter == 1)
                {
                    //just for first time that i-J<0
                    for(int h=V ; h>candidateNode ; h--)
                    {
                        extraDegree+= (pD[h+1]-pD[h])-2;
                    }
                    counter++;
                }
                if( (i-J) > 0 )
                {
                  y=F-(2*J)+1;
                  nodePair np = new nodePair(candidateNode, y);
                  R.add(np);
                }
                else
                {
                    y=F-(2*J)+1;
                    extraDegree+=(pD[candidateNode+1]-pD[candidateNode])-2;
                    y=y-extraDegree;

                    if(y<0)
                    {
                        if( !( ((pD[(candidateNode)+1]-pD[candidateNode])-1) >= (-y) ) )
                        {
                            J++;
                            continue;
                        }
                    }
                    nodePair np = new nodePair(candidateNode, y);
                    R.add(np);
                }
                J++;
            }
            
        }
         return R;
    }
    private int find(int Pi ,int[] D)
    {
        
        int first = 1;
        int end = V+1;
        int mid=((end+first)/2);
        
        while(first<=end)
        {
            mid=((end+first)/2);
            if(Pi == D[mid])
            {
                return mid;
            }
            if(Pi>D[mid] && Pi<D[mid+1])
            {
                return mid;
            }
            if(Pi<D[mid] & Pi>D[mid-1])
            {
                return mid-1;
            }
            if(Pi>D[mid])
                first = mid+1;
            else
                end = mid-1;
         
        }
        return mid;
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
    
}
