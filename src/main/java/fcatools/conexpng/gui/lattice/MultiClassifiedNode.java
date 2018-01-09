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
public class MultiClassifiedNode extends Node {

  /* */
	private static final long serialVersionUID = 1L;
	/**
     *
     */
	private List<List<Double>> probsList;

    /**
     * 
     * @param extent
     * @param intent
     * @param x
     * @param y
     */
    public MultiClassifiedNode(Set<String> extent, Set<String> intent, List<List<Double>> probsList, int x, int y) {
        super(extent, intent,x,y);
        for(List<Double> probs: probsList) 
        	this.probsList.add(probs);
        }

    /**
     *
     */
    public MultiClassifiedNode() {
    	super();
    	this.probsList = new ArrayList<List<Double>>();
    }
  
    public MultiClassifiedNode(List<List<Double>> probsList) {
    	super();
    	this.probsList = new ArrayList<List<Double>>();
    	for(List<Double> probs : probsList) {
    		List<Double> newProbs = new ArrayList<Double>();
    		for(Double p : probs)
    			newProbs.add(round(p));
    		this.probsList.add(newProbs);
    	}
    }
    
    /**
     * 
     * @param prob
     */
    public void addProbs(List<Double> probs) {
        probsList.add(probs);
    }

    public void setProbs(int i, List<Double> probs) {
        probsList.set(i,probs);
    }
    
    public void addProb(int i, Double prob) {
    	if(probsList.size()==i && probsList.get(i) == null)
    		probsList.add(new ArrayList<Double>());
        probsList.get(i).add(prob);
    }
  
    public List<List<Double>> getProbsList() {
        return probsList;
    }
    
    public List<Double> getProbs(int i) {
        return probsList.get(i);
    }
    
    public Double getProb(int i, int j) {
        return getProbs(i).get(j);
    }
    
    public List<Integer> getProbClass(int i) {
    	if(i>=probsList.size()) return null;
    	int maxIndex = 0;
    	List<Integer> cList = new ArrayList<Integer>();
    	cList.add(0);
    	for(int j=1;j<probsList.get(i).size();j++) {
    		if(probsList.get(i).get(maxIndex) < probsList.get(i).get(j)) {
    			maxIndex = j;
    			cList.clear();
    			cList.add(j);
    		}
    		else if(probsList.get(i).get(maxIndex) == probsList.get(i).get(j)) {
    			cList.add(j);
    		}
    	}
        return cList;
     
    }
  
    public List<List<Integer>> getProbClasses() {
    	List<List<Integer>> cList = new ArrayList<List<Integer>>();
    	for(int i=0;i<probsList.size();i++)
    		cList.add(getProbClass(i));
    	return cList;
    }
    	
    
    public Double round(Double prob) {
    	return ((int)(prob*100))/100.0;
    	
    }
}
