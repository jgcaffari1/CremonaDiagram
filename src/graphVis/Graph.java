package graphVis;

/**
 * sources: https://javatutorial.net/graphs-java-example NOTE - the searches are called searches
 * because they are really checking that there is apath between two different nodes, which is more
 * advanced than simply checking for an edge between the two.
 * https://docs.oracle.com/middleware/11119/jdev/api-reference-esdk/javax/ide/util/Graph.html -
 * ultimately, need the same methods as in java's implementation - need to make this class iterable
 * - instead of repeating the hash table code. make edges iterable? The hash table implementaion
 * complicates things.
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Stack;

/***
 * graph implementation
 * 
 * @author jgcaf
 * @param <K>
 * @param <V>
 */
public class Graph<K extends Comparable<K>, V> {

	/**
	 * test basic functionality first, then work on adding traversals.
	 */

	private Hashtable<K, Vertex<K, V>> members;
	private double totalCost = 0;

	public Graph() {
		members = new Hashtable<K, Vertex<K, V>>();
	}

	/**
	 * comparator that sorts each vertex based on its to total weight, useful for
	 * spanning trees algorithms.
	 */
	private class CompareWeights implements Comparator<Vertex<K, V>> {
		public int compare(Vertex<K, V> vertex1, Vertex<K, V> vertex2) {
			return vertex1.totalWeight.compareTo(vertex2.totalWeight);
		}
	}

	/**
	 * adds a vertex with the given key value pair to the graph
	 * 
	 * @param key   - the key of the new vertex
	 * @param value - the value stored in the vertex
	 */
	public void add(K key, V value) {
		Vertex<K, V> newMember = new Vertex<K, V>(key, value);
		if (members.get(key) == null) {
			members.put(key, newMember);
		} else {
			members.get(key).setValue(value);
		}
	}

	/**
	 * adds a new vertex to the graph. If the vertex is already in the graph, then
	 * the value is copied.
	 * 
	 * @param vertex
	 */
	public void add(Vertex<K, V> vertex) {
		Vertex<K, V> newMember = new Vertex<K, V>(vertex.getKey(), vertex.getValue());
		if (members.get(newMember.getKey()) == null) {
			members.put(newMember.getKey(), newMember);
		} else {
			members.get(newMember.getKey()).setValue(vertex.getValue());
		}
	}

	/**
	 * gets the total cost of the current graph. Useful for spanning trees
	 * algorithms.
	 * 
	 * @return
	 */
	public double getCost() {
		return totalCost;
	}

	/**
	 * sets the total cost of the edges in the current graph.
	 * 
	 * @param cost
	 */
	public void setCost(double cost) {
		totalCost = cost;
	}

	/**
	 * removes the specified vertex, and all edges ending at it.
	 * 
	 * @param key - the key of the specified vertex.
	 * @return - true if successful, false if item was not found or removed.
	 */
	public boolean remove(K key) {
		if (members.get(key) == null) {
			return false;
		}
		PriorityQueue<Vertex<K, V>> q = sortVertexes();
		while (q.size() > 0) {
			Vertex<K, V> c = q.poll();
			// completely remove all edges to and from this vertex:
			if (isThereAnEdge(c.getKey(), key)) {
				disconnectAll(c.getKey(), key);
			}
		}
		// delete the vertex after all its edges are deleted:
		if (members.remove(key) == null) {
			return false;
		}
		return true;
	}

	/**
	 * removes edges between the two keys:
	 * 
	 * @param key - key of the ending vertex being removed.
	 * @return
	 */
	public boolean disconnect(K startKey, K endKey) {
		return members.get(startKey).disconnect(endKey);
	}

	/**
	 * removes edges between the two keys:
	 * 
	 * @param key - key of the ending vertex being removed.
	 * @return
	 */
	public boolean disconnectAll(K startKey, K endKey) {
		return (members.get(startKey).disconnect(endKey) && members.get(endKey).disconnect(startKey));
	}

