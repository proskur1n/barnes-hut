public class MortonCode {

	// Maximum number of subdivisions along a single dimension that can be stored in
	// a single long (3 * 21 < 64 bits).
	public static final int PRECISION = 21;
	public static final long CLUSTERS_PER_DIMENSION = 1 << PRECISION;

	// Takes 3 floating point values between [0, 1) and returns the
	// corresponding Z-order curve. If any coordinate cannot be represented, a
	// negative integer is returned. Note that this function actually returns
	// reversed bit-sequence of the corresponding Morton code, so that you can
	// bitwise-AND the last three bits to get the next index.
	public static long get(double x, double y, double z) {
		long _x = (long) (x * CLUSTERS_PER_DIMENSION);
		long _y = (long) (y * CLUSTERS_PER_DIMENSION);
		long _z = (long) (z * CLUSTERS_PER_DIMENSION);

		if (!inRange(_x) || !inRange(_y) || !inRange(_z)) {
			// Point lies outside the region representable by this Morton code.
			return -1;
		}

		long m = (spaceOutBits(_x) << 2) | (spaceOutBits(_y) << 1) | spaceOutBits(_z);
		return Long.reverse(m) >>> 1;
	}

	private static boolean inRange(long val) {
		return 0 <= val && val < CLUSTERS_PER_DIMENSION;
	}

	// Space out the first 21 bits of 'x'. Argument 'x' must be < 2^21. Otherwise,
	// this function will return rubbish.
	private static long spaceOutBits(long x) {
		// This bit twiddling magic is taken from:
		// https://www.forceflow.be/2013/10/07/morton-encodingdecoding-through-bit-interleaving-implementations/
		x = (x | x << 32) & 0x1f00000000ffffL;
		x = (x | x << 16) & 0x1f0000ff0000ffL;
		x = (x | x << 8) & 0x100f00f00f00f00fL;
		x = (x | x << 4) & 0x10c30c30c30c30c3L;
		x = (x | x << 2) & 0x1249249249249249L;
		return x;
	}
}
