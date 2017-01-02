package com.mygdx.game.atlaspacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import xbrz.Xbrz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Json;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.EntityExtraTile;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.catalog.entry.FloorTransition;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class MyGdxGameAtlasPacker extends ApplicationAdapter {
	
	static private final String REAL_ASSETS_PATH = "../../android/assets/" ;
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGdxGameAtlasPacker(), config);
	}
	
	@Override
	public void create() {
		
		//TextureUnpacker unpacker = new TextureUnpacker();
		//TextureAtlasData atlasData = new TextureAtlasData(Gdx.files.internal("data/uiskin.atlas"), Gdx.files.internal("data"), false);
		//unpacker.splitAtlas(atlasData, "data\\uiskin");
		
		packUITextures();
		
		packDefaultTextures();
		
		scaleTextures();
				
		packScaledTextures();
		
		Gdx.app.exit();
	}
	

	static public void packUITextures() {
		
		Settings settings = new Settings();
		
		//settings.fast = true;
		//settings.grid = true;
		settings.paddingX = 2;
		settings.paddingY = 2;
		//settings.useIndexes = false;
		//settings.maxWidth = settings.maxHeight = 1024;
		settings.duplicatePadding = true;
		settings.filterMag = TextureFilter.Linear;
		settings.filterMin = TextureFilter.Linear;
		
		TexturePacker.process(settings, "ui", REAL_ASSETS_PATH + "ui", "ui");
	}
	
	static public void packDefaultTextures() {
		
		Settings settings = new Settings();
		
		settings.fast = true;
		settings.grid = true;
		settings.paddingX = 2;
		settings.paddingY = 2;
		settings.useIndexes = false;
		settings.maxWidth = settings.maxHeight = 1024;
		settings.duplicatePadding = true;
		
		TexturePacker.process(settings, "splitted", REAL_ASSETS_PATH + "tiles", WorldTiles.ATLAS_1X_NAME);
	}
	
	static public void packScaledTextures() {
		
		Settings settings = new Settings();
		
		settings.fast = true;
		settings.grid = true;
		settings.paddingX = 2;
		settings.paddingY = 2;
		settings.useIndexes = false;
		settings.maxWidth = settings.maxHeight = 1024;
		settings.duplicatePadding = true;
		
		TexturePacker.process(settings, "splitted2x", REAL_ASSETS_PATH + "tiles", WorldTiles.ATLAS_2X_NAME);
		
		TexturePacker.process(settings, "splitted4x", REAL_ASSETS_PATH + "tiles", WorldTiles.ATLAS_4X_NAME);
	}	
	
	static PixmapIO.PNG pngWriter = new PixmapIO.PNG();
	
	static private int[] scales = new int[] {2, 4};
	
	static public void scaleTextures() {
		
		WorldTiles tiles = new WorldTiles(Gdx.files.internal(REAL_ASSETS_PATH + "tiles/" + WorldTiles.ATLAS_1X_NAME + ".atlas"));
		Catalog catalog = new Json().fromJson(Catalog.class, Gdx.files.internal(REAL_ASSETS_PATH + "data/catalog.txt"));
		catalog.init(tiles);
		
		tiles.getTiles()[0].getTexture().getTextureData().prepare();
		Pixmap pixmap = tiles.getTiles()[0].getTexture().getTextureData().consumePixmap();
		
		//List of atlas regions that must NOT be combines becuase they are used in more than one item.
		ArrayList<AtlasRegion> atlasRegionsToNotCombine = new ArrayList<AtlasRegion>();
		ArrayList<AtlasRegion> usedAtlasRegions = new ArrayList<AtlasRegion>();

		for (EntityEntry entity : catalog.getEntities()) {
			
			if (entity.getExtraTiles() != null && entity.getExtraTiles().length > 0) {
				
				if (entity.getImageTexture() != null) {
					if (usedAtlasRegions.contains(entity.getImageTexture()))
						atlasRegionsToNotCombine.add(entity.getImageTexture());
					else
						usedAtlasRegions.add(entity.getImageTexture());
				}
				
				for (int j = 0; j < entity.getExtraTiles().length; j++) {
					EntityExtraTile extraTile = entity.getExtraTiles()[j];
					
					if (usedAtlasRegions.contains(extraTile.getImageTexture()))
						atlasRegionsToNotCombine.add(extraTile.getImageTexture());
					else
						usedAtlasRegions.add(extraTile.getImageTexture());
				}
				
			} else {
				if (usedAtlasRegions.contains(entity.getImageTexture()))
					atlasRegionsToNotCombine.add(entity.getImageTexture());
				else
					usedAtlasRegions.add(entity.getImageTexture());
			}
		}		
		
		for (int i = 0; i < scales.length; i++) {
			
			int scale = scales[i];
			
			for (EntityEntry entity : catalog.getEntities()) {
				if (entity.getExtraTiles() != null && entity.getExtraTiles().length > 0) {
					
					boolean skip = false;
					
					AtlasRegion[][] combinedRegions = new AtlasRegion[][] {
							new AtlasRegion[] { null, null, null, null, null },
							new AtlasRegion[] { null, null, null, null, null },
							new AtlasRegion[] { null, null, null, null, null },
							new AtlasRegion[] { null, null, null, null, null },
							new AtlasRegion[] { null, null, null, null, null },
					};
					
					if (entity.getImageTexture() != null && atlasRegionsToNotCombine.contains(entity.getImageTexture()))
						skip = true; //Don't combine entries whose tiles are used in more than one combination
					
					combinedRegions[2][2] = entity.getImageTexture();
					
					for (int j = 0; j < entity.getExtraTiles().length; j++) {
						EntityExtraTile extraTile = entity.getExtraTiles()[j];
						
						if (combinedRegions[2 - extraTile.getOffsetY()][2 - extraTile.getOffsetX()] != null)
							skip = true; //Don't combine entries with overlapping tiles
						
						if (atlasRegionsToNotCombine.contains(extraTile.getImageTexture()))
							skip = true; //Don't combine entries whose tiles are used in more than one combination
						
						combinedRegions[2 - extraTile.getOffsetY()][2 - extraTile.getOffsetX()] = extraTile.getImageTexture();
					}

					if (!skip) {
						scaleCombinedTiles(
								pixmap,
								combinedRegions,
								16,
								scale,
								2
						);
					} else {
						//Don't combine, scale each image individually
						if (entity.getImageTexture() != null)
							scale(pixmap, entity.getImageTexture(), scale, hasAlpha(pixmap, entity.getImageTexture()) ? 2 : 0);

						for (int j = 0; j < entity.getExtraTiles().length; j++) {
							EntityExtraTile extraTile = entity.getExtraTiles()[j];
							scale(pixmap, extraTile.getImageTexture(), scale, hasAlpha(pixmap, extraTile.getImageTexture()) ? 2 : 0);
						}						
					}
				} else {
					//No extra simple, simple scale
					scale(pixmap, entity.getImageTexture(), scale, hasAlpha(pixmap, entity.getImageTexture()) ? 2 : 0);
				}
			}
			
			for(FloorEntry floor : catalog.getFloors()) {
				scale(pixmap, floor.getImageTexture(), scale, 0);
				
				if (floor.getImageVariationsTextures() != null) {
					for (AtlasRegion variation : floor.getImageVariationsTextures()) {
						scale(pixmap, variation, scale, 0);
					}
				}
				
				if (floor.getTransitions() != null) {
					
					for (FloorTransition transition : floor.getTransitions()) {
						
						scaleCombinedTiles(
								pixmap,
								new AtlasRegion[][] {
									new AtlasRegion[] {
											transition.getTopRightTexture(), transition.getTopTexture(), transition.getTopLeftTexture()
									},
									new AtlasRegion[] {
											transition.getRightTexture(), floor.getImageTexture(), transition.getLeftTexture() 
									},
									new AtlasRegion[] {
											transition.getBottomRightTexture(), transition.getBottomTexture(), transition.getBottomLeftTexture()
									}
								},
								16,
								scale,
								0
						);
						
						scale(pixmap, transition.getTopLeftCornerTexture(), scale, 0);
						scale(pixmap, transition.getTopRightCornerTexture(), scale, 0);
						scale(pixmap, transition.getBottomLeftCornerTexture(), scale, 0);
						scale(pixmap, transition.getBottomRightCornerTexture(), scale, 0);
					}
				}
			}
			
			//Treat everything else as entities (2 border)
			for (AtlasRegion atlasRegion : tiles.getTilesNotUsed()) {
				scale(pixmap, atlasRegion, scale, hasAlpha(pixmap, atlasRegion) ? 2 : 0);
			}
		}
		
		pixmap.dispose();
	}
	
	static private boolean hasAlpha(Pixmap texturePixmap, AtlasRegion atlasRegion) {
		
		int width = atlasRegion.getRegionWidth();
		int height = atlasRegion.getRegionHeight();
		int offsetX = atlasRegion.getRegionX();
		int offsetY = atlasRegion.getRegionY();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = texturePixmap.getPixel(x + offsetX, offsetY - y - 1);
				int a = color & 0xFF;
				if (a != 0xFF)
					return true;
			}
		}
		
		return false;
	}
	
	static public void scaleCombinedTiles(Pixmap texturePixmap, AtlasRegion[][] atlasRegions, int tileSize, int scale, int border) {
		pngWriter.setFlipY(true);
		
		int combinedWidth = atlasRegions.length * tileSize;
		int combinedHeight = atlasRegions[0].length * tileSize;
		
		int[] pixels = new int[combinedWidth * combinedHeight];
		
		for (int regionX = 0; regionX < atlasRegions.length; regionX++) {
			for (int regionY = 0; regionY < atlasRegions[regionX].length; regionY++) {
				AtlasRegion atlasRegion = atlasRegions[regionX][regionY];
				
				if (atlasRegion == null)
					continue;
				
				if (atlasRegion.getRegionHeight() != tileSize || atlasRegion.getRegionWidth() != tileSize) {
					Gdx.app.error("PACK", "Tile "  + atlasRegion.name + " size isn't " + tileSize + "x" + tileSize);
					return;
				}
				
				int targetOffsetX = regionY * tileSize;
				int targetOffsetY = regionX * tileSize;
				
				int offsetX = atlasRegion.getRegionX();
				int offsetY = atlasRegion.getRegionY();
				
				for (int y = 0; y < tileSize; y++)
					for (int x = 0; x < tileSize; x++)
						pixels[(y + targetOffsetY) * combinedWidth + x + targetOffsetX] = texturePixmap.getPixel(x + offsetX, offsetY - y - 1);
			}
		}
		
		int[] scaledPixels = scale(pixels, combinedWidth, combinedHeight, scale, border);
		int scaledPixelsWidth = combinedWidth * scale;
		
		/*
		int scaledPixelsHeight = combinedHeight * scale;
		
		//Save combined to temp file
		Pixmap scaledTemp = new Pixmap(scaledPixelsWidth, scaledPixelsHeight, Format.RGBA8888);
		scaledTemp.getPixels().asIntBuffer().put(scaledPixels);
						
		try {
			if (atlasRegions[2][2] != null) {
				FileHandle output = Gdx.files.local("data/test" + scale + "x/" + "combined-" + atlasRegions[2][2].name + ".png");
				pngWriter.write(output, scaledTemp);
			}
		}catch(IOException ex) {
			Gdx.app.error("PACK", "Error scaling tiles", ex);
		}
		scaledTemp.dispose();
		*/
		
		for (int regionX = 0; regionX < atlasRegions.length; regionX++) {
			for (int regionY = 0; regionY < atlasRegions[regionX].length; regionY++) {
				AtlasRegion atlasRegion = atlasRegions[regionX][regionY];
				
				if (atlasRegion == null)
					continue;
				
				int sourceOffsetX = regionY * tileSize * scale;
				int sourceOffsetY = regionX * tileSize * scale;
				
				int[] localScaledPixels = new int[tileSize * scale * tileSize * scale];
				
				int offset = 0;
				for (int y = 0; y < tileSize * scale; y++)
					for (int x = 0; x < tileSize * scale; x++)
						localScaledPixels[offset++] = scaledPixels[(y + sourceOffsetY) * scaledPixelsWidth + x + sourceOffsetX];
				
				Pixmap scaled = new Pixmap(tileSize * scale, tileSize * scale, Format.RGBA8888);
				scaled.getPixels().asIntBuffer().put(localScaledPixels);
								
				String fileName = atlasRegion.name + ".png";
				
				try {
					FileHandle output = Gdx.files.local("splitted" + scale + "x/" + fileName);
					pngWriter.write(output, scaled);
					Gdx.app.log("PACK", "Scaled " + fileName + " by " + scale + "x and saved to " + output.path());
				}catch(IOException ex) {
					Gdx.app.error("PACK", "Error scaling tiles", ex);
				}
				scaled.dispose();		
								
			}
		}		
	}
	
	static public void scale(Pixmap texturePixmap, AtlasRegion atlasRegion, int scale, int border) {
		
		pngWriter.setFlipY(true);
		
		int offsetX = atlasRegion.getRegionX();
		int offsetY = atlasRegion.getRegionY();
		int width = atlasRegion.getRegionWidth();
		int height = atlasRegion.getRegionHeight();
		
		int[] pixels = new int[width * height];
		
		int offset = 0;
		
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				pixels[offset++] = texturePixmap.getPixel(x + offsetX, offsetY - y - 1);
		
		scaleAndSave(pixels, width, height, scale, border, atlasRegion.name + ".png");
	}
	
	static public void scale(FileHandle png, int scale, int border) {
		
		pngWriter.setFlipY(false);
		
		Pixmap original = new Pixmap(png);
		
		int width = original.getWidth();
		int height = original.getHeight();
		
		int[] originalPixels = new int[width * height];
		
		original.getPixels().asIntBuffer().get(originalPixels);
		
		scaleAndSave(originalPixels, width, height, scale, border, png.name());
		
		original.dispose();
	}
	
	static public int[] scale(int[] originalPixels, int width, int height, int scale, int border) {

		//Replace all fully-transparent pixels with white transparent pixels, it works better with
		//the scaling algorithm
		for (int i = 0; i < originalPixels.length; i++)
			if ((originalPixels[i] >>> 24) == 0)
				originalPixels[i] = 0xFFFFFF00; //transparent white
		
		if (border > 0) {
			int newWidth = width + border * 2;
			int newHeight = height + border * 2;
			int[] newPixels = new int[newWidth * newHeight];
			Arrays.fill(newPixels, 0xFFFFFF00); //transparent white

			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					newPixels[(y + border) * newWidth + (x + border)] = originalPixels[y * width + x];

			originalPixels = newPixels;
			width = newWidth;
			height = newHeight;
		}
		
		int[] scaledPixels = new int[width * height * scale * scale];
		
		Xbrz.INSTANCE.scale(scale, originalPixels, scaledPixels, width, height);
		
		width *= scale;
		height *= scale;
		
		if (border > 0) {
			int newWidth = width - border * 2 * scale;
			int newHeight = height - border * 2 * scale;
				 
			int[] newPixels = new int[newWidth * newHeight];

			for (int y = 0; y < newHeight; y++)
				for (int x = 0; x < newWidth; x++)
					newPixels[y * newWidth + x] = scaledPixels[(y + border * scale) * width + (x + border * scale)];

			scaledPixels = newPixels;
			width -= border * 2 * scale;
			height -= border * 2 * scale;
		}		
		
		return scaledPixels;
	}
	
	static public void scaleAndSave(int[] originalPixels, int width, int height, int scale, int border, String fileName) {
		
		int[] scaledPixels = scale(originalPixels, width, height, scale, border);
		
		width *= scale;
		height *= scale;
		
		Pixmap scaled = new Pixmap(width, height, Format.RGBA8888);
		scaled.getPixels().asIntBuffer().put(scaledPixels);
		
		try {
			FileHandle output = Gdx.files.local("splitted" + scale + "x/" + fileName);
			pngWriter.write(output, scaled);
			Gdx.app.log("PACK", "Scaled " + fileName + " by " + scale + "x and saved to " + output.path());
		}catch(IOException ex) {
			Gdx.app.error("PACK", "Error scaling tiles", ex);
		}
		scaled.dispose();		
	}
	
	

	
	/*
	static private int tileId = 0; 
	
	static private void split(String path) {
		Pixmap texture = new Pixmap(Gdx.files.internal(path));
		
		try {
			PixmapIO.PNG pngWriter = new PixmapIO.PNG();
			pngWriter.setFlipY(false);
			
			int sizeX = texture.getWidth() / 17;
			int sizeY = texture.getHeight() / 17;
			
			Pixmap temp = new Pixmap(16, 16, Format.RGBA8888);
			
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					
					temp.setColor(0, 0, 0, 0);
					temp.fill();
					temp.drawPixmap(texture, 0, 0, x * 17, y * 17, 16, 16);
					
					
					pngWriter.write(
							Gdx.files.local("data/splitted/tile-" + tileId + ".png"),
							temp);
					
					tileId++;
				}
			}
			
		} catch(IOException ex) {
			
		}		
	}
	*/
}
