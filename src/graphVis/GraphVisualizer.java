/**
 * graph visualizer by joe caffarini 8/2020 - quarantine is still happening.  
 */

package graphVis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import controlP5.ControlP5;
import controlP5.Textarea;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * graph visualizer class - creates a gui for displaying and interacting with
 * the graph data. structure.
 * 
 * @author jgcaf
 */
public class GraphVisualizer extends PApplet {
	final static int MAX_X = 600;
	final static int GUI_X = 100;
	final static int MAX_Y = 600;
	final static int BLUE_COLOR_FACTOR = 10;
	final static int RED_COLOR_FACTOR = 10;
	final static int GREEN_COLOR_FACTOR = 5;
	final int SPEED = 100;
	final double ARROW_SIZE = 10;
	private int numberOfMovers;
	Vector centroid;
	Integer anchorKey = null;
	boolean mouseClicked = false;
	boolean mousePresent = false;
	boolean displaySpanning = false;
	boolean reset = false;
	Mover mouseOver;
	int myColorBackground = color(255, 255, 255);
	ControlP5 cp5;
	String textValue = "";
	PImage bg;
	String[] textTargets;
	List<String> buttonList;

	ArrayList<Vertex<Integer, Mover>> dijShortestPath;
	ArrayList<Integer> startVertexes;
	ArrayList<Integer> endVertexes;
	ArrayList<Mover> flaggedForRemoval;
	PriorityQueue<Mover> selectedMovers;
	Graph<Integer, Mover> gMain;

	Mover[] savedMovers;
	Graph<Integer, Mover> gSpanning;
	Graph<Integer, Mover> gLast;
	Textarea keyBoardControlDisplay;

	public GraphVisualizer() {
		// create a priority queue that will sort selected vertexes from closest to
		// farthest from mouse.
		initializeStructures();
		numberOfMovers = 1;
		textTargets = new String[] { "start", "end" };
		buttonList = Arrays.asList("DFS", "BFS", "Primm's", "Kruskal's");
	}

	/**
	 * setup the canvas:
	 */
	@Override
	public void setup() {
		// only build the gui and initialize structures upon startup, then just reset
		// items:
		if (!reset) {
			cp5 = new ControlP5(this);
			bg = loadImage("lineArt2.jpg");
			initializeStructures();
			frameRate(SPEED);
			background(myColorBackground);
			resetButtons();
		} else {
			for (int i : startVertexes) {
				gMain.remove(i);
			}
			while (startVertexes.size() > 0) {
				endVertexes.remove(startVertexes.remove(0));
			}
			while (flaggedForRemoval.size() > 0) {
				flaggedForRemoval.remove(0);
			}
			while (selectedMovers.size() > 0) {
				selectedMovers.poll();
			}
			numberOfMovers = 1;
		}

		keyBoardControlDisplay = cp5.addTextarea("txt").setPosition(0, 350).setSize(100, 100)
				.setFont(createFont("arial", 10)).setLineHeight(14).setColor(color(0))
				.setColorBackground(color(255, 255)).setColorForeground(color(255, 100));
		;
		keyBoardControlDisplay.setText("Keyboard Controls: \n" + " q - exit\n" + " r - reset\n" + " n - add new ball\n"
				+ " p - pin/freeze all items\n" + " d - unfreeze all items\n" + " e - erase vertex\n"
				+ " z - unselect all vertexes\n" + " a - add undirected edge\n"
				+ " s - add directed edge start-> finish\n" + " t - delete edge\n"
				+ " y - delete directed edge start -> finish\n" + " l - reset spanning tree/ display main graph\n"
				+ " k - turn sound on\n" + " o - turn sound off\n");

	}

