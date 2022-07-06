import codedraw.CodeDraw;

public class Vector3 {

	private double x;
	private double y;
	private double z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(double value) {
		this.x = value;
		this.y = value;
		this.z = value;
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
		double dx = v.x - x;
		double dy = v.y - y;
		double dz = v.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public String toString() {
		return String.format("[%g, %g, %g]", x, y, z);
	}

	public long getMortonCode(Vector3 origin, double size) {
		double _x = (x - origin.x) / size;
		double _y = (y - origin.y) / size;
		double _z = (z - origin.z) / size;
		return MortonCode.get(_x, _y, _z);
	}

	// Draws a filled circle with a specified radius centered at the (x,y)
	// coordinates of this vector in the canvas associated with 'cd'. The
	// z-coordinate is not used.
	public void drawAsFilledCircle(CodeDraw cd, double radius) {
		Vector3 p = projectOnScreen(cd);
		cd.fillCircle(p.x, p.y, Math.max(p.z * radius, 1.5));
	}

	// Draws a square with its top-left corner at the (x, y) coordinates of this
	// vector. The z coordinate is not used.
	public void drawAsSquare(CodeDraw cd, double width) {
		Vector3 p = projectOnScreen(cd);
		cd.drawRectangle(p.x, p.y, p.z * width, p.z * width);
	}

	private Vector3 projectOnScreen(CodeDraw cd) {
		double factor = cd.getWidth() / Simulation.SECTION_SIZE;
		return new Vector3(
				(x + Simulation.SECTION_SIZE / 2) * factor,
				(y + Simulation.SECTION_SIZE / 2) * factor,
				factor);
	}

	// Calculates a + b * m and stores the result in a. Be aware of the side
	// effects, as this method changes the vector in-place.
	public static void multThenAdd(Vector3 a, Vector3 b, double m) {
		a.x += b.x * m;
		a.y += b.y * m;
		a.z += b.z * m;
	}

	// Performs linear interpolation of vectors 'v1' and 'v2' with the
	// coefficients 'f1' und 'f2'.
	// Result is calculated as (v1 * f1 + v2 * f2) / (f1 + f2).
	public static Vector3 merge(Vector3 v1, double f1, Vector3 v2, double f2) {
		double f = 1.0 / (f1 + f2);
		return new Vector3(
				(v1.x * f1 + v2.x * f2) * f,
				(v1.y * f1 + v2.y * f2) * f,
				(v1.z * f1 + v2.z * f2) * f);
	}
}
