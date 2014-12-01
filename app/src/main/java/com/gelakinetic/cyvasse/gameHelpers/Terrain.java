package com.gelakinetic.cyvasse.gameHelpers;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseLinear;

import com.gelakinetic.cyvasse.fragments.CyvasseGameFragment;

public class Terrain {

	public enum TerrainType {
		FIELD, RIVER, MOUNTAIN, FORTRESS
	}

	// Saved data
	public TerrainType			terrainType;
	public byte							hp;
	public byte							player;
	// Transient data
	public CyvasseSprite		sprite;
	private float						mPos[]			= null;
	private Text						hpText;
	private float						mTextPos[]	= null;

	public static final int	BYTE_SIZE		= 3;

	/**
	 * A constructor for initializing a Terrain from saved bytes
	 * 
	 * @param tileBytes
	 *          All of the saved data
	 * @param index
	 *          The index where this Terrain's bytes begin
	 * @param x
	 *          The integer tile column
	 * @param y
	 *          The integer tile row
	 * @param game
	 *          The CyvasseGame to add this Terrain to
	 */
	public Terrain(byte[] tileBytes, int index, int x, int y, CyvasseGameFragment game) {
		this.terrainType = TerrainType.values()[tileBytes[index]];
		this.hp = tileBytes[index + 1];
		this.player = tileBytes[index + 2];
		SetAttributesFromTerrainType(this.terrainType, game, x, y, false);
	}

	/**
	 * A constructor for initializing a Terrain from scratch
	 * 
	 * @param x
	 *          The integer tile column
	 * @param y
	 *          The integer tile row
	 * @param game
	 *          The CyvasseGame to add this Terrain to
	 * @param terrain
	 *          The type of this terrain
	 * @param player
	 *          The player who controls this terrain
	 */
	public Terrain(int x, int y, CyvasseGameFragment game, TerrainType terrain, byte player) {
		this.terrainType = terrain;
		this.player = player;
		SetAttributesFromTerrainType(this.terrainType, game, x, y, true);
	}

	/**
	 * This method is used by various constructors to initialize a Terrain's
	 * attributes based on it's TerrainType
	 * 
	 * @param tt
	 *          The TerrainType of this Terrain
	 * @param game
	 *          The CyvasseGame to add this Terrain to
	 * @param x
	 *          The integer tile column
	 * @param y
	 *          The integer tile row
	 * @param setHP
	 *          true if the HP should be set from attributes, false if it was
	 *          already set from saved data
	 */
	private void SetAttributesFromTerrainType(TerrainType tt, CyvasseGameFragment game, int x, int y, boolean setHP) {

		TextureRegion mTextureRegion = null;

		switch (tt) {
			case FIELD:
				if (setHP) {
					this.hp = -1;
				}
				mTextureRegion = null;
				break;
			case RIVER:
				if (setHP) {
					this.hp = -1;
				}
				mTextureRegion = game.mRiverTextureRegion;
				break;
			case MOUNTAIN:
				if (setHP) {
					this.hp = -1;
				}
				mTextureRegion = game.mMountainTextureRegion;
				break;
			case FORTRESS:
				if (setHP) {
					this.hp = 10;
				}
				mTextureRegion = game.mFortressTextureRegion;
				break;
			default:
				break;
		}

		if (this.hp != -1) {
			TextOptions to;
			if (player == 1) {
				to = new TextOptions(HorizontalAlign.RIGHT);
			}
			else {
				to = new TextOptions(HorizontalAlign.LEFT);
			}
			this.hpText = new Text(0, 0, game.mBlackFont, Integer.toString(this.hp), to, game.getVertexBufferObjectManager());
			this.hpText.setZIndex(CyvasseGameFragment.Z_BUFFER_UNIT_TEXT);

			if (player == 0) {
				this.hpText.setRotation(180);
			}
			game.scene.attachChild(this.hpText);
		}

		float centerX = (game.textureWidth * x) * game.scaleFactor - game.offsetX;
		float centerY = (game.textureWidth * y) * game.scaleFactor - game.offsetY;

		if (mTextureRegion != null) {
			this.sprite = new CyvasseSprite(centerX, centerY, mTextureRegion, game.getVertexBufferObjectManager());
			this.sprite.setScale(game.scaleFactor);
			this.sprite.setZIndex(CyvasseGameFragment.Z_BUFFER_TERRAIN);
			if (player == 0) {
				this.sprite.setRotation(180);
			}
			this.setPosition(game, x, y);

			game.scene.attachChild(this.sprite);
		}
		game.scene.sortChildren();
	}