	/**
	 * Adds the directed edge from the start vertex to the end vertex
	 * 
	 * @param startKey - key for the starting node.
	 * @param endKey   - key for the ending node.
	 * @return - false if failure to add edge - meaning the nodes are missing or
	 *         edge already exists.
	 */
	public boolean directedConnect(K startKey, K endKey) {
		return members.get(startKey).connect(members.get(endKey));

	}

	/**
	 * Adds the directed edge from the start vertex to the end vertex. It also
	 * assigns a non default weight to the edge.
	 * 
	 * @param startKey - key for the starting node.
	 * @param endKey   - key for the ending node.
	 * @param weight   - the weight of the edge
	 * @return - false if failure to add edge - meaning the nodes are missing or
	 *         edge already exists.
	 */
	public boolean directedConnect(K startKey, K endKey, double weight) {
		if (startKey == null || endKey == null || members.get(startKey) == null) {
			return false;
		}
		return members.get(startKey).connect(members.get(endKey), weight);
	}

	/**
	 * changs the weight of a given edge.
	 * 
	 * @param startKey  - the key of the starting vertex of the edge
	 * @param endKey    - key of the ending vertex of the edge.
	 * @param newWeight - the new weight of the specified edge.
	 */
	public void changeEdgeWeight(K startKey, K endKey, double newWeight) {
		members.get(startKey).getOutEdge(endKey).setWeight(newWeight);
		members.get(endKey).getInEdge(startKey).setWeight(newWeight);
	}

	/**
	 * gets the weight of the specified edge
	 * 
	 * @param startKey - the key of the starting vertex of the edge
	 * @param endKey   - key of the ending vertex of the edge.
	 * @return - the weight of the specified edge.
	 */
	public double getOutEdgeWeight(K startKey, K endKey) {
		return members.get(startKey).getOutEdge(endKey).getWeight();
	}

	/**
	 * this creates an undirected connection between the two nodes.
	 * 
	 * @param startKey - key for the starting node.
	 * @param endKey   - key for the ending node.
	 * @return - false if failure to add both edges - meaning the vertexes are
	 *         missing or edges already exist.
	 */
	public boolean connect(K startKey, K endKey) {
		if (members.get(startKey) == null || members.get(endKey) == null) {
			return false;
		}
		boolean forward = members.get(startKey).connect(members.get(endKey));
		boolean reverse = members.get(endKey).connect(members.get(startKey));
		return (forward && reverse);
	}

	/**
	 * this creates an undirected connection between the two nodes.
	 * 
	 * @param startKey - key for the starting node.
	 * @param endKey   - key for the ending node.
	 * @param weight   - the weight of the edge.
	 * @return - false if failure to add both edges - meaning the vertexes are
	 *         missing or edges already exist.
	 */
	public boolean connect(K startKey, K endKey, double weight) {
		if (members.get(startKey) == null || members.get(endKey) == null) {
			return false;
		}
		boolean forward = members.get(startKey).connect(members.get(endKey), weight);
		boolean reverse = members.get(endKey).connect(members.get(startKey), weight);
		return (forward && reverse);
	}

	/**
	 * gets the number of vertexes in the graph
	 * 
	 * @return the number of vertexes in the graph
	 */
	public int getNumberOfVertexes() {
		return members.size();
	}

	/**
	 * gets the vertex with the given key
	 * 
	 * @param key - the key of the vertex being searched for
	 * @return the vertex with the matching key.
	 */
	public V get(K key) {
		if (members.containsKey(key)) {
			return members.get(key).getValue();
		} else {
			return null;
		}
	}

