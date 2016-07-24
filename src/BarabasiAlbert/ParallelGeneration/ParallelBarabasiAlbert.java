
package BarabasiAlbert.ParallelGeneration;

/**
 *
 * @author Sarah
 */

import barabasigenerator.WriteFile;
import barabasigenerator.nodePair;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graphstream.graph.*;

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
    //Candidate nodes list
        List[] CandidateNodes;
    //Analysis random array
        int[] A ;
    //Is fined target node 
        boolean[] IsFined;
    //Last node which found its target
        int LastNode;
    
            
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
            //prepareLogFiles();
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
       //@1
        int x = 0;
        if(V%5 == 0)
            x = 0;
        else
            x = 1;
        int n = (V / 5 + x)*5; 
        V = n;
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
       
        WriteFile w4 = new WriteFile("PUpdatedGraph.txt",false);
        w4.writeToFile("", false);
       
    }
    private void initialize()
    {
       //Target nodes list
        List PR = new LinkedList<>();
        ParallelResult = PR;
        //Candidate nodes list
        List[] CN = new List[K+1];//start array with 1
        CandidateNodes = CN;
        //Analysis random array
        int[] AR = new int[K+1];
        A = AR;
        //Is fined target node 
        boolean[] IF = new boolean[K+1];
        IsFined = IF;
        for(int j=1; j<K+1 ; j++)
            IsFined[j] = false;
        LastNode = 0;
    }
    public Graph makeParallelBarabasiAlbert(int i) throws InterruptedException, ExecutionException, IOException{
        
        K=i;
        initialize();
        getResultsOfAddingKNodesToGraph();
        Graph newGraph = UpdatedGraph();
        return newGraph;
        
    }
    
    private void getResultsOfAddingKNodesToGraph() throws InterruptedException, ExecutionException, IOException
    {
        //check parallel mode
        System.out.println("**Check Parallel Mode**");
        
         //Time
        temp = 0;
        startTime = System.nanoTime(); 
        //A Array generation
        //-> 1
        makeAnalysisRandomNumbersArray();
        //Time
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_2-1: Time to makeAnalysisRandomNumbersArray :"+estimatedTime+"ns");
        
        //Time
        startTime = System.nanoTime(); 
        //-> 2
        findCandidateNodesByThreads();
        //Time 
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_2-2 : Time to findCandidateNodesByThreads :"+estimatedTime+"ns");     
        
       //Time
        startTime = System.nanoTime(); 
        //Find Target Nodes parallel
        //-> 3
        findTargetNodesByThread();
        //Time
        estimatedTime = System.nanoTime() - startTime;
        temp+=estimatedTime;
        System.out.println("4_2-3 : Time to findTargetNodesByThread :"+estimatedTime+"ns");
        System.out.println("4_2 : Time to get result in parallel :"+temp+"ns");
        
        
    }
    
    private void makeAnalysisRandomNumbersArray()
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
    
     private void findCandidateNodesByThreads() throws InterruptedException, ExecutionException
    {
         
        Set<Future<List>> set = new HashSet<>();
        for(int j=1 ; j<=K ; j++){
           Callable<List> callable = new Task(j);
           Future<List> future = executor.submit(callable);
           set.add(future);
           
        }
        //wait for task completion
        List T;
        nodePair np;
        for(Future<List> currTask : set){
            try{
                T = currTask.get();
                if(!T.isEmpty())
                {
                    np = (nodePair) T.get(0);
                    ParallelResult.add(np);
                }
            }
            catch(Throwable thrown)
                    {
                        //sleep(150);
                    } 
        }
    }
    
    private final class Task implements Callable<List> {
        
        private final int i;
        
        Task(int i ){
          this.i=i;
        }
        
        @Override public List call() {
            
            List R;
            R = findCandidateNodesInParallel(i);
            CandidateNodes[i] = R;
            List T = null;
            try {
                T = checkForTarget(i);
            } catch (IOException ex) {
                Logger.getLogger(ParallelBarabasiAlbert.class.getName()).log(Level.SEVERE, null, ex);
            }
            return T;
        }
    }
    
    private List findCandidateNodesInParallel(int i) {
        int Pi = P[i];
        List R = new LinkedList<>();
        int firstArrayLastIndex = D[V+1]-1;
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
            int Z = find(Pi);
            int T = Pi-D[Z];
            nodePair np = new nodePair(Z,T);
            R.add(np);
            int counter = i-1-T;
            int x = 0;
            while(counter>0 && Z>1)
            {
                x = D[Z]-D[Z-1];
                counter-=x;
                Z=Z-1;
                T=Pi-D[Z];
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
                        extraDegree+= (D[h+1]-D[h])-2;
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
                    extraDegree+=(D[candidateNode+1]-D[candidateNode])-2;
                    y=y-extraDegree;

                    if(y<0)
                    {
                        if( !( ((D[(candidateNode)+1]-D[candidateNode])-1) >= (-y) ) )
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
    
    private int find(int Pi)
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
    
    private List checkForTarget(int i) throws IOException
    {
        List R = new LinkedList<>();
        int FirstArrayLastIndex = (graph.getEdgeCount()*2)+V;
        //String f="(";
        //WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        nodePair np;
        if( IsFined[i] == false)
        {
        if(CandidateNodes[i].size() == 1)
        {
            np = (nodePair) CandidateNodes[i].get(0);
            nodePair PairNode = new nodePair(V+i, np.key());
            R.add(PairNode);
            //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
            IsFined[i] = true;
            if(i == LastNode+1)
                LastNode++;
            return R;
        }
        if(P[i]<FirstArrayLastIndex)
        {
            np = (nodePair) CandidateNodes[i].get(0);
            if(A[i]<= np.value())
            {
                        nodePair PairNode = new nodePair(V+i, np.key());
                        R.add(PairNode);
                        //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                        IsFined[i] = true;
                        if(i == LastNode+1)
                            LastNode++;
                        return R;
            }
            if(i == LastNode+1)
            {
                int j=1;
                int Z;
                int T;
                int c;
                    while(j<CandidateNodes[i].size())
                    {
                        np = (nodePair) CandidateNodes[i].get(j);
                        Z = np.key();
                        T = np.value();
                         if(j ==  CandidateNodes[i].size()-1)
                        {
                            nodePair PairNode = new nodePair(V+i, Z);
                            R.add(PairNode);
                            //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                            if(i == LastNode+1)
                                LastNode++;
                            break;
                        }
                        c=countOfOne( Z);
                        if(A[i]<=T+c)
                        {
                            nodePair PairNode = new nodePair(V+i, Z);
                            R.add(PairNode);
                            //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                            IsFined[i] = true;
                            if(i == LastNode+1)
                                LastNode++;
                            break;
                        }
                        j++;
                    }
            }
        }
        }
        return R;
    }
    
    private int countOfOne(int Z)
    {
        int numberOfTargetsFromThis=0;
        int index = ParallelResult.indexOf(Z);
        while(index != -1)
        {
            numberOfTargetsFromThis++;
            index = ParallelResult.indexOf(Z);
        }
        return numberOfTargetsFromThis;
    }
    private void findTargetNodesByThread() throws IOException, InterruptedException, ExecutionException
    {
        //Log
        //String f="(";
        //WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        nodePair np;
        int FirstArrayLastIndex = (graph.getEdgeCount()*2)+V;
        for(int i=1; i<=K ; i++)
        {
            if(IsFined[i] == false)
            {
                if(CandidateNodes[i].size() == 1)
                {
                    np = (nodePair) CandidateNodes[i].get(0);
                    nodePair PairNode = new nodePair(V+i, np.key());
                    //targetResults[i].add(PairNode);
                    ParallelResult.add(PairNode);
                    //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                    IsFined[i] = true;
                    if(i == LastNode+1)
                        LastNode++;
                    continue;
                }
                if(P[i]> FirstArrayLastIndex)
                {
                    findTargetNodeAmongCandidateNodesByThread(i);
                }
                else
                {
                    np = (nodePair) CandidateNodes[i].get(0);
                    int Z = np.key();
                    int T = np.value();
                    int c=0;
                    if(A[i]<= T)
                    {
                        nodePair PairNode = new nodePair(V+i, Z);
                        ParallelResult.add(PairNode);
                        //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                         IsFined[i] = true;
                        if(i == LastNode+1)
                            LastNode++;
                        continue;
                    }
                    
                    int j=1;
                    while(j<CandidateNodes[i].size())
                    {
                        np = (nodePair) CandidateNodes[i].get(j);
                        Z = np.key();
                        T = np.value();
                        if(j ==  CandidateNodes[i].size()-1)
                        {
                            nodePair PairNode = new nodePair(V+i, Z);
                            ParallelResult.add(PairNode);
                            //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                             IsFined[i] = true;
                            if(i == LastNode+1)
                                LastNode++;
                            break;
                        }
                        c=countOfOne( Z);
                        if(A[i]<=T+c)
                        {
                            nodePair PairNode = new nodePair(V+i, Z);
                            ParallelResult.add(PairNode);
                           //dataTargetNodes.writeToFile(f.concat(String.valueOf(PairNode.key()).concat(",").concat(String.valueOf(PairNode.value())).concat(")")), true);
                             IsFined[i] = true;
                            if(i == LastNode+1)
                                LastNode++;
                            break;
                        }
                        j++;
                    }
                }
            }
        }
    }
    
    private void findTargetNodeAmongCandidateNodesByThread(int i ) throws InterruptedException, ExecutionException
    {
        Set<Future> set = new HashSet<>();
        for(int j=0 ; j<CandidateNodes[i].size() ; j++)
        {
           nodePair np = (nodePair) CandidateNodes[i].get(j);
           Future future = executor.submit(new Task2(i , np));
           set.add(future);
        }
         //wait for task completion
        for(Future currTask : set){
            try{
                currTask.get();
            }
            catch(Throwable thrown)
                    {
                        
                    }        
        }
    }
    
    private final class Task2 implements Runnable {
        
        nodePair np;
        int c;
        int i;
        Task2(int i , nodePair np){
          this.np = np;
          this.i = i;
          c = countTargetNodesFromThis(np.key());
        }
        
        @Override public void run(){
            
            try {
                CheckThisCandidateNode(i , np , c);
            } catch (IOException ex) {
                Logger.getLogger(ParallelBarabasiAlbert.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private int countTargetNodesFromThis(int Z ){
        
        int numberOfTargetsFromThis=0;
        for(int j=0; j<ParallelResult.size() ; j++)
        {
            nodePair np = (nodePair) ParallelResult.get(j);
            if(np.value() >= Z)
            {
                numberOfTargetsFromThis++;
            }
        }
        return numberOfTargetsFromThis;
    }
    
    private void CheckThisCandidateNode(int i , nodePair np , int c) throws IOException
    {
        //String f="(";
        //WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        
        if(np.value()>0)
        {
            if(c>=np.value())
            {
                nodePair PairNode = new nodePair(V+i, np.key());
                ParallelResult.add(PairNode);
                //dataTargetNodes.writeToFile(f.concat(String.valueOf(V+i).concat(",").concat(String.valueOf(np.key())).concat(")")), true);
                IsFined[i] = true;
                if(i == LastNode+1)
                    LastNode++;
                
            }
        }
        else if(np.value()==0)
        {
            if(c==np.value())
            {
                nodePair PairNode = new nodePair(V+i, np.key());
                ParallelResult.add(PairNode);
                //dataTargetNodes.writeToFile(f.concat(String.valueOf(V+i).concat(",").concat(String.valueOf(np.key())).concat(")")), true);
                IsFined[i] = true;
                if(i == LastNode+1)
                    LastNode++;
                
            }
        }
        else //np.value()<0
        {
            if(c==0)
            {
                nodePair PairNode = new nodePair(V+i, np.key());
                ParallelResult.add(PairNode);
                //dataTargetNodes.writeToFile(f.concat(String.valueOf(V+i).concat(",").concat(String.valueOf(np.key())).concat(")")), true);
                IsFined[i] = true;
                if(i == LastNode+1)
                    LastNode++;   
            }   
        }
    }
    
    private Graph UpdatedGraph() throws IOException
    {
        //log
        //String f="(";
        //WriteFile dataTargetNodes = new WriteFile( "PUpdatedGraph.txt" , true );
        
        int index = ParallelResult.indexOf(null);
        int bound = ParallelResult.size();
        if (index != -1)
            bound = index;
        for (int i=0; i<bound ; i++) 
        {
            nodePair np = (nodePair) ParallelResult.get(i);           
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
                 //dataTargetNodes.writeToFile(f.concat(node0ID).concat(",").concat(node1ID).concat(")"), true);
            }           
        }
        return graph;
    }
}
