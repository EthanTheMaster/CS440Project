package test;

import solver.*;

import java.util.*;

public class StressTester {
    public static long maxFlow(int layerSize, int numLayers) {
        LinearProgram p = new LinearProgram();

        // network[u][v] holds the capacity of edge (u, v)
        HashMap<String, HashMap<String, Double>> network = new HashMap<>();
        HashMap<String, Variable> flows = new HashMap<>();
        String s = "s";
        String t = "t";

        String flowFormat = "f.%s.%s";

        Set<String> lastLayer = new HashSet<>(Collections.singletonList(s));
        for (int l = 0; l < numLayers; l++) {
            // Create next layer
            Set<String> nextLayer = new HashSet<>();
            for (int i = 0; i < layerSize; i++) {
                String vertexName = String.format("L%d_V%d", l, i);
                nextLayer.add(vertexName);
            }

            // Create connections between last and next layer
            for (String u : lastLayer) {
                HashMap<String, Double> neighbors = new HashMap<>();
                for (String v : nextLayer) {
                    double capacity = new Random().nextDouble()*1000.0 + 1.0;
                    neighbors.put(v, capacity);

                    String flowName = String.format(flowFormat, u, v);
                    flows.put(flowName, p.registerVariable(flowName, 0.0, capacity));
                }
                network.put(u, neighbors);
            }

            lastLayer = nextLayer;
        }
        // Connect last layer to the sink vertex
        for (String u : lastLayer) {
            HashMap<String, Double> neighbors = new HashMap<>();
            double capacity = new Random().nextDouble()*1000.0 + 1.0;
            neighbors.put(t, capacity);
            network.put(u, neighbors);

            String flowName = String.format(flowFormat, u, t);
            flows.put(flowName, p.registerVariable(flowName, 0.0, capacity));
        }

        // Establish flow conservation constraints
        for (String v : network.keySet()) {
            if (!v.equals(s) && !v.equals(t)) {
                ArrayList<Variable> variables = new ArrayList<>();
                ArrayList<Double> weights = new ArrayList<>();
                // Look at all outflows
                for (String w : network.get(v).keySet()) {
                    String flowName = String.format(flowFormat, v, w);
                    variables.add(flows.get(flowName));
                    weights.add(1.0);
                }
                // Look at all inflows
                for (String u : network.keySet()) {
                    if (network.get(u).containsKey(v)) {
                        String flowName = String.format(flowFormat, u, v);
                        variables.add(flows.get(flowName));
                        weights.add(-1.0);
                    }
                }
                p.addConstraint(new Constraint(variables, weights, Relation.EQ, 0.0));
            }
        }

        // Establish optimization
        ArrayList<Variable> variables = new ArrayList<>();
        ArrayList<Double> weights = new ArrayList<>();
        // Look at all outflows from the source
        for (String v : network.get(s).keySet()) {
            String flowName = String.format(flowFormat, s, v);
            variables.add(flows.get(flowName));
            weights.add(1.0);
        }
        p.setObjective(new ObjectiveFunction(ObjectiveGoal.MAXIMIZE, variables, weights));

        long start = System.currentTimeMillis();
        p.solve();
        long time = System.currentTimeMillis() - start;

        return time;
    }
}
