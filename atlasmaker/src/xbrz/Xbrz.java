package xbrz;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Xbrz extends Library
{
	Xbrz INSTANCE = (Xbrz) Native.loadLibrary("xbrz", Xbrz.class);
    
	void scale(int scale, int[] originalPixels, int[] resizedPixels, int originalWidth, int originalHeight);
}
