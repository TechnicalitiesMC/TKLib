package com.technicalitiesmc.lib.circuit.interfaces.wire;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public abstract class RedstoneConductor {

    @Nullable
    private Network network;
    private boolean propagate = true;
    private int input;

    public abstract int getPower();

    public abstract void visit(Visitor visitor);

    public abstract void onPropagated(int power);

    public abstract void scheduleSequentialUpdate();

    public void doSequentialUpdate() {
        // If we don't have a network, build it and force propagation
        // The network will be assigned via the wire interface so no further
        // network reconstructions will happen during this sequential update
        if (network == null) {
            Network.build(this);
            propagate = true;
        }
        // Propagate if needed
        // This flag will be set to false when the wire gets a new power level
        // so no further propagations will happen during this sequential update
        if (network != null && propagate) {
            network.propagate();
        }
    }

    public void setInput(int newInput) {
        // If the input has changed and either
        //  - It's turning off and this wire was potentially contributing to the network's value (input == power)
        //  - The new input would contribute to the current signal (newInput > power)
        var power = getPower();
        if (newInput != input && ((input == power && newInput < power) || newInput > power)) {
            // Schedule a propagation pass
            propagate = true;
            scheduleSequentialUpdate();
        }
        // Update the input regardless of if it results in a network update or not
        input = newInput;
    }

    public void setInputOnLoad(int newInput) {
        input = newInput;
    }

    public void invalidateNetwork() {
        if (network != null) {
            network.invalidate();
        }
    }

    public interface Visitor {

        void accept(RedstoneConductor conductor);

    }

    private static final class Network {

        private final Set<RedstoneConductor> conductors;

        private Network(Set<RedstoneConductor> conductors) {
            this.conductors = conductors;
        }

        public void propagate() {
            var power = 0;
            for (var conductor : conductors) {
                power = Math.max(power, conductor.input);
            }
            for (var conductor : conductors) {
                conductor.propagate = false;
                conductor.onPropagated(power);
            }
        }

        public void invalidate() {
            for (var conductor : conductors) {
                conductor.network = null;
                conductor.propagate = true;
                conductor.scheduleSequentialUpdate();
            }
        }

        public static void build(RedstoneConductor origin) {
            var conductors = new HashSet<RedstoneConductor>();
            var queue = new ArrayDeque<RedstoneConductor>();
            var network = new Network(conductors);
            queue.add(origin);
            while (!queue.isEmpty()) {
                var conductor = queue.pop();
                if (conductors.add(conductor)) {
                    conductor.network = network;
                    conductor.visit(queue::add);
                }
            }
        }

    }

}
