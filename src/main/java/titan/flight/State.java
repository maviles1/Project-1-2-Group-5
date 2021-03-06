package titan.flight;

import titan.interfaces.RateInterface;
import titan.interfaces.StateInterface;

import java.util.HashMap;
import java.util.Map;

public class State implements StateInterface {

    static double[] mass;
    public static Map<Integer, String> names;
    public Vector3d[] positions;
    public Vector3d[] velocities;
    public static double[] radius;
    double time;
    SpaceObject [] bodies;

    public State(Vector3d[] positions, Vector3d[] velocities, double time) {
        this.positions = positions;
        this.velocities = velocities;
        this.time = time;
        bodies = new Planet [positions.length];
//        for (int i = 0; i < positions.length; i++){
//            bodies[i] = new Planet(names.get(i), mass[i], positions[i], velocities[i], radius[i]);
//        }
    }
    @Override
    public StateInterface addMul(double step, RateInterface r) {
        Rate rate = (Rate) r;

        Vector3d[] newPositions = new Vector3d[positions.length];
        Vector3d[] newVelocities = new Vector3d[velocities.length];

        for (int i = 0; i < velocities.length; i++) {
            newPositions[i] = positions[i].addMul(step, rate.getPosRates()[i]);
            newVelocities[i] = velocities[i].addMul(step, rate.getVelRates()[i]);
        }

        return new State(newPositions, newVelocities, time + step);
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < positions.length; i++) {
            s += names.get(i)
                    + " { x=" + positions[i].getX()
                    + ", y=" + positions[i].getY()
                    + ", z=" + positions[i].getZ()
                    + " vx=" + velocities[i].getX()
                    + ", vy=" + velocities[i].getY()
                    + ", vz=" + velocities[i].getZ() + " }\n";
        }
        return s;
    }

    public static void setMass(double[] mass) {
        State.mass = mass;
    }

    public static void setNames() {
        names = new HashMap<>();
        for (int i = 0; i < SpaceObjectBuilder.spaceObjects.size(); i++) {
            names.put(i, SpaceObjectBuilder.spaceObjects.get(i).getName());
        }
    }

    public State copy(){
        Vector3d [] positions = new Vector3d[this.positions.length];
        Vector3d [] velocities = new Vector3d[this.velocities.length];
        for (int i = 0; i < this.positions.length; i++){
            positions[i] = this.positions[i];
            velocities[i] = this.velocities[i];
        }
        return new State(positions, velocities, 0);
    }

    public static void setRadius(double [] radius){
        State.radius = radius;
    }

    @Override
    public Vector3d[] getPositions(){
        return positions;
    }

    public Vector3d[] getVelocities(){
        return velocities;
    }

    public SpaceObject [] getBodies(){
        return bodies;
    }

    public double getTime() {
        return this.time;
    }
}
