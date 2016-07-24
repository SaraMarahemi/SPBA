/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphHandling;

import java.util.SortedSet;

/**
 *
 * @author Pooriya
 * @param <V>
 */
public interface Node
{
    //Get index id of node
    Integer getIndex();
    //Get adjacent vertex list of node
    SortedSet<Node> getAdjacentVertex();
    
    //Add new node to adjacent List
    boolean addAdjacentVertex(Node newAdjacent);
//    //Add new node to adjacent List based on it's index id
//    boolean addAdjacentVertex(Integer newAdjacent);
    
    //Remove an adjacent node from the list
    boolean removeAdjacentVertex(Node removeAdjacent);
//    //Remove an adjacent node from the list based on it's index id
//    boolean removeAdjacentVertex(Integer removeAdjacent);
}
