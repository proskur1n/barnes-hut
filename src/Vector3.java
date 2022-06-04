import codedraw.CodeDraw;

public class Vector3 {

    public double x;
    public double y;
    public double z;

    public Vector3() {
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3(double value) {
        this.x = value;
        this.y = value;
        this.z = value;
    }

    public void setToZero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3 plus(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 minus(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 times(double d) {
        return new Vector3(x * d, y * d, z * d);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        double len = length();
        x /= len;
        y /= len;
        z /= len;
    }

    public double distanceSqrd(Vector3 v) {
        return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y) + (v.z - z) * (v.z - z);
    }

    public String toString() {
        return String.format("[%g, %g, %g]", x, y, z);
    }

    // Draws a filled circle with a specified radius centered at the (x,y)
    // coordinates of this vector in the canvas associated with 'cd'. The
    // z-coordinate is not used.
    public void drawAsFilledCircle(CodeDraw cd, double radius) {
        double factor = cd.getWidth() / Simulation.SECTION_SIZE;
        cd.fillCircle(
                (x + Simulation.SECTION_SIZE / 2) * factor,
                (y + Simulation.SECTION_SIZE / 2) * factor,
                Math.max(radius * factor, 1.5));
    }

    // Calculates a + b * m and stores the result in a.
    public static void multThenAdd(Vector3 a, Vector3 b, double m) {
        a.x += b.x * m;
        a.y += b.y * m;
        a.z += b.z * m;
    }
}
