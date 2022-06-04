import codedraw.CodeDraw;
import codedraw.Palette;
import java.awt.Color;

public class Body extends Massive {

    private Vector3 velocity;
    private Vector3 force;
    private Color color;

    private static final Color[] palette = {
            Palette.BISQUE,
            Palette.TURQUOISE,
            Palette.STEEL_BLUE
    };

    public Body(double mass, Vector3 position, Vector3 velocity) {
        super(mass, position);
        this.velocity = velocity;
        this.force = new Vector3();
        this.color = getRandomColor();
    }

    // Returns the Morton code based on this bodie's position.
    public long getMortonCode(Vector3 origin, double size) {
        double x = (position.x - origin.x) / size;
        double y = (position.y - origin.y) / size;
        double z = (position.z - origin.z) / size;
        return MortonCode.get(x, y, z);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double radius() {
        final double SUN_MASS = 1.989e30;
        final double SUN_RADIUS = 696340e3;
        return SUN_RADIUS * Math.sqrt(mass / SUN_MASS);
    }

    // Adds the gravitational force exerted by 'b' on this body. The current
    // position and velocity remain unchanged until you call update.
    public void addGravitationalForceFrom(Massive b) {
        // I got a large speedup from 66ms to only 39ms for 10000 bodies by
        // simply replacing Vector3 with pure math.
        double dx = b.position.x - position.x;
        double dy = b.position.y - position.y;
        double dz = b.position.z - position.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double m = Simulation.G * mass * b.mass / (len * len * len);
        force.x += dx * m;
        force.y += dy * m;
        force.z += dz * m;
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

    public void draw(CodeDraw cd) {
        cd.setColor(color);
        position.drawAsFilledCircle(cd, radius());
    }

    private static Color getRandomColor() {
        int i = (int) (Math.random() * palette.length);
        return palette[i];
    }
}