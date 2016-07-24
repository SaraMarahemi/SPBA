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
public interface Graph
{
    //Ading a pre-consreucted vertex
    Node addVertex(Node vertex);
    //Adding a simple vertex with index number
    Node addVertex(Integer vertex);
    //getting a constructed vertex
    Node addVertex();
    
    //Adding an Edge between two vertex based on their index
    boolean addEdge(Integer vertexOne, Integer vertexTwo);
    //Adding an Edge between two vertex based on their object
    boolean addEdge(Node vertexOne , Node vertexTwo);
    
    //Get vertex List
    SortedSet<Node> getVertexList();
    //Get vertex List for neighbors of a specific vertex based on it's index
    SortedSet<Node> getAdjacentVertices(Integer index);
    //Get vertex List for neighbors of a specific vertex based on it's object
    SortedSet<Node> getAdjacentVertices(Node index);
    
    public Node getVertexByIndex(Integer index);
    
    int getNodeCount();
    int getEdgeCount();
}
