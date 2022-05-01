import codedraw.CodeDraw;
import codedraw.Palette;
import java.awt.Color;
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

    // TODO cleanup
    public static final double SUN_MASS = 1.989e30; // kilograms
    public static final double SUN_RADIUS = 696340e3; // meters
    public static final double OVERALL_SYSTEM_MASS = 20 * SUN_MASS; // kilograms

    public static void main(String[] args) {
        CodeDraw cd = new CodeDraw();
        Random random = new Random(2022);
        Body[] bodies = new Body[NUMBER_OF_BODIES];
        Octree octree = new Octree(new Vector3(), SECTION_SIZE);

        // for (int i = 0; i < bodies.length; i++) {
        //     bodies[i] = new Body(
        //             Math.abs(random.nextGaussian()) * SUN_MASS * 0.9, // KG
        //             // Math.abs(random.nextGaussian()) * OVERALL_SYSTEM_MASS / bodies.length, // KG
        //             getRandomVector(random, 0.2 * AU * 2),
        //             getRandomVector(random, 5e3));
        // }

        for (int i = 0; i < bodies.length / 2; i++) {
            bodies[i] = new Body(
                    Math.abs(random.nextGaussian()) * SUN_MASS * 0.9,
                    getRandomVector(random, 0.2 * AU).minus(new Vector3(AU / 3)),
                    getRandomVector(random, 5e3));
        }
        for (int i = 0; i < bodies.length / 2; i++) {
            bodies[bodies.length / 2 + i] = new Body(
                    Math.abs(random.nextGaussian()) * SUN_MASS * 0.9,
                    getRandomVector(random, 0.2 * AU).plus(new Vector3(AU / 3)),
                    getRandomVector(random, 5e3));
        }

        for (int seconds = 0;; ++seconds) {
            updatePhysics(bodies, octree);
            if (seconds % DRAW_EVERY_NTH_FRAME == 0) {
                drawSimulation(cd, bodies, null);
            }
        }
    }

    private static void updatePhysics(Body[] bodies, Octree octree) {
        octree.rebuild(bodies);
        octree.calculateForce(bodies, THRESHOLD);
        for (int i = 0; i < bodies.length; ++i) {
            bodies[i].update(DELTA_TIME);
        }
    }

    private static void drawSimulation(CodeDraw cd, Body[] bodies, Octree octree) {
        cd.clear(Palette.BLACK);
        for (Body body : bodies) {
            body.draw(cd);
        }
        if (octree != null) {
            octree.visualize(cd);
        }
        cd.show();
    }

    // public static Body[] spawnGalaxies(int numberOfGalaxies) {

    //     for (int i = 0; i < bodies.length / 2; i++) {
    //         bodies[i] = new Body(
    //                 Math.abs(random.nextGaussian()) * SUN_MASS * 0.9, // KG
    //                 // Math.abs(random.nextGaussian()) * OVERALL_SYSTEM_MASS / bodies.length, // KG
    //                 getRandomVector(random, 0.2 * AU).minus(new Vector3(AU / 3)),
    //                 getRandomVector(random, 5e3));
    //     }

    //     final double averageRadius = 0.2 * AU;
    //     final int bodiesProGalaxy = NUMBER_OF_BODIES / numberOfGalaxies;

    //     Random random = new Random();
    //     Body[] bodies = new Body[NUMBER_OF_BODIES];
    //     int k = 0;

    //     Vector3 or

    //     Vector3 offset = getRandomVector(random, averageRadius);
    //     Vector3 position = offset.plus(getRandomVector(random, ))

    //     for (int i = 0; i < numberOfGalaxies; ++i) {
    //         int count = i == 0 ? bodies.length - bodiesProGalaxy * (numberOfGalaxies - 1) : bodiesProGalaxy;
    //         for (int j = 0; j < bodiesProGalaxy; ++i) {

    //         }
    //     }
    // };

    public static Vector3 getRandomVector(Random random, double side) {
        return new Vector3(
                random.nextGaussian() * side,
                random.nextGaussian() * side,
                random.nextGaussian() * side);
    }
}
