package titan.landing;

import titan.flight.Rate;
import titan.flight.State;
import titan.flight.Vector3d;
import titan.interfaces.RateInterface;
import titan.interfaces.StateInterface;

import java.util.Vector;

public class LandingFunction implements RateInterface {
    private double titanforce = 1.352; //titan gravity m/s (ms-2)
    private double distance; //probe to titans distance
    private final Vector3d titanpos = new Vector3d(0,0,0);
    private Vector3d gravity;

    public Vector3d getGravityVector(Vector3d shuttle) //shuttles position
    {
        distance = shuttle.dist(titanpos);
        gravity = titanpos.sub(shuttle);
        gravity = gravity.mul(titanforce/gravity.norm());

        return new Vector3d(gravity.getX(), gravity.getY(), gravity.getZ());
    }

    //wind
    public Vector3d[] getWindVectors()
    {
        WindModel wm = new WindModel();
        // create several wind vectors that affect te shuttle
        //

        return wm.getWindVectors();
    }

    //thruster


    public RateInterface call(double t, StateInterface y)
    {
        State state = (State)y;
        double dt = t - state.getTime();
        Vector3d change = getGravityVector(state.getPositions()[0]); // Gravity + wind + thruster

        Vector3d[] velocity = new Vector3d[1];
        Vector3d[] acceleration = new Vector3d[1];

        acceleration[0] = change;
        velocity[0] = state.getVelocities()[0];

        return new Rate(velocity, acceleration);
    }

}