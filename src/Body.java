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
		this.force = new Vector3(0.0);
		this.mass = mass;
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
		// TODO: Refactor
		// I got a large speedup from 66ms to only 39ms for 10000 bodies by
		// simply replacing Vector3 with pure math.
		double dx = position2.x - position.x;
		double dy = position2.y - position.y;
		double dz = position2.z - position.z;
		double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double m = Simulation.G * mass * mass2 / (len * len * len);
		force.x += dx * m;
		force.y += dy * m;
		force.z += dz * m;
		// Vector3 direction = b.position.minus(position);
		// double distance = direction.length();
		// direction.normalize();
		// double force = Simulation.G * mass * b.mass / (distance * distance);
		// this.force = this.force.plus(direction.times(force));
	}

	// TODO: Refactor ?
	// Adds the gravitational force exerted by 'b' on this body. The current
	// position and velocity remain unchanged until you call update.
	// public void addForceFrom(Massive b) {
	// 	// I got a large speedup from 66ms to only 39ms for 10000 bodies by
	// 	// simply replacing Vector3 with pure math.
	// 	double dx = b.position.x - position.x;
	// 	double dy = b.position.y - position.y;
	// 	double dz = b.position.z - position.z;
	// 	double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
	// 	double m = Simulation.G * mass * b.mass / (len * len * len);
	// 	force.x += dx * m;
	// 	force.y += dy * m;
	// 	force.z += dz * m;
	// 	// Vector3 direction = b.position.minus(position);
	// 	// double distance = direction.length();
	// 	// direction.normalize();
	// 	// double force = Simulation.G * mass * b.mass / (distance * distance);
	// 	// this.force = this.force.plus(direction.times(force));
	// }

	// Moves the body according to its current force and velocity. Afterwards,
	// this bodie's force is reset to 0.
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
