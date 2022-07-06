import codedraw.CodeDraw;
import codedraw.Palette;

public class Octree {

	private Vector3 corner;
	private double size;
	private OctreeNode root;
	private int numberOfBodies;

	public Octree(Vector3 center, double size) {
		this.corner = center.minus(new Vector3(size / 2));
		this.size = size;
		this.root = null;
		this.numberOfBodies = 0;
	}

	// Removes all existing bodies and tries to insert all the new ones.
	public void rebuild(Body[] bodies) {
		this.root = null;
		this.numberOfBodies = 0;
		for (Body body : bodies) {
			BodyMortonCodePair pair = new BodyMortonCodePair(body, corner, size);
			if (pair.hasNextOctant()) {
				if (root == null) {
					double st = size / Simulation.THRESHOLD;
					root = new OctreeNode(pair, st * st);
				} else {
					root.insert(pair);
				}
				++numberOfBodies;
			}
		}
	}

	// Uses the Barnes-Hut algorithm to calculate gravitational forces between
	// bodies. THRESHOLD controls the accuracy of force approximation. A larger
	// threshold value means an inaccurate but faster simulation. Do not forget
	// to rebuild the octree before calling this function.
	public void calculateForce(Body[] bodies) {
		if (root != null) {
			for (Body body : bodies) {
				root.addForceTo(body, size);
			}
		}
	}

	public int numberOfBodies() {
		return numberOfBodies;
	}

	// Visualizes parts of the data structure with a thin wireframe.
	public void visualize(CodeDraw cd) {
		cd.setColor(Palette.WHITE);
		cd.setLineWidth(1.0);
		cd.setAntiAliased(false);
		if (root != null) {
			root.visualize(cd, corner, size);
		}
		cd.setAntiAliased(true);
	}
}

class OctreeNode {

	private BodyMortonCodePair pair;
	private OctreeNode[] children;
	private Vector3 averagePosition;
	private double totalMass;
	private double sizeOverThresholdSqrd;

	public OctreeNode(BodyMortonCodePair pair, double sizeOverThresholdSqrd) {
		this.pair = pair;
		this.averagePosition = pair.position();
		this.totalMass = pair.mass();
		this.sizeOverThresholdSqrd = sizeOverThresholdSqrd;
	}

	// Tries to recursively insert body into the octree.
	public void insert(BodyMortonCodePair newPair) {
		if (isLeaf()) {
			// Split the leaf into 8 octants.
			children = new OctreeNode[8];
			children[pair.nextOctant()] = new OctreeNode(pair, sizeOverThresholdSqrd / 4);
			pair = null;
		}

		int index = newPair.nextOctant();
		if (children[index] == null) {
			children[index] = new OctreeNode(newPair, sizeOverThresholdSqrd / 4);
		} else {
			children[index].insert(newPair);
		}

		this.averagePosition = Vector3.merge(averagePosition, totalMass,
				newPair.position(), newPair.mass());
		this.totalMass += newPair.mass();
	}

	// Approximates the gravitational force exerted on body by all the other
	// bodies inside this octant.
	public void addForceTo(Body body, double size) {
		if (averagePosition == body.position()) {
			// Trying to calculate the gravitational force between a body and
			// itself leads to NaN.
			return;
		}
		if (isLeaf() || canApproximate(body, size)) {
			body.addForceFrom(averagePosition, totalMass);
			return;
		}
		for (OctreeNode node : children) {
			if (node != null) {
				node.addForceTo(body, size / 2);
			}
		}
	}

	public void visualize(CodeDraw cd, Vector3 corner, double size) {
		if (isLeaf()) {
			corner.drawAsSquare(cd, size);
			return;
		}
		for (int i = 0; i < 8; ++i) {
			if (children[i] != null) {
				double x = ((i % 4) % 2) * size / 2;
				double y = ((i % 4) / 2) * size / 2;
				children[i].visualize(cd, corner.plus(new Vector3(x, y, 0)), size / 2);
			}
		}
	}

	private boolean isLeaf() {
		return children == null;
	}

	// Returns whether the gravitational force from all bodies within this
	// octant can be approximated as if they were a single body.
	private boolean canApproximate(Body body, double size) {
		return sizeOverThresholdSqrd < body.distanceSqrd(averagePosition);
	}
}
