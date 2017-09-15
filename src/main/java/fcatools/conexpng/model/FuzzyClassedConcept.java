package fcatools.conexpng.model;

import java.util.ArrayList;
import java.util.List;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;

public class FuzzyClassedConcept extends LatticeConcept {

	private List<Double> probs;

	public FuzzyClassedConcept() {
		super();
		probs = new ArrayList<Double>();
	}
	
	@Override
	public String toString() {
		return super.toString() + " [probs=" + probs + "]";
	}

	public FuzzyClassedConcept(Concept<String, FullObject<String, String>> c) {
		for(FullObject<String, String> obj: c.getExtent())
			this.getExtent().add(obj);
		for(String att: c.getIntent())
			this.getIntent().add(att);
		probs = new ArrayList<Double>();
	}
	
	public List<Double> getProb() {
		return probs;
	}

	public void setProb(List<Double> probs) {
		this.probs = probs;
	}

	public void addProb(Double prob) {
		this.probs.add(prob);
	}
}
