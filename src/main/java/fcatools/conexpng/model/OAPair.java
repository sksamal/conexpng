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
	
}
