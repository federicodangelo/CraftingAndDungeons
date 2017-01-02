package com.fangelo.craftinganddungeons.world.generator.utils;

public class HashXX {
	// static private final int PRIME32_1 = (int) 2654435761L;
	static private final int PRIME32_2 = (int) 2246822519L;
	static private final int PRIME32_3 = (int) 3266489917L;
	static private final int PRIME32_4 = 668265263;
	static private final int PRIME32_5 = 374761393;

	static public int range(int seed, int min, int max, int x, int y) {
		return min + (int) (getRandom(seed, x, y) % (max - min));
	}

	static public int range(int seed, int min, int max, int x) {
		return min + (int) (getRandom(seed, x) % (max - min));
	}

	static public int getRandom(int seed, int buf1, int buf2) {
		// Simplified from GetHash() for only 2 parameters
		int h32 = seed + PRIME32_5;
		h32 += 8; // (uint)len * 4;

		h32 += buf1 * PRIME32_3;
		h32 = rotateLeft(h32, 17) * PRIME32_4;

		h32 += buf2 * PRIME32_3;
		h32 = rotateLeft(h32, 17) * PRIME32_4;

		h32 ^= h32 >> 15;
		h32 *= PRIME32_2;
		h32 ^= h32 >> 13;
		h32 *= PRIME32_3;
		h32 ^= h32 >> 16;

		return h32;
	}

	static public int getRandom(int seed, int buf) {
		int h32 = seed + PRIME32_5;
		h32 += 4;
		h32 += buf * PRIME32_3;
		h32 = rotateLeft(h32, 17) * PRIME32_4;
		h32 ^= h32 >> 15;
		h32 *= PRIME32_2;
		h32 ^= h32 >> 13;
		h32 *= PRIME32_3;
		h32 ^= h32 >> 16;
		return h32;
	}

	private static int rotateLeft(int value, int count) {
		return (value << count) | (value >>> (32 - count));
	}
}
