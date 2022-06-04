import codedraw.CodeDraw;
import codedraw.Palette;

public class Octree {

	// Root node has the depth 0.
	public static final int MAX_DEPTH = MortonCode.PRECISION;

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
			insert(body);
		}
	}

	// Tries to insert the body into the octree and returns false if body could
	// not be inserted. This can happen if body is outside this octree or
	// MAX_DEPTH was reached during insertion.
	private boolean insert(Body body) {
		long mask = body.getMortonCode(corner, size);
		if (mask < 0) {
			// Body lies outside this octree.
			return false;
		}

		if (root == null) {
			root = new OctreeNode(body, mask);
			numberOfBodies = 1;
			return true;
		}

		if (root.insert(body, mask, 0)) {
			++numberOfBodies;
			return true;
		}
		return false;
	}

	// Uses the Barnes-Hut algorithm to calculate gravitational forces between
	// bodies. Threshold controls the accuracy of force approximation. A larger
	// threshold value means an inaccurate but faster simulation. Do not forget
	// to rebuild the octree before calling this function.
	public void calculateForce(Body[] bodies, double threshold) {
		if (root == null) {
			return;
		}
		for (Body body : bodies) {
			root.addForceTo(body, threshold, size);
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
			double factor = cd.getWidth() / Simulation.SECTION_SIZE;
			double x = (corner.x + Simulation.SECTION_SIZE / 2) * factor;
			double y = (corner.y + Simulation.SECTION_SIZE / 2) * factor;
			double w = size * factor;
			root.visualize(cd, x, y, w);
		}
		cd.setAntiAliased(true);
	}
}

class OctreeNode {

	private Massive representative;
	private long rmask;
	private OctreeNode[] children;

	public OctreeNode(Massive massive, long mask) {
		this.representative = massive;
		this.rmask = mask;
	}

	// Tries to recursively insert body into the octree. Returns false if
	// MAX_DEPTH was reached while descending the tree.
	public boolean insert(Body body, long mask, int depth) {
		if (depth >= Octree.MAX_DEPTH) {
			// Can't go go deeper than MAX_DEPTH. Refuse to insert the point.
			return false;
		}

		if (isLeaf()) {
			// Split the leaf into 8 octants.
			int index = (int) (rmask & MortonCode.INDEX_MASK);
			children = new OctreeNode[8];
			children[index] = new OctreeNode(representative, rmask >>> 3);
			// Representative will later be used for the Barnes-Hut algorithm.
			representative = new Massive(representative);
		}

		boolean inserted = false;
		int index = (int) (mask & MortonCode.INDEX_MASK);
		if (children[index] == null) {
			children[index] = new OctreeNode(body, mask >>> 3);
			inserted = true;
		} else {
			inserted = children[index].insert(body, mask >>> 3, depth + 1);
		}

		if (inserted) {
			// Representative stores the total mass and average position of all
			// inserted bodies.
			Massive.merge(representative, body);
		}
		return inserted;
	}

	// Approximates the gravitational force exerted on body by all the other
	// bodies inside this octant.
	public void addForceTo(Body body, double threshold, double size) {
		if (representative == body) {
			// Trying to calculate the gravitational force between a body and
			// itself leads to NaN.
			return;
		}

		if (isLeaf() || canApproximateWithBarnesHut(body, threshold, size)) {
			body.addGravitationalForceFrom(representative);
			return;
		}

		for (OctreeNode node : children) {
			if (node != null) {
				node.addForceTo(body, threshold, size / 2);
			}
		}
	}

	public void visualize(CodeDraw cd, double x, double y, double w) {
		if (isLeaf()) {
			cd.drawRectangle(x, y, w, w);
			return;
		}

		for (int i = 0; i < 8; ++i) {
			if (children[i] != null) {
				double newX = x + ((i % 4) % 2) * w / 2;
				double newY = y + ((i % 4) / 2) * w / 2;
				children[i].visualize(cd, newX, newY, w / 2);
			}
		}
	}

	private boolean isLeaf() {
		return children == null;
	}

	private boolean canApproximateWithBarnesHut(Body body, double threshold, double size) {
		return size * size < threshold * threshold * body.distanceSqrd(representative);
	}
}
