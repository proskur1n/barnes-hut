import codedraw.CodeDraw;
import codedraw.Palette;
import java.awt.Color;

public class Body {

    private Vector3 position;
    private Vector3 velocity;
    private Vector3 force;
    private double mass;
    private Color color;

    private static final Color[] palette = {
            Palette.BISQUE,
            Palette.TURQUOISE,
            Palette.STEEL_BLUE
    };

    public Body(double mass, Vector3 position, Vector3 velocity) {
        this.position = position;
        this.velocity = velocity;
        this.force = new Vector3();
        this.mass = mass;
        this.color = getRandomColor();
    }

    // Returns the Morton code based on this bodie's position.
    public long getMortonCode(Vector3 origin, double size) {
        double x = (position.x - origin.x) / size;
        double y = (position.y - origin.y) / size;
        double z = (position.z - origin.z) / size;
        return MortonCode.get(x, y, z);
    }

    public double distanceSqrd(Body other) {
        return position.distanceSqrd(other.position);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Adds the gravitational force exerted by 'b' on this body. The current
    // position and velocity remain unchanged until you call update.
    public void addGravitationalForceFrom(Body b) {
        // I got a large speedup from 66ms to only 39ms for 10000 bodies by
        // simply replacing Vector3 with pure math.
        double x = b.position.x - position.x;
        double y = b.position.y - position.y;
        double z = b.position.z - position.z;
        double len = Math.sqrt(x * x + y * y + z * z);
        double m = Simulation.G * mass * b.mass / (len * len * len);
        force.x += x * m;
        force.y += y * m;
        force.z += z * m;
        // Vector3 direction = b.position.minus(position);
        // double distance = direction.length();
        // direction.normalize();
        // double force = Simulation.G * mass * b.mass / (distance * distance);
        // this.force = this.force.plus(direction.times(force));
    }

    // Moves the body according to its current force and velocity. Afterwards,
    // this bodie's force is reset to 0.
    public void update(double deltaTime) {
        Vector3 acceleration = force.times(1 / mass); // F = m*a -> a = F/m
        Vector3.multThenAdd(velocity, acceleration, deltaTime);
        Vector3.multThenAdd(position, velocity, deltaTime);
        force.setToZero();
    }

    // Returns an incomplete copy of this body that can be used to calculate
    // gravitational force. There is only a small subset of methods that you can
    // call on a partial body.
    public Body makePartialCopy() {
        return new Body(mass, position);
    }

    // Merges the mass and position of bodies 'a' and 'b' and saves the result
    // in 'a'. Body 'b' remains unchanged.
    public static void partialMerge(Body a, Body b) {
        double f = b.mass / (a.mass + b.mass);
        Vector3.mix(a.position, b.position, f);
        a.mass = a.mass + b.mass;
    }

    public void draw(CodeDraw cd) {
        cd.setColor(color);
        // TODO remove this mass to radius
        position.drawAsFilledCircle(cd, SpaceDraw.massToRadius(mass));
    }

    // Used by makePartialCopy.
    private Body(double mass, Vector3 position) {
        this.mass = mass;
        this.position = new Vector3(position);
    }

    private static Color getRandomColor() {
        int i = (int) (Math.random() * palette.length);
        return palette[i];
    }
}