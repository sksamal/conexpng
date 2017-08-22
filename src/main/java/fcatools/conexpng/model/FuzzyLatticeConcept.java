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
public class FuzzyLatticeConcept implements Concept<String, FullObject<String, FuzzyObject<String,Double>>> {

    private ListSet<FullObject<String, FuzzyObject<String,Double>>> extent;
    private ListSet<String> intent;

    public FuzzyLatticeConcept() {
        extent = new ListSet<FullObject<String, FuzzyObject<String,Double>>>();
        intent = new ListSet<>();
    }

    @Override
    public Set<FullObject<String, FuzzyObject<String,Double>>> getExtent() {
        return this.extent;
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
