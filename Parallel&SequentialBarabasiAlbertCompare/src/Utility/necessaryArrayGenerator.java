
package Utility;

import GraphHandling.Graph;
import GraphHandling.Node;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Sarah
 */
public class necessaryArrayGenerator 
{
    public static ArrayList<Integer> makeRandomNumbers(int firstArrayLastIndex , int n , int m)
    {
        ArrayList<Integer> randomNumbers = new ArrayList<>();
 //       randomNumbers.add(0);
        Random randomGenerator = new Random();
        for(int i=1 ; i<=n ; i++)
        {
            randomNumbers.add(randomGenerator.nextInt(firstArrayLastIndex));  
            firstArrayLastIndex+= m*2;
        }
        return randomNumbers;
    }
    
    public static ArrayList<Integer> makeDArrayFromFirstGraph(Graph firstGraph)
    {
        ArrayList<Integer> DArray = new ArrayList<>();
//        DArray.add(0);
        int base=1;
        DArray.add(base);
        for(Node node: firstGraph.getVertexList())
        {
            base += node.getAdjacentVertex().size() +1;
            DArray.add(base);
        }
        return DArray;
    }
}
