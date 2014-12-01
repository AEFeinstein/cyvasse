package com.gelakinetic.cyvasse.gameHelpers;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class CyvasseSprite extends Sprite {

	public int board_x;
	public int board_y;
	
	public CyvasseSprite(float centerX, float centerY, ITextureRegion color, VertexBufferObjectManager vertexBufferObjectManager) {
		super(centerX, centerY, color, vertexBufferObjectManager);
	}

}
