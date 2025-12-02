package hydraulic;

/**
 * Represents the sink, i.e. the terminal element of a system
 *
 */
public class Sink extends Element {

	/**
	 * Constructor
	 * @param name name of the sink element
	 */
	public Sink(String name) {
		super(name);
	}
	
	@Override
	public void connect(Element elem) {
		// Sink has no output, so connect() has no effect
		// Do nothing as per requirements
	}
	
	@Override
	public Element getOutput() {
		// Sink has no output
		return null;
	}
}
