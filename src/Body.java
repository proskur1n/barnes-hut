import codedraw.CodeDraw;
import codedraw.Palette;
import java.awt.Color;

public class Body {

	private final Vector3 position;
	private final Vector3 velocity;
	private final double mass;
	private Vector3 force;
	private Color color;

	private static final Color[] palette = {
			Palette.BISQUE,
			Palette.TURQUOISE,
			Palette.STEEL_BLUE
	};

	public Body(double mass, Vector3 position, Vector3 velocity) {
		this.position = position;
		this.velocity = velocity;
		this.mass = mass;
		this.force = new Vector3(0.0);
		this.color = getRandomColor();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Vector3 position() {
		return position;
	}

	public double mass() {
		return mass;
	}

	public double radius() {
		final double SUN_MASS = 1.989e30;
		final double SUN_RADIUS = 696340e3;
		return SUN_RADIUS * Math.sqrt(mass / SUN_MASS);
	}

	public double distanceSqrd(Vector3 v) {
		return position.distanceSqrd(v);
	}

	// Adds the gravitational force exerted by a celestial body with the
	// 'position2' and 'mass2' on this body. The current position and velocity
	// remain unchanged until you call update.
	public void addForceFrom(Vector3 position2, double mass2) {
		Vector3 delta = position2.minus(position);
		double len = delta.length();
		double ff = Simulation.G * mass * mass2 / (len * len * len);
		Vector3.multThenAdd(force, delta, ff);
	}

	// Moves the body according to its current force and velocity. Afterwards,
	// this body's force is reset to 0.
	public void update(double deltaTime) {
		Vector3 acceleration = force.times(1 / mass); // F = m*a -> a = F/m
		Vector3.multThenAdd(velocity, acceleration, deltaTime);
		Vector3.multThenAdd(position, velocity, deltaTime);
		force = new Vector3(0.0);
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
