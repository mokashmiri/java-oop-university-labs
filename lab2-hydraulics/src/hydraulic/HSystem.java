package hydraulic;

/**
 * Main class that acts as a container of the elements for
 * the simulation of a hydraulics system
 * 
 */
public class HSystem {
	
	private Element[] elements;
	private int count;

	public HSystem() {
		elements = new Element[100]; // Maximum 100 elements as per hint
		count = 0;
	}

// R1
	/**
	 * Adds a new element to the system
	 * 
	 * @param elem the new element to be added to the system
	 */
	public void addElement(Element elem){
		if (elem != null && count < 100) {
			elements[count] = elem;
			count++;
		}
	}

	/**
	 * returns the number of elements currently present in the system
	 * 
	 * @return count of elements
	 */
	public int size() {
        return count;
    }

	/**
	 * returns the element added so far to the system
	 * 
	 * @return an array of elements whose length is equal to 
	 * 							the number of added elements
	 */
	public Element[] getElements(){
		Element[] result = new Element[count];
		for (int i = 0; i < count; i++) {
			result[i] = elements[i];
		}
		return result;
	}

// R4
	/**
	 * Starts the simulation of the system.
	 * The notifications about the simulations are sent
	 * to an observer object
	 * Before starting simulation, the parameters of the
	 * elements in the system must be defined
	 * 
	 * @param observer the observer receiving notifications
	 */
	public void simulate(SimulationObserver observer){
		simulate(observer, false);
	}


// R6
	/**
	 * Deletes a previously added element 
	 * with the given name from the system
	 */
	public boolean deleteElement(String name) {
		// Find the element to delete
		Element elementToDelete = null;
		int indexToDelete = -1;
		
		for (int i = 0; i < count; i++) {
			if (elements[i] != null && elements[i].getName().equals(name)) {
				elementToDelete = elements[i];
				indexToDelete = i;
				break;
			}
		}
		
		if (elementToDelete == null) {
			return false; // Element not found
		}
		
		// Check if it's a Split or Multisplit with more than one output connected
		if (elementToDelete instanceof Split) {
			Split split = (Split) elementToDelete;
			Element[] outputs = split.getOutputs();
			int connectedOutputs = 0;
			for (Element output : outputs) {
				if (output != null) {
					connectedOutputs++;
				}
			}
			if (connectedOutputs > 1) {
				return false; // Cannot delete split/multisplit with multiple outputs
			}
		}
		
		// Find upstream and downstream elements
		Element upstreamElement = findUpstreamElement(elementToDelete);
		Element downstreamElement = getSingleDownstreamElement(elementToDelete);
		
		// Fix upstream connection: either bypass to downstream or clear the link
		if (upstreamElement != null) {
			if (upstreamElement instanceof Split) {
				Split upstreamSplit = (Split) upstreamElement;
				Element[] outputs = upstreamSplit.getOutputs();
				for (int i = 0; i < outputs.length; i++) {
					if (outputs[i] == elementToDelete) {
						// If there is a downstream element, rewire to it; otherwise, clear the slot
						if (downstreamElement != null) {
							upstreamSplit.connect(downstreamElement, i);
						} else {
							upstreamSplit.connect(null, i);
						}
						break;
					}
				}
			} else {
				// Single-output upstream
				if (downstreamElement != null) {
					upstreamElement.connect(downstreamElement);
				} else {
					upstreamElement.connect(null);
				}
			}
		}
		
		// Remove the element from the array
		for (int i = indexToDelete; i < count - 1; i++) {
			elements[i] = elements[i + 1];
		}
		elements[count - 1] = null;
		count--;
		
		return true;
	}
	
	/**
	 * Find the upstream element that connects to the given element
	 * @param element the element to find upstream for
	 * @return the upstream element or null if not found
	 */
	private Element findUpstreamElement(Element element) {
		for (int i = 0; i < count; i++) {
			Element candidate = elements[i];
			if (candidate != null && isConnectedTo(candidate, element)) {
				return candidate;
			}
		}
		return null;
	}
	
