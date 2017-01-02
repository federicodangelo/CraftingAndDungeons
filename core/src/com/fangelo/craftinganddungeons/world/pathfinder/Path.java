package com.fangelo.craftinganddungeons.world.pathfinder;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Path implements Poolable {
	private LongArray path = new LongArray();
	
	public void clear() {
		path.clear();
	}
	
	public int size() {
		return path.size;
	}
	
	private GridPoint2 tmp = new GridPoint2();
	
	public GridPoint2 get(int i) {
		long v = path.get(i);
		
		int x = (int) (v >>> 32);
		int y = (int) (v & 0xFFFFFFFF);
		
		tmp.set(x, y);
		
		return tmp;
	}
	
	public void add(int x, int y) {
		long v = (((long) x) << 32) | y;
		path.add(v);
	}
	
	public void reverse() {
		path.reverse();
	}

	@Override
	public void reset() {
		clear();
	}
}
