package com.fangelo.craftinganddungeons.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

//World camera, internally it keeps the camera always at (0,0,0) to prevent jittering when the 
//camera position becomes too big, the drawback is that every thing that renders must
//offset the position manually.
public class WorldCamera implements Batch {
	
	private SpriteBatch batch; 
	
	private OrthographicCamera camera;
	private float cameraPositionX;
	private float cameraPositionY;
	private boolean cameraChanged; 
	private String cameraMapId;
	
	public WorldCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
		camera.zoom = 0.25f;
		camera.position.set(0, 0, 0);
		cameraMapId = "";
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
	}
	
	public final float getX() {
		return cameraPositionX;
	}
	
	public final void setX(float x) {
		cameraPositionX = x;
		cameraChanged = true;
	}
	
	public final float getY() {
		return cameraPositionY;
	}
	
	public final void setY(float y) {
		cameraPositionY = y;
		cameraChanged = true;
	}
	
	public final String getMapId() {
		return cameraMapId;
	}
	
	public final void setMapId(String mapId) {
		cameraMapId = mapId;
	}
		
	public final void setPosition(float x, float y) {
		
		if (isDrawing())
			throw new RuntimeException("Don't move the camera while drawing!!");
		
		cameraPositionX = x;
		cameraPositionY = y;
		cameraChanged = true;
	}
	
	public final void translate(float dx, float dy) {
		cameraPositionX += dx;
		cameraPositionY += dy;
		cameraChanged = true;
	}
	
	public final Vector3 unproject(Vector3 screenCoords) {
		screenCoords = camera.unproject(screenCoords);
		screenCoords.x += cameraPositionX;
		screenCoords.y += cameraPositionY;
		
		return screenCoords;
	}
	
	public final float getZoom() {
		return camera.zoom;
	}
	
	public final void setZoom(float zoom) {
		if (isDrawing())
			throw new RuntimeException("Don't move the camera while drawing!!");
		
		camera.zoom = zoom;
		cameraChanged = true;
	}
	
	public final float getViewportWidth() {
		return camera.viewportWidth;
	}
	
	public final float getViewportHeight() {
		return camera.viewportHeight;
	}
		
	public final void update() {
		camera.update();
	}
	
	public final void resize(int width, int height) {
		
		if (isDrawing())
			throw new RuntimeException("Don't move the camera while drawing!!");
		
	    camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();	    
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void begin() {
		
		if (cameraChanged) {
			batch.setProjectionMatrix(camera.combined);
			cameraChanged = false;
		}
		
		batch.begin();
	}

	@Override
	public void end() {
		batch.end();
	}

	@Override
	public void setColor(Color tint) {
		batch.setColor(tint);
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		batch.setColor(r, g, b, a);
	}

	@Override
	public void setColor(float color) {
		batch.setColor(color);
	}

	@Override
	public Color getColor() {
		return batch.getColor();
	}

	@Override
	public float getPackedColor() {
		return batch.getPackedColor();
	}

	@Override
	public void draw(Texture texture, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, int srcX, int srcY, int srcWidth,
			int srcHeight, boolean flipX, boolean flipY) {
		
		batch.draw(
				texture, 
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				originX, originY,
				width, height,
				scaleX, scaleY,
				rotation,
				srcX, srcY, srcWidth, srcHeight,
				flipX, flipY
		);
	}

	@Override
	public void draw(Texture texture, float x, float y, float width,
			float height, int srcX, int srcY, int srcWidth, int srcHeight,
			boolean flipX, boolean flipY) {

		batch.draw(
				texture, 
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				width, height,
				srcX, srcY, srcWidth, srcHeight,
				flipX, flipY
		);
	}

	@Override
	public void draw(Texture texture, float x, float y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		
		batch.draw(
				texture, 
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				srcX, srcY, srcWidth, srcHeight
		);
	}

	@Override
	public void draw(Texture texture, float x, float y, float width,
			float height, float u, float v, float u2, float v2) {
		
		batch.draw(
				texture,
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				width, height,
				y, v, u2, v2
		);
	}

	@Override
	public void draw(Texture texture, float x, float y) {
		batch.draw(
				texture,
				(float) (x - cameraPositionX), (float) (y - cameraPositionY)
		);
	}

	@Override
	public void draw(Texture texture, float x, float y, float width,
			float height) {
		
		batch.draw(
				texture, 
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				width, height
		);
	}

	@Override
	public void draw(Texture texture, float[] spriteVertices, int offset,
			int count) {

		//We don't support this, because offsetting every vertex will be really slow..
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void draw(TextureRegion region, float x, float y) {
		
	}

	@Override
	public void draw(TextureRegion region, float x, float y, float width,
			float height) {
		
		batch.draw(
				region,
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				width, height
		);
	}
	
	@Override
	public void draw(TextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation) {
		
		batch.draw(
				region,
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				originX, originY, width, height,
				scaleX, scaleY,
				rotation
		);
	}

	@Override
	public void draw(TextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, boolean clockwise) {
		
		batch.draw(
				region,
				(float) (x - cameraPositionX), (float) (y - cameraPositionY),
				originX, originY, width, height,
				scaleX, scaleY,
				rotation, clockwise
		);
	}

	@Override
	public void draw(TextureRegion region, float width, float height,
			Affine2 transform) {
		
		//We don't want to modify the transform.. so don't support this
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void flush() {
		batch.flush();
	}

	@Override
	public void disableBlending() {
		batch.disableBlending();
	}

	@Override
	public void enableBlending() {
		batch.enableBlending();
	}

	@Override
	public void setBlendFunction(int srcFunc, int dstFunc) {
		batch.setBlendFunction(srcFunc, dstFunc);
	}

	@Override
	public int getBlendSrcFunc() {
		return batch.getBlendSrcFunc();
	}

	@Override
	public int getBlendDstFunc() {
		return batch.getBlendDstFunc();
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Matrix4 getTransformMatrix() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setProjectionMatrix(Matrix4 projection) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setTransformMatrix(Matrix4 transform) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setShader(ShaderProgram shader) {
		batch.setShader(shader);
	}

	@Override
	public ShaderProgram getShader() {
		return batch.getShader();
	}

	@Override
	public boolean isBlendingEnabled() {
		return batch.isBlendingEnabled();
	}

	@Override
	public boolean isDrawing() {
		return batch.isDrawing();
	}
}