	/**
	 * initialize the data structures for the application:
	 */
	private void initializeStructures() {
		// initialize distance tracking queues:
		selectedMovers = new PriorityQueue<Mover>(new CompareDistance());
		gMain = new Graph<>();
		startVertexes = new ArrayList<>();
		endVertexes = new ArrayList<>();
		savedMovers = new Mover[2];
		numberOfMovers = 1;
		reset = false;
		anchorKey = null;
		mouseClicked = false;
		mousePresent = false;
		displaySpanning = false;
		mouseOver = null;
		flaggedForRemoval = new ArrayList<>();
		textValue = "";
		gSpanning = null;
		gLast = null;
	}

	/**
	 * main method for starting applet.
	 * 
	 * @param args
	 */
	public static void main(java.lang.String[] args) {
		PApplet.main("graphVis.GraphVisualizer" );
	}

	/**
	 * set initial settings:
	 */
	@Override
	public void settings() {
		size(GUI_X + MAX_X, MAX_Y);

	}

	/**
	 * draw application.
	 */
	@Override
	public void draw() {
		background(myColorBackground);
		image(bg, 0, 0, GUI_X, MAX_Y);

		// display the spanning tree if one was created:
		if (displaySpanning) {
			// apply graph forces:
			graphForces(gSpanning);
			displayMovers(gSpanning);
		} else {// otherwise display the main graph:
			graphForces(gMain);
			displayMovers(gMain);
		}
	}

	/**
	 * comparator for sorting movers based off of distance from mouse.
	 */
	class CompareDistance implements Comparator<Mover> {
		public int compare(Mover m1, Mover m2) {
			return m1.distanceFromMouse.compareTo(m2.distanceFromMouse);
		}
	}

	/**
	 * Forms or Changes an undirected edge between the vertexes with the specified
	 * id numbers
	 * 
	 * @param start  - the id of the starting vertex
	 * @param end    - the id of the ending vertex.
	 * @param weight - the weight of the edge.
	 */
	public void createEdge(int start, int end, double weight) {
		if (start < 0 || end < 0) {
			return;
		}
		// add weight to matrix only if the original graph is being displayed:
		if (weight != 0 && !displaySpanning) {
			gMain.connect(start, end, weight);
		}
	}

	/**
	 * creates a directed edge from start to end with the specified weight.
	 * 
	 * @param start  - id of the starting vertex
	 * @param end    - id of the ending vertex
	 * @param weight - the weight of the edge.
	 */
	public void createDirectedEdge(int start, int end, double weight) {
		if (start < 0 || end < 0) {
			return;
		}
		// add weight to matrix
		if (weight != 0 && !displaySpanning) {
			gMain.directedConnect(start, end, weight);
		}
	}

	/**
	 * removes the edge betwen the specified vertexes
	 * 
	 * @param start - the id of the starting vertex
	 * @param end   - the id of the ending vertex.
	 */
	public void removeEdge(int start, int end) {
		if (start < 0 || end < 0 || gMain.getVertex(start) == null) {
			return;
		}
		if (!displaySpanning) {
			gMain.getVertex(start).disconnect(gMain.getVertex(end));
		}
	}

	/**
	 * removes the vertex from the graph
	 * 
	 * @param target - the id of the vertex being removed.
	 */
	private void removeVertex(int target) {
		// reset all edges in the adjacency matrix:
		if (!displaySpanning) {
			Integer idToRemove = target;
			if (gMain.get(idToRemove) != null) {
				// remove all edges ending at this vertex:
				clearSaved();
				// turn sound off for mover:
				gMain.get(idToRemove).soundOn = false;
				// remove sounds from this mover
				gMain.get(idToRemove).removeSoundsFromEdgesStartingHere();
				// remove the sounds of all edges connecting to this vertex:
				for (int i : startVertexes) {
					gMain.get(i).removeSoundsEndingAt(idToRemove);
				}

			}
			startVertexes.remove(idToRemove);
			endVertexes.remove(idToRemove);
			selectedMovers.remove(gMain.get(idToRemove));
			gMain.remove(idToRemove);
		}
	}

