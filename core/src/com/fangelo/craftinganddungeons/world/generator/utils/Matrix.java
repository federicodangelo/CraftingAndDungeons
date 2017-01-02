package com.fangelo.craftinganddungeons.world.generator.utils;

import java.util.Arrays;

public class Matrix {
	public int size;
	public byte[] values;

	public Matrix(int size) {
		this.size = size;
		this.values = new byte[size * size];
	}

	public void fill(byte value) {
		Arrays.fill(values, value);
	}

	public byte getValue(int x, int y) {
		return values[x + y * size];
	}

	public void setValue(int x, int y, byte value) {
		values[x + y * size] = value;
	}
}