package hydraulic;

/**
 * Represents a split element, a.k.a. T element
 * 
 * During the simulation each downstream element will
 * receive a stream that is half the input stream of the split.
 */

public class Split extends Element {
	
	protected Element[] outputs;

	/**
	 * Constructor
	 * @param name name of the split element
	 */
	public Split(String name) {
		super(name);
		this.outputs = new Element[2]; // Split has exactly 2 outputs
	}
	
	@Override
	public void connect(Element elem) {
		// Connect to first output (index 0)
		connect(elem, 0);
	}
	
	@Override
	public void connect(Element elem, int index) {
		if (index >= 0 && index < 2) {
			outputs[index] = elem;
		}
	}
	
	@Override
	public Element getOutput() {
		// Return first output for backward compatibility
		return outputs[0];
	}
	
	@Override
	public Element[] getOutputs() {
		return outputs.clone(); // Return a copy to prevent external modification
	}
	
}
