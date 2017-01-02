package com.fangelo.craftinganddungeons.world.generator.utils;

import com.badlogic.gdx.math.RandomXS128;

//Original from: http://scrawkblog.com/2013/03/05/perlin-noise-pulgin-for-unity/

//Usage:

//- Instantiate perlin noise generator with seed
//	var perlin : PerlinNoise = new PerlinNoise(0);
//- Then evaluate in 1D / 2D / 3D
//	var noise1D : float = perlin.FractalNoise1D(x, octaves, frq, amp);
//	var noise2D : float = perlin.FractalNoise2D(x, y,  octaves, frq, amp);
//	var noise3D : float = perlin.FractalNoise3D(x, y, z, octaves, frq, amp);

public class PerlinNoise {
	static private final int B = 256;
	private int[] m_perm = new int[B + B];
	private int offsetX;
	private int offsetY;

	static private final RandomXS128 rnd = new RandomXS128();

	// NOT THREAD SAFE!! Reuses rnd
	public PerlinNoise(long seed, int offsetX, int offsetY) {
		rnd.setSeed(seed);
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		int i, j, k;
		for (i = 0; i < B; i++) {
			m_perm[i] = i;
		}

		while (--i != 0) {
			k = m_perm[i];
			j = rnd.nextInt(B);
			m_perm[i] = m_perm[j];
			m_perm[j] = k;
		}

		for (i = 0; i < B; i++) {
			m_perm[B + i] = m_perm[i];
		}
	}

	float FADE(float t) {
		return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
	}

	float LERP(float t, float a, float b) {
		return (a) + (t) * ((b) - (a));
	}

	float GRAD2(int hash, float x, float y) {
		// This method uses the mod operator which is slower
		// than bitwise operations but is included out of interest
		// int h = hash % 16;
		// float u = h<4 ? x : y;
		// float v = h<4 ? y : x;
		// int hn = h%2;
		// int hm = (h/2)%2;
		// return ((hn != 0) ? -u : u) + ((hm != 0) ? -2.0f*v : 2.0f*v);

		int h = hash & 7;
		float u = h < 4 ? x : y;
		float v = h < 4 ? y : x;
		return (((h & 1) != 0) ? -u : u)
				+ (((h & 2) != 0) ? -2.0f * v : 2.0f * v);
	}

	float noise2D(float x, float y) {
		// returns a noise value between -0.75 and 0.75
		int ix0, iy0, ix1, iy1;
		float fx0, fy0, fx1, fy1, s, t, nx0, nx1, n0, n1;

		ix0 = (int) x; // Integer part of x
		iy0 = (int) y; // Integer part of y
		fx0 = x - ix0; // Fractional part of x
		fy0 = y - iy0; // Fractional part of y
		fx1 = fx0 - 1.0f;
		fy1 = fy0 - 1.0f;
		ix1 = (ix0 + 1) & 0xff; // Wrap to 0..255
		iy1 = (iy0 + 1) & 0xff;
		ix0 = ix0 & 0xff;
		iy0 = iy0 & 0xff;

		t = FADE(fy0);
		s = FADE(fx0);

		nx0 = GRAD2(m_perm[ix0 + m_perm[iy0]], fx0, fy0);
		nx1 = GRAD2(m_perm[ix0 + m_perm[iy1]], fx0, fy1);

		n0 = LERP(t, nx0, nx1);

		nx0 = GRAD2(m_perm[ix1 + m_perm[iy0]], fx1, fy0);
		nx1 = GRAD2(m_perm[ix1 + m_perm[iy1]], fx1, fy1);

		n1 = LERP(t, nx0, nx1);

		return 0.507f * LERP(s, n0, n1);
	}

	private float noise2D(float x, float y, int octNum, float frq,
			float amp) {
		float gain = 1.0f;
		float sum = 0.0f;

		for (int i = 0; i < octNum; i++) {
			sum += noise2D(x * gain / frq, y * gain / frq) * amp / gain;
			gain *= 2.0f;
		}
		return sum;
	}
	
	public int fractalNoise2D(int x, int y, int octNum, int frq, int amp) {
		float noise = noise2D(x + offsetX, y + offsetY, octNum, frq, amp);
		
        //fractalNoise2D returns values in the -0.75 / 0.75 range (for amplitude 1, 1 octave), so remap using that as a reference
        int noiseInt = (int) (((noise + 0.75f) / 1.5f) * 255f);
        
        if (noiseInt > 255)
            noiseInt = 255;
        else if (noiseInt < 0)
            noiseInt = 0;
        
        return noiseInt;
	}
}
