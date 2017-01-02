package com.fangelo.craftinganddungeons.world.generator.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Palette {
	
	private Color color = new Color();
	private Vector3 a = new Vector3(0.5f, 0.5f, 0.5f);
	private Vector3 b = new Vector3(0.5f, 0.5f, 0.5f);
	private Vector3 c = new Vector3(1.0f, 1.0f, 1.0f);
	private Vector3 d = new Vector3(0.0f, 0.33f, 0.66f);
	
	public Palette() {
		a.set(0.5f, 0.5f, 0.5f);
		b.set(0.5f, 0.5f, 0.5f);
		c.set(1.0f, 1.0f, 1.0f);
		d.set(0.0f, 0.33f, 0.66f);
	}
	
	public float[] getLookupTable() {
		float[] table = new float[256];
		for (int i = 0; i < 256; i++)
			table[i] = get(i / 255.0f).toFloatBits();
		
		return table;
	}
	
	public Color get(float t) {
		
		float x = a.x + b.x * (float) Math.cos(6.28318f * (c.x * t + d.x));
		float y = a.y + b.y * (float) Math.cos(6.28318f * (c.y * t + d.y));
		float z = a.z + b.z * (float) Math.cos(6.28318f * (c.z * t + d.z));
		
		return color.set(x, y, z, 1.0f);
	}

}
