package fcatools.conexpng.gui.lattice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class implements the nodes of the lattice graph. It is the model for the
 * concepts. Each concept are represented as a node which knows his position ,
 * the objects and attributes as well as the labels which contained visible
 * objects resp. attributes.
 * 
 */
public class ClassifiedNode extends Node {

  /* */
	private static final long serialVersionUID = 1L;
	/**
     *
     */
	private List<Double> probs;

    /**
     * 
     * @param extent
     * @param intent
     * @param x
     * @param y
     */
    public ClassifiedNode(Set<String> extent, Set<String> intent, List<Double> probs, int x, int y) {
        super(extent, intent,x,y);
        for(Double p : probs)
        	this.probs.add(round(p));
        }

    /**
     *
     */
    public ClassifiedNode() {
    	super();
    	this.probs = new ArrayList<Double>();
    }
  
    public ClassifiedNode(List<Double> probs) {
    	super();
    	this.probs = new ArrayList<Double>();
    	for(Double p : probs)
    	this.probs.add(round(p));
    }
    
    /**
     * 
     * @param prob
     */
    public void addProb(Double prob) {
        probs.add(round(prob));
    }

  
    public List<Double> getProbs() {
        return probs;
    }
    
    public List<Integer> getProbClass() {
    	int maxIndex = 0;
    	List<Integer> cList = new ArrayList<Integer>();
    	cList.add(0);
    	for(int i=1;i<probs.size();i++) {
    		if(probs.get(maxIndex) < probs.get(i)) {
    			maxIndex = i;
    			cList.clear();
    			cList.add(i);
    		}
    		else if(probs.get(maxIndex) == probs.get(i)) {
    			cList.add(i);
    		}
    	}
        return cList;
     
    }
   
    
    public Double round(Double prob) {
    	return ((int)(prob*100))/100.0;
    	
    }
}