	/**
	 * adds a new vertex to the graph and updates the adjacency matrix.
	 */
	public void addNewVertex() {
		// add a new vertex with a key next in the id sequence:
		if (!displaySpanning) {
			Integer idToAdd = numberOfMovers;
			gMain.add(idToAdd, new Mover(idToAdd, this));
			startVertexes.add(idToAdd);
			endVertexes.add(idToAdd);
			numberOfMovers++;

		}
	}

	/**
	 * displays all of the items in the graph.
	 */
	private void displayMovers(Graph<Integer, Mover> g) {
		for (Integer i : startVertexes) {
			if (g.get(i) != null) {
				g.get(i).display();
				textAlign(CENTER, CENTER);
				textSize((int) g.get(i).radius);
				fill(0);
				text(Integer.toString(i), (float) g.get(i).location.x, (float) g.get(i).location.y);
			}
		}
	}

	/**
	 * plots the nodes and edges of the graph, with friction slowing all edges and
	 * with
	 */
	private void graphForces(Graph<Integer, Mover> g) {
		Edge<Integer, Mover> c;
		Double weight;
		// draw all edges:
		drawEdges(g);
		// apply physics to each edge:
		for (int i : startVertexes) {
			if (g.get(i) != null) {
				for (int j : endVertexes) {
					if (i != j) {
						// apply friction to each vertex:
						g.get(i).applyDragForce(10, 0.2);
						// keep objects from sticking by adding a repulsive force between them:
						g.get(i).push(g.get(j));
						c = g.getVertex(i).getOutEdge(j);

						if (mouseOverEdge(c) && mousePressed && mouseButton == RIGHT) {
							g.get(i).pushSpring(g.get(j), 1000);
						}
						if (c != null) {
							// apply the spring force from the edge weights between these objects:
							// using the weights as the spring constants:
							weight = c.getWeight();
							g.get(j).springForce(g.get(i), weight, 10);
						}
						// add collisions between movers:
						g.get(i).collision(g.get(j), (float) 0.9);
						// draw direction markers on graph:
						c = g.getVertex(i).getOutEdge(j);
						drawArrow(c);
						// visualize in and out degree:
						changeColorBasedOnDegree(i);
					}
				}
				// move edge in bounds:
				g.get(i).moveInBounds();
				// update the vertexes distance from the mouse:
				g.get(i).updateDistance(mouseX, mouseY);
				// select the movers if they were clicked:
				selectMover(g.get(i));
				// update physical states of this vertex:
				g.get(i).update();
			}

		}

		// remove any vertexes flagged for removal:
		removeflaggedVertexes();
	}

	/**
	 * draws the edges from the graph:
	 * 
	 * @param g - the current graph
	 */
	private void drawEdges(Graph<Integer, Mover> g) {
		if (startVertexes.size() == 0 || g.size() == 0) {
			return;
		}
		PriorityQueue<Edge<Integer, Mover>> edges = g.getEdges(startVertexes.get(0));
		Edge<Integer, Mover> c;
		while (edges.size() > 0) {
			c = edges.poll();
			selectEndsOfEdge(c);
			drawEdge(c);
		}
	}

