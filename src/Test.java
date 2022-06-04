public class Test {
	public static void main(String[] args) {
		section("Test Morton Code");
		{
			for (int i = 0; i < 20; ++i) {
				double x = Math.random();
				double y = Math.random();
				double z = Math.random();

				long morton = MortonCode.get(x, y, z);
				long expected = simpleMortonCode(x, y, z);
				checkBits(morton, expected);
			}
			checkValue(MortonCode.get(1.1, 0.5, 0.1), -1);
			checkValue(MortonCode.get(-50, -10, 0.0), -1);
			checkValue(MortonCode.get(-0.001, 0.1, 0.1), -1);
			end();
		}
	}

	private static long simpleMortonCode(double x, double y, double z) {
		return interleaveBits(
				(long) (x * MortonCode.CLUSTERS_PER_DIMENSION),
				(long) (y * MortonCode.CLUSTERS_PER_DIMENSION),
				(long) (z * MortonCode.CLUSTERS_PER_DIMENSION));
	}

	private static long interleaveBits(long x, long y, long z) {
		long m = 0;
		for (long i = 0; i < 21; ++i) {
			m = (m + (z & 1)) << 1;
			z >>>= 1;

			m = (m + (y & 1)) << 1;
			y >>>= 1;

			m = (m + (x & 1)) << 1;
			x >>>= 1;
		}
		return m >>> 1;
	}

	private static void section(String name) {
		System.out.println(name);
	}

	private static void end() {
		System.out.println("\tSuccessful ✅");
	}

	private static <T> void error(T given, T expected) {
		System.out.println("\tError: expected value: " + expected + ", but given " + given);
		System.exit(1);
	}

	private static void checkBits(long given, long expected) {
		if (given != expected) {
			error(Long.toBinaryString(given), Long.toBinaryString(expected));
		}
	}

	private static void checkValue(long given, long expected) {
		if (given != expected) {
			error(given, expected);
		}
	}
}