	/**
	 * Set the position of this Terrain, and move the associated sprite
	 * 
	 * @param game
	 *          The CyvasseGame to add this Terrain to
	 * @param x
	 *          The integer tile column
	 * @param y
	 *          The integer tile row
	 */
	public void setPosition(CyvasseGameFragment game, int x, int y) {
		float centerX = (game.textureWidth * x) * game.scaleFactor - game.offsetX;
		float centerY = (game.textureWidth * y) * game.scaleFactor - game.offsetY;

		if (mPos != null) {
			float dist = (float) Math.sqrt((centerX - mPos[0]) * (centerX - mPos[0]) + (centerY - mPos[1]) * (centerY - mPos[1]));
			this.sprite.registerEntityModifier(new MoveModifier(dist / CyvasseGameFragment.CAMERA_WIDTH, mPos[0], centerX, mPos[1], centerY, EaseLinear.getInstance()));
		}
		else {
			mPos = new float[2];
		}
		mPos[0] = centerX;
		mPos[1] = centerY;

		this.sprite.setPosition(centerX, centerY);

		if (this.hpText != null) {
			int borderPixels = (int) (CyvasseGameFragment.CAMERA_HEIGHT - ((CyvasseGameFragment.NUM_TILES + CyvasseGameFragment.STAGING_AREA_TILES) * game.textureWidth * game.scaleFactor)) / 2;

			float textCenterX;
			float textCenterY;
			if (player == CyvasseGameFragment.PLAYER_1) {
				textCenterX = (game.textureWidth * (x)) * game.scaleFactor;
				textCenterY = (game.textureWidth * (y + 1)) * game.scaleFactor - this.hpText.getHeight() + borderPixels + 7;
			}
			else {
				textCenterX = (game.textureWidth * (x + 1)) * game.scaleFactor - this.hpText.getWidth();
				textCenterY = (game.textureWidth * (y)) * game.scaleFactor + borderPixels - 7;
			}

			if (mTextPos != null) {
				float dist = (float) Math.sqrt((textCenterX - mTextPos[0]) * (textCenterX - mTextPos[0]) + (textCenterY - mTextPos[1]) * (textCenterY - mTextPos[1]));
				this.sprite.registerEntityModifier(new MoveModifier(dist / CyvasseGameFragment.CAMERA_WIDTH, mTextPos[0], textCenterX, mTextPos[1], textCenterY, EaseLinear
						.getInstance()));
			}
			else {
				mTextPos = new float[2];
			}
			mTextPos[0] = textCenterX;
			mTextPos[1] = textCenterY;
			this.hpText.setPosition(textCenterX, textCenterY);
		}
	}

	/**
	 * Generate a byte array containing all necessary information to save this
	 * Terrain
	 * 
	 * @return
	 */
	public byte[] saveTerrain() {
		byte b[] = new byte[BYTE_SIZE];
		b[0] = (byte) terrainType.ordinal();
		b[1] = hp;
		b[2] = player;
		return b;
	}

	public void decreaseHP(int strength) {
		if (strength > this.hp) {
			this.hp = 0;
		}
		else {
			this.hp -= strength;
		}
		this.hpText.setText(Integer.toString(this.hp));
	}
}
