package com.gelakinetic.cyvasse.gameHelpers;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseLinear;

import android.content.Context;

import com.gelakinetic.cyvasse.R;
import com.gelakinetic.cyvasse.fragments.CyvasseGameFragment;

public class Unit {

	public enum UnitType {
		RABBLE, SPEARMEN, CROSSBOWMEN, LIGHT_HORSE, HEAVY_HORSE, ELEPHANT, CATAPULT, TREBUCHET, DRAGON, KING
	}

	// Saved attributes
	public UnitType					unitType;
	public byte							player;
	public boolean					hasMoved	= false;
	// Filled in from type
	public int							strength;
	public int							minAttackRange;
	public int							maxAttackRange;
	public int							moveDistance;
	public int							mountainPenalty;
	public int							riverPenalty;
	// Transient
	public CyvasseSprite		sprite;
	public Text							strengthText;
	private float						mPos[]		= null;
	private float[]					mTextPos;

	public static final int	BYTE_SIZE	= 3;

	/**
	 * A constructor for initializing a Unit from saved bytes
	 * 
	 * @param tileBytes
	 *          All of the saved data
	 * @param index
	 *          The index where this Unit's bytes begin
	 * @param game
	 *          The CyvasseGame to add this Unit to
	 * @param boardJ The unit's location
	 * @param boardI The unit's location
	 */
	public Unit(byte[] tileBytes, int index, CyvasseGameFragment game, int boardI, int boardJ) {
		this.unitType = UnitType.values()[tileBytes[index]];
		this.player = tileBytes[index + 1];
		this.hasMoved = (tileBytes[index + 2] == 1);
		SetAttributesFromUnitType(this.unitType, game, boardI, boardJ);
//		this.setPosition(game, boardI, boardJ);
	}

	/**
	 * A constructor for initializing a Unit from scratch
	 * 
	 * @param x
	 *          The integer tile column
	 * @param y
	 *          The integer tile row
	 * @param game
	 *          The CyvasseGame to add the Unit to
	 * @param type
	 *          The type of the unit
	 * @param player
	 *          The player who controls this Unit
	 */
	public Unit(int x, int y, CyvasseGameFragment game, UnitType type, byte player) {
		this.unitType = type;
		this.player = player;
		SetAttributesFromUnitType(type, game, x, y);
//		this.setPosition(game, x, y);
	}

