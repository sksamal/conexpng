package fcatools.conexpng.model;

public class OAPair<O,A> {
	O object;
	A attribute;

	public OAPair(A attribute,O object) {
		super();
		this.object = object;
		this.attribute = attribute;
	}
	public O getObject() {
		return object;
	}
	public void setObject(O object) {
		this.object = object;
	}
	public A getAttribute() {
		return attribute;
	}
	public void setAttribute(A attribute) {
		this.attribute = attribute;
	}
	
	@Override
	public String toString() {
		return "OAPair [object=" + object + ", attribute=" + attribute + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof OAPair)
			return (((OAPair) obj).getAttribute().equals(attribute) && ((OAPair) obj).getObject().equals(object));
		return false;
	}
	
	@Override
	public int hashCode() {
		return (object.hashCode()+attribute.hashCode());
	}
	
	
}
