/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphHandling;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Pooriya
 * @param 
 */
public class SimpleNode implements Node ,Comparable<SimpleNode>
{
    SortedSet<Node> adjacent;
    Integer index;

    public SimpleNode(Integer index)
    {
        this.index = index;
        adjacent = new TreeSet<>();
    }

    @Override
    public Integer getIndex()
    {
        return index;
    }

    @Override
    public SortedSet<Node> getAdjacentVertex()
    {
        return adjacent;
    }

    @Override
    public boolean addAdjacentVertex(Node newAdjacent)
    {
        return adjacent.add(newAdjacent);
    }

    @Override
    public boolean removeAdjacentVertex(Node removeAdjacent)
    {
        return adjacent.remove(removeAdjacent);
    }

//    @Override
//    public boolean addAdjacentVertex(Integer newAdjacent)
//    {
//        for(Node vertex: adjacent)
//            if(Objects.equals(vertex.getIndex(), newAdjacent))
//                return false;
//        return adjacent.add(new SimpleNode(newAdjacent));
//    }
//
//    @Override
//    public boolean removeAdjacentVertex(Integer removeAdjacent)
//    {
//        for(Node vertex: adjacent)
//            if(Objects.equals(vertex.getIndex(), removeAdjacent))
//            {
//                adjacent.remove(vertex);
//                return true;
//            }
//        return false;
//    }

    @Override
    public int compareTo(SimpleNode o)
    {
        return Integer.compare(this.index, o.index);
    }
}
