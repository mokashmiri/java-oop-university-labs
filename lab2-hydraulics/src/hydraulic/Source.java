package hydraulic;

/**
 * Represents a source of water, i.e. the initial element for the simulation.
 *
 * Lo status of the source is defined through the method
 * {@link #setFlow(double) setFlow()}.
 */
public class Source extends Element {
	
	private double flow;

	/**
	 * constructor
	 * @param name name of the source element
	 */
	public Source(String name) {
		super(name);
		this.flow = 0.0;
	}

	/**
	 * Define the flow of the source to be used during the simulation
	 *
	 * @param flow flow of the source (in cubic meters per hour)
	 */
	public void setFlow(double flow){
		this.flow = flow;
	}
	
	/**
	 * Get the flow of the source
	 * @return the flow value
	 */
	public double getFlow() {
		return flow;
	}
	
	@Override
	public void setMaxFlow(double maxFlow) {
		// Source has no input, so maxFlow has no effect
		// Do nothing as per requirements
	}

}