	/**
	 * This method is used by various constructors to initialize a Unit's
	 * attributes based on it's UnitType
	 * 
	 * @param ut
	 *          The type of piece the unit is
	 * @param game
	 *          The CyvasseGame the Unit exists in
	 */
	private void SetAttributesFromUnitType(UnitType ut, CyvasseGameFragment gameFrag, int x, int y) {
		TextureRegion mTextureRegion;
		Context game = gameFrag.getActivity();
		switch (ut) {
			case RABBLE:
				this.strength = getValue(game, R.string.rabble_strength);
				this.minAttackRange = getValue(game, R.string.rabble_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.rabble_max_attack_range);
				this.moveDistance = getValue(game, R.string.rabble_movement);
				this.mountainPenalty = getValue(game, R.string.rabble_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.rabble_penalty_river);
				mTextureRegion = gameFrag.mRabbleTextureRegion;
				break;
			case SPEARMEN:
				this.strength = getValue(game, R.string.spearmen_strength);
				this.minAttackRange = getValue(game, R.string.spearmen_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.spearmen_max_attack_range);
				this.moveDistance = getValue(game, R.string.spearmen_movement);
				this.mountainPenalty = getValue(game, R.string.spearmen_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.spearmen_penalty_river);
				mTextureRegion = gameFrag.mSpearmenTextureRegion;
				break;
			case CROSSBOWMEN:
				this.strength = getValue(game, R.string.crossbowmen_strength);
				this.minAttackRange = getValue(game, R.string.crossbowmen_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.crossbowmen_max_attack_range);
				this.moveDistance = getValue(game, R.string.crossbowmen_movement);
				this.mountainPenalty = getValue(game, R.string.crossbowmen_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.crossbowmen_penalty_river);
				mTextureRegion = gameFrag.mCrossbowmenTextureRegion;
				break;
			case LIGHT_HORSE:
				this.strength = getValue(game, R.string.light_horse_strength);
				this.minAttackRange = getValue(game, R.string.light_horse_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.light_horse_max_attack_range);
				this.moveDistance = getValue(game, R.string.light_horse_movement);
				this.mountainPenalty = getValue(game, R.string.light_horse_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.light_horse_penalty_river);
				mTextureRegion = gameFrag.mLightHorseTextureRegion;
				break;
			case HEAVY_HORSE:
				this.strength = getValue(game, R.string.heavy_horse_strength);
				this.minAttackRange = getValue(game, R.string.heavy_horse_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.heavy_horse_max_attack_range);
				this.moveDistance = getValue(game, R.string.heavy_horse_movement);
				this.mountainPenalty = getValue(game, R.string.heavy_horse_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.heavy_horse_penalty_river);
				mTextureRegion = gameFrag.mHeavyHorseTextureRegion;
				break;
			case ELEPHANT:
				this.strength = getValue(game, R.string.elephant_strength);
				this.minAttackRange = getValue(game, R.string.elephant_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.elephant_max_attack_range);
				this.moveDistance = getValue(game, R.string.elephant_movement);
				this.mountainPenalty = getValue(game, R.string.elephant_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.elephant_penalty_river);
				mTextureRegion = gameFrag.mElephantTextureRegion;
				break;
			case CATAPULT:
				this.strength = getValue(game, R.string.catapult_strength);
				this.minAttackRange = getValue(game, R.string.catapult_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.catapult_max_attack_range);
				this.moveDistance = getValue(game, R.string.catapult_movement);
				this.mountainPenalty = getValue(game, R.string.catapult_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.catapult_penalty_river);
				mTextureRegion = gameFrag.mCatapultTextureRegion;
				break;
			case TREBUCHET:
				this.strength = getValue(game, R.string.trebuchet_strength);
				this.minAttackRange = getValue(game, R.string.trebuchet_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.trebuchet_max_attack_range);
				this.moveDistance = getValue(game, R.string.trebuchet_movement);
				this.mountainPenalty = getValue(game, R.string.trebuchet_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.trebuchet_penalty_river);
				mTextureRegion = gameFrag.mTrebuchetTextureRegion;
				break;
			case DRAGON:
				this.strength = getValue(game, R.string.dragon_strength);
				this.minAttackRange = getValue(game, R.string.dragon_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.dragon_max_attack_range);
				this.moveDistance = getValue(game, R.string.dragon_movement);
				this.mountainPenalty = getValue(game, R.string.dragon_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.dragon_penalty_river);
				mTextureRegion = gameFrag.mDragonTextureRegion;
				break;
			case KING:
				this.strength = getValue(game, R.string.king_strength);
				this.minAttackRange = getValue(game, R.string.king_min_attack_range);
				this.maxAttackRange = getValue(game, R.string.king_max_attack_range);
				this.moveDistance = getValue(game, R.string.king_movement);
				this.mountainPenalty = getValue(game, R.string.king_penalty_mountain);
				this.riverPenalty = getValue(game, R.string.king_penalty_river);
				mTextureRegion = gameFrag.mKingTextureRegion;
				break;
			default:
				return;
		}

		int red, green, blue;
		this.sprite = new CyvasseSprite(0, 0, mTextureRegion, gameFrag.getVertexBufferObjectManager());

		if (player == CyvasseGameFragment.PLAYER_0) {
			red = gameFrag.sharedPrefs.getInt(game.getString(R.string.p1_red_key), 255);
			green = gameFrag.sharedPrefs.getInt(game.getString(R.string.p1_green_key), 253);
			blue = gameFrag.sharedPrefs.getInt(game.getString(R.string.p1_blue_key), 0);
		}
		else {
			red = gameFrag.sharedPrefs.getInt(game.getString(R.string.p2_red_key), 117);
			green = gameFrag.sharedPrefs.getInt(game.getString(R.string.p2_green_key), 178);
			blue = gameFrag.sharedPrefs.getInt(game.getString(R.string.p2_blue_key), 221);
		}

		this.sprite.setColor(red / 255.0f, green / 255.0f, blue / 255.0f);
		this.sprite.setScale(gameFrag.scaleFactor);
		this.sprite.setZIndex(CyvasseGameFragment.Z_BUFFER_UNIT);

		TextOptions to;
		if (player == 1) {
			to = new TextOptions(HorizontalAlign.RIGHT);
		}
		else {
			to = new TextOptions(HorizontalAlign.LEFT);
		}
		this.strengthText = new Text(0, 0, gameFrag.mWhiteFont, Integer.toString(this.strength), to, gameFrag.getVertexBufferObjectManager());
		this.strengthText.setZIndex(CyvasseGameFragment.Z_BUFFER_UNIT_TEXT);

		if (player == 0) {
			this.sprite.setRotation(180);
			this.strengthText.setRotation(180);
		}

		setPosition(gameFrag, x, y);
		
		gameFrag.scene.attachChild(this.sprite);
		gameFrag.scene.attachChild(this.strengthText);
		gameFrag.scene.sortChildren();
	}

