package fcatools.conexpng.model;

import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.utils.ListSet;
import fcatools.conexpng.io.locale.LocaleHandler;

/**
 * This class implemented the Concept interface of the fcalib.
 * 
 */
public class LatticeConcept implements Concept<String, FullObject<String, String>> {

    private ListSet<FullObject<String, String>> extent;
    private ListSet<String> intent;
    private Long id = (long)0;

    public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public void addToId(long i) {
		this.id+= i;
	}

	public LatticeConcept() {
        extent = new ListSet<>();
        intent = new ListSet<>();
    }

    @Override
    public Set<FullObject<String, String>> getExtent() {
        return this.extent;
    }
    
    public Set<String> getExtentIds() {
    	ListSet<String> extentids = new ListSet<String> ();
    	for(FullObject<String, String> o : this.extent)
    		extentids.add(o.getIdentifier());
        return extentids;
    }

    @Override
    public Set<String> getIntent() {
        return this.intent;
    }

    @Override
    public String toString() {
        return LocaleHandler.getString("LatticeConcept.toString.objects") + extent + "\n"
                + LocaleHandler.getString("LatticeConcept.toString.attributes") + intent + "\n";
    }
}
