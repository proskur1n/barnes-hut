// Massive is defined by its mass and position.
public class Massive {

    protected double mass;
    protected Vector3 position;

    protected Massive(double mass, Vector3 position) {
        this.mass = mass;
        this.position = position;
    }

    // Creates an independent copy of the other Massive.
    public Massive(Massive other) {
        this.mass = other.mass;
        this.position = new Vector3(other.position);
    }

    public double distanceSqrd(Massive other) {
        return position.distanceSqrd(other.position);
    }

    // Merges the mass and position of 'a' and 'b' and saves the result in 'a'.
    // Massive 'b' remains unchanged.
    public static void merge(Massive a, Massive b) {
        // TODO replace with simple math ?
        double f = b.mass / (a.mass + b.mass);
        Vector3.mix(a.position, b.position, f);
        a.mass = a.mass + b.mass;
    }
}
