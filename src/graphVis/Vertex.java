/**
 *  A zero gravity Cremona Diagram created using processing and ControlIP5. 
 *  
 *  I ask that you cite / reference my github repo if you use this code as a reference.   
 *  
 *  Copyright (C) 2020  Joe Caffarini jgcaffari1@gmail.com
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *  
 *  See the GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *  
 */


package graphVis;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 * a vertex for storing key value pairs in a graph
 * 
 * @author joe caffarini
 * @param <K> - the key data type
 * @param <V> - the value data type
 */
public class Vertex<K extends Comparable<K>, V> {
	/**
	 * plan on drawing these as circles with the keys in the middle.
	 */
	private K key;
	private V value;
	boolean visited;
	boolean iterateOverInEdges = false;

	/**
	 * comparator for sorting vertexes based off of total weight.
	 */
	class CompareEdges implements Comparator<Edge<K, V>> {
		public int compare(Edge<K, V> edge1, Edge<K, V> edge2) {
			return Double.valueOf(edge1.getWeight()).compareTo(edge2.getWeight());
		}
	}

	// initialize variables for dijstra's:
	Double totalWeight = (double) Integer.MAX_VALUE;
	Vertex<K, V> predecessor = null;

	// the outEdges are stored based on the ending vertex.
	private Hashtable<K, Edge<K, V>> outEdges;
	// hash table of in edges, indexed by start key.
	private Hashtable<K, Edge<K, V>> inEdges;

	/**
	 * creates a new vertex with the given key value pair.
	 * 
	 * @param key
	 * @param value
	 */
	public Vertex(K key, V value) {
		outEdges = new Hashtable<>();
		inEdges = new Hashtable<>();
		this.key = key;
		this.value = value;
		visited = false;
	}

	/**
	 * forms a connection between this vertex and the specified vertex.
	 * 
	 * @param vertex - the vertex at the end of the new edge.
	 * @return - true if connection was made.
	 */
	public boolean connect(Vertex<K, V> vertex) {
		Edge<K, V> newEdge = new Edge<K, V>(this, vertex);
		if (!outEdges.contains(newEdge)) {
			outEdges.put(vertex.getKey(), newEdge);
			// add edge to list of in edges.
			vertex.addInEdge(newEdge);
			return true;
		}
		return false;
	}

	/**
	 * adds an in edge to this vertex.
	 * 
	 * @param newInEdge - the edge that this vertex is receiving.
	 */
	public void addInEdge(Edge<K, V> newInEdge) {
		inEdges.put(newInEdge.getStart().getKey(), newInEdge);
	}

	/**
	 * gets the in edge that starts at the specific key.
	 * 
	 * @param key - the key that is the starting node for a given in edge.
	 * @return the Edge with the given starting key, null if it cannot find it.
	 */
	public Edge<K, V> getInEdge(K key) {
		return inEdges.get(key);
	}

