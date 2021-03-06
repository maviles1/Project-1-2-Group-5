package titan.flight;

import titan.interfaces.ProbeSimulatorInterface;
import titan.interfaces.StateInterface;
import titan.interfaces.Vector3dInterface;

import java.net.URL;
import java.util.ArrayList;


public class ProbeSimulator implements ProbeSimulatorInterface {

    private ArrayList<SpaceObject> spaceObjects;
    private StateInterface[] states;
    private double [] probeMass;
    private Solver solver;

    /**
     * @param space contains a List of all planets
     */
    public ProbeSimulator(ArrayList<SpaceObject> space){
        spaceObjects=space;
        solver = new Solver();
    }

    //TODO
    public ProbeSimulator(){
        URL url = getClass().getClassLoader().getResource("solar_system_data-2020_04_01.txt");
        SpaceObjectBuilder builder = new SpaceObjectBuilder(url.getPath());
        spaceObjects = builder.getSpaceObjects();
        //generate the position and velocity vector
        double[] radius = new double[]{700000, 2439.7, 6051.8, 6371, 1737.1, 3389.5, 69911, 58232, 2575.7, 25362, 2462.2, 10};
        for (int j = 0; j < spaceObjects.size(); j++) {
            spaceObjects.get(j).setRadius(radius[j]);
        }

        solver = new Solver();
    }
    /*
     * Simulate the solar system, including a probe fired from Earth at 00:00h on 1 April 2020.
     *
     * @param   p0      the starting position of the probe, relative to the earth's position.
     * @param   v0      the starting velocity of the probe, relative to the earth's velocity.
     * @param   ts      the times at which the states should be output, with ts[0] being the initial time.
     * @return  an array of size ts.length giving the position of the probe at each time stated,
     *          taken relative to the Solar System barycentre.
     */

    public Vector3dInterface[] trajectory(Vector3dInterface p0, Vector3dInterface v0, double[] ts) {
        //conversion of the input relative to the Solar System barycentre
        Vector3d v = (Vector3d) v0.add(spaceObjects.get(3).getVelocity());
        Vector3d p = (Vector3d) p0.add(spaceObjects.get(3).getPosition());

        //create probe
        Probe probe = new Probe("Probe", 78000, p, v);
        probe.setFuelMass(30000);
        spaceObjects.add(probe);
        //create State with planets and probe
        State universe = initUn();

        //creates all the states of the simulation
        StateInterface[] s = solver.solve(new ODEFunction(), universe, ts);
        this.probeMass = solver.getProbeMass();
        states=s;
        Vector3d[] vector = new Vector3d[s.length];
        for(int i=0;i<s.length;i++){
            State ph = (State) s[i];
            vector[i] = ph.getPositions()[ph.getPositions().length-1];
        }
        return vector;
    }

    /*
     * Simulate the solar system with steps of an equal size.
     * The final step may have a smaller size, if the step-size does not exactly divide the solution time range.
     *
     * @param   tf      the final time of the evolution.
     * @param   h       the size of step to be taken
     * @return  an array of size round(tf/h)+1 giving the position of the probe at each time stated,
     *          taken relative to the Solar System barycentre
     */
    public Vector3dInterface[] trajectory(Vector3dInterface p0, Vector3dInterface v0, double tf, double h) {
        //conversion
        Vector3d v = (Vector3d) v0.add(spaceObjects.get(3).getVelocity());
        Vector3d p = (Vector3d) p0.add(spaceObjects.get(3).getPosition());

        //create probe
        Probe probe = new Probe("Probe", 78000, p, v);

        spaceObjects.add(probe);
        //create State with planets and probe
        State universe = initUn();

//        Solver solver = new Solver();
//        VerletSolver solver1 = new VerletSolver();
      //  tf=h*2000; //custom 5 step

        //creates all the states of the simulation
        StateInterface[] s = solver.solve(new ODEFunction(), universe, tf, h);
//        StateInterface[] s = solver1.pstep(new ODEFunction(), tf, universe, h);
        this.probeMass = solver.getProbeMass();
        states=s;

        Vector3d[] vector = new Vector3d[s.length];
        for(int i=0;i<s.length;i++){
            State ph = (State) s[i];
            vector[i] = ph.getPositions()[ph.getPositions().length-1];
        }

        return vector;
    }
    public StateInterface[] getStates() {
        return states;
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    public State initUn() {
        //create positions and velocities arrays to represent the state
        Vector3d[] positions = new Vector3d[spaceObjects.size()];
        Vector3d[] velocities = new Vector3d[spaceObjects.size()];
        double[] mass = new double[spaceObjects.size()];

        int i = 0;
        for (SpaceObject spaceObject : spaceObjects) {
            positions[i] = spaceObject.getPosition();
            velocities[i] = spaceObject.getVelocity();
            mass[i++] = spaceObject.getMass();
        }

        //create the initial state
        State.setMass(mass);
        State.setNames();
        State.setRadius(new double[]{700000, 2439.7, 6051.8, 6371e3, 1737.1, 3389.5, 69911, 58232, 2575.5e3, 25362, 2462.2, 10, 1});

        return new State(positions, velocities, 0);
    }

    public StateInterface[] simulation(){
        return states;
    }

    public double [] getProbeMass(){
        return probeMass;
    }
}
