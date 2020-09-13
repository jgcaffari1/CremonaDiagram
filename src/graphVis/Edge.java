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

/**
 * class for specifying an edge between two vertexes in the graph
 * 
 * @author joe caffarini
 * @param <K> - the comparable key data type
 * @param <V> - the value data type.
 */
public class Edge<K extends Comparable<K>, V> {
	/**
	 * plan on drawing these as a solid lines.
	 */

	private Vertex<K, V> start;
	private Vertex<K, V> end;
	private double weight;

	/**
	 * creates a new edge wit the start and end vertexes
	 * 
	 * @param start - the starting vertex.
	 * @param end   - the ending vertex.
	 */
	public Edge(Vertex<K, V> start, Vertex<K, V> end) {
		this.start = start;
		this.end = end;
		weight = 1.0;
	}

	/**
	 * creates a new edge wit the start and end vertexes
	 * 
	 * @param start  - the starting vertex.
	 * @param end    - the ending vertex.
	 * @param weight - the weight of the new vertex.
	 */
	public Edge(Vertex<K, V> start, Vertex<K, V> end, double weight) {
		this.start = start;
		this.end = end;
		this.weight = weight;
	}

	/**
	 * checks if this Edge is equal to the other Edge
	 * 
	 * @param otherEdge - the other edge being compared.
	 * @return - true if matching, galse if not matching.
	 */
	public boolean equals(Edge<K, V> otherEdge) {
		if (otherEdge == null) {
			return false;
		}
		return (start.equals(otherEdge.getStart()) && end.equals(otherEdge.getEnd()));
	}

	/**
	 * checks if the given key corresponds to an end vertex in the edge - this is
	 * used during removal.
	 * 
	 * @param key - key of the vertex being checked:
	 * @return true if the key is the endpoint of the edge.
	 */
	public boolean equals(K key) {
		return endsAt(key);
	}

	/**
	 * checks if the current edge ends at the specified key.
	 * 
	 * @param key - specified key.
	 * @return - true if ending at key, false otherwise.
	 */
	public boolean endsAt(K key) {
		return (end.equals(key));
	}

	/**
	 * checks if the current edge starts at the specified key.
	 * 
	 * @param key - specified key.
	 * @return - true if starting at key, false otherwise.
	 */
	public boolean startsAt(K key) {
		return (start.equals(key));
	}

	/**
	 * compares the given key to that of the ending index. Meant to sort Edges based
	 * off of ending node. Going off ending node because edges are already explored
	 * from their starting node first, so sorting based off the starting node would
	 * not accomplish anything.
	 * 
	 * @param key - the key of the ending node being searched for
	 * @return
	 */
	public int compareTo(K key) {
		return this.start.getKey().compareTo(key);
	}

	/**
	 * compares edges based off of the edge weights.
	 * 
	 * @param otherEdge - the other edges being compared
	 * @return
	 */
	public int compareTo(Edge<K, V> otherEdge) {
		return Double.valueOf(this.weight).compareTo(otherEdge.weight);
	}

	/**
	 * gets the starting vertex of this edge.
	 * 
	 * @return the starting vertex of this edge.
	 */
	public Vertex<K, V> getStart() {
		return start;
	}

	/**
	 * gets the ending vertex of this edge.
	 * 
	 * @return - the ending vertex of this edge.
	 */
	public Vertex<K, V> getEnd() {
		return end;
	}

	/**
	 * gets the weight of this edge.
	 * 
	 * @return - the current weight of this edge.
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * sets the weight of the edge.
	 * 
	 * @param weight - the new weight of the edge.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * changed the endpoint of this edge.
	 * 
	 * @param newEnd - the new ending vertex of the edge.
	 */
	public void setEnd(Vertex<K, V> newEnd) {
		end = newEnd;
	}

	/**
	 * changes the starting vertex of the edge
	 * 
	 * @param newStart - the new starting vertex of the edge.
	 */
	public void setStart(Vertex<K, V> newStart) {
		start = newStart;
	}

	/**
	 * converts the edge to a string for printing.
	 */
	public String toString() {
		String returnString = start.toString();

		if (end.hasOutConnectionTo(start.getKey())) {
			// if connection is undirected, then draw a line:
			returnString = returnString + "--";
		} else {
			// if connection is directed, then draw arrow.
			returnString = returnString + " -> ";
		}
		returnString = returnString + end.toString();
		return returnString;
	}

	/**
	 * checks if this edge is undirected by looking that it is mirrored from start
	 * to end and from end to start in the out edges of each vertex. If the edges
	 * have different weights then it is considered a different edge.
	 * 
	 * @return true if edge is undirected, false otherwise.
	 */
	public boolean isUndirected() {
		return (end.hasOutConnectionTo(start.getKey()) && start.hasOutConnectionTo(end.getKey())
				&& end.getOutEdge(start.getKey()).getWeight() == start.getOutEdge(end.getKey()).getWeight());
	}

}
