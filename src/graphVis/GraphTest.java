package graphVis;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class GraphTest {
	Graph<Integer, String> g;

	@Before
	public void setUp() throws Exception {
		this.g = new Graph<Integer, String>();

	}

	@BeforeEach
	public void ResetTable() throws Exception {
		this.g = new Graph<Integer, String>();

	}

	@After
	public void tearDown() throws Exception {

	}

	// TODO use physics to separate the vertexes - make edges like rubber bands

	@Test
	public void Test00_basic_insert_removal() {
		g.add(0, "0");
		g.add(10, "10");
		String val0 = g.get(0);
		String val10 = g.get(10);
		if (val0 == null || val10 == null) {
			fail("Vertexes not inserted properly");
		} else if (!val0.equals("0") || !val10.equals("10")) {
			fail("could not retrieve or insert values");
		}

		g.add(10, "S");
		val10 = g.get(10);
		if (!val0.equals("0") || !val10.equals("S")) {
			fail("Failure to update value");
		}
		boolean removed10 = g.remove(10);
		val10 = g.get(10);
		if (removed10 == false || val10 != null) {
			fail("failure to remove key");
		}
	}

	@Test
	public void Test01_test_basic_connections_and_disconnections() {
		Hashtable<Integer, Edge<Integer, String>> edges = new Hashtable<>();
		g.add(0, "0");
		g.add(10, "10");
		Edge<Integer, String> edgeForward = new Edge<>(g.getVertex(0), g.getVertex(10));
		Edge<Integer, String> edgeReverse = new Edge<>(g.getVertex(10), g.getVertex(0));

		assertEquals(true, g.connect(0, 10));
		edges = g.getEdges();
		if (!edges.get(10).equals(10)) {
			fail("failure to create edge");
		}
		if (!(g.getVertex(0).getOutEdge(10).equals(edgeForward) && g.getVertex(10).getOutEdge(0).equals(edgeReverse))) {
			fail("Edge not formed correctly");
		}

		assertEquals(true, g.disconnect(0, 10));
		if (!(g.getVertex(0).getOutEdge(10) == null && g.getVertex(10).getOutEdge(0).equals(edgeReverse))) {
			fail("forward edge not disconnected correctly");
		}

		assertEquals(true, g.disconnect(10, 0));
		if (!(g.getVertex(0).getOutEdge(10) == null && g.getVertex(10).getOutEdge(0) == null)) {
			fail("Reverse edge not disconnected correctly");
		}

		assertEquals(false, g.disconnect(10, 0));
		assertEquals(true, g.connect(0, 10));
		assertEquals(true, g.disconnectAll(10, 0));
		if (!(g.getVertex(0).getOutEdge(10) == null && g.getVertex(10).getOutEdge(0) == null)) {
			fail("Both sides of undirectional edge not disconnected");
		}

		assertEquals(true, g.directedConnect(0, 10));
		if (!(g.getVertex(0).getOutEdge(10).equals(edgeForward) && g.getVertex(10).getOutEdge(0) == null)) {
			fail("directional edge not formed correctly");
		}
	}

	@Test
	public void Test02_DFT_test() {
		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);

		assertEquals(true, gLocal.directedConnect("A", "B"));
		assertEquals(true, gLocal.directedConnect("A", "C"));
		assertEquals(true, gLocal.directedConnect("A", "D"));
		assertEquals(true, gLocal.directedConnect("C", "E"));
		assertEquals(true, gLocal.directedConnect("D", "F"));
		assertEquals(true, gLocal.directedConnect("F", "C"));
		assertEquals(true, gLocal.isThereAnEdge("A", "B"));

		ArrayList<Vertex<String, Integer>> traversalResults = new ArrayList<>();
		traversalResults = gLocal.depthFirstTraversal("D");
		for (Vertex<String, Integer> elem : traversalResults) {
			returnString = returnString + elem.toString();
			assertEquals(false, elem.visited);
		}
		assertEquals("DFCE", returnString);
		System.out.println(returnString);

		returnString = "";

		traversalResults = gLocal.depthFirstTraversal("C");
		for (Vertex<String, Integer> elem : traversalResults) {
			returnString = returnString + elem.toString();
			assertEquals(false, elem.visited);
		}
		assertEquals("CE", returnString);
		System.out.println(returnString);

		returnString = "";

		traversalResults = gLocal.depthFirstTraversal("B");
		for (Vertex<String, Integer> elem : traversalResults) {
			returnString = returnString + elem.toString();
			assertEquals(false, elem.visited);
		}
		assertEquals("B", returnString);
		System.out.println(returnString);

		returnString = "";

		traversalResults = gLocal.depthFirstTraversal("A");
		for (Vertex<String, Integer> elem : traversalResults) {
			returnString = returnString + elem.toString();
			assertEquals(false, elem.visited);
		}
		assertEquals("ADFCEB", returnString);
		System.out.println(returnString);

		// check that it terminates cycles:
		returnString = "";
		assertEquals(true, gLocal.directedConnect("E", "A"));

		traversalResults = gLocal.depthFirstTraversal("D");
		for (Vertex<String, Integer> elem : traversalResults) {
			returnString = returnString + elem.toString();
			assertEquals(false, elem.visited);
		}
		assertEquals("DFCEAB", returnString);
		System.out.println(returnString);

	}

	@Test
	public void Test03_BFT_test() {
		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		ArrayList<Vertex<String, Integer>> traversalResults = new ArrayList<>();

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);

		assertEquals(true, gLocal.directedConnect("A", "B"));
		assertEquals(true, gLocal.directedConnect("A", "C"));
		assertEquals(true, gLocal.directedConnect("A", "D"));
		assertEquals(true, gLocal.directedConnect("C", "E"));
		assertEquals(true, gLocal.directedConnect("D", "F"));
		assertEquals(true, gLocal.directedConnect("F", "C"));
		assertEquals(true, gLocal.directedConnect("E", "A"));

		traversalResults = gLocal.breadthFirstTraversal("A");
		for (Vertex<String, Integer> elem : traversalResults) {
			returnString = returnString + elem.toString();
			assertEquals(false, elem.visited);
		}
		assertEquals("ADCBFE", returnString);
		System.out.println(returnString);

	}

	@Test
	public void Test04_topological_order_test() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		ArrayList<Vertex<String, Integer>> orderedGraph;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("D", 3);
		gLocal.add("F", 4);
		gLocal.add("G", 5);
		gLocal.add("H", 6);

		assertEquals(true, gLocal.directedConnect("A", "B"));
		assertEquals(true, gLocal.directedConnect("A", "D"));
		assertEquals(true, gLocal.directedConnect("A", "F"));
		assertEquals(true, gLocal.directedConnect("F", "G"));
		assertEquals(true, gLocal.directedConnect("F", "D"));
		assertEquals(true, gLocal.directedConnect("B", "D"));
		assertEquals(true, gLocal.directedConnect("B", "H"));
		assertEquals(true, gLocal.directedConnect("H", "G"));
		assertEquals(true, gLocal.directedConnect("G", "D"));

		orderedGraph = gLocal.topologicalOrdering();
		for (Vertex<String, Integer> elem : orderedGraph) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("ABHFGD", returnString);
		System.out.println();
	}

	/**
	 * This algorithm does not seem to work. If you set B to be very large, the
	 * algorithm will stop between A and B. need to make it consider the smallest
	 * edge weight. It works to some extent, but sometimes it simply doesn't. it is
	 * visiting all edges in increasing order , but if b is too high it will stop
	 * there.
	 */
	@Test
	public void Test05_dijkstras() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		ArrayList<Vertex<String, Integer>> shortestPath;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);

		assertEquals(true, gLocal.directedConnect("A", "B", 4.0));
		assertEquals(true, gLocal.directedConnect("A", "C", 2.0));
		assertEquals(true, gLocal.directedConnect("A", "E", 15.0));
		assertEquals(true, gLocal.directedConnect("C", "D", 5.0));
		assertEquals(true, gLocal.directedConnect("B", "D", 1.0));
		assertEquals(true, gLocal.directedConnect("B", "E", 10.0));
		assertEquals(true, gLocal.directedConnect("D", "E", 3.0));
		assertEquals(true, gLocal.directedConnect("D", "F", 0.0));
		assertEquals(true, gLocal.directedConnect("F", "D", 2.0));
		assertEquals(true, gLocal.directedConnect("F", "H", 4.0));
		assertEquals(true, gLocal.directedConnect("G", "H", 4.0));

		shortestPath = gLocal.dijShortestPath("A");
		for (Vertex<String, Integer> elem : shortestPath) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("ABDFH", returnString);
		System.out.println();

		// change the weight of node B - test the various mechanisms where this should
		// happen:
		gLocal.changeEdgeWeight("A", "B", 14.0);
		if (14.0 != gLocal.getOutEdgeWeight("A", "B")) {
			fail("Weights failed to update");
		}
		assertEquals(false, gLocal.directedConnect("A", "B", 20.0));
		if (20.0 != gLocal.getOutEdgeWeight("A", "B")) {
			fail("Weights failed to update");
		}
		returnString = "";
		shortestPath = gLocal.dijShortestPath("A");
		for (Vertex<String, Integer> elem : shortestPath) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("ACDE", returnString);
		System.out.println();

	}

	@Test
	public void Test06_dijkstras_uniformWeights() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		ArrayList<Vertex<String, Integer>> shortestPath;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);

		assertEquals(true, gLocal.directedConnect("A", "B"));
		assertEquals(true, gLocal.directedConnect("A", "C"));
		assertEquals(true, gLocal.directedConnect("A", "E"));
		assertEquals(true, gLocal.directedConnect("C", "D"));
		assertEquals(true, gLocal.directedConnect("B", "D"));
		assertEquals(true, gLocal.directedConnect("B", "E"));
		assertEquals(true, gLocal.directedConnect("D", "E"));
		assertEquals(true, gLocal.directedConnect("D", "F"));
		assertEquals(true, gLocal.directedConnect("F", "D"));
		assertEquals(true, gLocal.directedConnect("F", "H"));
		assertEquals(true, gLocal.directedConnect("G", "H"));

		shortestPath = gLocal.dijShortestPath("A");
		for (Vertex<String, Integer> elem : shortestPath) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			// assertEquals(false,elem.visited);
		}
		// assertEquals("ABD", returnString);
		System.out.println();
	}

	/**
	 * Check the implementation of a BFS spanning tree
	 */
	@Test
	public void Test07_BFS_Spanning_undirected() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		Graph<String, Integer> gSpan;
		ArrayList<Vertex<String, Integer>> subTreeBFS;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);
		gLocal.add("I", 8);

		assertEquals(true, gLocal.connect("A", "B"));
		assertEquals(true, gLocal.connect("A", "D"));
		assertEquals(true, gLocal.connect("A", "C"));
		assertEquals(true, gLocal.connect("B", "C"));
		assertEquals(true, gLocal.connect("C", "E"));
		assertEquals(true, gLocal.connect("D", "C"));
		assertEquals(true, gLocal.connect("D", "F"));
		assertEquals(true, gLocal.connect("D", "G"));
		assertEquals(true, gLocal.connect("E", "F"));
		assertEquals(true, gLocal.connect("E", "H"));
		assertEquals(true, gLocal.connect("G", "H"));
		assertEquals(true, gLocal.connect("H", "I"));

		gSpan = gLocal.breadthFirstSpanning("A");
		subTreeBFS = gSpan.breadthFirstTraversal("D");
		returnString = "";
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("DGFHI", returnString);
		System.out.println();

		subTreeBFS = gSpan.breadthFirstTraversal("G");
		returnString = "";
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("GHI", returnString);
		System.out.println();

		subTreeBFS = gSpan.breadthFirstTraversal("C");
		returnString = "";
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("CE", returnString);
		System.out.println();

		subTreeBFS = gSpan.breadthFirstTraversal("B");
		returnString = "";
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("B", returnString);
		System.out.println();
	}

	@Test
	public void Test08_DFS_Spanning_undirected() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		Graph<String, Integer> gSpan;
		ArrayList<Vertex<String, Integer>> subTreeBFS;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);
		gLocal.add("I", 8);

		assertEquals(true, gLocal.connect("A", "B"));
		assertEquals(true, gLocal.connect("A", "D"));
		assertEquals(true, gLocal.connect("A", "C"));
		assertEquals(true, gLocal.connect("B", "C"));
		assertEquals(true, gLocal.connect("C", "E"));
		assertEquals(true, gLocal.connect("D", "C"));
		assertEquals(true, gLocal.connect("D", "F"));
		assertEquals(true, gLocal.connect("D", "G"));
		assertEquals(true, gLocal.connect("E", "F"));
		assertEquals(true, gLocal.connect("E", "H"));
		assertEquals(true, gLocal.connect("G", "H"));
		assertEquals(true, gLocal.connect("H", "I"));

		gSpan = gLocal.depthFirstSpanning("A");
		subTreeBFS = gSpan.breadthFirstTraversal("H");
		returnString = "";
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("HIEFCB", returnString);
		System.out.println();

	}

	@Test
	public void Test08_DFS_Spanning_directed_graph() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		Graph<String, Integer> gSpan;
		ArrayList<Vertex<String, Integer>> subTreeBFS;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);
		gLocal.add("I", 8);

		assertEquals(true, gLocal.directedConnect("A", "B"));
		assertEquals(true, gLocal.directedConnect("A", "D"));
		assertEquals(true, gLocal.directedConnect("B", "C"));
		assertEquals(true, gLocal.directedConnect("B", "A"));
		assertEquals(true, gLocal.directedConnect("C", "E"));
		assertEquals(true, gLocal.directedConnect("D", "C"));
		assertEquals(true, gLocal.directedConnect("D", "F"));
		assertEquals(true, gLocal.directedConnect("E", "B"));
		assertEquals(true, gLocal.directedConnect("E", "F"));
		assertEquals(true, gLocal.directedConnect("E", "G"));
		assertEquals(true, gLocal.directedConnect("E", "I"));
		assertEquals(true, gLocal.directedConnect("F", "I"));
		assertEquals(true, gLocal.directedConnect("G", "H"));
		assertEquals(true, gLocal.directedConnect("G", "F"));
		assertEquals(true, gLocal.directedConnect("H", "F"));

		gSpan = gLocal.depthFirstSpanning("A");
		subTreeBFS = gSpan.breadthFirstTraversal("A");
		returnString = "";
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("ADFCIEGBH", returnString);
		System.out.println();

	}

	@Test
	public void Test09_primms() {

		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		Graph<String, Integer> gSpan;
		ArrayList<Vertex<String, Integer>> subTreeBFS;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);
		gLocal.add("I", 8);

		assertEquals(true, gLocal.connect("A", "B", 1.0));
		assertEquals(true, gLocal.connect("A", "D", 2.0));
		assertEquals(true, gLocal.connect("B", "E", 3.0));
		assertEquals(true, gLocal.connect("B", "C", 2.0));
		assertEquals(true, gLocal.connect("C", "F", 5.0));
		assertEquals(true, gLocal.connect("D", "E", 3.0));
		assertEquals(true, gLocal.connect("D", "G", 4.0));
		assertEquals(true, gLocal.connect("E", "H", 4.0));
		assertEquals(true, gLocal.connect("E", "F", 1.0));
		assertEquals(true, gLocal.connect("F", "I", 3.0));
		assertEquals(true, gLocal.connect("H", "I", 6.0));
		assertEquals(true, gLocal.connect("G", "H", 1.0));

		gSpan = gLocal.primms("A");
		subTreeBFS = gSpan.breadthFirstTraversal("E");
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		assertEquals("EFI", returnString);
		System.out.println();
	}

	@Test
	public void Test10_kruskals() {
		/**
		 * the kruskals implementation creates an undirected spanning tree. this is
		 * because it creates a spanning tree that cannot be traversed in a single bfs
		 * if directed connections are used.
		 */
		Graph<String, Integer> gLocal = new Graph<String, Integer>();
		Graph<String, Integer> gSpan;
		ArrayList<Vertex<String, Integer>> subTreeBFS;

		String returnString = "";
		gLocal.add("A", 0);
		gLocal.add("B", 1);
		gLocal.add("C", 2);
		gLocal.add("D", 3);
		gLocal.add("E", 4);
		gLocal.add("F", 5);
		gLocal.add("G", 6);
		gLocal.add("H", 7);
		gLocal.add("I", 8);

		assertEquals(true, gLocal.connect("A", "B", 1.0));
		assertEquals(true, gLocal.connect("A", "D", 2.0));
		assertEquals(true, gLocal.connect("B", "E", 3.0));
		assertEquals(true, gLocal.connect("B", "C", 2.0));
		assertEquals(true, gLocal.connect("C", "F", 5.0));
		assertEquals(true, gLocal.connect("D", "E", 3.0));
		assertEquals(true, gLocal.connect("D", "G", 4.0));
		assertEquals(true, gLocal.connect("E", "H", 4.0));
		assertEquals(true, gLocal.connect("E", "F", 1.0));
		assertEquals(true, gLocal.connect("F", "I", 3.0));
		assertEquals(true, gLocal.connect("H", "I", 6.0));
		assertEquals(true, gLocal.connect("G", "H", 1.0));

		gSpan = gLocal.kruskals("A");
		subTreeBFS = gSpan.breadthFirstTraversal("A");
		for (Vertex<String, Integer> elem : subTreeBFS) {
			returnString = returnString + elem.toString();
			System.out.print(elem);
			assertEquals(false, elem.visited);
		}
		// assertEquals("EFI", returnString);
		System.out.println();
		System.out.println(gSpan.getCost());
		if (17.0 != gSpan.getCost()) {
			fail("minimum tree not found");
		}
	}

}