	/**
	 * checks if the mouse is over an edge:
	 * 
	 * @param c - the edge being checked
	 * @return - true if the mouse is over the edge, false otherwise.
	 */
	private boolean mouseOverEdge(Edge<Integer, Mover> c) {
		if (c == null) {
			return false;
		}
		Vector line = Vector.sub(c.getEnd().getValue().location, c.getStart().getValue().location);
		line.normalize();
		Vector rStartNorm = new Vector(mouseX, mouseY);
		rStartNorm.sub(c.getStart().getValue().location);
		Vector rStart = rStartNorm.copy();
		rStartNorm.normalize();

		Vector rEndNorm = new Vector(mouseX, mouseY);
		rEndNorm.sub(c.getEnd().getValue().location);
		rEndNorm.normalize();
		// want to check when perpendicular distance is within a certain threshold:
		// determine the sine of the angle betwen rStart and the line
		double sinA = Vector.cross(line, rStartNorm);
		double cosA = Vector.Dot(line, rStartNorm);
		double cosB = Vector.Dot(line, rEndNorm);
		if ((rStart.mag() * Math.abs(sinA) <= ARROW_SIZE) && !c.getStart().getValue().mouseNear(10)
				&& !c.getEnd().getValue().mouseNear(10) && cosA > 0 && cosB < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * allows the user to select the two vertexes at the ends of the an edge with
	 * the mouse.
	 * 
	 * @param c - the selected edge
	 */
	public void selectEndsOfEdge(Edge<Integer, Mover> c) {
		if (mouseOverEdge(c) && mousePressed && mouseButton == LEFT) {
			clearSaved();
			saveMover(0, c.getStart().getValue());
			saveMover(1, c.getEnd().getValue());
		}
	}

	/**
	 * removes the flagged vertex.
	 */
	private void removeflaggedVertexes() {
		if (flaggedForRemoval != null) {
			for (Mover m : flaggedForRemoval) {
				int remove = m.getLabel();
				removeVertex(remove);
			}
		}
	}

	/**
	 * draws an edge
	 * 
	 * @param c - the edge being drawn.
	 */
	private void drawEdge(Edge<Integer, Mover> c) {
		// check if there is an edge between these objects:
		if (c != null) {
			// display edges as red for spanning trees:
			if (mouseOverEdge(c)) {
				stroke(255, 0, 0);
				cp5.get(Textfield.class, "weight").setColor(color(255, 255, 0)).setText(Double.toString(c.getWeight()));
			} else if (displaySpanning) {
				stroke(255, 64, 0);
			} else {
				stroke(0, 0, 0);
			}
			if (c.getWeight() == 0) {
				// still want to see 0 edges
				strokeWeight((float) 0.01);
			} else {
				strokeWeight((float) Math.abs(c.getWeight()));
			}
			line((float) c.getStart().getValue().location.x, (float) c.getStart().getValue().location.y,
					(float) c.getEnd().getValue().location.x, (float) c.getEnd().getValue().location.y);
		}
	}

	/**
	 * marks the directionality of the edge on the graph
	 * 
	 * @param c - the edge being drawn
	 */
	private void drawArrow(Edge<Integer, Mover> c) {
		if (c == null) {
			return;
		}
		Vertex<Integer, Mover> startVert = c.getStart();
		Vertex<Integer, Mover> endVert = c.getEnd();
		// change coordinate system to be centered on start vertex:
		Vector edgeDirection = Vector.sub(endVert.getValue().location, startVert.getValue().location);
		double magnitude = edgeDirection.mag();
		edgeDirection.normalize();
		// scale the radius to be short of the radius and marker size:
		edgeDirection.scale(magnitude - (endVert.getValue().radius + ARROW_SIZE));
		// convert to global coordinates:
		edgeDirection = Vector.add(edgeDirection, startVert.getValue().location);
		// draw marker
		pushMatrix();
		stroke(255);
		fill(0);
		strokeWeight(3);
		circle((float) edgeDirection.x, (float) edgeDirection.y, (float) ARROW_SIZE);
		popMatrix();
	}

	/**
	 * selects the mover closest to the mouse:
	 */
	private void selectMover(Mover m) {
		if (m.mouseIsOver()) {
			// remove mover from queue:
			selectedMovers.remove(m);
			// add mover with updated distance:
			selectedMovers.add(m);
		}
	}

	/**
	 * callback for when the mouse is dragged
	 */
	public void mouseDragged() {

		Vector mouse;
		if (!selectedMovers.isEmpty()) {
			// get the closest mover:
			Mover m = selectedMovers.poll();
			mouse = new Vector(mouseX, mouseY);
			// if mouse is over this element, then move it:
			if (m.mouseIsOver() && mousePressed == true) {
				if (mouseButton == RIGHT) {
					m.pin = true;
				}
				// set location of vertex to the mouse's location
				m.location = mouse;
				// update the distance of the mover from the mouse:
				m.updateDistance(mouseX, mouseY);
				// specify that the mouse is over this mover:
				mouseOver = m;
			}
			// ad mover to queue:
			selectedMovers.add(m);
		}
	}

	/**
	 * clears the text from the given text box when the user activates it if it
	 * matches the default text.
	 * 
	 * @param text        - the text box handle.
	 * @param startString - the default start text.
	 */
	private void clearTextOnActive(Textfield text, String startString) {
		if (text.isActive() && text.getText().equals(startString)) {
			text.clear();
		} else if (!text.isActive() && text.getText().equals("")) {
			text.setText(startString);
		}
	}

	/**
	 * perform right click operation on the mouse to pin it.
	 */
	private void rightClick() {
		Mover m = selectedMovers.remove();
		// if mouse is over this element, then remove it from the queue:
		if (m.mouseIsOver()) {
			m.rightClicked();
		}

	}

	/**
	 * selects the specfic mouse for connecting.
	 */
	private void leftClick() {
		Mover m = selectedMovers.remove();
		// if mouse is over this element, then remove it from the queue:
		if (m.mouseIsOver()) {
			mouseOver = m;
			// save any clicked mover if shift is held: 
			saveMoverSequential(m);
		} else {
			mouseOver = null;
		}
		clearTextOnActive(cp5.get(Textfield.class, "weight"), "edgeWeight");
	}

	/**
	 * sets the mouse clicked state.
	 */
	public void mousePressed() {
		mouseClicked = true;

		if (mouseButton == RIGHT && !selectedMovers.isEmpty()) {
			rightClick();
		}
		if (mouseButton == LEFT && !selectedMovers.isEmpty()) {
			leftClick();
		}
		clearTextOnActive(cp5.get(Textfield.class, "start"), "startID");
		clearTextOnActive(cp5.get(Textfield.class, "end"), "endID");
		cp5.get(Textfield.class, "weight").clear();
	}

	/**
	 * resets the mouse clicked state
	 */
	public void mouseReleased() {
		mouseClicked = false;
	}

	/**
	 * +" q - exit" +" r - reset" +" n - add new ball" +" p - pin/freeze all items"
	 * +" d - unfreeze all items" +" e - erase vertex" +" z - unselect all vertexes"
	 * +" a - add undirected edge" +" s - add directed edge start-> finish" +" t -
	 * delete edge" +" y - delete directed edge start -> finish" +" l - reset
	 * spanning tree/ display main graph" +" k - turn sound on" +" o - turn sound
	 * off"
	 */
	@Override
	public void keyPressed() {
		if(keyPressed && keyCode == SHIFT) {
			//clear keys by pressing the shift key
			clearSaved();
		}
		switch (key) {
		case ('q'): {
			// quit application:
			exit();
			break;
		}
		case ('r'): {// reset
			reset = true;
			setup();
			break;
		}
		case ('n'): {// add new vertex
			addNewVertex();
			break;
		}
		case ('d'): {
			// drop selected items
			while (selectedMovers.size() > 0) {
				selectedMovers.remove().pin = false;
			}

			break;
		}
		case ('p'): {
			// pins all edges:
			for (int i = 1; i <= gMain.size(); i++) {
				gMain.get(i).pin = !gMain.get(i).pin;
				selectedMovers.add(gMain.get(i));
			}
			break;
		}
		case ('e'): {
			// erase vertex:
			if (mouseOver != null) {
				// flag the last selected vertex for removal:
				flaggedForRemoval.add(mouseOver);
			}
			mouseOver = null;
			break;
		}
		case ('a'): {
			// make undirected edge.
			connect();
			break;
		}
		case ('s'): {
			// make directed.
			directed();
			break;
		}
		case ('t'): {
			// remove edge entirely.
			deleteUndirectedEdge();
			break;
		}
		case ('y'): {
			// remove one side of edge.
			deleteDirectedEdge();
			break;
		}
		case ('z'): {
			// clear saved:
			clearSaved();
			break;
		}
		case ('l'): {// reset the spanning trees:
			displaySpanning = false;
			break;
		}
		case ('k'): {// turn sound on
			for (int i : startVertexes) {
				gMain.get(i).soundOn = true;
			}
			break;
		}
		case ('o'): {// turn sound off
			for (int i : startVertexes) {
				gMain.get(i).soundOn = false;
			}
			break;
		}

		default: {
			// reset key to be useless character:
			key = '=';
			break;
		}
		}
	}

	/**
	 * de selects all movers:
	 */
	private void clearSaved() {
		for (int i = 0; i < savedMovers.length; i++) {
			if (savedMovers[i] != null) {
				savedMovers[i].saved = false;
			}
			savedMovers[i] = null;
		}
		cp5.get(Textfield.class, "start").clear();
		cp5.get(Textfield.class, "end").clear();
	}

	/**
	 * checks if enough vertexes have been selected to form an edge
	 * 
	 * @return true if 2 vertexes have been selected.
	 */
	private boolean canMakeEdge() {
		return (savedMovers[0] != null && savedMovers[1] != null);
	}

	/**
	 * checks if a specific mover has already been selected
	 * 
	 * @param m - the mover being checked.
	 * @return true if the mover was already selected, false otherwise.
	 */
	private boolean moverSaved(Mover m) {
		for (int i = 0; i < savedMovers.length; i++) {
			if (savedMovers[i] != null && m != null && m.equals(savedMovers[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * saves selects the mover
	 * 
	 * @param index - the index to where the mover will be saved.
	 * @param Mover m - the mover being saved/selected.
	 */
	private void saveMover(int index, Mover m) {
		if (m == null || moverSaved(m)) {
			clearSaved();
			return;
		}

		// save vertex as starting vertex:
		if (savedMovers[index] == null && m != null) {
			savedMovers[index] = m;
			m.saved = true;
			cp5.get(Textfield.class, textTargets[index]).setColor(color(0, 255, 0))
					.setText(savedMovers[index].toString());

		} else if (savedMovers[index] != null && m != null) {
			savedMovers[index].saved = false;
			savedMovers[index] = m;
			m.saved = true;
			cp5.get(Textfield.class, textTargets[index]).setColor(color(0, 255, 0))
					.setText(savedMovers[index].toString());
		}

	}

	/**
	 * saves the movers for creating edges in the order tehy were clicked.
	 * 
	 * @param m - the specified mover.
	 */
	private void saveMoverSequential(Mover m) {
		if (m == null || moverSaved(m)) {
			clearSaved();
			return;
		}
		// if a mover that has not been clicked before is selected when two are already
		// selected, then
		// the selections are reset and the new mover is
		// saved
		if ((!moverSaved(m) && canMakeEdge())) {
			clearSaved();
		}

		if (savedMovers[0] == null) {
			saveMover(0, m);
		} else if (savedMovers[1] == null) {
			saveMover(1, m);
		}
	}

	/**
	 * gets the edge value form the text box, if nothing, or an invalid value is
	 * entered, then it returns 1.00 by default.
	 * 
	 * @return the current value entered as the edge weight, or 1.00 by default.
	 */
	public Double getEdgeValue() {
		String weight = cp5.get(Textfield.class, "weight").getText();
		if (weight == null) {
			return 1.00;
		}
		try {
			Double edge = Double.parseDouble(weight);
			return edge;
		} catch (NumberFormatException e) {
			return 1.00;
		}
	}

	/**
	 * gets the id number form the specified text box
	 * 
	 * @param fieldName - the name of the text box being targeted, should be "start"
	 *                  or "end"
	 * @return the id number
	 * @throws NumberFormatException - if the text box does not contain parseable
	 *                               integers.
	 */
	public Integer getMover(String fieldName) throws NumberFormatException {
		String textBoxContents = cp5.get(Textfield.class, fieldName).getText();
		if (textBoxContents == null || textBoxContents.equals("")) {
			throw new NumberFormatException();
		}
		// otherwise parse the numbers from text boxes
		Integer id = Integer.parseInt(textBoxContents);
		return id;
	}

	/**
	 * callback for creating an undirected edge with a button.
	 */
	public void connect() {
		if (!displaySpanning) {
			try {
				// get values from the text box:
				Integer startID = getMover("start");
				Integer endID = getMover("end");
				Double edge = getEdgeValue();
				createEdge(startID, endID, edge);
				// restore initial states:
				clearSaved();
				resetText(true);
			} catch (NumberFormatException e) { // if the text box does not have valid inputs, see if they
												// were selected from the mouse.
				resetText(false);
				// by default, if vertexes are selected by mouse, create edge with weight 1.00
				// with the
				// button:
				if (canMakeEdge()) {
					createEdge(savedMovers[0].getLabel(), savedMovers[1].getLabel(), 1.00);
					resetText(true);
				}
				clearSaved();
			}
		}
	}

	/**
	 * callback for creating a directed edge with a button.
	 */
	public void directed() {
		if (!displaySpanning) {
			try {

				Integer startID = getMover("start");
				Integer endID = getMover("end");
				Double edge = getEdgeValue();
				createDirectedEdge(startID, endID, edge);
				clearSaved();
				resetText(true);
			} catch (NumberFormatException e) { // if the text box does not have valid inputs, see if they
												// were selected from the mouse.
				resetText(false);
				// by default, if vertexes are selected by mouse, create edge with weight 1.00
				// with the
				// button:
				if (canMakeEdge()) {
					createDirectedEdge(savedMovers[0].getLabel(), savedMovers[1].getLabel(), 1.00);
					resetText(true);
					clearSaved();
				}

			}
		}
	}

	/**
	 * deletes an edge going from start to finish in the main graph.
	 */
	public void deleteDirectedEdge() {
		if (!displaySpanning) {
			try {
				Integer startID = getMover("start");
				Integer endID = getMover("end");
				removeEdge(startID, endID);
				clearSaved();
				resetText(true);
			} catch (NumberFormatException e) {
				// if text unputs do not specify which edge to remove, then remove the edge from
				// the
				// selected
				// vertexes:
				resetText(false);
				if (canRemoveEdge()) {
					removeEdge(savedMovers[0].getLabel(), savedMovers[1].getLabel());
					resetText(true);
					clearSaved();

				}
			}
		}
	}

	/**
	 * delete both sides of a given edge between the user selected vertexes.
	 */
	public void deleteUndirectedEdge() {
		if (!displaySpanning) {
			try {
				Integer startID = getMover("start");
				Integer endID = getMover("end");
				removeEdge(startID, endID);
				removeEdge(endID, startID);
				clearSaved();
				resetText(true);
			} catch (NumberFormatException e) {
				resetText(false);
				// if the starting and ending vertexes are not entered through the text line,
				// then remove
				// the
				// edge between the selected vertexes.
				if (canRemoveEdge()) {
					removeEdge(savedMovers[0].getLabel(), savedMovers[1].getLabel());
					removeEdge(savedMovers[1].getLabel(), savedMovers[0].getLabel());
					resetText(true);
					clearSaved();
				}
			}
		}
	}

	/**
	 * changes the color of a vertex based on its degree in the main graph.
	 * 
	 * @param id - the id of the vertex changing color.
	 */
	private void changeColorBasedOnDegree(int id) {
		int outDegree = gMain.getVertex(id).getOutDegree();
		int inDegree = gMain.getVertex(id).getInDegree();
		double degreeFraction = ((double) (inDegree + outDegree)) / ((double) gMain.getEdges().size() + 1.00);

		int red = (int) (degreeFraction * 200 + 55);
		gMain.get(id).setGreen(red);
	}

	/**
	 * checks if there exists an edge going either way between the mouse selected
	 * vertexes.
	 * 
	 * @return true if there is an edge, false otherwise.
	 */
	private boolean canRemoveEdge() {
		if (canMakeEdge()) {
			if (gMain.getVertex(savedMovers[0].getLabel()).hasOutConnectionTo(savedMovers[1].getLabel())
					|| gMain.getVertex(savedMovers[1].getLabel()).hasOutConnectionTo(savedMovers[0].getLabel())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * resets the text fields with color feedback on the user input.
	 * 
	 * @param validInput - true to turn test green for valid input formatting, false
	 *                   to turn text red for invalid input formatting
	 */
	private void resetText(boolean validInput) {
		if (validInput) {
			cp5.get(Textfield.class, "start").clear();
			cp5.get(Textfield.class, "end").clear();
			cp5.get(Textfield.class, "weight").clear();
			cp5.get(Textfield.class, "start").setColor(color(0, 255, 0)).setText("startID");
			cp5.get(Textfield.class, "end").setColor(color(0, 255, 0)).setText("endID");
			cp5.get(Textfield.class, "weight").setColor(color(0, 255, 0)).setText("edgeWeight");
		} else {
			cp5.get(Textfield.class, "start").setColor(color(255, 0, 0)).setText("startID");
			cp5.get(Textfield.class, "end").setColor(color(255, 0, 0)).setText("endID");
			cp5.get(Textfield.class, "weight").setColor(color(255, 0, 0)).setText("edgeWeight");
		}
	}

	/**
	 * performs the specific spanning operation on the main graph from the dropdown
	 * menu.
	 * 
	 * @param n - the number corresponding to the specific dropdown menu option.
	 */
	public void dropdown(int n) {
		Integer startID;

		try {
			startID = getMover("start");
		} catch (NumberFormatException e) {
			// if there is no id present, then start at the first virtex.
			startID = 1;
		}

		switch (n) {
		case (0): {// DFS
			gSpanning = gMain.depthFirstSpanning(startID);
			displaySpanning = true;
			// move through all the vertexes in the spanning tree and change their color.
			break;
		}
		case (1): {// BFS
			gSpanning = gMain.breadthFirstSpanning(startID);
			displaySpanning = true;
			break;
		}
		case (2): {// primm's
			gSpanning = gMain.primms(startID);
			displaySpanning = true;
			break;
		}
		case (3): { // kruskal's
			gSpanning = gMain.kruskals(startID);
			displaySpanning = true;
			break;
		}
		default: {// default is to display main graph
			displaySpanning = false;
			break;
		}
		}

	}

	/**
	 * resets the buttons and text feilds in the gui.
	 */
	public void resetButtons() {

		cp5.addTextfield("start").setPosition(10, 0).setSize(40, 40).setAutoClear(false).setText("startID");

		cp5.addTextfield("end").setPosition(50, 0).setSize(40, 40).setAutoClear(false).setText("endID");

		cp5.addTextfield("weight").setPosition(10, 40).setSize(80, 40).setAutoClear(false).setText("edgeWeight");

		cp5.addBang("connect").setPosition(10, 80).setSize(80, 40).setCaptionLabel("Connect")
				.setColorActive(color(0, 255, 0)).getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);

		cp5.addBang("directed").setPosition(10, 120).setSize(80, 40).setColorActive(color(0, 255, 0))
				.setCaptionLabel("Con Start->End").getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);

		cp5.addBang("deleteUndirectedEdge").setPosition(10, 160).setSize(80, 40).setColorActive(color(255, 0, 0))
				.setCaptionLabel("Disconnect").getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);

		cp5.addBang("deleteDirectedEdge").setPosition(10, 200).setSize(80, 40).setColorActive(color(255, 0, 0))
				.setCaptionLabel("Dis start->end").getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);

		cp5.addBang("addNewVertex").setPosition(10, 240).setSize(80, 40).setCaptionLabel("ADD BALL")
				.setColorActive(color(0, 255, 0)).getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);

		cp5.addScrollableList("dropdown").setPosition(10, 300).setSize(80, 40).setBarHeight(20).setItemHeight(20)
				.addItems(buttonList).setCaptionLabel("spanning trees");
	}
}
