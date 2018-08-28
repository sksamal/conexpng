package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;

public class FuzzyMultiClassedConcept extends LatticeConcept {

	private List<List<Double>> probsList;

	public FuzzyMultiClassedConcept() {
		super();
		probsList = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return super.toString() + " [probs=" + probsList + "]";
	}

	public FuzzyMultiClassedConcept(Concept<String, FullObject<String, String>> c) {
		for(FullObject<String, String> obj: c.getExtent())
			this.getExtent().add(obj);
		for(String att: c.getIntent())
			this.getIntent().add(att);
		this.setId(((LatticeConcept)c).getId());
		probsList = new ArrayList<>();
	}
	
	public List<List<Double>> getProbsList() {
		return probsList;
	}
	
	public List<Double> getProbs(int i) {
		return probsList.get(i);
	}

	public void setProbList(List<List<Double>> probsList) {
		this.probsList = probsList;
	}

	public void addProbs(List<Double> probs) {
		this.probsList.add(probs);
	}
	
	public void addProb(int i, Double prob) {
		if(i<this.probsList.size())
			this.probsList.get(i).add(round(prob));
		else {
			List<Double> dbList = new ArrayList<Double>();
			dbList.add(round(prob));
			this.probsList.add(dbList);
		}
	}
	
	 public Double round(Double prob) {
	    	return ((int)(prob*100))/100.0;
	    	
	    }
	 
		public List<List<Integer>> getProbClass() {
			int maxIndex = 0;
			List<List<Integer>> cList = new ArrayList<List<Integer>>();
			for(int j=0;j<getProbsList().size();j++) {
				List<Integer> ccList = new ArrayList<Integer>();
				ccList.add(0);
			for(int i=1;i<getProbs(j).size();i++) {
				if(getProbs(j).get(maxIndex) < getProbs(j).get(i)) {
					maxIndex = i;
					ccList.clear();
					ccList.add(i);
				}
				else if(getProbs(j).get(maxIndex) == getProbs(j).get(i)) {
					ccList.add(i);
				}
			}
			  cList.add(ccList);
			}
		    return cList;
		 
		}

	
}