	/**
	 * gets the specified vertex from the graph.
	 * 
	 * @param key - the key of the overlap vertex.
	 * @return the vertex with the specified keys,
	 */
	public Vertex<K, V> getVertex(K key) {
		if (members.containsKey(key)) {
			return members.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Gets all edges in the graph - meant for testing purposes only.
	 * 
	 * @return an object containing all edges in the graph.
	 */
	public Hashtable<K, Edge<K, V>> getEdges() {
		Hashtable<K, Edge<K, V>> edges = new Hashtable<>();
		Enumeration<K> keys = members.keys();
		K key = null;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			edges.putAll(members.get(key).getEdges());
		}
		return edges;
	}

	/**
	 * gets and sorts all edges in a graph.
	 * 
	 * @param startKey - the key of the vertex to start the graph
	 * @return a sorted queue of edges.
	 */
	public PriorityQueue<Edge<K, V>> getEdges(K startKey) {
		Enumeration<K> keys = members.keys();
		K nextKey = null;
		// initialize priority queue of edges:
		if (members.get(startKey) == null) {
			return new PriorityQueue<Edge<K, V>>();
		}
		PriorityQueue<Edge<K, V>> edges = members.get(startKey).sortOutEdgesByWeight();
		// members.get(startKey).visited = true;
		// add all edges to the same priority queue:
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			edges.addAll(members.get(nextKey).sortOutEdgesByWeight());
		}

		return edges;
	}

	/**
	 * perform a depth first traversal of the given graph.
	 * 
	 * @param startKey - the key where the traversal is starting.
	 * @return the list of vertexes from the dft.
	 */
	public ArrayList<Vertex<K, V>> depthFirstTraversal(K startKey) {
		// get the vertex with the respective key:

		ArrayList<Vertex<K, V>> traversal = new ArrayList<>();
		dftHelper(startKey, traversal);

		// iterate through traversal list and reset visited status:
		for (Vertex<K, V> vertex : traversal) {
			vertex.visited = false;
		}

		return traversal;
	}

