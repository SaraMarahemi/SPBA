package BarabasiAlbert.ParallelGeneration;

/**
 *
 * @author Sarah
 */
import GraphHandling.Graph;
import Utility.nodePair;
import Utility.WriteFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ParallelBarabasiAlbert {
    
    //Graph
        Graph graph; 
        int V; //Numbers of nodes in first graph
        int m; 
        int K; //Numbers of nodes which connect to first graph in parallel
    //Threads Manager
        ExecutorService executor;
//    //Time Measurement
//        long startTime;
//        long estimatedTime;
//        long totalTime;
//        long temp;
    //D & P Array
        ArrayList<Integer> D;
        ArrayList<Integer> P;
    //Target nodes list
        TreeMap<Integer, Integer> ParallelResult;
    //Candidate nodes list
        List[] CandidateNodes;
    //Analysis random array
        int[] A ;
    //Is fined target node 
        boolean[] IsFined;
    //Last node which found its target
        int LastNode;
            
    public ParallelBarabasiAlbert(Graph firstGraph , ArrayList<Integer> D , ArrayList<Integer> P , int K) throws IOException {
        
        //Graph
        graph = firstGraph;
        V = firstGraph.getNodeCount();
        //Threads Manager
        executor = Executors.newFixedThreadPool(K);
        //D & P Array
        this.D = D;
        this.P = P;
        //Log
        prepareLogFiles();
        //Target nodes list
        ParallelResult = new TreeMap<>(); 
        //Candidate nodes list
        CandidateNodes = new List[K+1];//start array with 1
        //Analysis random array
        A = new int[K+1];
        //Is fined target node 
        IsFined =  new boolean[K+1];
        for(int j=1; j<=K ; j++)
            IsFined[j] = false;
        LastNode = 0;
        
    } 
    
    public void CloseExecutor()
    {
        executor.shutdown();
    }
    
    private void prepareLogFiles() throws IOException
    {
       
        WriteFile w3 = new WriteFile("ParallelBarabasiAlbertResult.txt",false);
        w3.writeToFile("", false);   
    }

    public void makeParallelBarabasiAlbert() throws InterruptedException, ExecutionException, IOException{
        
        //check parallel mode
        System.out.println("**Check Parallel Mode**");
        //A Array generation
        //-> 1
        makeAnalysisOnRandomNumbersArray();
        //-> 2
        findCandidateNodesByThreads();
        //-> 3
        findTargetNodesByThread();
        updateGraph();
    }
    
    public Graph getGraph()
    {
        return graph;
    }
    
    private void makeAnalysisOnRandomNumbersArray()
    {
        for(int i=1; i<=K ; i++)
            A[i]=0;
        for(int i=1; i<P.size() ; i++)
            for(int j = i+1; j<P.size() ; j++)
                if(P.get(j)>P.get(i))
                    A[j]++;
    }
    
    private void findCandidateNodesByThreads() throws InterruptedException, ExecutionException
    {
        Set<Future> set = new HashSet<>();
        for(int j=1 ; j<=K ; j++){
           Future future = executor.submit(new Task(j));
           set.add(future);
        }
        //wait for task completion
        for(Future currTask : set){
            try{
                currTask.get();
            }
            catch(Throwable thrown)
            {
                System.out.println("Error");
            }        
        }
        
    }
    
    private final class Task implements Runnable {
        
        private final int i;
        
        Task(int i ){
          this.i=i;
        }
        
        @Override public void run() {
            
            List R;
            R = findCandidateNodesInParallel(i);
            CandidateNodes[i] = R;
            try {
                checkForTarget(i);
            } catch (IOException ex) {
                Logger.getLogger(ParallelBarabasiAlbert.class.getName()).log(Level.SEVERE, null, ex);
            }
         
        }
    }
    
    private List findCandidateNodesInParallel(int i){
        int Pi = P.get(i);
        List R = new LinkedList<>();
        int firstArrayLastIndex = D.get(V+1)-1;
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
            int T = Pi-D.get(Z);
            nodePair np = new nodePair(Z,T);
            R.add(np);
            int counter = i-1-T;
            int x = 0;
            while(counter>0 && Z>1)
            {
                x = D.get(Z)-D.get(Z-1);
                counter-=x;
                Z=Z-1;
                T=Pi-D.get(Z);
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
                        extraDegree+= (D.get(h+1)-D.get(h))-2;
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
                    extraDegree+=(D.get(candidateNode+1)-D.get(candidateNode))-2;
                    y=y-extraDegree;

                    if(y<0)
                    {
                        if( !( ((D.get((candidateNode)+1)-D.get(candidateNode))-1) >= (-y) ) )
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
            if(Pi == D.get(mid))
            {
                return mid;
            }
            if(Pi>D.get(mid) && Pi<D.get(mid+1))
            {
                return mid;
            }
            if(Pi<D.get(mid) & Pi>D.get(mid-1))
            {
                return mid-1;
            }
            if(Pi>D.get(mid))
                first = mid+1;
            else
                end = mid-1;
         
        }
        return mid;
    }
    
    private void checkForTarget(int i) throws IOException
    {
        int FirstArrayLastIndex = (graph.getEdgeCount()*2)+V;
        WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        nodePair np;
        
        if(CandidateNodes[i].size() == 1)
        {
            np = (nodePair) CandidateNodes[i].get(0);
            addToResult(i, np.key(), dataTargetNodes);
            return;
        }
        if(P.get(i)<FirstArrayLastIndex)
        {
            
            np = (nodePair) CandidateNodes[i].get(0);
            if(A[i]<= np.value())
            {
                addToResult(i, np.key(), dataTargetNodes);
                return;
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
                        c=countOfOne( Z);
                        if(A[i]<=T+c)
                        {
                            addToResult(i, Z, dataTargetNodes);
                            break;
                        }
                        j++;
                    }
            }
        }
    }
    
    private void addToResult(int i , int Z , WriteFile dataTargetNodes) throws IOException
    {
        String f="(";
        ParallelResult.put(V+i, Z);
        dataTargetNodes.writeToFile(f.concat(String.valueOf(V+i).concat(",").concat(String.valueOf(Z)).concat(")")), true);
        IsFined[i] = true;
        if(i == LastNode+1)
            LastNode++;
    }
  
    private int countOfOne(int Z)
    {
        int numberOfTargetsFromThis=0;
        Iterator<Integer> parallelResultValuesResult = ParallelResult.values().iterator();
        while(parallelResultValuesResult.hasNext())
        {
            if(parallelResultValuesResult.next() == Z)
            {
                numberOfTargetsFromThis++;
            }
        }
        return numberOfTargetsFromThis;
    }
    
    private void findTargetNodesByThread() throws IOException, InterruptedException, ExecutionException
    {
        //Log
        String f="(";
        WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        nodePair np;
        int FirstArrayLastIndex = (graph.getEdgeCount()*2)+V;
        for(int i=1; i<=K ; i++)
        {
            if(IsFined[i] == false)
            {
                if(P.get(i)> FirstArrayLastIndex)
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
                        addToResult(i, Z, dataTargetNodes);
                        continue;
                    }
                    
                    int j=1;
                    while(j<CandidateNodes[i].size())
                    {
                        np = (nodePair) CandidateNodes[i].get(j);
                        Z = np.key();
                        T = np.value();
                        c=countOfOne( Z);
                        if(A[i]<=T+c)
                        {
                            addToResult(i, Z, dataTargetNodes);
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
                        System.out.println("Error");
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
        Iterator<Integer> parallelResultValuesResult = ParallelResult.values().iterator();
        while(parallelResultValuesResult.hasNext())
        {
            if(parallelResultValuesResult.next() >= Z)
            {
                numberOfTargetsFromThis++;
            }
        }
        return numberOfTargetsFromThis;
    }
    
    private void CheckThisCandidateNode(int i , nodePair np , int c) throws IOException
    {
        
        WriteFile dataTargetNodes = new WriteFile( "ParallelBarabasiAlbertResult.txt" , true );
        
        if(np.value()>0)
        {
            if(c>=np.value())
            {
                addToResult(i, np.key(), dataTargetNodes); 
            }
        }
        else if(np.value()==0)
        {
            if(c==np.value())
            {
                addToResult(i, np.key(), dataTargetNodes); 
            }
        }
        else //np.value()<0
        {
            if(c==0)
            {
                addToResult(i, np.key(), dataTargetNodes);               
            }
            
        }
        
    }
    
    private void updateGraph() throws IOException
    {
        Iterator<Entry<Integer,Integer>> iterator = ParallelResult.entrySet().iterator();
        while (iterator.hasNext()) 
        {
            Map.Entry<Integer,Integer> entry = iterator.next();
            graph.addEdge(graph.addVertex(entry.getKey()), graph.addVertex(entry.getValue()));
        }
    }
    
}