package hydraulic;

import java.util.Stack;

/**
 * Hydraulics system builder providing a fluent API
 */
public class HBuilder {
	
	private HSystem system;
	private Element currentElement;
	private Stack<Split> splitStack;
	private Stack<Integer> outputIndexStack;
	private boolean inSplitMode;

    /**
     * Add a source element with the given name
     * 
     * @param name name of the source element to be added
     * @return the builder itself for chaining 
     */
    public HBuilder addSource(String name) {
        system = new HSystem();
        Source source = new Source(name);
        system.addElement(source);
        currentElement = source;
        inSplitMode = false;
        splitStack = new Stack<>();
        outputIndexStack = new Stack<>();
        return this;
    }

    /**
     * returns the hydraulic system built with the previous operations
     * 
     * @return the hydraulic system
     */
    public HSystem complete() {
        return system;
    }

    /**
     * creates a new tap and links it to the previous element
     * 
     * @param name name of the tap
     * @return the builder itself for chaining 
     */
    public HBuilder linkToTap(String name) {
        Tap tap = new Tap(name);
        system.addElement(tap);
        
        if (inSplitMode && !splitStack.isEmpty()) {
            Split currentSplit = splitStack.peek();
            int currentOutputIndex = outputIndexStack.peek();
            // If we are at the start of an output branch, attach to the split; otherwise, chain
            if (currentElement == currentSplit) {
            currentSplit.connect(tap, currentOutputIndex);
            } else {
                currentElement.connect(tap);
            }
        } else {
            currentElement.connect(tap);
        }
        
        currentElement = tap;
        return this;
    }

    /**
     * creates a sink and link it to the previous element
     * @param name name of the sink
     * @return the builder itself for chaining 
     */
    public HBuilder linkToSink(String name) {
        Sink sink = new Sink(name);
        system.addElement(sink);
        
        if (inSplitMode && !splitStack.isEmpty()) {
            Split currentSplit = splitStack.peek();
            int currentOutputIndex = outputIndexStack.peek();
            if (currentElement == currentSplit) {
            currentSplit.connect(sink, currentOutputIndex);
            } else {
                currentElement.connect(sink);
            }
        } else {
            currentElement.connect(sink);
        }
        
        currentElement = sink;
        return this;
    }

    /**
     * creates a split and links it to the previous element
     * @param name of the split
     * @return the builder itself for chaining 
     */
    public HBuilder linkToSplit(String name) {
        Split split = new Split(name);
        system.addElement(split);
        
        if (inSplitMode && !splitStack.isEmpty()) {
            Split currentSplit = splitStack.peek();
            int currentOutputIndex = outputIndexStack.peek();
            if (currentElement == currentSplit) {
            currentSplit.connect(split, currentOutputIndex);
            } else {
                currentElement.connect(split);
            }
        } else {
            currentElement.connect(split);
        }
        
        currentElement = split;
        splitStack.push(split);
        outputIndexStack.push(0);
        inSplitMode = true;
        return this;
    }

    /**
     * creates a multisplit and links it to the previous element
     * @param name name of the multisplit
     * @param numOutput the number of output of the multisplit
     * @return the builder itself for chaining 
     */
    public HBuilder linkToMultisplit(String name, int numOutput) {
        Multisplit multisplit = new Multisplit(name, numOutput);
        system.addElement(multisplit);
        
        if (inSplitMode && !splitStack.isEmpty()) {
            Split currentSplit = splitStack.peek();
            int currentOutputIndex = outputIndexStack.peek();
            if (currentElement == currentSplit) {
            currentSplit.connect(multisplit, currentOutputIndex);
            } else {
                currentElement.connect(multisplit);
            }
        } else {
            currentElement.connect(multisplit);
        }
        
        currentElement = multisplit;
        splitStack.push(multisplit);
        outputIndexStack.push(0);
        inSplitMode = true;
        return this;
    }

    /**
     * introduces the elements connected to the first output 
     * of the latest split/multisplit.
     * The element connected to the following outputs are 
     * introduced by {@link #then()}.
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder withOutputs() {
        if (!outputIndexStack.isEmpty()) {
            outputIndexStack.pop();
            outputIndexStack.push(0);
        }
        // Start defining children from the split itself
        if (!splitStack.isEmpty()) {
            currentElement = splitStack.peek();
        }
        return this;     
    }

    /**
     * inform the builder that the next element will be
     * linked to the successive output of the previous split or multisplit.
     * The element connected to the first output is
     * introduced by {@link #withOutputs()}.
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder then() {
        if (inSplitMode && !outputIndexStack.isEmpty()) {
            int currentIndex = outputIndexStack.pop();
            outputIndexStack.push(currentIndex + 1);
        }
        // Move back to the split so the next branch attaches to it
        if (!splitStack.isEmpty()) {
            currentElement = splitStack.peek();
        }
        return this;
    }

    /**
     * completes the definition of elements connected
     * to outputs of a split/multisplit. 
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder done() {
        if (!splitStack.isEmpty()) {
            splitStack.pop();
            outputIndexStack.pop();
            
            if (!splitStack.isEmpty()) {
                currentElement = splitStack.peek();
                inSplitMode = true;
            } else {
                inSplitMode = false;
            }
        }
        return this;
    }

    /**
     * define the flow of the previous source
     * 
     * @param flow flow used in the simulation
     * @return the builder itself for chaining 
     */
    public HBuilder withFlow(double flow) {
        if (currentElement instanceof Source) {
            ((Source) currentElement).setFlow(flow);
        }
        return this;
    }

    /**
     * define the status of a tap as open,
     * it will be used in the simulation
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder open() {
        if (currentElement instanceof Tap) {
            ((Tap) currentElement).setOpen(true);
        }
        return this;
    }

    /**
     * define the status of a tap as closed,
     * it will be used in the simulation
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder closed() {
        if (currentElement instanceof Tap) {
            ((Tap) currentElement).setOpen(false);
        }
        return this;
    }

    /**
     * define the proportions of input flow distributed
     * to each output of the preceding a multisplit
     * 
     * @param props the proportions
     * @return the builder itself for chaining 
     */
    public HBuilder withProportions(double... props) {
        if (currentElement instanceof Multisplit) {
            ((Multisplit) currentElement).setProportions(props);
        }
        return this;
    }
    
    /**
     * define the proportions of input flow distributed
     * to each output of the preceding a multisplit
     * (legacy method name with typo for backward compatibility)
     * 
     * @param props the proportions
     * @return the builder itself for chaining 
     */
    public HBuilder withPropotions(double[] props) {
        if (currentElement instanceof Multisplit) {
            ((Multisplit) currentElement).setProportions(props);
        }
        return this;
    }

    /**
     * define the maximum flow theshold for the previous element
     * 
     * @param max flow threshold
     * @return the builder itself for chaining 
     */
    public HBuilder maxFlow(double max) {
        if (currentElement != null) {
            currentElement.setMaxFlow(max);
        }
        return this;
    }
}