# Maxwell-Cremona Diagram Visualizer
 A simple program for creating a zero gravity Cremona Diagram.  
 
 Download the package and click on the .jar file to run the program.  If you have a suggestion on another algorithm to visualize, let me know.  

## Background

Maxwell-Cremona Diagrams (also known as Reciporical Force Diagrams) were originally developed by the mathematician Pierre Varignon, as a way of finding the equilibrium of ropes under tension.  Pierre Varignon refered to his work as "Graphical Statics", and it was not published in detail until after his death in 1725.  It wasn't until much later that   James Clerk Maxwell and Luigi Cremona developed a more detailed theory of Reciporical Force Diagrams in the 1800s.  

   ![Pierre Varignon Drawings](/GIFS/reciporicalForceVari.png)  
    __Figure 1__: Varignon's Reciporical Force Diagrams (Erickson).  Images depict a network of ropes tied under tension. I reccomend reading Erickson's open source book for mor information.    

Maxwell-Cremona diagrams are an active area of research, and are used in the present day to model internal stresses from a structure (Block et al, Baker et al.).  This model is meant to be more dynamic than traditional Maxwell-Cremona diagrams, and instead models a complex, dampened spring system.  However, the same general shapes can be created. 

   __A)__  
     ![ChallengeImages_A](/GIFS/challengeImage1.png)  
   __B)__  
     ![ChallengeImages_B](/GIFS/challengeImage2.png)  
   __C)__  
     ![ChallengeImages_C](/GIFS/challengeImage3.png)  
   __Figure 2 (A-C)__: Reciporical Force Diagrams from Baker et al. These are a few suggestions for diagrams to attempt making with this application.  



## Controls  

* Press "n", or the "ADD BALL" Button to insert new Nodes.  

  ![Add New Node](/GIFS/AddNode.gif) 

* Click on the nodes to select them, once selected they can be connected with either a directed, or undirected edge:
   * "CON START -> END" or press "s" will create a directed edge.  
   * "CONNECT" or "a" will create an undirected edge.  
   
* Edges are removed similarly, pressing "y" or "t" to remove directed and undirected edges, respectively. 

  ![Creating and Removing Edges](/GIFS/connect.gif) 
  
* Pin nodes in place by right clicking on them. All nodes can be pinned/unpinned at once my pressing "p". 

  ![Freeze Nodes](/GIFS/Freeze.gif) 

* Drag selected nodes one at a time. 

  ![Drag Nodes](/GIFS/DragNodes.gif) 

* Right click edges to make them exert a force on their endpoints, clicking an edge selects its endpoints.   

  ![Interacting with Edges](/GIFS/edgeForce.gif) 
 

* Pressing "r" resets the diagram. q quits the program.  

* Pressing "e" with a selected Node will delete it, and all its associated edges.  

  ![Delete Nodes](/GIFS/deleteNode.gif) 
 
* The Edges act as springs, and can generate soud by pressing "k", the sounds can be turned off with "o". 

* Use the text boxes to change/ enter edge weights - default value is 1.00.  The ID boxes also allow for specific Nodes to be selected.
 
  ![Change Edge Weights](/GIFS/ChangeEdgeWeights.gif) 

* Pressing "z" deselects all vertexes.  

* Display different spanning trees from a dropdown menu. press "l" to return to the original diagram.  

  ![Spanning Trees](/GIFS/spanningTrees.gif) 

* All the keyboard shortcuts are within a scrolling text box underneath the buttons.  

## Works Cited  

Andreas Schlegel, . (2015, March 24). ControlP5, a GUI library for the programming environment Processing (Version v2.2.4). Zenodo. http://doi.org/10.5281/zenodo.16290

Baker, W. F., Beghini, L. L., Mazurek, A., Carrion, J., & Beghini, A. (2013). Maxwell’s reciprocal diagrams and discrete Michell frames. Structural and multidisciplinary    optimization, 48(2), 267-277.

Block, P., Fivet, C., & Van Mele, T. (2016). Reciprocal diagrams: Innovative applications of past theories. International Journal of Space Structures, 31(2–4), 84–84. https://doi.org/10.1177/0266351116660789

Erickson, J. (1999). Algorithms.

Shiffman, D. (2012). The nature of code: Simulating natural systems with processing. Nature of Code.  



