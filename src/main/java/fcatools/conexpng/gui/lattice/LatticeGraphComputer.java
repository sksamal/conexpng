package fcatools.conexpng.gui.lattice;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;
import fcatools.conexpng.gui.lattice.algorithms.ExternalFreeseLatticeGraphAlgorithm;
import fcatools.conexpng.gui.lattice.algorithms.FreeseLatticeGraphAlgorithm;
import fcatools.conexpng.gui.lattice.algorithms.ILatticeGraphAlgorithm;
import fcatools.conexpng.gui.lattice.algorithms.TrivialLatticeGraphAlgorithm;
import fcatools.conexpng.io.locale.LocaleHandler;

/**
 * This class computes the lattice graph. It contains the available algorithms
 * for lattice layout and provides methods to change the default.
 */
public class LatticeGraphComputer {

    private static LatticeGraph graph;
    private static Set<Concept<String, FullObject<String, String>>> lattConcepts;
    private static HashMap<String, ILatticeGraphAlgorithm> algorithms;
    private static ILatticeGraphAlgorithm usedAlgorithm;
    private static int screenWidth;
    private static int screenHeight;

    /**
     * Initialize: Create algorithms, select default.
     */
    public static void init() {
        algorithms = new HashMap<>();
        algorithms.put("Trivial", new TrivialLatticeGraphAlgorithm());
        algorithms.put("Freese", new FreeseLatticeGraphAlgorithm());
        algorithms.put("ExternalFreese", new ExternalFreeseLatticeGraphAlgorithm());
        usedAlgorithm = algorithms.get("ExternalFreese");
    }

    /**
     * Change the selected algorithm.
     * 
     * @param name
     *            name of algorithm
     */
    public static void chooseAlgorithm(String name) {
        if (!algorithms.containsKey(name)) {
            System.err.println(LocaleHandler.getString("LatticeGraphComputer.chooseAlgorithm.error"));
        } else {
            usedAlgorithm = algorithms.get(name);
        }
    }

    /**
     * This method computes the lattice graph.
     * 
     * @param concepts
     *            set of concepts of the lattice.
     * @param bounds
     *            of the viewport
     * @return the lattice graph which has to be drawn
     */
    public static LatticeGraph computeLatticeGraph(Set<Concept<String, FullObject<String, String>>> concepts,
            Rectangle bounds) {
        lattConcepts = concepts;
        screenWidth = bounds.width;
        screenHeight = bounds.height;
        initGraph();
        graph.computeAllIdeals();
        computeVisibleObjectsAndAttributes();
        graph = usedAlgorithm.computeLatticeGraphPositions(graph, screenWidth, screenHeight);
        return graph;
    }

    /**
     * Initialize the graph.
     */
    public static void initGraph() {
        graph = new LatticeGraph();

        Iterator<Concept<String, FullObject<String, String>>> iter = lattConcepts.iterator();
        while (iter.hasNext()) {
            Node n = new Node();
            Concept<String, FullObject<String, String>> c = (Concept<String, FullObject<String, String>>) iter.next();
            n.addAttributs(c.getIntent());

            ListSet<String> extent = new ListSet<>();
            for (FullObject<String, String> fo : c.getExtent()) {
                extent.add(fo.getIdentifier());
            }
            n.getObjects().addAll(extent);
            graph.getNodes().add(n);
        }

        graph.removeAllDuplicates();

        List<Node> topNode = new ArrayList<>();
        for (Node u : graph.getNodes()) {
            topNode.add(u);
            Set<String> uEx = u.getObjects();
            for (Node v : graph.getNodes()) {
                Set<String> vEx = v.getObjects();
                if (isLowerNeighbour(uEx, vEx)) {
                    v.addChildNode(u);
                    u.addParentNode(v);
                    graph.getEdges().add(new Edge(u, v));
                    topNode.remove(u);
                }
            }
        }
        Queue<Node> q = new LinkedList<>();
        q.addAll(topNode);
        while (!q.isEmpty()) {
            Node n = q.remove();
            for (Node v : n.getChildNodes()) {
                if (v.getLevel() == 0 || v.getLevel() == n.getLevel()) {
                    v.setLevel(n.getLevel() + 1);
                    graph.setMaxLevel(v.getLevel());
                    v.setX((int) (Math.random() * 500));
                    v.setY(100 * v.getLevel());
                    v.positionLabels();
                    q.add(v);
                }
            }
        }

    }

    /**
     * 
     * @param subEx
     * @param superEx
     * @return
     */
    public static boolean isSubconcept(Set<String> subEx, Set<String> superEx) {
        if (subEx == superEx) {
            return false;
        }
        if (subEx.size() > superEx.size()) {
            return false;
        }
        for (String s : subEx) {
            if (!superEx.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param subEx
     * @param superEx
     * @return
     */
    public static boolean isLowerNeighbour(Set<String> subEx, Set<String> superEx) {
        if (subEx == superEx) {
            return false;
        }
        if (!isSubconcept(subEx, superEx)) {
            return false;
        }
        for (Node n : graph.getNodes()) {
            Set<String> set = n.getObjects();
            if (!subEx.equals(set)) {
                if (!superEx.equals(set)) {
                    if (isSubconcept(subEx, set)) {
                        if (isSubconcept(set, superEx)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void computeVisibleObjectsAndAttributes() {
        // calc which obj/attr has to be shown
        Set<String> usedObj = new TreeSet<>();
        Set<String> usedAttr = new TreeSet<>();
        Node maxNode = new Node();
        Node minNode;
        if (graph.getNodes().size() == 0) {
            minNode = new Node();
        } else {
            minNode = graph.getNode(0);
        }

        for (Node u : graph.getNodes()) {
            if (u.getIdeal().size() >= maxNode.getIdeal().size()) {
                maxNode = u;
            } else if (u.getIdeal().size() <= minNode.getIdeal().size()) {
                minNode = u;
            }
        }

        Queue<Node> pq = new LinkedList<>();
        pq.add(maxNode);
        while (!pq.isEmpty()) {
            Node n = pq.remove();
            for (String a : n.getAttributes()) {
                if (!usedAttr.contains(a)) {
                    n.setVisibleAttribute(a);
                    usedAttr.add(a);
                }
            }
            for (Node u : n.getChildNodes()) {
                pq.add(u);
            }
        }

        pq.add(minNode);
        while (!pq.isEmpty()) {
            Node n = pq.remove();
            for (String o : n.getObjects()) {
                if (!usedObj.contains(o)) {
                    n.setVisibleObject(o);
                    usedObj.add(o);
                }
            }
            for (Node u : graph.getNodes()) {
                if (u.getChildNodes().contains(n)) {
                    pq.add(u);
                }
            }
        }
    }
}
