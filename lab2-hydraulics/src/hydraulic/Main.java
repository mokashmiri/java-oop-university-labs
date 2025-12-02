package hydraulic;

/**
 * Example main class to demonstrate the hydraulic system
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Hydraulic System Simulation ===\n");
        
        // Create a simple system
        HSystem system = new HSystem();
        
        // Create elements
        Source source = new Source("Source1");
        Tap tap = new Tap("Tap1");
        Split split = new Split("Split1");
        Sink sinkA = new Sink("SinkA");
        Sink sinkB = new Sink("SinkB");
        
        // Add elements to system
        system.addElement(source);
        system.addElement(tap);
        system.addElement(split);
        system.addElement(sinkA);
        system.addElement(sinkB);
        
        // Connect elements
        source.connect(tap);
        tap.connect(split);
        split.connect(sinkA, 0);
        split.connect(sinkB, 1);
        
        // Set simulation parameters
        source.setFlow(20.0);
        tap.setOpen(true);
        
        // Run simulation
        System.out.println("Running simulation with flow = 20.0\n");
        system.simulate((SimulationObserver.Level level, String type, String name, double inFlow, double... outFlow) -> {
            System.out.println(type + " [" + name + "]:");
            if (!Double.isNaN(inFlow)) {
                System.out.println("  Input flow:  " + inFlow);
            }
            System.out.print("  Output flow: ");
            for (double f : outFlow) {
                if (!Double.isNaN(f)) System.out.print(f + " ");
            }
            System.out.println("\n");
        });
        
        System.out.println("=== Simulation Complete ===");
        System.out.println("Total elements in system: " + system.size());
    }
}