	/**
	 * sorts vertexes in increasing order by total weight.
	 * 
	 * @return - a soted list of vertexes.
	 */
	public PriorityQueue<Vertex<K, V>> sortVertexes() {
		PriorityQueue<Vertex<K, V>> q = new PriorityQueue<>(new CompareWeights());
		Enumeration<K> keys = members.keys();
		K nextKey = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			q.add(members.get(nextKey));
		}
		return q;
	}

	/**
	 * performs the work for the depth first traversal.
	 * 
	 * @param key       - the key of the vertex being visited.
	 * @param traversal - the list containing the depth first traversal.
	 */
	private void dftHelper(K key, ArrayList<Vertex<K, V>> traversal) {
		Vertex<K, V> currentVertex = members.get(key);
		// if the graph is empty or the vertex has already been visited then terminate
		// program.
		if (currentVertex == null || currentVertex.visited == true) {
			return;
		}

		// add current vertex to traversal list if it hasn't already been visited:
		traversal.add(currentVertex);
		// mark vertex as visited:
		currentVertex.visited = true;

		// get the list of edges originating from this vertex:
		Hashtable<K, Edge<K, V>> edgesOfCurrentVertex = currentVertex.getEdges();
		// build iterator for the edges:
		Enumeration<K> keys = edgesOfCurrentVertex.keys();
		K nextKey = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			if (!edgesOfCurrentVertex.get(nextKey).getEnd().visited) {
				// if the ending node has not been visited, then move to that vertex:
				dftHelper(edgesOfCurrentVertex.get(nextKey).getEnd().getKey(), traversal);
			}
		}
	}

	/**
	 * checks if an edge exists between the specified vertexes
	 * 
	 * @param startKey - key of the starting vertex
	 * @param endKey   - key of the ending vertex
	 * @return true if an edge exists.
	 */
	public boolean isThereAnEdge(K startKey, K endKey) {
		return (members.get(startKey).hasOutConnectionTo(endKey));
	}

	/**
	 * perform a breadth first traversal of the given graph.
	 * 
	 * @param startKey - the key where the traversal is starting.
	 * @return the list of vertexes from the bft, returns null if the starting
	 *         vertex is not in the graph.
	 */
	public ArrayList<Vertex<K, V>> breadthFirstTraversal(K startKey) {
		// get the vertex with the respective key:
		Vertex<K, V> currentVertex = members.get(startKey);
		Vertex<K, V> tmp = null;
		Hashtable<K, Edge<K, V>> edgesOfCurrentVertex;
		Enumeration<K> keys;
		K nextKey = null;

		// if current vertex is not in the graph, then return null.
		if (currentVertex == null) {
			return null;
		}

		ArrayList<Vertex<K, V>> queue = new ArrayList<>();
		ArrayList<Vertex<K, V>> traversal = new ArrayList<>();
		// add initial vertex to the queue:
		currentVertex.visited = true;
		traversal.add(currentVertex);
		queue.add(currentVertex);
		while (0 < queue.size()) {
			// dequeue the first element:
			currentVertex = queue.remove(0);
			// get the list of edges originating from this vertex:
			edgesOfCurrentVertex = currentVertex.getEdges();
			// build iterator for the edges:
			keys = edgesOfCurrentVertex.keys();
			nextKey = null;
			// add each unvisited successor of the current node to the queue:
			while (keys.hasMoreElements()) {
				nextKey = keys.nextElement();
				if (!edgesOfCurrentVertex.get(nextKey).getEnd().visited) {
					tmp = edgesOfCurrentVertex.get(nextKey).getEnd();
					tmp.visited = true;
					queue.add(tmp);
					traversal.add(tmp);
				}
			}
		}
		// iterate through traversal list and reset visited status:
		for (Vertex<K, V> vertex : traversal) {
			vertex.visited = false;
		}
		return traversal;
	}

	/**
	 * topologically sorts the given graph this algorithm is from lecture26 for
	 * cs400
	 * 
	 * @return array containing the topological sorted vertexes.
	 */
	public ArrayList<Vertex<K, V>> topologicalOrdering() {
		ArrayList<Vertex<K, V>> topologicalOrdering = new ArrayList<>();
		Integer NUM = members.size();
		Vertex<K, V>[] ordered = new Vertex[NUM];
		Stack<Vertex<K, V>> stack = new Stack<>();
		Vertex<K, V> c = null;
		Vertex<K, V> unvisitedSuccessor = null;
		Enumeration<K> keys = members.keys();
		K key = null;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			if (members.get(key).getInDegree() == 0) {
				members.get(key).visited = true;
				stack.add(members.get(key));
			}
		}
		while (!stack.isEmpty()) {
			c = stack.peek();
			unvisitedSuccessor = c.getUnvisitedSuccessor();
			// if current node has no unvisited successors, then remove it from the stack
			// and add it to the ordered list:
			if (unvisitedSuccessor == null) {
				c = stack.pop();
				ordered[NUM - 1] = c;
				NUM--;

			} else {
				unvisitedSuccessor.visited = true;
				stack.add(unvisitedSuccessor);
			}
		}

		// iterate through traversal list and reset visited status:
		for (int i = 0; i < ordered.length; i++) {
			ordered[i].visited = false;
			topologicalOrdering.add(ordered[i]);
		}
		return topologicalOrdering;
	}

	/**
	 * perform a breadth first spanning of the given graph.
	 * 
	 * @param startKey - the key where the traversal is starting.
	 * @return The spanning graph from the starting node
	 */
	public Graph<K, V> breadthFirstSpanning(K startKey) {
		// get the vertex with the respective key:
		Vertex<K, V> currentVertex = members.get(startKey);
		Vertex<K, V> tmp = null;
		Graph<K, V> g = new Graph<K, V>();
		Hashtable<K, Edge<K, V>> edgesOfCurrentVertex;
		Enumeration<K> keys;
		K nextKey = null;

		// if current vertex is not in the graph, then return null.
		if (currentVertex == null) {
			return null;
		}

		ArrayList<Vertex<K, V>> queue = new ArrayList<>();
		ArrayList<Vertex<K, V>> traversal = new ArrayList<>();
		// add initial vertex to the queue:
		currentVertex.visited = true;
		traversal.add(currentVertex);
		// add first node to the graph:
		g.add(currentVertex);
		queue.add(currentVertex);
		while (0 < queue.size()) {
			// dequeue the first element:
			currentVertex = queue.remove(0);
			// get the list of edges originating from this vertex:
			edgesOfCurrentVertex = currentVertex.getEdges();
			// build iterator for the edges:
			keys = edgesOfCurrentVertex.keys();
			nextKey = null;
			// add each unvisited successor of the current node to the queue:
			while (keys.hasMoreElements()) {
				nextKey = keys.nextElement();
				if (!edgesOfCurrentVertex.get(nextKey).getEnd().visited) {
					tmp = edgesOfCurrentVertex.get(nextKey).getEnd();
					// add new node to the graph:
					g.add(tmp);
					// form directed connection between the current node and the next:
					g.directedConnect(currentVertex.getKey(), tmp.getKey(),
							edgesOfCurrentVertex.get(nextKey).getWeight());
					tmp.visited = true;
					queue.add(tmp);
					traversal.add(tmp);
				}
			}
		}
		// iterate through traversal list and reset visited status:
		resetTraversalVariables();
		return g;
	}

	/**
	 * Dijkstras algorithm for finding the shortest path:
	 * 
	 * @param startingKey - the key to start the search:
	 * @return the list containing the vertexes on the shortest path.
	 */
	public ArrayList<Vertex<K, V>> dijShortestPath(K startingKey) {
		ArrayList<Vertex<K, V>> shortestPath = new ArrayList<>();
		PriorityQueue<Vertex<K, V>> q = new PriorityQueue<>(new CompareWeights());
		Vertex<K, V> start = members.get(startingKey);
		Vertex<K, V> c = null;
		Vertex<K, V> s = null;
		Edge<K, V> currentEdge;
		PriorityQueue<Edge<K, V>> edges;
		start.totalWeight = 0.0;
		q.add(start);
		// for each vertex
		while (!q.isEmpty()) {
			c = q.remove();
			// mark c as visited
			c.visited = true;
			// build iterator for the edges:
			edges = c.sortOutEdgesByWeight();
			// evaluate the
			while (!edges.isEmpty()) {
				currentEdge = edges.remove();
				s = currentEdge.getEnd();
				// if the next vertex has not been visited, and the cost to move to that node is
				// less, then update the weight:
				if (!s.visited || (s.totalWeight > (c.totalWeight + currentEdge.getWeight()))) {
					s.totalWeight = c.totalWeight + currentEdge.getWeight();
					// store pred
					s.predecessor = c;
					// add successor node to queue.
					q.add(s);
				}
			}

		}
		// work backwards through the list to get the shortest path
		// and reset the variables.
		while (s != null) {
			shortestPath.add(0, s);
			s = s.predecessor;
		}
		// reset traversal variables for all nodes - if this step is not done, then the
		// search results will be the same every time this method is
		// called for this graph, so long as all the vertexes are still present.
		resetTraversalVariables();
		return shortestPath;
	}

	/**
	 * resets the state variables for all vertexes for dijkstras and other
	 * traversal/ search algorithms
	 */
	private void resetTraversalVariables() {
		Enumeration<K> keys = members.keys();
		Vertex<K, V> c;
		K nextKey = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			c = members.get(nextKey);
			c.visited = false;
			c.totalWeight = (double) Integer.MAX_VALUE;
			c.predecessor = null;
		}
	}

	/**
	 * generate a depth first spanning graph of the current graph structure.
	 * 
	 * @param startKey - the key of the vertex from which to start spanning.
	 * @return - the depth first spanning tree.
	 */
	public Graph<K, V> depthFirstSpanning(K startKey) {
		// get the vertex with the respective key:
		Graph<K, V> g = new Graph<K, V>();
		ArrayList<Vertex<K, V>> traversal = new ArrayList<>();
		dfSpanningHelper(startKey, traversal, g);

		// iterate through traversal list and reset visited status:
		for (Vertex<K, V> vertex : traversal) {
			vertex.visited = false;
		}

		return g;
	}

	/**
	 * helper function for depth first spanning
	 * 
	 * @param key       - key of the current vertex.
	 * @param traversal - the list containg all nodes in the traversal.
	 * @param gMain     - the new graph containing the spanning tree.
	 */
	private void dfSpanningHelper(K key, ArrayList<Vertex<K, V>> traversal, Graph<K, V> g) {
		Vertex<K, V> currentVertex = members.get(key);
		// if the graph is empty or the vertex has already been visited then terminate
		// program.
		if (currentVertex == null || currentVertex.visited == true) {
			return;
		}

		// add current vertex to traversal list if it hasn't already been visited:
		traversal.add(currentVertex);
		g.add(currentVertex);
		// mark vertex as visited:
		currentVertex.visited = true;

		// get the list of edges originating from this vertex:
		Hashtable<K, Edge<K, V>> edgesOfCurrentVertex = currentVertex.getEdges();
		// build iterator for the edges:
		Enumeration<K> keys = edgesOfCurrentVertex.keys();
		K nextKey = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			if (!edgesOfCurrentVertex.get(nextKey).getEnd().visited) {
				// if the ending node has not been visited, then move to that vertex:
				dfSpanningHelper(edgesOfCurrentVertex.get(nextKey).getEnd().getKey(), traversal, g);
				// add edge to graph:
				g.directedConnect(currentVertex.getKey(), edgesOfCurrentVertex.get(nextKey).getEnd().getKey(),
						edgesOfCurrentVertex.get(nextKey).getWeight());
			}
		}
	}

	/**
	 * Primms Minimum Spanning Tree algorithm
	 * 
	 * @param startKey - the key to start at
	 * @return a graph containing the minimum spanning tree.
	 */
	public Graph<K, V> primms(K startKey) {
		PriorityQueue<Vertex<K, V>> q = new PriorityQueue<>(new CompareWeights());
		Vertex<K, V> start = members.get(startKey);
		Graph<K, V> g = new Graph<K, V>();
		Vertex<K, V> c = null;
		Vertex<K, V> s = null;
		Edge<K, V> currentEdge;
		PriorityQueue<Edge<K, V>> edges;
		start.totalWeight = 0.0;
		q.add(start);
		while (!q.isEmpty()) {
			c = q.remove();
			// NOTE - this algorithm does not need to mark items as visited, because it
			// sorts both the edges and the vertexes in priority queues based
			// on the current weight and we have the current weights maxed out for unvisited
			// nodes.
			// add current vertex to graph:
			g.add(c);
			// if the current node has a predecessor, then make a connection from it in the
			// spanning tree:
			if (c.predecessor != null) {
				g.directedConnect(c.predecessor.getKey(), c.getKey(), c.getInEdge(c.predecessor.getKey()).getWeight());
			}
			// build iterator for the edges:
			edges = c.sortOutEdgesByWeight();
			// evaluate the
			while (!edges.isEmpty()) {
				currentEdge = edges.remove();
				s = currentEdge.getEnd();
				// if the next vertex has not been visited, and the cost to move to that node is
				// less, then update the weight:
				if ((s.totalWeight > (c.totalWeight + currentEdge.getWeight()))) {
					// update successor's total weight:
					s.totalWeight = c.totalWeight + currentEdge.getWeight();
					// store predecessor:
					s.predecessor = c;
					// add successor node to priority queue:
					q.add(s);
				}
			}

		}
		// iterate through traversal list and reset visited status:
		resetTraversalVariables();
		return g;
	}

	/**
	 * forms a minimum spanning tree with kruskal's algorithm
	 * 
	 * @param startKey - the key to start at
	 * @return a graph containing the minimum spanning tree. implementation modified
	 *         from:
	 *         https://ravindraranwala.blogspot.com/2019/12/minimum-spanning-trees-kruskals_8.html
	 *         this generates a fully connected minimum spanning tree, however, the
	 *         directionality of the connections makes it so that not all edges
	 *         appear during a breadth first traversal.
	 */
	public Graph<K, V> kruskals(K startKey) {
		// this implementation produces an min spanning tree of correct length, but the
		// edges in the subtrees are opposing directions so that they do not all appear
		// in a bfs.
		// this uses a linked list to connect separate spanning trees before adding them
		// to the graph. The sets are not set structures, but are instead linked lists
		// Consumer to display a number
		ArrayList<Edge<K, V>> traveledEdges = new ArrayList<>();
		Edge<K, V> c = null;
		// initialize graph:
		Graph<K, V> g = new Graph<>();
		// sort edges in increasing order by weight:
		PriorityQueue<Edge<K, V>> edges = getEdges(startKey);
		// initialize traversal variables:
		makeSet();
		double totalCost = 0.0;
		while (!edges.isEmpty()) {
			c = edges.remove();
			// check if this edge is part of a larger subtree that contains both vertexes in
			// the edge::
			if (!findSet(c.getStart()).equals(findSet(c.getEnd()))) {
				totalCost = totalCost + c.getWeight();
				// if not, then connect the vertexes:
				g.add(c.getStart());
				g.add(c.getEnd());
				traveledEdges.add(c);
				g.connect(c.getStart().getKey(), c.getEnd().getKey(), c.getWeight());
				g.setCost(totalCost);
				union(c.getStart(), c.getEnd());
			}
		}
		resetTraversalVariables();
		return g;
	}

	/**
	 * finds the start of a subtree - it should iterate backwards in reverse weight
	 * order.
	 * 
	 * @param u - the current vertex being searched.
	 * @return - the root of the subtre containing the vertex.
	 */
	private Vertex<K, V> findSet(Vertex<K, V> u) {
		if (u != u.predecessor) {
			u.predecessor = findSet(u.predecessor);
		}
		return u.predecessor;

	}

	/**
	 * Combines the two sets of vertexes into a larger subtree
	 * 
	 * @param u - the starting vertex of an edge.
	 * @param v - the ending vertex of an edge.
	 */
	private void link(Vertex<K, V> u, Vertex<K, V> v) {
		if (u.totalWeight > v.totalWeight)
			v.predecessor = u;
		else {
			u.predecessor = v;
			if (u.totalWeight == v.totalWeight)
				v.totalWeight += 1;
		}
	}

	/**
	 * initializes the sets for kruskals. All vertexes start as being sets with
	 * themselves, i.e they are their own pred.
	 */
	private void makeSet() {
		// build iterator for the edges:
		Hashtable<K, Vertex<K, V>> clone = (Hashtable<K, Vertex<K, V>>) members.clone();
		Enumeration<K> keys = clone.keys();
		K nextKey = null;
		Vertex<K, V> u = null;
		while (keys.hasMoreElements()) {
			nextKey = keys.nextElement();
			u = clone.remove(nextKey);
			u.predecessor = u;
			u.totalWeight = 0.0;
		}
	}

	/**
	 * combines the roots of the two subtrees
	 * 
	 * @param u - the subtree containing the start vertex of the new edge
	 * @param v - the subtree containing the end vertex of the new edge.
	 */
	private void union(Vertex<K, V> u, Vertex<K, V> v) {
		link(findSet(u), findSet(v));
	}

	/**
	 * gets the number of vertexes in the graph
	 * 
	 * @return the number of vertexes.
	 */
	public int size() {
		return members.size();
	}

}
