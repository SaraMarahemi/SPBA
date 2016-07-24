/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphHandling;

import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Pooriya
 */
public class SimpleGraph implements Graph
{
    SortedSet<Node> vertices;
    int edgeCount;

    public SimpleGraph()
    {
        vertices = new TreeSet<>();
        edgeCount = 0;
    }

    @Override
    public Node addVertex(Node vertex)
    {
        Iterator<Node> iterator = vertices.iterator();
        while(iterator.hasNext())
        {
            Node itemOnList = iterator.next();
            if(Objects.equals(itemOnList.getIndex(), vertex.getIndex()))
                return itemOnList;
        }
        vertices.add(vertex);
        return vertex;
    }

    @Override
    public Node addVertex(Integer vertex)
    {
        Node resultOfSearchForIndex = getVertexByIndex(vertex);
        if( resultOfSearchForIndex == null) 
        {
            Node newNodeToAdd = new SimpleNode(vertex);
            vertices.add(newNodeToAdd);
            return newNodeToAdd;
        }
        else
        {
            return getVertexByIndex(vertex);
        }
    }
    
//    private boolean containVertex(Integer vertex)
//    {
//        for(Node node: vertices)
//            if(Objects.equals(node.getIndex(), vertex))
//                return true;
//        return false;
//    }
    
    @Override
    public Node getVertexByIndex(Integer index)
    {
        for(Node node: vertices)
            if(Objects.equals(node.getIndex(), index))
                return node;
        return null;
    }

    @Override
    public Node addVertex()
    {
        Node toAddNode = new SimpleNode(vertices.size());
        if(vertices.add(toAddNode) == true)
            return toAddNode;
        else
            return null;
    }

    @Override
    public boolean addEdge(Integer vertexOne, Integer vertexTwo)
    {
        Node firstVertex = null , secondVertex = null;
        for(Node vertex: vertices)
        {
            if(Objects.equals(vertex.getIndex(), vertexOne))
                firstVertex= vertex;
            else
                if(Objects.equals(vertex.getIndex(), vertexTwo))
                    secondVertex = vertex;
        }
        if(firstVertex == null || secondVertex == null)
            return false;
        return addEdge(firstVertex , secondVertex);
    }

    @Override
    public boolean addEdge(Node vertexOne, Node vertexTwo)
    {
        if ((vertexOne.addAdjacentVertex(vertexTwo) && vertexTwo.addAdjacentVertex(vertexOne)) == true)
        {
            edgeCount++;
            return true;
        }
        return false;
    }

    @Override
    public SortedSet<Node> getVertexList()
    {
        return vertices;
    }

    @Override
    public SortedSet<Node> getAdjacentVertices(Integer index)
    {
        Node result = getVertexByIndex(index);
        if( result == null)
            return null;
        return result.getAdjacentVertex();
    }

    @Override
    public SortedSet<Node> getAdjacentVertices(Node index)
    {
        return index.getAdjacentVertex();
    }

    @Override
    public int getNodeCount()
    {
        return vertices.size();
    }

    @Override
    public int getEdgeCount()
    {
        return edgeCount;
    }
    
    
}