	/**
	 * Get the single downstream element (for elements with single output)
	 * @param element the element to get downstream for
	 * @return the downstream element or null if not found
	 */
	private Element getSingleDownstreamElement(Element element) {
		if (element instanceof Split) {
			Split split = (Split) element;
			Element[] outputs = split.getOutputs();
			// Return the first non-null output
			for (Element output : outputs) {
				if (output != null) {
					return output;
				}
			}
		} else {
			return element.getOutput();
		}
		return null;
	}

// R7
	/**
	 * Starts the simulation of the system; if {@code enableMaxFlowCheck} is {@code true},
	 * checks also the elements maximum flows against the input flow.
	 * If {@code enableMaxFlowCheck} is {@code false}  a normal simulation as
	 * the method {@link #simulate(SimulationObserver)} is performed.
	 * Before performing a checked simulation, the max flows of the elements in the
	 * system must be defined.
	 */
	public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
		// Find the source element (root of the tree)
		Source source = findSource();
		if (source == null) {
			return; // No source found, cannot simulate
		}
		
		// Start simulation from the source
		simulateElement(source, observer, enableMaxFlowCheck);
	}
	
	/**
	 * Find the source element in the system
	 * @return the source element or null if not found
	 */
	private Source findSource() {
		for (int i = 0; i < count; i++) {
			if (elements[i] instanceof Source) {
				return (Source) elements[i];
			}
		}
		return null;
	}
	
	/**
	 * Recursively simulate an element and its downstream elements
	 * @param element the element to simulate
	 * @param observer the observer to notify
	 * @param enableMaxFlowCheck whether to check max flow
	 */
	private void simulateElement(Element element, SimulationObserver observer, boolean enableMaxFlowCheck) {
		if (element == null) {
			return;
		}
		
		double inputFlow = SimulationObserver.NO_FLOW;
		double[] outputFlows = null;
		
		// Calculate input and output flows based on element type
		if (element instanceof Source) {
			Source source = (Source) element;
			inputFlow = SimulationObserver.NO_FLOW; // Source has no input
			outputFlows = new double[]{source.getFlow()};
		} else if (element instanceof Tap) {
			Tap tap = (Tap) element;
			// For tap, we need to get input flow from upstream
			inputFlow = getInputFlow(element);
			if (enableMaxFlowCheck && inputFlow > element.maxFlow) {
				observer.notifyFlowError(element.getClass().getSimpleName(), element.getName(), inputFlow, element.maxFlow);
				// Continue simulation even after error
			}
			outputFlows = new double[]{tap.isOpen() ? inputFlow : 0.0};
		} else if (element instanceof Split) {
			Split split = (Split) element;
			inputFlow = getInputFlow(element);
			if (enableMaxFlowCheck && inputFlow > element.maxFlow) {
				observer.notifyFlowError(element.getClass().getSimpleName(), element.getName(), inputFlow, element.maxFlow);
				// Continue simulation even after error
			}
			Element[] outputs = split.getOutputs();
			outputFlows = new double[outputs.length];
			
			if (split instanceof Multisplit) {
				Multisplit multisplit = (Multisplit) split;
				double[] proportions = multisplit.getProportions();
				for (int i = 0; i < outputs.length; i++) {
					outputFlows[i] = outputs[i] != null ? inputFlow * proportions[i] : 0.0;
				}
			} else {
				// Regular split - divide equally
				for (int i = 0; i < outputs.length; i++) {
					outputFlows[i] = outputs[i] != null ? inputFlow / 2.0 : 0.0;
				}
			}
		} else if (element instanceof Sink) {
			Sink sink = (Sink) element;
			inputFlow = getInputFlow(element);
			if (enableMaxFlowCheck && inputFlow > element.maxFlow) {
				observer.notifyFlowError(element.getClass().getSimpleName(), element.getName(), inputFlow, element.maxFlow);
				// Continue simulation even after error
			}
			outputFlows = new double[]{SimulationObserver.NO_FLOW}; // Sink has no output
		}
		
		// Notify observer about this element
		observer.notifyFlow(element.getClass().getSimpleName(), element.getName(), inputFlow, outputFlows);
		
		// Recursively simulate downstream elements
		if (element instanceof Split) {
			Split split = (Split) element;
			Element[] outputs = split.getOutputs();
			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i] != null) {
					simulateElement(outputs[i], observer, enableMaxFlowCheck);
				}
			}
		} else if (element.getOutput() != null) {
			simulateElement(element.getOutput(), observer, enableMaxFlowCheck);
		}
	}
	
	
	/**
	 * Get the input flow for an element by finding its upstream element
	 * @param element the element to get input flow for
	 * @return the input flow
	 */
	private double getInputFlow(Element element) {
		java.util.Map<Element, Double> flowMap = new java.util.HashMap<>();
		return getInputFlow(element, flowMap);
	}
	
	/**
	 * Get the input flow for an element by finding its upstream element
	 * This version uses a map to avoid infinite recursion
	 * @param element the element to get input flow for
	 * @param flowMap the map storing calculated flows
	 * @return the input flow
	 */
	private double getInputFlow(Element element, java.util.Map<Element, Double> flowMap) {
		if (element instanceof Source) {
			return SimulationObserver.NO_FLOW; // Source has no input
		}
		
		// Check if we already calculated this element's input flow
		if (flowMap.containsKey(element)) {
			return flowMap.get(element);
		}
		
		// Find the upstream element that connects to this element
		for (int i = 0; i < count; i++) {
			Element upstream = elements[i];
			if (upstream != null && isConnectedTo(upstream, element)) {
				double outputFlow = getOutputFlowForConnection(upstream, element, flowMap);
				flowMap.put(element, outputFlow);
				return outputFlow;
			}
		}
		
		flowMap.put(element, 0.0);
		return 0.0; // Default if no upstream found
	}
	
	/**
	 * Get the output flow from upstream element that connects to downstream element
	 * @param upstream the upstream element
	 * @param downstream the downstream element
	 * @param flowMap the map storing calculated flows
	 * @return the output flow
	 */
	private double getOutputFlowForConnection(Element upstream, Element downstream, java.util.Map<Element, Double> flowMap) {
		if (upstream instanceof Split) {
			Split split = (Split) upstream;
			Element[] outputs = split.getOutputs();
			double inputFlow = getInputFlow(upstream, flowMap);
			
			// Find which output connects to the downstream element
			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i] == downstream) {
					if (split instanceof Multisplit) {
						Multisplit multisplit = (Multisplit) split;
						double[] proportions = multisplit.getProportions();
						return inputFlow * proportions[i];
					} else {
						// Regular split - divide equally
						return inputFlow / 2.0;
					}
				}
			}
		} else {
			// Single output element
			return getOutputFlow(upstream, flowMap);
		}
		return 0.0;
	}
	
	/**
	 * Check if upstream element is connected to downstream element
	 * @param upstream the upstream element
	 * @param downstream the downstream element
	 * @return true if connected
	 */
	private boolean isConnectedTo(Element upstream, Element downstream) {
		if (upstream instanceof Split) {
			Split split = (Split) upstream;
			Element[] outputs = split.getOutputs();
			for (Element output : outputs) {
				if (output == downstream) {
					return true;
				}
			}
		} else if (upstream.getOutput() == downstream) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get the output flow from an element (used for calculating input flow of downstream elements)
	 * @param element the element to get output flow from
	 * @return the output flow
	 */
	private double getOutputFlow(Element element) {
		java.util.Map<Element, Double> flowMap = new java.util.HashMap<>();
		return getOutputFlow(element, flowMap);
	}
	
	/**
	 * Get the output flow from an element (used for calculating input flow of downstream elements)
	 * This version uses a map to avoid infinite recursion
	 * @param element the element to get output flow from
	 * @param flowMap the map storing calculated flows
	 * @return the output flow
	 */
	private double getOutputFlow(Element element, java.util.Map<Element, Double> flowMap) {
		if (element instanceof Source) {
			return ((Source) element).getFlow();
		} else if (element instanceof Tap) {
			Tap tap = (Tap) element;
			double inputFlow = getInputFlow(element, flowMap);
			return tap.isOpen() ? inputFlow : 0.0;
		} else if (element instanceof Split) {
			Split split = (Split) element;
			double inputFlow = getInputFlow(element, flowMap);
			if (split instanceof Multisplit) {
				Multisplit multisplit = (Multisplit) split;
				double[] proportions = multisplit.getProportions();
				// Return the total output flow (sum of all outputs)
				double total = 0.0;
				for (double prop : proportions) {
					total += inputFlow * prop;
				}
				return total;
			} else {
				// Regular split - total output equals input
				return inputFlow;
			}
		}
		return 0.0;
	}

// R8
	/**
	 * creates a new builder that can be used to create a 
	 * hydraulic system through a fluent API 
	 * 
	 * @return the builder object
	 */
    public static HBuilder build() {
		return new HBuilder();
    }
}
