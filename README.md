# GraphVisualizer
 A simple program for creating a zero gravity Cremona Diagram.  This graph implementation also contains some basic graph algorithms.  
 
 If you have a suggestion on another algorithm to visualize, let me know.  

## Controls  

* Press "n", or the "ADD BALL" Button to insert new Nodes.

* Click on the nodes to select them, once selected they can be connected with either a directed, or undirected edge:
   * "CON START -> END" or press "s" will create a directed edge.  
   * "CONNECT" or "a" will create an undirected edge.  

* Edges are removed similarly, pressing "y" or "t" to remove directed and undirected edges, respectively. 

* Pin nodes in place by right clicking on them. All nodes can be pinned/unpinned at once my pressing "p". 

* Drag selected nodes one at a time. 

* Right click edges to make them exert a force on their endpoints, clicking an edge selects its endpoints.    

* Pressing "r" resets the diagram. q quits the program.  

* Pressing "e" with a selected Node will delete it, and all its associated edges.  

* The Edges act as springs, and can generate soud by pressing "k", the sounds can be turned off with "o". 

* Use the text boxes to change/ enter edge weights - default value is 1.00.  The ID boxes also allow for specific Nodes to be selected.   

* Pressing "z" deselects all vertexes.  

* Display different spanning trees from a dropdown menu. press "l" to return to the original diagram.  

* All the keyboard shortcuts are within a scrolling text box underneath the buttons.  

## Works Cited

Shiffman, D. (2012). The nature of code: Simulating natural systems with processing. Nature of Code.  

Andreas Schlegel, . (2015, March 24). ControlP5, a GUI library for the programming environment Processing (Version v2.2.4). Zenodo. http://doi.org/10.5281/zenodo.16290

