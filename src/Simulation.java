import codedraw.CodeDraw;
import codedraw.Palette;
import java.util.Random;

public class Simulation {

	// --- Physics constants, do not change ---
	public static final double G = 6.6743e-11;
	public static final double AU = 150e9; // Astronomical unit

	// --- Simulation constants ---
	public static final int NUMBER_OF_BODIES = 10000;
	public static final double SECTION_SIZE = 2 * AU;

	// --- Performance constants ---
	// Threshold for Barned-Hut approximation.
	public static final double THRESHOLD = 1.0;
	// Artificially speed up the simumulation.
	public static final double DELTA_TIME = 70.0;
	public static final int DRAW_EVERY_NTH_FRAME = 1;

	public static void main(String[] args) {
		CodeDraw cd = new CodeDraw();
		Random random = new Random(2022);
		Body[] bodies = new Body[NUMBER_OF_BODIES];
		Octree octree = new Octree(new Vector3(), SECTION_SIZE);

		Vector3 center1 = new Vector3(AU / 3);
		Vector3 center2 = new Vector3(-AU / 3);
		for (int i = 0; i < bodies.length; ++i) {
			double mass = Math.abs(random.nextGaussian()) * 1e30;
			Vector3 position = getRandomVector(random, 0.2 * AU);
			if (i % 2 == 0) {
				position = position.plus(center1);
			} else {
				position = position.plus(center2);
			}
			Vector3 velocity = getRandomVector(random, 5e3);
			bodies[i] = new Body(mass, position, velocity);
		}

		for (int seconds = 0;; ++seconds) {
			octree.rebuild(bodies);
			octree.calculateForce(bodies, THRESHOLD);
			for (Body body : bodies) {
				body.update(DELTA_TIME);
			}

			if (seconds % DRAW_EVERY_NTH_FRAME == 0) {
				cd.clear(Palette.BLACK);
				for (Body body : bodies) {
					body.draw(cd);
				}
				// octree.visualize(cd);
				cd.show();
			}
		}
	}

	public static Vector3 getRandomVector(Random random, double side) {
		return new Vector3(
				random.nextGaussian() * side,
				random.nextGaussian() * side,
				random.nextGaussian() * side);
	}
}
