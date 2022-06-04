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
        double totalMass = a.mass + b.mass;
        Vector3 v1 = a.position.times(a.mass);
        Vector3 v2 = b.position.times(b.mass);
        a.position = v1.plus(v2).times(1 / totalMass);
        a.mass = totalMass;
    }
}
