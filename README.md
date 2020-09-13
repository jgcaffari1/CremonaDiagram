# GraphVisualizer
 A simple program for creating a zero gravity Cremona Diagram.  This graph implementation also contains some basic graph algorithms.  

## Controls  

Press "n", or the "ADD BALL" Button to insert new Nodes.

Click on the nodes to select them, once selected they can be connected with either a directed, or undirected edge:
 "CON START -> END" or press "s" will create a directed edge.  
 "CONNECT" or "a" will create an undirected edge.  

Edges are removed similarly, pressing "y" or "t" to remove directed or undirected edges, respectively. 

Can pin nodes in place by right clicking on them. All nodes can be pinned/unpinned at once my pressing "p". 

Can drag selected nodes one at a time. 

Can right click edges to make them exert a force on their endpoints, clicking an edge selects its endpoints.    

"r" resets the diagram. 

Pressing "e" with a selected Node will delete it, and all its associated edges.  

The Edges act as springs, and can generate soud by pressing "k", the sounds can be turned off with "o". 

Can use the text boxes to change/ enter edge weights - default value is 1.00.  The ID boxes also allow for specific Nodes to be selected.   

"z" deselects all vertexes.  

Can display different spanning trees from a dropdown menu. press "l" to return to the original diagram.  

All the keyboard shortcuts are within a scrolling text box underneath the buttons.  

## Works Cited

Shiffman, D. (2012). The nature of code: Simulating natural systems with processing. Nature of Code.  

Andreas Schlegel, . (2015, March 24). ControlP5, a GUI library for the programming environment Processing (Version v2.2.4). Zenodo. http://doi.org/10.5281/zenodo.16290