	/**
	 * This method moves the Unit sprite and associated text to the given tile on
	 * the board
	 * 
	 * @param game
	 *          The CyvasseGame the Unit exists in
	 * @param x
	 *          The integer tile column
	 * @param y
	 *          The integer tile row
	 */
	public void setPosition(CyvasseGameFragment game, int x, int y) {

		int borderPixels = (int) (CyvasseGameFragment.CAMERA_HEIGHT - ((CyvasseGameFragment.NUM_TILES + CyvasseGameFragment.STAGING_AREA_TILES) * game.textureWidth * game.scaleFactor)) / 2;

		float centerX = (game.textureWidth * x) * game.scaleFactor - game.offsetX;
		float centerY = (game.textureWidth * y) * game.scaleFactor - game.offsetY;
		float textCenterX;
		float textCenterY;
		if (player == CyvasseGameFragment.PLAYER_1) {
			textCenterX = (game.textureWidth * (x + 1)) * game.scaleFactor - this.strengthText.getWidth();
			textCenterY = (game.textureWidth * (y + 1)) * game.scaleFactor - this.strengthText.getHeight() + borderPixels + 7;
		}
		else {
			textCenterX = (game.textureWidth * (x)) * game.scaleFactor;
			textCenterY = (game.textureWidth * (y)) * game.scaleFactor + borderPixels - 7;
		}

		if (mPos != null) {
			float dist = (float) Math.sqrt((centerX - mPos[0]) * (centerX - mPos[0]) + (centerY - mPos[1]) * (centerY - mPos[1]));
			this.sprite.registerEntityModifier(new MoveModifier(dist / CyvasseGameFragment.CAMERA_WIDTH, mPos[0], centerX, mPos[1], centerY, EaseLinear.getInstance()));
			this.strengthText.registerEntityModifier(new MoveModifier(dist / CyvasseGameFragment.CAMERA_WIDTH, mTextPos[0], textCenterX, mTextPos[1], textCenterY, EaseLinear
					.getInstance()));
		}
		else {
			mPos = new float[2];
			mTextPos = new float[2];
		}
		mPos[0] = centerX;
		mPos[1] = centerY;
		mTextPos[0] = textCenterX;
		mTextPos[1] = textCenterY;

		this.sprite.setPosition(centerX, centerY);
		this.strengthText.setPosition(textCenterX, textCenterY);
	}

	/**
	 * A static helper method which pulls Integer values out of resource strings
	 * 
	 * @param mCtx
	 *          The context which can access the resources
	 * @param resid
	 *          The resource ID of the string to be read
	 * @return The integer value of the requested string
	 */
	public int getValue(Context mCtx, int resid) {
		try {
			return Integer.valueOf(mCtx.getString(resid));
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Generate a byte array containing all necessary information to save this
	 * Unit
	 * 
	 * @return BYTE_SIZE bytes, with the information to persist
	 */
	public byte[] saveUnit() {
		byte b[] = new byte[BYTE_SIZE];
		b[0] = (byte) unitType.ordinal();
		b[1] = player;
		b[2] = (byte) ((hasMoved) ? 1 : 0);
		return b;
	}
}
