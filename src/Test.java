import java.util.NoSuchElementException;

public class Test {
	public static void main(String[] args) {
		section("Test MortonCode");
		{
			for (int i = 0; i < 20; ++i) {
				double x = Math.random();
				double y = Math.random();
				double z = Math.random();

				long morton = MortonCode.get(x, y, z);
				long expected = simpleMortonCode(x, y, z);
				checkBits(morton, expected);
			}
			checkValue(MortonCode.get(1.1, 0.5, 0.1), -1L);
			checkValue(MortonCode.get(-50, -10, 0.0), -1L);
			checkValue(MortonCode.get(-0.001, 0.1, 0.1), -1L);
			end();
		}
		section("Test BodyMortonCodePair");
		{
			Body b1 = new Body(0.0, new Vector3(0.0), new Vector3(0.0));
			BodyMortonCodePair pair1 = new BodyMortonCodePair(b1, new Vector3(0.0), 10.0);
			checkValue(pair1.hasNextOctant(), true);

			Body b2 = new Body(0.0, new Vector3(-2.0, 1.0, 1.0), new Vector3(0.0));
			BodyMortonCodePair pair2 = new BodyMortonCodePair(b2, new Vector3(0.0), 10.0);
			checkValue(pair2.hasNextOctant(), false);

			Body b3 = new Body(0.0, new Vector3(-1.0), new Vector3(0.0));
			BodyMortonCodePair pair3 = new BodyMortonCodePair(b3, new Vector3(-5.0), 25.0);
			checkValue(pair3.hasNextOctant(), true);

			for (int i = 0; i < MortonCode.PRECISION; ++i) {
				pair3.nextOctant();
			}
			try {
				pair3.nextOctant();
				checkValue(true, false);
			} catch (NoSuchElementException e) {
				checkValue(true, true);
			}
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

	private static <T> void checkValue(T given, T expected) {
		if (!given.equals(expected)) {
			error(given, expected);
		}
	}
}
