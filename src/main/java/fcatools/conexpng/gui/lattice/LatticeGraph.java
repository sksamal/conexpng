package fcatools.conexpng.gui.lattice;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;
import fcatools.conexpng.model.LatticeConcept;

/**
 * This class implemented the graph model for the lattice. It contains a list of
 * nodes and edges.
 * 
 */
public class LatticeGraph {

    private List<Node> nodes;
    private List<Edge> edges;
	private Node bottomNode;
    private int maxLevel;
 
    /**
     *
     */
    public LatticeGraph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * 
     * @param nodes
     * @param edges
     */
    public LatticeGraph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * 
     * @param i
     * @return
     */
    public Node getNode(int i) {
        return nodes.get(i);
    }

	public Node getBottomNode() {
		return bottomNode;
	}

	public void setBottomNode(Node bottomNode) {
		this.bottomNode = bottomNode;
	}
    /**
     * 
     * @return
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * 
     * @param nodes
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * 
     * @param i
     * @return
     */
    public Edge getEdge(int i) {
        return edges.get(i);
    }

    /**
     * 
     * @return
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * 
     * @param edges
     */
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    /**
     * Sets maximum level of nodes in this graph. Needed to calculate the level
     * in {@link #addEdges(Set)} method.
     * 
     * @param maxLevel
     *            maximum level of nodes in this graph
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean missingEdges() {
        return edges.isEmpty() && nodes.size() > 1;
    }

    public void translate(int d, int e) {
        for (Node n : nodes) {
            n.setX(n.getX() + d);
            n.setY(n.getY() + e);
            n.getObjectsLabel().update(d, e);
            n.getAttributesLabel().update(d, e);
        }
    }

    public Set<Concept<String, FullObject<String, String>>> getConcepts() {
    	Set<Concept<String, FullObject<String, String>>> concepts = new ListSet<Concept<String, FullObject<String, String>>> ();
    	for(Node n: this.nodes) {
    		Concept<String, FullObject<String, String>> c = new LatticeConcept();
    		for(FullObject<String,String> o : n.getFullObjects())
    			c.getExtent().add(o);
    
    		for(String a : n.getAttributes())
    			c.getIntent().add(a);
    		
  //  		System.out.println(n.getObjects() + " :" + n.getAttributes());
    		((LatticeConcept)c).setId(n.getId());
    		concepts.add(c);
    	}
    	return concepts;
	}

    public void createLattice(Set<Concept<String, FullObject<String, String>>> concepts) {
    	LatticeGraphComputer.init();
        LatticeGraph temp = LatticeGraphComputer.computeLatticeGraph(concepts, new Rectangle(800, 600));
        this.setNodes(temp.getNodes());
        this.setBottomNode(temp.getBottomNode());
        for (Edge e : temp.edges) {
            Node u = getNodeWithIntent(e.getU().getAttributes());
            Node v = getNodeWithIntent(e.getV().getAttributes());
            if (u != null && v != null && !u.equals(v)) {
                u.getObjects().addAll(e.getU().getObjects());
                v.getObjects().addAll(e.getV().getObjects());
                u.setVisibleAttributes(e.getU().getVisibleAttributes());
                v.setVisibleAttributes(e.getV().getVisibleAttributes());
                u.setVisibleObjects(e.getU().getVisibleObjects());
                v.setVisibleObjects(e.getV().getVisibleObjects());
                u.setLevel(temp.maxLevel - e.getU().getLevel());
                v.setLevel(temp.maxLevel - e.getV().getLevel());
                v.addChildNode(u);
                u.addParentNode(v);
                if (u.getAttributesLabel().getX() == 0 && u.getAttributesLabel().getY() == 0)
                    u.getAttributesLabel().setXY(e.getU().getAttributesLabel().getX(),
                            e.getU().getAttributesLabel().getY());
                if (v.getAttributesLabel().getX() == 0 && v.getAttributesLabel().getY() == 0)
                    v.getAttributesLabel().setXY(e.getV().getAttributesLabel().getX(),
                            e.getV().getAttributesLabel().getY());
                if (u.getObjectsLabel().getX() == 0 && u.getObjectsLabel().getY() == 0)
                    u.getObjectsLabel().setXY(e.getU().getObjectsLabel().getX(), e.getU().getObjectsLabel().getY());
                if (v.getObjectsLabel().getX() == 0 && v.getObjectsLabel().getY() == 0)
                    v.getObjectsLabel().setXY(e.getV().getObjectsLabel().getX(), e.getV().getObjectsLabel().getY());
                edges.add(new Edge(u, v));
            }
        }
        computeAllIdeals();
    }
	public void addEdges(Set<Concept<String, FullObject<String, String>>> concepts) {
    	System.out.println("Add edges called");
        LatticeGraph temp = LatticeGraphComputer.computeLatticeGraph(concepts, new Rectangle(800, 600));
        for (Edge e : temp.edges) {
            Node u = getNodeWithIntent(e.getU().getAttributes());
            Node v = getNodeWithIntent(e.getV().getAttributes());
            if (u != null && v != null && !u.equals(v)) {
                u.getObjects().addAll(e.getU().getObjects());
                v.getObjects().addAll(e.getV().getObjects());
                u.setVisibleAttributes(e.getU().getVisibleAttributes());
                v.setVisibleAttributes(e.getV().getVisibleAttributes());
                u.setVisibleObjects(e.getU().getVisibleObjects());
                v.setVisibleObjects(e.getV().getVisibleObjects());
                u.setLevel(temp.maxLevel - e.getU().getLevel());
                v.setLevel(temp.maxLevel - e.getV().getLevel());
                v.addChildNode(u);
                u.addParentNode(v);
                if (u.getAttributesLabel().getX() == 0 && u.getAttributesLabel().getY() == 0)
                    u.getAttributesLabel().setXY(e.getU().getAttributesLabel().getX(),
                            e.getU().getAttributesLabel().getY());
                if (v.getAttributesLabel().getX() == 0 && v.getAttributesLabel().getY() == 0)
                    v.getAttributesLabel().setXY(e.getV().getAttributesLabel().getX(),
                            e.getV().getAttributesLabel().getY());
                if (u.getObjectsLabel().getX() == 0 && u.getObjectsLabel().getY() == 0)
                    u.getObjectsLabel().setXY(e.getU().getObjectsLabel().getX(), e.getU().getObjectsLabel().getY());
                if (v.getObjectsLabel().getX() == 0 && v.getObjectsLabel().getY() == 0)
                    v.getObjectsLabel().setXY(e.getV().getObjectsLabel().getX(), e.getV().getObjectsLabel().getY());
                edges.add(new Edge(u, v));
            }
        }
        computeAllIdeals();
    }

    public void removeAllDuplicates() {
        ArrayList<Node> duplicates = new ArrayList<>();
        for (Node n : getNodes()) {
            if (n.getObjects().isEmpty() && n.getAttributes().isEmpty()) {
                duplicates.add(n);
            }
        }
        getNodes().removeAll(duplicates);
        duplicates.clear();
        for (int i = 0; i < getNodes().size() - 1; i++) {
            Node u = getNodes().get(i);
            for (int j = i + 1; j < getNodes().size(); j++) {
                Node v = getNodes().get(j);
                if (u.getObjects().equals(v.getObjects()) && u.getAttributes().equals(v.getAttributes())) {
                    duplicates.add(v);
                }
            }

        }
        getNodes().removeAll(duplicates);
        for (Node n : getNodes()) {
            n.getChildNodes().removeAll(duplicates);
        }
    }

    /**
     *
     */
    public void computeAllIdeals() {
        // sort the list of nodes from bottom to top

        ArrayList<Node> q = new ArrayList<>();
        for (Node n : getNodes()) {
            if (q.size() == 0) {
                q.add(n);
            } else {
                for (int i = 0; i < q.size(); i++) {
                    if (q.get(i).getObjects().containsAll(n.getObjects())
                            || q.get(i).getObjects().size() > n.getObjects().size()) {
                        q.add(i, n);
                        break;
                    }
                    if (i + 1 == q.size()) {
                        q.add(i + 1, n);
                        break;
                    }
                }
            }
            n.positionLabels();
        }
        for (int i = 1; i < q.size(); i++) {
            Node u = q.get(i);
            for (int j = i - 1; j >= 0; j--) {
                Node v = q.get(j);
                if (u.getObjects().containsAll(v.getObjects()) && v.getAttributes().containsAll(u.getAttributes())) {
                    u.getIdeal().add(v);
                }
            }
        }
    }

    private Node getNodeWithIntent(Set<String> attributes) {
        for (Node n : nodes) {
            if (n.getAttributes().containsAll(attributes) && attributes.containsAll(n.getAttributes()))
                return n;
        }
        return null;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Returns the direct upper neighbors of each node in a list of lists. First
     * list are the direct upper neighbors of first node and so on.
     * 
     * @return list of lists of upper neighbors of all nodes
     */
    public List<List<Node>> getUpperNeighbors() {
        List<List<Node>> list = new ArrayList<List<Node>>();
        for (Node currentNode : nodes) {
            List<Node> neighborList = new ArrayList<Node>();
            for (Node n : nodes) {
                if (n.compareTo(currentNode) == 1) {
                    neighborList.add(n);
                }
            }
            list.add(neighborList);
        }
        return list;
    }

}