	/**
	 * removes the edge with the specific ending key starting at this vertex.
	 * 
	 * @param key - the key of the edge endpoint.
	 * @return true if successful, false otherwise.
	 */
	public boolean removeInEdge(K key) {
		if (inEdges.remove(key) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * created a weighted connection, or updates the weights if the connection
	 * already exists.
	 * 
	 * @param vertex - the ending vertex
	 * @param weight - the weight of the edge
	 * @return true if connection attempt was successful, false if it failed.
	 */
	public boolean connect(Vertex<K, V> vertex, double weight) {
		Edge<K, V> newEdge = new Edge<K, V>(this, vertex, weight);
		if (vertex == null) {
			return false;
		}
		if (getOutEdge(vertex.getKey()) == null) {
			outEdges.put(vertex.getKey(), newEdge);
			vertex.addInEdge(newEdge);
			return true;
		} else {
			// updates the weight if the edge already exists.
			getOutEdge(vertex.getKey()).setWeight(weight);
			return false;
		}
	}

	/**
	 * removes outEdges that end at the given vertex.
	 * 
	 * @param vertex - the vertex at the endpoint of the edge being removed.
	 * @return
	 */
	public boolean disconnect(Vertex<K, V> vertex) {
		if (outEdges.remove(vertex.getKey()) != null) {
			// remove the in edge from this node:
			vertex.removeInEdge(this.key);
			return true;
		}
		return false;
	}

	/**
	 * disconnects this vertex from the vertex with the given key.
	 * 
	 * @param key - key of the ending vertex being removed.
	 * @return
	 */
	public boolean disconnect(K key) {
		Edge<K, V> currentEdge = outEdges.remove(key);
		if (currentEdge != null) {
			currentEdge.getEnd().removeInEdge(this.key);
			return true;
		}
		return false;
	}

	/**
	 * gets the key of this vertex.
	 * 
	 * @return the key of the vertex.
	 */
	public K getKey() {
		return key;
	}

	/**
	 * gets the value stored in this vertex.
	 * 
	 * @return the value stored in this vertex.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * changes the value of this vertex.
	 * 
	 * @param value - the new value.
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * gets outEdges that end at the given key:
	 * 
	 * @param key - they key of the vertex ending the edge.
	 * @return the edge starting at the current vertex and ending at the vertex with
	 *         the given key, or null if the edge does not exist.
	 */
	public Edge<K, V> getOutEdge(K key) {
		return outEdges.get(key);
	}

	/**
	 * determines if this vertex equals another vertex.
	 * 
	 * @param otherVertex - the other vertex beign compared.
	 * @return true if they are the same.
	 */
	public boolean equals(Vertex<K, V> otherVertex) {
		return otherVertex.getKey().equals(key);
	}

	/**
	 * Determines if this key matches the argument key.
	 * 
	 * @param otherKey - the key of the other vertex.
	 * @return - true if they are the same false otherwise.
	 */
	public boolean equals(K otherKey) {
		return key.equals(otherKey);
	}

	/**
	 * checks if the current vertex is connected to the vertex with the specified
	 * key
	 * 
	 * @param key - the key of the potential ending vertex.
	 * @return true if there is an edge, false otherwise.
	 */
	public boolean hasOutConnectionTo(K key) {
		if (outEdges.get(key) != null) {
			return true;
		}
		return false;
	}

	/**
	 * This checks to see if there is an in connection from the given key.
	 * 
	 * @param key - the key of the starting vertex.
	 * @return true if there is an in connection, false otherwise.
	 */
	public boolean hasInConnectionFrom(K key) {
		if (inEdges.get(key) != null) {
			return true;
		}
		return false;
	}

	/**
	 * gets the out degree of this vertex.
	 * 
	 * @return the our degree
	 */
	public int getOutDegree() {
		return outEdges.size();
	}

	/**
	 * gets the in degree of this vertex
	 * 
	 * @return the in degree
	 */
	public int getInDegree() {
		return inEdges.size();
	}

	/**
	 * gets all outEdges of this vertex. Keys are indexed with the key where the
	 * edge ends.
	 * 
	 * @return all outEdges.
	 */
	public Hashtable<K, Edge<K, V>> getEdges() {
		return outEdges;
	}

	/**
	 * these compare algorithms will sort items in descending order when used in a
	 * priority queue - so strings will be sorted in reverse alphabetical order
	 */

	/**
	 * compares this vertex to another vertex based on key.
	 * 
	 * @param key - the key of the other vertex.
	 * @return -1 if the other vertex is less than this node, 0 if equal, and 1 if
	 *         greater than.
	 */
	public int compareTo(K key) {
		return this.key.compareTo(key);
	}

	/**
	 * compares this vertex to another vertex
	 * 
	 * @param otherVertex - other vertex being compared.
	 * @return -1 if the other vertex is less than this node, 0 if equal, and 1 if
	 *         greater than.
	 */
	public int compareTo(Vertex<K, V> otherVertex) {
		return this.key.compareTo(otherVertex.getKey());
	}

	/**
	 * converts the current vertex to a string.
	 */
	public String toString() {
		return key.toString();
	}

	/**
	 * gets the first unvisited successor found.
	 * 
	 * @return an unvisited successor
	 */
	public Vertex<K, V> getUnvisitedSuccessor() {
		Enumeration<K> keys = outEdges.keys();
		K nextKey = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			if (!outEdges.get(nextKey).getEnd().visited) {
				return outEdges.get(nextKey).getEnd();
			}
		}
		return null;
	}

	/**
	 * sorts the out edges by their weights in increasing order.
	 * 
	 * @return - sorted list of edges.
	 */
	public PriorityQueue<Edge<K, V>> sortOutEdgesByWeight() {
		PriorityQueue<Edge<K, V>> q = new PriorityQueue<Edge<K, V>>(new CompareEdges());
		Enumeration<K> keys = outEdges.keys();
		K nextKey = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			q.add(outEdges.get(nextKey));
		}
		return q;
	}

}
