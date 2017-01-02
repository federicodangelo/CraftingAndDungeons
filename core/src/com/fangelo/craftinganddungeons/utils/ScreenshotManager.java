package com.fangelo.craftinganddungeons.utils;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.fangelo.craftinganddungeons.PlatformAdapter;
import com.fangelo.craftinganddungeons.ui.ScreenManager;

public class ScreenshotManager {
	
	private static int counter = 1;
	
    public static void saveScreenshot(){
    	
    	FileHandle screenshotsFolder = PlatformAdapter.getInstance().getScreenshotFolder();
    	
    	if (screenshotsFolder == null) {
    		ScreenManager.showNotification("Screenshot FAILED!!");
    		Gdx.app.error("SCREENSHOT", "Not valid screenshot folder found");
    		return;
    	}
    	
        try{
            FileHandle fh;
            
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            
            String todayString = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DAY_OF_MONTH);
            
            int maxTries = 999;
            
            do {
        		fh = screenshotsFolder.child("screenshot-" + todayString + "-"  + counter++ + ".png");
            } while (fh.exists() && maxTries-- > 0);
            
            if (maxTries <= 0) {
            	//Ran out of tries..
            	ScreenManager.showNotification("Screenshot FAILED!!");
            	Gdx.app.error("SCREENSHOT", "No valid available screenshot filename found");
            	return;
            }
            
            Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
            
            ScreenManager.showNotification("Screenshot saved to " + fh.path());
            
            PlatformAdapter.getInstance().updateScreenshotFolder(fh);
            
        }catch (Exception e){
        	ScreenManager.showNotification("Screenshot FAILED!!");
        	Gdx.app.error("SCREENSHOT", e.getMessage());
        }
    }

    private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

        if (yDown) {
            // Flip the pixmap upside down
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w * h * 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
            pixels.clear();
        }

        return pixmap;
    }
}
