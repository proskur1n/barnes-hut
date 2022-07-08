import java.util.NoSuchElementException;

// Represents a tuple of body and the corresponding Morton code. It is used to
// simplify insertion into the octree.
public class BodyMortonCodePair {

	private final Body body;
	private long mortonCode;
	private int remaining;

	public BodyMortonCodePair(Body body, Vector3 corner, double size) {
		this.body = body;
		this.mortonCode = body.position().getMortonCode(corner, size);
		if (mortonCode < 0) {
			this.remaining = 0;
		} else {
			this.remaining = MortonCode.PRECISION;
		}
	}

	public Vector3 position() {
		return body.position();
	}

	public double mass() {
		return body.mass();
	}

	// Returns false if there is not enough precision for the next subdivision
	// or the body lies outside the range that can be represented by this Morton
	// code.
	public boolean hasNextOctant() {
		return remaining > 0;
	}

	// Returns a number in the interval [0, 8) with the index of the next
	// octant.
	public int nextOctant() {
		if (remaining <= 0) {
			throw new NoSuchElementException("Not enough precision for subdivision");
		}
		int octant = (int) (mortonCode & 0b111);
		mortonCode >>= 3;
		--remaining;
		return octant;
	}
}
