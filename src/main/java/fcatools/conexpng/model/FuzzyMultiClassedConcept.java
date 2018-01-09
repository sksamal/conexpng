package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.List;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;

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
			this.probsList.get(i).add(prob);
		else {
			List<Double> dbList = new ArrayList<Double>();
			dbList.add(prob);
			this.probsList.add(dbList);
		}
	}
}
