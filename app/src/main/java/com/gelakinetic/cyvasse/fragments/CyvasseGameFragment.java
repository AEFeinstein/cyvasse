package com.gelakinetic.cyvasse.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.fragment.compat.LayoutGameFragment;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.gelakinetic.cyvasse.R;
import com.gelakinetic.cyvasse.activities.MainActivity;
import com.gelakinetic.cyvasse.gameHelpers.Coord;
import com.gelakinetic.cyvasse.gameHelpers.CyvasseSprite;
import com.gelakinetic.cyvasse.gameHelpers.MovementCalculator;
import com.gelakinetic.cyvasse.gameHelpers.MovesAndAttacks;
import com.gelakinetic.cyvasse.gameHelpers.Terrain;
import com.gelakinetic.cyvasse.gameHelpers.Terrain.TerrainType;
import com.gelakinetic.cyvasse.gameHelpers.Tile;
import com.gelakinetic.cyvasse.gameHelpers.Unit;
import com.gelakinetic.cyvasse.gameHelpers.Unit.UnitType;
import com.gelakinetic.cyvasse.gameHelpers.UnitsInvolvedInCombat;
/* TODO multi
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
*/
public class CyvasseGameFragment extends LayoutGameFragment {
	/********************
	 * Constants *
	 ********************/

	public static final int						Z_BUFFER_BOARD								= 0;
	public static final int						Z_BUFFER_TERRAIN							= 1;
	public static final int						Z_BUFFER_HIGHLIGHT						= 2;
	public static final int						Z_BUFFER_UNIT									= 3;
	public static final int						Z_BUFFER_UNIT_TEXT						= 4;
	public static final int						Z_BUFFER_HAS_MOVED						= 5;
	public static final int						Z_BUFFER_SELECTED_UNIT				= 6;
	public static final int						Z_BUFFER_SELECTED_UNIT_TEXT		= 7;
	public static final int						Z_BUFFER_CURTAIN							= 8;

	public static final byte					PLAYER_0											= 0;
	public static final byte					PLAYER_1											= 1;
	private static final String				MAKE_TOAST_AND_FINISH					= "toast_and_finish";

	private static final int					mActivePlayer_INDEX						= 0;
	private static final int					movesLeft_INDEX								= 1;
	private static final int					gameState_INDEX								= 2;
	private static final int					gameMode_INDEX								= 3;
	private static final int					STAGING_AREA_TILES_INDEX			= 4;
	private static final int					LOWER_TILE_BOUND_INDEX				= 5;
	private static final int					NUM_TILES_INDEX								= 6;
	private static final int					UPPER_TILE_BOUND_INDEX				= 7;
	private static final int					NUM_MOVES_INDEX								= 8;
	private static final int					SAVE_DIALOG										= 1;
	private static final int					WAIT_FOR_PLAYER_DIALOG				= 2;
	private static final int					ONLINE_QUIT_DIALOG						= 3;
	private static final int					PLAYER_LEFT_DIALOG						= 4;
	private static final String				DIALOG_TAG										= "dialog";

	/*********************************
	 * One Time Initialized Fields *
	 *********************************/

	public static int									NUM_TILES;
	public static int									CAMERA_WIDTH;
	public static int									CAMERA_HEIGHT;
	public static int									STAGING_AREA_TILES;
	public static int									LOWER_TILE_BOUND;
	public static int									UPPER_TILE_BOUND;
	public static int									NUM_MOVES;
	private static Activity						mActivity;
	private static Fragment						mFragment;

	private ITextureRegion						mDarkWoodTextureRegion;
	private ITextureRegion						mLightWoodTextureRegion;

	public TextureRegion							mMountainTextureRegion;
	public TextureRegion							mRiverTextureRegion;
	public TextureRegion							mStagingTextureRegion;
	public TextureRegion							mCurtainTextureRegion;
	public TextureRegion							mFortressTextureRegion;

	public TextureRegion							mRabbleTextureRegion;
	public TextureRegion							mSpearmenTextureRegion;
	public TextureRegion							mCrossbowmenTextureRegion;
	public TextureRegion							mLightHorseTextureRegion;
	public TextureRegion							mHeavyHorseTextureRegion;
	public TextureRegion							mElephantTextureRegion;
	public TextureRegion							mCatapultTextureRegion;
	public TextureRegion							mTrebuchetTextureRegion;
	public TextureRegion							mDragonTextureRegion;
	public TextureRegion							mKingTextureRegion;

	public float											textureWidth;
	public float											scaleFactor;
	public float											offsetX;
	public float											offsetY;
	private float tileDimen;

	private Tile[][]									board;
	private GameMode									gameMode											= GameMode.GAME_MODE_SETUP;
	private GameState									gameState											= GameState.NO_UNIT_SELECTED;
	private byte											mActivePlayer									= PLAYER_0;
	private int												movesLeft;
	private Coord											selectedUnit									= new Coord();
	private Coord											selectedTerrain								= new Coord();
	private boolean										hasUnitMoved									= false;

	public Scene											scene;
	private Text											movesLeftText;
	public Font												mWhiteFont;
	public Font												mBlackFont;
	private Color											highlightRed									= new Color(1.0f, 0, 0, 0.5f);
	private Color											highlightBlue									= new Color(0, 0, 1.0f, 0.5f);
	private Color											highlightGrey									= new Color(0.75f, 0.75f, 0.75f, 0.5f);
	private ArrayList<Rectangle>			highlightRectangles						= new ArrayList<Rectangle>();
	private ArrayList<Rectangle>			hasMovedRectangles						= new ArrayList<Rectangle>();
	private ArrayList<CyvasseSprite>	curtains											= new ArrayList<CyvasseSprite>();

	public SharedPreferences					sharedPrefs;

	private int												numPieces[]										= { 0, 0 };
	private MovementCalculator				movCalc												= new MovementCalculator();
	private MovesAndAttacks						currentMovesAndAttacks				= new MovesAndAttacks();
	private ArrayList<CyvasseSprite>	stagingTiles_p0								= new ArrayList<CyvasseSprite>();
	private ArrayList<CyvasseSprite>	stagingTiles_p1								= new ArrayList<CyvasseSprite>();
	private Text											doneText;
	private boolean										gameIsOver										= false;

	/*
	 * Multiplayer Stuff
	 */
	/* TODO multi
	public static final boolean				mIsOnline = false;
	private int												mPlayerNum;
	private boolean										mWaitingForOpponentsPlacement	= false;
	private boolean										mReceivedOpponentsPlacement		= false;
	private byte[]										mOpponentTileBytes;
	*/
	
	/************
	 * Enums
	 ************/

	public enum GameState {
		NO_UNIT_SELECTED, FRIENDLY_UNIT_SELECTED, ENEMY_UNIT_SELECTED, FRIENDLY_TERRAIN_SELECTED, UNIT_ATTACKING
	}

	public enum GameMode {
		GAME_MODE_SETUP, GAME_MODE_PLAY
	}

	/************
	 * Error Handler
	 ************/
	static Handler	errorHandler	= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		Bundle data = msg.getData();
																		if (data.containsKey(MAKE_TOAST_AND_FINISH)) {
																			Toast.makeText(mActivity, data.getString(MAKE_TOAST_AND_FINISH), Toast.LENGTH_LONG).show();
																			mFragment.getFragmentManager().popBackStack();
																		}
																	}
																};

	/*****************
	 * Methods
	 *****************/

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	public CyvasseGameFragment() {
		// Dat empty constructor
	}

	/*
	 * @Override public void onResume() { super.onResume(); this.onResumeGame(); }
	 * 
	 * @Override public void onPause() { super.onPause(); this.onPauseGame(); }
	 */
	@Override
	public void onCreate(Bundle pSavedInstanceState) {
		/* TODO multi
		if (this.getArguments() != null) {
			mIsOnline = this.getArguments().getBoolean("isOnline");
			STAGING_AREA_TILES = this.getArguments().getInt(MainActivity.STAGING_AREA_TILES_KEY);
			mPlayerNum = this.getArguments().getInt(MainActivity.PLAYER_NUM_KEY);
			mActivePlayer = (byte) mPlayerNum;
		}
		*/
		mActivity = this.getActivity();
		mFragment = this;
		super.onCreate(pSavedInstanceState);
	}

	public EngineOptions onCreateEngineOptions() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		CAMERA_HEIGHT = displaymetrics.heightPixels;
		CAMERA_WIDTH = displaymetrics.widthPixels;

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		try {
			File f = new File(mActivity.getFilesDir(), "game.save");
			/* TODO multi
			if (mIsOnline) {
				// standardized online setup
				NUM_TILES = 10;
				NUM_MOVES = 3;
				movesLeft = NUM_MOVES;

				LOWER_TILE_BOUND = STAGING_AREA_TILES / 2;
				UPPER_TILE_BOUND = LOWER_TILE_BOUND + NUM_TILES;

				if (mPlayerNum == PLAYER_0) {
					camera.setRotation(180);
				}
			}
			else */ if (f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				byte[] settings = new byte[9];
				fis.read(settings, 0, 9);

				/* Usually initialized when declared */
				mActivePlayer = settings[mActivePlayer_INDEX];
				// gameState = GameState.values()[settings[gameState_INDEX]];
				gameMode = GameMode.values()[settings[gameMode_INDEX]];

				/* Otherwise loaded in Else */
				NUM_TILES = settings[NUM_TILES_INDEX];
				NUM_MOVES = settings[NUM_MOVES_INDEX];
				movesLeft = settings[movesLeft_INDEX];

				STAGING_AREA_TILES = settings[STAGING_AREA_TILES_INDEX];
				LOWER_TILE_BOUND = settings[LOWER_TILE_BOUND_INDEX];
				UPPER_TILE_BOUND = settings[UPPER_TILE_BOUND_INDEX];

				fis.close();
			}
			else {
				NUM_TILES = Integer.parseInt(sharedPrefs.getString(getString(R.string.board_size_key), "10"));
				NUM_MOVES = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_moves_key), "3"));
				movesLeft = NUM_MOVES;

				STAGING_AREA_TILES = (int) Math.floor((CAMERA_HEIGHT - CAMERA_WIDTH) / (CAMERA_WIDTH / NUM_TILES));
				if (STAGING_AREA_TILES % 2 == 1) {
					STAGING_AREA_TILES--;
				}

				LOWER_TILE_BOUND = STAGING_AREA_TILES / 2;
				UPPER_TILE_BOUND = LOWER_TILE_BOUND + NUM_TILES;
			}
		}
		catch (FileNotFoundException e) {
			Toast.makeText(this.getActivity(), e.toString(), Toast.LENGTH_LONG).show();
		}
		catch (IOException e) {
			Toast.makeText(this.getActivity(), e.toString(), Toast.LENGTH_LONG).show();
		}

		return new EngineOptions(ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
	}

	public void onCreateResources(OnCreateResourcesCallback ocrc) {
		int imageTextureDimen = 200;
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		/*******************
		 Fields *
		 */
		BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), imageTextureDimen * 4, imageTextureDimen * 5, TextureOptions.BILINEAR);

		mDarkWoodTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/field_dark.png",
				0, 0);
		mLightWoodTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/field_light.png",
				imageTextureDimen, 0);
		mRabbleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/rabble.png",
				imageTextureDimen * 2, 0);
		mSpearmenTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/spearmen.png",
				imageTextureDimen * 3, 0);

		mCrossbowmenTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/crossbowmen.png",
				0, imageTextureDimen);
		mLightHorseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/light_horse.png",
				imageTextureDimen, imageTextureDimen);
		mHeavyHorseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/heavy_horse.png",
				imageTextureDimen * 2, imageTextureDimen);
		mElephantTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/elephant.png",
				imageTextureDimen * 3, imageTextureDimen);

		mCatapultTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/catapult.png",
				0, imageTextureDimen * 2);
		mTrebuchetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/trebuchet.png",
				imageTextureDimen, imageTextureDimen * 2);
		mDragonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/dragon.png",
				imageTextureDimen * 2, imageTextureDimen * 2);
		mKingTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "units/king.png",
				imageTextureDimen * 3, imageTextureDimen * 2);

		mMountainTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/mountain.png",
				0, imageTextureDimen * 3);
		mRiverTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/river.png",
				imageTextureDimen, imageTextureDimen * 3);
		mStagingTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/staging.png",
				imageTextureDimen * 2, imageTextureDimen * 3);
		mCurtainTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/curtain.png",
				imageTextureDimen * 3, imageTextureDimen * 3);

		mFortressTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getActivity(), "terrains/fortress.png",
				0, imageTextureDimen * 4);

		mBitmapTextureAtlas.load();

		tileDimen = CAMERA_WIDTH / (float) NUM_TILES;

		float fontScalar;
		if (this.getResources().getString(R.string.is_tablet).equalsIgnoreCase("TRUE")) {
			fontScalar = 0.35f;
		}
		else {
			fontScalar = 0.5f;
		}
		this.mWhiteFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), imageTextureDimen, imageTextureDimen,
				Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/MagicMedieval.ttf"), Math.round(tileDimen * fontScalar), Color.WHITE_ARGB_PACKED_INT);
		this.mWhiteFont.load();
		this.mBlackFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), imageTextureDimen, imageTextureDimen,
				Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/MagicMedieval.ttf"), Math.round(tileDimen * fontScalar), Color.BLACK_ARGB_PACKED_INT);
		this.mBlackFont.load();

		ocrc.onCreateResourcesFinished();
	}

	public void onCreateScene(OnCreateSceneCallback ocsc) {

		scene = new Scene();
		scene.setBackground(new Background(0, 0, 0));

		board = new Tile[NUM_TILES][NUM_TILES + STAGING_AREA_TILES];

		textureWidth = mDarkWoodTextureRegion.getWidth();
		scaleFactor = tileDimen / textureWidth;
		offsetX = (textureWidth - tileDimen) / 2.0f;
		offsetY = (textureWidth - tileDimen) / 2.0f - (CAMERA_HEIGHT - (tileDimen * (NUM_TILES + STAGING_AREA_TILES))) / 2.0f;

		for (int i = 0; i < NUM_TILES; i++) {
			for (int j = 0; j < NUM_TILES + STAGING_AREA_TILES; j++) {
				float centerX = (i * tileDimen) - offsetX;
				float centerY = (j * tileDimen) - offsetY;
				ITextureRegion color;
				if (i % 2 == j % 2) {
					color = mDarkWoodTextureRegion;
				}
				else {
					color = mLightWoodTextureRegion;
				}

				if (j < LOWER_TILE_BOUND || j >= NUM_TILES + LOWER_TILE_BOUND) {
					color = mStagingTextureRegion;
				}

				CyvasseSprite sTile = new CyvasseSprite(centerX, centerY, color, this.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionUp()) {
							handleTouch(this.board_x, this.board_y, pSceneTouchEvent.getAction(), false);
						}
						return true;
					}
				};
				sTile.board_x = i;
				sTile.board_y = j;
				sTile.setScale(scaleFactor);
				sTile.setZIndex(Z_BUFFER_BOARD);

				scene.attachChild(sTile);
				scene.sortChildren();
				scene.registerTouchArea(sTile);

				if (color == mStagingTextureRegion) {
					if (j < LOWER_TILE_BOUND) {
						stagingTiles_p0.add(sTile);
					}
					else if (j >= NUM_TILES + LOWER_TILE_BOUND) {
						stagingTiles_p1.add(sTile);
					}

					board[i][j] = new Tile(i, j, null, null, (byte) -1, this);
				}
				else {
					board[i][j] = new Tile(i, j, TerrainType.FIELD, null, (byte) -1, this);
				}
			}
		}

		/*
		 * Use the preferences to see how many units there should be
		 */
		try {
			int tileByteSize = Unit.BYTE_SIZE + Terrain.BYTE_SIZE;
			File f = new File(mActivity.getFilesDir(), "game.save");
			if (/* TODO multi!mIsOnline &&*/ f.exists()) {
				byte tileBytes[] = new byte[tileByteSize * NUM_TILES * (NUM_TILES + STAGING_AREA_TILES)];
				FileInputStream fis = new FileInputStream(f);
				fis.skip(9); // jump over the state bytes and get to the board!
				fis.read(tileBytes, 0, tileBytes.length);
				fis.close();

				for (int i = 0; i < NUM_TILES; i++) {
					for (int j = 0; j < NUM_TILES + STAGING_AREA_TILES; j++) {
						int index = (j * (NUM_TILES) + i) * tileByteSize;
						Terrain t;
						Unit u;
						if (tileBytes[index] != -2 && tileBytes[index + 1] != -2 && tileBytes[index + 2] != -2) {
							t = new Terrain(tileBytes, index, i, j, this);
						}
						else {
							t = null;
						}
						if (tileBytes[index + 3] != -2 && tileBytes[index + 4] != -2 && tileBytes[index + 5] != -2) {
							u = new Unit(tileBytes, index + 3, this, i, j);
							numPieces[u.player]++;
							if (u.hasMoved) {
								Rectangle r = createRectangle(i, j);
								r.setColor(highlightGrey);
								r.setZIndex(Z_BUFFER_HAS_MOVED);
								hasMovedRectangles.add(r);
								scene.attachChild(r);
								scene.sortChildren();
							}
						}
						else {
							u = null;
						}
						board[i][j] = new Tile(u, t);
					}
				}

				switch (gameMode) {
					case GAME_MODE_PLAY:
						for (CyvasseSprite s : stagingTiles_p0) {
							this.detachChildRunnable(s);
						}
						for (CyvasseSprite s : stagingTiles_p1) {
							this.detachChildRunnable(s);
						}
						stagingTiles_p0.clear();
						stagingTiles_p1.clear();
						drawMovesText();
						break;
					case GAME_MODE_SETUP:
						drawCurtains((mActivePlayer + 1) % 2);
						checkIfPiecesArePlaced();
						break;
				}
				f.delete();
			}
			else {
				/* Set up the initial units */
				UnitType unitTypes[] = UnitType.values();
				int numInitialUnits[] = new int[unitTypes.length];
				numInitialUnits[UnitType.RABBLE.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_rabble_key), "3"));
				numInitialUnits[UnitType.SPEARMEN.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_spearmen_key), "2"));
				numInitialUnits[UnitType.CROSSBOWMEN.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_crossbowmen_key), "2"));
				numInitialUnits[UnitType.LIGHT_HORSE.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_light_horse_key), "3"));
				numInitialUnits[UnitType.HEAVY_HORSE.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_heavy_horse_key), "2"));
				numInitialUnits[UnitType.ELEPHANT.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_elephant_key), "2"));
				numInitialUnits[UnitType.CATAPULT.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_catapult_key), "2"));
				numInitialUnits[UnitType.TREBUCHET.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_trebuchet_key), "2"));
				numInitialUnits[UnitType.DRAGON.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_dragon_key), "1"));
				numInitialUnits[UnitType.KING.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_king_key), "1"));

				TerrainType terrainTypes[] = TerrainType.values();
				int numInitialTerrains[] = new int[terrainTypes.length];
				numInitialTerrains[TerrainType.MOUNTAIN.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_mountain_key), "5"));
				numInitialTerrains[TerrainType.RIVER.ordinal()] = Integer.parseInt(sharedPrefs.getString(getString(R.string.num_river_key), "5"));

				/*
				 * Make sure there is enough space
				 */
				int totalUnits = 0, totalTerrains = 0;
				for (int i : numInitialUnits) {
					totalUnits += i;
				}
				for (int i : numInitialTerrains) {
					totalTerrains += i;
				}
				if (totalUnits + totalTerrains > NUM_TILES * ((NUM_TILES + STAGING_AREA_TILES) / 2)) {
					Message msg = Message.obtain();
					Bundle data = new Bundle();
					data.putString(MAKE_TOAST_AND_FINISH,
							String.format(getString(R.string.too_many_pieces), totalUnits + totalTerrains, NUM_TILES * ((NUM_TILES + STAGING_AREA_TILES) / 2)));
					msg.setData(data);
					errorHandler.sendMessage(msg);
					return;
				}
				if (totalUnits < movesLeft) {
					movesLeft = totalUnits;
				}

				/*
				 * Place the units sequentially Add all units if offline, only add
				 * active players units if online. Opponent pieces will be placed later
				 */
				int xIndex = NUM_TILES - 1;
				int yIndex = 0;

				/* TODO multi				if (!mIsOnline || (mIsOnline && mPlayerNum == PLAYER_0))*/ {
					addTerrain((int) Math.floor((NUM_TILES - 1) / 2.0f), STAGING_AREA_TILES / 2, TerrainType.FORTRESS, PLAYER_0);

					for (int i = 0; i < numInitialUnits.length; i++) {
						for (int j = 0; j < numInitialUnits[i]; j++) {
							addUnit(xIndex, yIndex, unitTypes[i], PLAYER_0);
							xIndex--;
							if (xIndex < 0) {
								xIndex = NUM_TILES - 1;
								yIndex++;
							}
						}
					}

					for (int i = 0; i < numInitialTerrains.length; i++) {
						for (int j = 0; j < numInitialTerrains[i]; j++) {
							if (board[xIndex][yIndex].terrain == null || board[xIndex][yIndex].terrain.terrainType == TerrainType.FIELD) {
								addTerrain(xIndex, yIndex, terrainTypes[i], PLAYER_0);
							}
							else {
								j--;
							}
							xIndex--;
							if (xIndex < 0) {
								xIndex = NUM_TILES - 1;
								yIndex++;
							}
						}
					}
				}

				xIndex = 0;
				yIndex = NUM_TILES + STAGING_AREA_TILES - 1;

				/* TODO multi	if (!mIsOnline || (mIsOnline && mPlayerNum == PLAYER_1)) */{
					addTerrain((int) Math.ceil((NUM_TILES - 1) / 2.0f), STAGING_AREA_TILES / 2 + NUM_TILES - 1, TerrainType.FORTRESS, PLAYER_1);

					for (int i = 0; i < numInitialUnits.length; i++) {
						for (int j = 0; j < numInitialUnits[i]; j++) {
							addUnit(xIndex, yIndex, unitTypes[i], PLAYER_1);
							xIndex++;
							if (xIndex >= NUM_TILES) {
								xIndex = 0;
								yIndex--;
							}
						}
					}

					for (int i = 0; i < numInitialTerrains.length; i++) {
						for (int j = 0; j < numInitialTerrains[i]; j++) {
							if (board[xIndex][yIndex].terrain == null || board[xIndex][yIndex].terrain.terrainType == TerrainType.FIELD) {
								addTerrain(xIndex, yIndex, terrainTypes[i], PLAYER_1);
							}
							else {
								j--;
							}
							xIndex++;
							if (xIndex >= NUM_TILES) {
								xIndex = 0;
								yIndex--;
							}
						}
					}
				}
				/* TODO multi
				if (mIsOnline) {
					drawCurtains((mPlayerNum + 1) % 2);
				}
				else*/ {
					drawCurtains((mActivePlayer + 1) % 2);
				}
			}
		}
		catch (FileNotFoundException e) {
			Toast.makeText(this.getActivity(), e.toString(), Toast.LENGTH_LONG).show();
		}
		catch (IOException e) {
			Toast.makeText(this.getActivity(), e.toString(), Toast.LENGTH_LONG).show();
		}

		ocsc.onCreateSceneFinished(scene);
	}

	private void addTerrain(int x, int y, TerrainType type, byte player) {
		board[x][y].terrain = new Terrain(x, y, this, type, player);
	}

	private void addUnit(int x, int y, UnitType type, byte player) {
		board[x][y].unit = new Unit(x, y, this, type, player);
		numPieces[player]++;
	}

	private void handleTouch(int x, int y, int touchType, boolean isTouchFromOpponent) {
		if (gameIsOver) {
			return;
		}
		switch (gameMode) {
			case GAME_MODE_SETUP: {
				handleSetupTouch(x, y, touchType);
				break;
			}
			case GAME_MODE_PLAY: {
				if (LOWER_TILE_BOUND <= y && y < UPPER_TILE_BOUND) {
					/* TODO multi
					if (mIsOnline && mActivePlayer == mPlayerNum && !isTouchFromOpponent) {
						// Process own touches, send them if necessary
						String data = x + ":" + y + ":" + touchType;
						this.sendRealTimeMessage(data.getBytes());
						handlePlayTouch(x, y, touchType);
					}
					else if (mIsOnline && mActivePlayer != mPlayerNum && isTouchFromOpponent) {
						// Mirror opponent touches
						handlePlayTouch(x, y, touchType);
					}
					// Ignore touches from nonactive player when online
					else if (!mIsOnline)*/ {
						handlePlayTouch(x, y, touchType);
					}
				}
				break;
			}
			default: {
				break;
			}
		}
	}

	private void handlePlayTouch(int x, int y, int touchType) {
		switch (gameState) {
			case NO_UNIT_SELECTED:
				if (touchType == MotionEvent.ACTION_DOWN && board[x][y].unit != null && !board[x][y].unit.hasMoved) {

					selectedUnit.x = x;
					selectedUnit.y = y;
					hasUnitMoved = false;

					board[x][y].unit.sprite.setZIndex(Z_BUFFER_SELECTED_UNIT);
					board[x][y].unit.strengthText.setZIndex(Z_BUFFER_SELECTED_UNIT_TEXT);

					if (board[x][y].unit.player == mActivePlayer) {
						currentMovesAndAttacks = movCalc.findMovesThenAttacks(board, selectedUnit);

						/*
						 * cull invalid attacks off the bat
						 */
						for (int i = 0; i < currentMovesAndAttacks.attacks.size(); i++) {
							Coord attack = currentMovesAndAttacks.attacks.get(i);
							if (board[attack.x][attack.y].unit != null && board[attack.x][attack.y].unit.player == mActivePlayer) {
								currentMovesAndAttacks.attacks.remove(i);
								i--;
							}
						}

						gameState = GameState.FRIENDLY_UNIT_SELECTED;
						for (Coord move : currentMovesAndAttacks.moves) {
							Rectangle r = createRectangle(move.x, move.y);
							r.setColor(highlightBlue);
							r.setZIndex(Z_BUFFER_HIGHLIGHT);
							highlightRectangles.add(r);
							scene.attachChild(r);
						}
						for (Coord attack : currentMovesAndAttacks.attacks) {
							if (!currentMovesAndAttacks.moves.contains(attack)) {
								Rectangle r = createRectangle(attack.x, attack.y);
								r.setColor(highlightRed);
								r.setZIndex(Z_BUFFER_HIGHLIGHT);
								highlightRectangles.add(r);
								scene.attachChild(r);
							}
						}
					}
					else {
						currentMovesAndAttacks = movCalc.findEnemyMovesAndAttacks(board, selectedUnit);
						gameState = GameState.ENEMY_UNIT_SELECTED;
						for (Coord c : currentMovesAndAttacks.attacks) {
							Rectangle r = createRectangle(c.x, c.y);
							r.setColor(highlightRed);
							r.setZIndex(Z_BUFFER_HIGHLIGHT);
							highlightRectangles.add(r);
							scene.attachChild(r);
						}
					}

					scene.sortChildren();
				}
				break;
			case FRIENDLY_UNIT_SELECTED:
				if (touchType == MotionEvent.ACTION_DOWN) {
					if (x == selectedUnit.x && y == selectedUnit.y) {
						/*
						 * The unit was not moved. It might still be able to attack
						 * Otherwise this isn't treated as a move, just as a cancel
						 */
						hasUnitMoved = false;
						gameState = GameState.UNIT_ATTACKING;
						startAttack();
					}
					else if (currentMovesAndAttacks.moves.contains(new Coord(x, y))) {
						/*
						 * The unit was moved somewhere. Move the unit and check for
						 * potential attacks Note, artillery units can't attack if they've
						 * been moved.
						 */
						hasUnitMoved = true;
						for (Rectangle rect : highlightRectangles) {
							detachChildRunnable(rect);
						}
						highlightRectangles.clear();

						/*
						 * Move the unit
						 */
						board[x][y].unit = board[selectedUnit.x][selectedUnit.y].unit;
						board[selectedUnit.x][selectedUnit.y].unit = null;
						board[x][y].unit.setPosition(this, x, y);
						board[x][y].unit.sprite.setZIndex(Z_BUFFER_UNIT);
						board[x][y].unit.strengthText.setZIndex(Z_BUFFER_UNIT_TEXT);
						board[x][y].unit.hasMoved = true;

						selectedUnit.x = x;
						selectedUnit.y = y;

						/*
						 * If there is a valid attack, and it's not artillery, start the
						 * attacking process Otherwise clear the unit and return to default
						 * state
						 */
						if (board[x][y].unit.minAttackRange == 1) {
							gameState = GameState.UNIT_ATTACKING;
							startAttack();
						}
						else {
							deselectUnit(true, true);
							gameState = GameState.NO_UNIT_SELECTED;
						}
					}
				}
				break;
			case UNIT_ATTACKING:
				/*
				 * There is a unit attacking, might be artillery or not
				 * Movement.uniqueAttacks has the attacks listed Movement.uniqueMoves
				 * will move the unit without attacking
				 */
				if (touchType == MotionEvent.ACTION_DOWN) {
					if (currentMovesAndAttacks.moves.contains(new Coord(x, y))) {
						gameState = GameState.NO_UNIT_SELECTED;
						deselectUnit(hasUnitMoved, hasUnitMoved);
					}
					else if (currentMovesAndAttacks.attacks.contains(new Coord(x, y))) {

						// Attack a unit if it exists, then attack a fortress if there is no
						// unit on it
						if (board[x][y].unit != null && board[x][y].unit.player != mActivePlayer) {

							/*
							 * Attack the target unit. Get a list of casualties.
							 */
							UnitsInvolvedInCombat units = movCalc.fightUnit(board, new Coord(x, y), selectedUnit);

							float pDuration = 0.5f;
							int pFromRotation;
							int pToRotation;

							for (Coord c : units.attackingUnits) {
								if (board[c.x][c.y].unit.player == PLAYER_1) {
									pFromRotation = 0;
									pToRotation = 45;
								}
								else {
									pFromRotation = 180;
									pToRotation = 225;
								}
								RotationModifier rm = new RotationModifier(pDuration / 2, pFromRotation, pToRotation, new ChainModifierListener(board[c.x][c.y].unit.sprite,
										new RotationModifier(pDuration / 2, pToRotation, pFromRotation)));
								board[c.x][c.y].unit.sprite.registerEntityModifier(rm);
							}
							for (Coord c : units.deadUnits) {
								if (board[c.x][c.y].unit.player == PLAYER_1) {
									pFromRotation = 0;
									pToRotation = 90;
								}
								else {
									pFromRotation = 180;
									pToRotation = 270;
								}
								RotationModifier rm = new RotationModifier(pDuration, pFromRotation, pToRotation, new DetachUnitAfterModifierListener(c.x, c.y));
								board[c.x][c.y].unit.sprite.registerEntityModifier(rm);
							}
							scene.sortChildren();

							/*
							 * Attacking counts as a move
							 */
							board[selectedUnit.x][selectedUnit.y].unit.hasMoved = true;

							gameState = GameState.NO_UNIT_SELECTED;
							deselectUnit(true, !units.deadUnits.contains(selectedUnit));
						}
						else if (board[x][y].terrain != null && board[x][y].terrain.terrainType == TerrainType.FORTRESS && board[x][y].terrain.player != mActivePlayer) {
							/*
							 * Attacking a fortress
							 */
							float pDuration = 0.5f;
							int pFromRotation;
							int pToRotation;

							if (board[selectedUnit.x][selectedUnit.y].unit.player == PLAYER_1) {
								pFromRotation = 0;
								pToRotation = 45;
							}
							else {
								pFromRotation = 180;
								pToRotation = 225;
							}
							RotationModifier rm = new RotationModifier(pDuration / 2, pFromRotation, pToRotation, new ChainModifierListener(
									board[selectedUnit.x][selectedUnit.y].unit.sprite, new RotationModifier(pDuration / 2, pToRotation, pFromRotation)));
							board[selectedUnit.x][selectedUnit.y].unit.sprite.registerEntityModifier(rm);

							board[x][y].terrain.decreaseHP(board[selectedUnit.x][selectedUnit.y].unit.strength);
							deselectUnit(true, true);
						}
					}
				}
				break;
			case ENEMY_UNIT_SELECTED:
				/*
				 * no move
				 */
				if (touchType == MotionEvent.ACTION_UP) {
					deselectUnit(false, false);
					gameState = GameState.NO_UNIT_SELECTED;
				}
				break;
			default:
				break;
		}
	}

	private Rectangle createRectangle(int x, int y) {
		return new Rectangle(x * tileDimen, y * tileDimen + ((CAMERA_HEIGHT - (tileDimen * (NUM_TILES + STAGING_AREA_TILES))) / 2), tileDimen, tileDimen,
				this.getVertexBufferObjectManager());
	}

	private void deselectUnit(boolean hasMoved, boolean drawHighlight) {
		gameState = GameState.NO_UNIT_SELECTED;

		/*
		 * Check if the game is over, either by killing all units or destroying the
		 * fortress
		 */
		if (hasMoved) {
			if (numPieces[PLAYER_0] == 0 || (board[(int) Math.floor((NUM_TILES - 1) / 2.0f)][STAGING_AREA_TILES / 2].terrain.hp == 0)) {
				// WINNER
				Text winText = new Text(0, 0, this.mWhiteFont, getString(R.string.you_win), new TextOptions(HorizontalAlign.LEFT), this.getVertexBufferObjectManager());
				winText.setPosition(0, (CAMERA_HEIGHT) / 2 + (tileDimen * ((NUM_TILES + 2) / 2)));
				// LOSER
				Text loseText = new Text(0, 0, this.mWhiteFont, getString(R.string.you_lose), new TextOptions(HorizontalAlign.LEFT),
						this.getVertexBufferObjectManager());
				loseText.setRotation(180);
				loseText.setPosition(CAMERA_WIDTH - loseText.getWidth(), (CAMERA_HEIGHT) / 2 - (tileDimen * ((NUM_TILES + 2) / 2)) - loseText.getHeight());

				scene.attachChild(winText);
				scene.attachChild(loseText);
				scene.sortChildren();
				gameIsOver = true;
			}
			else if (numPieces[PLAYER_1] == 0 || (board[(int) Math.ceil((NUM_TILES - 1) / 2.0f)][STAGING_AREA_TILES / 2 + NUM_TILES - 1].terrain.hp == 0)) {
				// WINNER
				Text winText = new Text(0, 0, this.mWhiteFont, getString(R.string.you_win), new TextOptions(HorizontalAlign.LEFT), this.getVertexBufferObjectManager());
				winText.setRotation(180);
				winText.setPosition(CAMERA_WIDTH - winText.getWidth(), (CAMERA_HEIGHT) / 2 - (tileDimen * ((NUM_TILES + 2) / 2)) - winText.getHeight());
				// LOSER
				Text loseText = new Text(0, 0, this.mWhiteFont, getString(R.string.you_lose), new TextOptions(HorizontalAlign.LEFT),
						this.getVertexBufferObjectManager());
				loseText.setPosition(0, (CAMERA_HEIGHT) / 2 + (tileDimen * ((NUM_TILES + 2) / 2)));

				scene.attachChild(winText);
				scene.attachChild(loseText);
				scene.sortChildren();
				gameIsOver = true;
			}
		}

		if (board[selectedUnit.x][selectedUnit.y].unit != null) {
			board[selectedUnit.x][selectedUnit.y].unit.sprite.setZIndex(Z_BUFFER_UNIT);
			board[selectedUnit.x][selectedUnit.y].unit.strengthText.setZIndex(Z_BUFFER_UNIT_TEXT);
		}

		for (Rectangle rect : highlightRectangles) {
			detachChildRunnable(rect);
		}

		highlightRectangles.clear();

		if (hasMoved) {
			if (drawHighlight) {
				Rectangle r = createRectangle(selectedUnit.x, selectedUnit.y);
				r.setColor(highlightGrey);
				r.setZIndex(Z_BUFFER_HAS_MOVED);
				hasMovedRectangles.add(r);
				scene.attachChild(r);
				scene.sortChildren();
			}
			decrementMovesLeft();
		}
		selectedUnit.x = -1;
		selectedUnit.y = -1;
	}

	private void decrementMovesLeft() {
		movesLeft--;

		if (movesLeft == 0) {
			mActivePlayer = (byte) ((mActivePlayer + 1) % 2);
			for (int i = 0; i < NUM_TILES; i++) {
				for (int j = LOWER_TILE_BOUND; j < UPPER_TILE_BOUND; j++) {
					if (board[i][j].unit != null) {
						board[i][j].unit.hasMoved = false;
					}
				}
			}
			for (Rectangle rect : hasMovedRectangles) {
				detachChildRunnable(rect);
			}
			hasMovedRectangles.clear();
			movesLeft = Math.min(numPieces[mActivePlayer], NUM_MOVES);
		}
		drawMovesText();
	}

	/*
	 * Note, the potential attacking tiles are populated in
	 * currentMovesAndAttacks, which is always filled before this
	 */
	private void startAttack() {
		/*
		 * Clear prior highlighting
		 */
		for (Rectangle rect : highlightRectangles) {
			detachChildRunnable(rect);
		}
		highlightRectangles.clear();

		/*
		 * Add the highlight rectangle to move but not attack
		 */
		currentMovesAndAttacks.moves.clear();
		currentMovesAndAttacks.moves.add(selectedUnit);
		Rectangle r = createRectangle(selectedUnit.x, selectedUnit.y);
		r.setColor(highlightBlue);
		r.setZIndex(Z_BUFFER_HIGHLIGHT);
		highlightRectangles.add(r);
		scene.attachChild(r);

		/*
		 * Add the highlight rectangles to attack
		 */
		currentMovesAndAttacks.attacks.clear();
		currentMovesAndAttacks.attacks = movCalc.findPotentialAttacks(board, selectedUnit);
		for (Coord attack : currentMovesAndAttacks.attacks) {
			Rectangle rect = createRectangle(attack.x, attack.y);
			rect.setColor(highlightRed);
			rect.setZIndex(Z_BUFFER_HIGHLIGHT);
			highlightRectangles.add(rect);
			scene.attachChild(rect);
		}
		scene.sortChildren();
	}

	private void handleSetupTouch(int x, int y, int touchType) {
		switch (gameState) {
			case NO_UNIT_SELECTED: {
				if (touchType == MotionEvent.ACTION_DOWN && board[x][y].unit != null && board[x][y].unit.player == mActivePlayer) {
					selectedUnit.x = x;
					selectedUnit.y = y;
					board[x][y].unit.sprite.setZIndex(Z_BUFFER_SELECTED_UNIT);
					board[x][y].unit.strengthText.setZIndex(Z_BUFFER_SELECTED_UNIT_TEXT);
					gameState = GameState.FRIENDLY_UNIT_SELECTED;

					currentMovesAndAttacks.moves.clear();
					for (int i = 0; i < NUM_TILES; i++) {
						if (mActivePlayer == PLAYER_0) {
							for (int j = LOWER_TILE_BOUND; j < NUM_TILES / 2 + LOWER_TILE_BOUND; j++) {
								if ((board[i][j].unit == null) || (selectedUnit.x == i && selectedUnit.y == j)) {
									currentMovesAndAttacks.moves.add(new Coord(i, j));
								}
							}
						}
						else {
							for (int j = NUM_TILES / 2 + LOWER_TILE_BOUND; j < NUM_TILES + LOWER_TILE_BOUND; j++) {
								if ((board[i][j].unit == null) || (selectedUnit.x == i && selectedUnit.y == j)) {
									currentMovesAndAttacks.moves.add(new Coord(i, j));
								}
							}
						}
					}

					for (Coord move : currentMovesAndAttacks.moves) {
						Rectangle r = createRectangle(move.x, move.y);
						r.setColor(highlightBlue);
						r.setZIndex(Z_BUFFER_HIGHLIGHT);
						highlightRectangles.add(r);
						scene.attachChild(r);
					}
					scene.sortChildren();
				}
				else if (touchType == MotionEvent.ACTION_DOWN && board[x][y].terrain != null && board[x][y].terrain.terrainType != TerrainType.FIELD
						&& board[x][y].terrain.terrainType != TerrainType.FORTRESS && board[x][y].terrain.player == mActivePlayer) {
					selectedTerrain.x = x;
					selectedTerrain.y = y;
					board[x][y].terrain.sprite.setZIndex(Z_BUFFER_SELECTED_UNIT);
					gameState = GameState.FRIENDLY_TERRAIN_SELECTED;

					currentMovesAndAttacks.moves.clear();
					for (int i = 0; i < NUM_TILES; i++) {
						if (mActivePlayer == PLAYER_0) {
							for (int j = LOWER_TILE_BOUND; j < NUM_TILES / 2 + LOWER_TILE_BOUND; j++) {
								if ((board[i][j].terrain.terrainType == TerrainType.FIELD) || (selectedTerrain.x == i && selectedTerrain.y == j)) {
									currentMovesAndAttacks.moves.add(new Coord(i, j));
								}
							}
						}
						else {
							for (int j = NUM_TILES / 2 + LOWER_TILE_BOUND; j < NUM_TILES + LOWER_TILE_BOUND; j++) {
								if ((board[i][j].terrain.terrainType == TerrainType.FIELD) || (selectedTerrain.x == i && selectedTerrain.y == j)) {
									currentMovesAndAttacks.moves.add(new Coord(i, j));
								}
							}
						}
					}

					for (Coord move : currentMovesAndAttacks.moves) {
						Rectangle r = createRectangle(move.x, move.y);
						r.setColor(highlightBlue);
						r.setZIndex(Z_BUFFER_HIGHLIGHT);
						highlightRectangles.add(r);
						scene.attachChild(r);
					}
					scene.sortChildren();
				}
				break;
			}
			case FRIENDLY_UNIT_SELECTED: {
				if (touchType == MotionEvent.ACTION_DOWN) {
					if (currentMovesAndAttacks.moves.contains(new Coord(x, y))) {
						gameState = GameState.NO_UNIT_SELECTED;

						/*
						 * Move the unit, if the new touch is different than the old one
						 */
						if (!(x == selectedUnit.x && y == selectedUnit.y)) {
							board[x][y].unit = board[selectedUnit.x][selectedUnit.y].unit;
							board[selectedUnit.x][selectedUnit.y].unit = null;
							board[x][y].unit.setPosition(this, x, y);
							selectedUnit.x = x;
							selectedUnit.y = y;
						}

						deselectUnit(false, false);
						scene.sortChildren();
					}
					checkIfPiecesArePlaced();
				}
				break;
			}
			case FRIENDLY_TERRAIN_SELECTED: {
				if (touchType == MotionEvent.ACTION_DOWN) {
					if (currentMovesAndAttacks.moves.contains(new Coord(x, y))) {
						gameState = GameState.NO_UNIT_SELECTED;

						for (Rectangle rect : highlightRectangles) {
							detachChildRunnable(rect);
						}
						highlightRectangles.clear();

						/*
						 * Move the terrain, if the new touch is different than the old one
						 */
						if (!(x == selectedTerrain.x && y == selectedTerrain.y)) {
							board[x][y].terrain = board[selectedTerrain.x][selectedTerrain.y].terrain;

							if (selectedTerrain.y < LOWER_TILE_BOUND || selectedTerrain.y >= UPPER_TILE_BOUND) {
								board[selectedTerrain.x][selectedTerrain.y].terrain = null;
							}
							else {
								board[selectedTerrain.x][selectedTerrain.y].terrain = new Terrain(selectedTerrain.x, selectedTerrain.y, this, TerrainType.FIELD, mActivePlayer);
							}
							board[x][y].terrain.setPosition(this, x, y);
						}
						board[x][y].terrain.sprite.setZIndex(Z_BUFFER_TERRAIN);

						selectedTerrain.x = -1;
						selectedTerrain.y = -1;
						scene.sortChildren();
					}
					checkIfPiecesArePlaced();
				}
				break;
			}
			default: {
				break;
			}
		}
	}

	private boolean checkIfPiecesArePlaced() {
		for (int i = 0; i < NUM_TILES; i++) {
			if (mActivePlayer == PLAYER_0) {
				for (int j = 0; j < LOWER_TILE_BOUND; j++) {
					if (board[i][j].terrain != null) {
						return false;
					}
					if (board[i][j].unit != null) {
						return false;
					}
				}
			}
			else {
				for (int j = UPPER_TILE_BOUND; j < NUM_TILES + STAGING_AREA_TILES; j++) {
					if (board[i][j].terrain != null) {
						return false;
					}
					if (board[i][j].unit != null) {
						return false;
					}
				}
			}
		}
		if (doneText == null) {

			/*
			 * Clear the staging tiles
			 */
			if (mActivePlayer == PLAYER_0) {
				for (CyvasseSprite s : stagingTiles_p0) {
					scene.unregisterTouchArea(s);
					detachChildRunnable(s);
				}
				stagingTiles_p0.clear();
			}
			else {
				for (CyvasseSprite s : stagingTiles_p1) {
					scene.unregisterTouchArea(s);
					detachChildRunnable(s);
				}
				stagingTiles_p1.clear();
			}

			doneText = new Text(0, 0, this.mWhiteFont, getString(R.string.touch_here_when_done), new TextOptions(HorizontalAlign.LEFT),
					this.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						/* TODO multi
						if (mIsOnline) {
							showDialog(WAIT_FOR_PLAYER_DIALOG);
							mWaitingForOpponentsPlacement = true;

							// exchange piece location information, check to start
							int tileByteSize = Unit.BYTE_SIZE + Terrain.BYTE_SIZE;
							byte[] piecePositionBytes = new byte[NUM_TILES * NUM_TILES / 2 * tileByteSize];
							for (int i = 0; i < NUM_TILES; i++) {
								for (int j = 0; j < NUM_TILES / 2; j++) {
									byte tmp[] = board[i][j + STAGING_AREA_TILES / 2 + mPlayerNum * NUM_TILES / 2].saveTile();
									System.arraycopy(tmp, 0, piecePositionBytes, (j * (NUM_TILES) + i) * tileByteSize, tileByteSize);
								}
							}
							sendRealTimeMessage(piecePositionBytes);

							checkStartOnlinePlay();
						}
						else*/ if (mActivePlayer == PLAYER_0) {
							/*
							 * Clear the done text
							 */
							detachChildRunnable(doneText);
							scene.unregisterTouchArea(doneText);
							doneText = null;

							/*
							 * Switch the players and draw the curtains
							 */
							drawCurtains(PLAYER_0);
							mActivePlayer = (byte) ((mActivePlayer + 1) % 2);
						}
						else if (mActivePlayer == PLAYER_1) {
							/*
							 * Clear the done text
							 */
							detachChildRunnable(doneText);
							scene.unregisterTouchArea(doneText);
							doneText = null;

							/*
							 * Switch the players and draw the curtains
							 */
							drawCurtains(3);
							mActivePlayer = (byte) ((mActivePlayer + 1) % 2);

							/*
							 * Start the game
							 */
							gameMode = GameMode.GAME_MODE_PLAY;
							drawMovesText();
							// Just in case
							for (CyvasseSprite s : stagingTiles_p0) {
								((CyvasseGameFragment) mFragment).detachChildRunnable(s);
							}
							for (CyvasseSprite s : stagingTiles_p1) {
								((CyvasseGameFragment) mFragment).detachChildRunnable(s);
							}
							stagingTiles_p0.clear();
							stagingTiles_p1.clear();
						}

						/*
						 * Clear any currently selected unit
						 */
						for (Rectangle rect : highlightRectangles) {
							detachChildRunnable(rect);
						}
						selectedTerrain.x = -1;
						selectedTerrain.y = -1;
						selectedUnit.x = -1;
						selectedUnit.y = -1;
						gameState = GameState.NO_UNIT_SELECTED;
						return true;
					}
					return true;
				}
			};

			/*
			 * Place the done text accordingly
			 */
			if (mActivePlayer == PLAYER_0) {
				doneText.setRotation(180);
				doneText.setPosition(CAMERA_WIDTH - doneText.getWidth(), (CAMERA_HEIGHT) / 2 - (tileDimen * ((NUM_TILES + 2) / 2)) - doneText.getHeight());
			}
			else {
				doneText.setPosition(0, (CAMERA_HEIGHT) / 2 + (tileDimen * ((NUM_TILES + 2) / 2)));
			}

			scene.registerTouchArea(doneText);
			scene.attachChild(doneText);
		}
		return true;
	}

	private void drawMovesText() {
		detachChildRunnable(movesLeftText);

		if (gameIsOver) {
			return;
		}

		String text = getResources().getQuantityString(R.plurals.moves_left, movesLeft, movesLeft);

		movesLeftText = new Text(0, 0, this.mWhiteFont, text, new TextOptions(HorizontalAlign.LEFT), this.getVertexBufferObjectManager());
		if (mActivePlayer == PLAYER_0) {
			movesLeftText.setRotation(180);
			movesLeftText.setPosition(CAMERA_WIDTH - movesLeftText.getWidth(), (CAMERA_HEIGHT) / 2 - (tileDimen * (NUM_TILES / 2)) - movesLeftText.getHeight());
		}
		else {
			movesLeftText.setPosition(0, (CAMERA_HEIGHT) / 2 + (tileDimen * (NUM_TILES / 2)));
		}
		scene.attachChild(movesLeftText);
	}

	private void drawCurtains(int player) {
		for (CyvasseSprite curtain : curtains) {
			detachChildRunnable(curtain);
		}
		curtains.clear();

		if (player == PLAYER_0 || player == PLAYER_1) {
			for (int i = 0; i < NUM_TILES; i++) {
				for (int j = 0; j < (NUM_TILES + STAGING_AREA_TILES) / 2; j++) {
					float centerX = (i * tileDimen) - offsetX;
					float centerY = (j + (player) * (NUM_TILES + STAGING_AREA_TILES) / 2) * tileDimen - offsetY;

					CyvasseSprite sCurtain = new CyvasseSprite(centerX, centerY, mCurtainTextureRegion, this.getVertexBufferObjectManager());
					sCurtain.setScale(scaleFactor);
					sCurtain.setZIndex(Z_BUFFER_CURTAIN);

					if (player == PLAYER_1) {
						sCurtain.setRotation(180);
					}

					scene.attachChild(sCurtain);
					curtains.add(sCurtain);
				}
			}
		}
		scene.sortChildren();
	}

	public void detachChildRunnable(final IEntity entity) {
		scene.postRunnable(new Runnable() {
			public void run() {
				scene.detachChild(entity);
			}
		});
	}

	public void detachUnitRunnable(final CyvasseSprite unitSprite, final Text unitstrengthText) {
		scene.postRunnable(new Runnable() {
			public void run() {
				scene.detachChild(unitSprite);
				scene.detachChild(unitstrengthText);
				scene.sortChildren();
			}
		});
	}

	/*
	 * The attaching this listener will remove the unit at x,y after the animation
	 * is complete It also handles numPieces
	 */
	class DetachUnitAfterModifierListener implements IEntityModifierListener {
		int										x, y;
		private CyvasseSprite	unitSprite;
		private Text					unitstrengthText;

		public DetachUnitAfterModifierListener(int x, int y) {
			this.x = x;
			this.y = y;
			this.unitSprite = board[x][y].unit.sprite;
			this.unitstrengthText = board[x][y].unit.strengthText;

			for (int i = 0; i < hasMovedRectangles.size(); i++) {
				Rectangle r = hasMovedRectangles.get(i);
				if (r.getX() == x * tileDimen && r.getY() == y * tileDimen + ((CAMERA_HEIGHT - (tileDimen * (NUM_TILES + STAGING_AREA_TILES))) / 2)) {
					detachChildRunnable(r);
					hasMovedRectangles.remove(i);
					break;
				}
			}

			numPieces[board[x][y].unit.player]--;
		}

		public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			board[x][y].unit = null;
		}

		public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
			((CyvasseGameFragment) mFragment).detachUnitRunnable(unitSprite, unitstrengthText);
		}
	}

	class ChainModifierListener implements IEntityModifierListener {

		private IEntityModifier	iEntityModifier;
		private IEntity					iEntity;

		public ChainModifierListener(IEntity ie, IEntityModifier iem) {
			this.iEntity = ie;
			this.iEntityModifier = iem;
		}

		public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

		}

		public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
			iEntity.registerEntityModifier(iEntityModifier);
		}
	}

	public void dismissDialog() {
		Fragment dialog = getFragmentManager().findFragmentByTag(DIALOG_TAG);
		if (dialog != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(dialog);
			ft.commit();
		}
	}

	public void showDialog(final int id) {
		// CyvasseGameFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
		if (prev != null) {
			ft.remove(prev);
		}

		// Create and show the dialog.
		CyvasseDialogFragment newFragment = new CyvasseDialogFragment() {

			@Override
			public void onDestroyView() {
				if (getDialog() != null) {
					getDialog().dismiss();
				}
				if (getDialog() != null && getRetainInstance()) {
					getDialog().setOnDismissListener(null);
				}
				super.onDestroyView();
			}

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				switch (id) {
					case SAVE_DIALOG: {
						Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/MagicMedieval.ttf");
						final Dialog dialog = new Dialog(this.getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
						dialog.setContentView(R.layout.dialog_styled);

						MainActivity.setUpDialog(dialog, tf, getString(R.string.back_dialog_title), null, getString(R.string.save), getString(R.string.cancel),
								getString(R.string.dont_save), false);

						dialog.findViewById(R.id.dialog_positive).setOnClickListener(new OnClickListener() {

							public void onClick(View v) { // Save first, then finish

								// If a unit moved, but hasn't attacked or anything, count its
								// move
								// When the game loads, it will have finished it's sequence

								if (hasUnitMoved) {
									decrementMovesLeft();
								}

								int tileByteSize = Unit.BYTE_SIZE + Terrain.BYTE_SIZE;

								byte[] saveBytes = new byte[9 + tileByteSize * NUM_TILES * (NUM_TILES + STAGING_AREA_TILES)];
								saveBytes[mActivePlayer_INDEX] = ((CyvasseGameFragment) mFragment).mActivePlayer;
								saveBytes[movesLeft_INDEX] = (byte) ((CyvasseGameFragment) mFragment).movesLeft;
								saveBytes[gameState_INDEX] = (byte) ((CyvasseGameFragment) mFragment).gameState.ordinal();
								saveBytes[gameMode_INDEX] = (byte) ((CyvasseGameFragment) mFragment).gameMode.ordinal();
								saveBytes[NUM_TILES_INDEX] = (byte) CyvasseGameFragment.NUM_TILES;
								saveBytes[STAGING_AREA_TILES_INDEX] = (byte) CyvasseGameFragment.STAGING_AREA_TILES;
								saveBytes[LOWER_TILE_BOUND_INDEX] = (byte) CyvasseGameFragment.LOWER_TILE_BOUND;
								saveBytes[UPPER_TILE_BOUND_INDEX] = (byte) CyvasseGameFragment.UPPER_TILE_BOUND;
								saveBytes[NUM_MOVES_INDEX] = (byte) CyvasseGameFragment.NUM_MOVES;

								for (int i = 0; i < NUM_TILES; i++) {
									for (int j = 0; j < NUM_TILES + STAGING_AREA_TILES; j++) {
										byte tmp[] = board[i][j].saveTile();
										System.arraycopy(tmp, 0, saveBytes, 9 + (j * (NUM_TILES) + i) * tileByteSize, tileByteSize);
									}
								}

								try {
									FileOutputStream fos = mActivity.openFileOutput("game.save", Context.MODE_PRIVATE);
									fos.write(saveBytes);
									fos.close();
								} catch (FileNotFoundException e) {
									Toast.makeText(mActivity, e.toString(), Toast.LENGTH_LONG).show();
								} catch (IOException e) {
									Toast.makeText(mActivity, e.toString(), Toast.LENGTH_LONG).show();
								}

								dialog.dismiss();
								getFragmentManager().popBackStack();
							}
						});

						((Button) dialog.findViewById(R.id.dialog_neutral)).setTypeface(tf);
						dialog.findViewById(R.id.dialog_neutral).setOnClickListener(new OnClickListener() {

							public void onClick(View v) { // Don't save, just finish
								dialog.dismiss();
								getFragmentManager().popBackStack();
							}
						});

						((Button) dialog.findViewById(R.id.dialog_negative)).setTypeface(tf);
						dialog.findViewById(R.id.dialog_negative).setOnClickListener(new OnClickListener() {

							public void onClick(View v) { // close the dialog
								dialog.dismiss();
							}
						});

						return dialog;
					}
					case WAIT_FOR_PLAYER_DIALOG: {
						Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/MagicMedieval.ttf");

						final Dialog dialog = new Dialog(this.getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
						dialog.setContentView(R.layout.dialog_styled);
						MainActivity.setUpDialog(dialog, tf, "Waiting for the other player", null, null, null, null, true);

						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						this.setCancelable(false);

						return dialog;
					}
					case ONLINE_QUIT_DIALOG: {
						Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/MagicMedieval.ttf");

						final Dialog dialog = new Dialog(this.getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
						dialog.setContentView(R.layout.dialog_styled);

						MainActivity.setUpDialog(dialog, tf, "do you really want to forefit an online match?", null, "Yep", "Nah", null, false);

						dialog.findViewById(R.id.dialog_positive).setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								dismissDialog();
								getFragmentManager().popBackStack();
								/* TODO multi
								((MainActivity) CyvasseGameFragment.this.getActivity()).endOnlineGame();
								*/
							}
						});
						dialog.findViewById(R.id.dialog_negative).setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								dismissDialog();
							}
						});

						return dialog;
					}
					case PLAYER_LEFT_DIALOG: {

						Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/MagicMedieval.ttf");

						final Dialog dialog = new Dialog(this.getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
						dialog.setContentView(R.layout.dialog_styled);

						MainActivity.setUpDialog(dialog, tf, "The other player disconnected.", null, "D'oh", null, null, false);
						dialog.findViewById(R.id.dialog_positive).setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								dismissDialog();
								getFragmentManager().popBackStack();
								/* TODO multi
								((MainActivity) CyvasseGameFragment.this.getActivity()).endOnlineGame();
								*/
							}
						});
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						this.setCancelable(false);
						return dialog;
					}
					default: {
						return null;
					}
				}
			}
		};
		newFragment.show(ft, DIALOG_TAG);
	}

	@Override
	protected int getLayoutID() {
		return R.layout.fragment_game_layout;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.xmllayoutexample_rendersurfaceview;
	}

	public boolean onInterceptBackKey() {
		/* TODO multi
		if (mIsOnline) {
			showDialog(ONLINE_QUIT_DIALOG);
			return true;
		}
		*/
		if (gameIsOver) {
			return true;
		}
		else {
			showDialog(SAVE_DIALOG);
			return false;
		}
	}
	
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}


	/* TODO multi
	public void receiveRealTimeMessage(final RealTimeMessage arg0) {
		switch (gameMode) {
			case GAME_MODE_SETUP:
				// Store the opponents pieces to draw later, check if the game should
				// start
				mOpponentTileBytes = arg0.getMessageData();
				mReceivedOpponentsPlacement = true;
				checkStartOnlinePlay();
				break;
			case GAME_MODE_PLAY:
				// Perform the opponent's touch event
				String data[] = new String(arg0.getMessageData()).split(":");
				handleTouch(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), true);
				break;
		}
	}

	public void sendRealTimeMessage(byte[] message) {
		((MainActivity) this.getActivity()).sendRealTimeMessage(message);
	}

	public void checkStartOnlinePlay() {
		if (mWaitingForOpponentsPlacement && mReceivedOpponentsPlacement) {
			mActivePlayer = PLAYER_0;
			gameMode = GameMode.GAME_MODE_PLAY;

			// Draw the opponent's units, terrain
			
			int tileByteSize = Unit.BYTE_SIZE + Terrain.BYTE_SIZE;

			for (int i = 0; i < NUM_TILES; i++) {
				for (int j = 0; j < NUM_TILES / 2; j++) {
					int index = (j * (NUM_TILES) + i) * tileByteSize;
					Terrain t;
					Unit u;

					int boardI = i;
					int boardJ = j + STAGING_AREA_TILES / 2 + ((mPlayerNum + 1) % 2) * NUM_TILES / 2;

					if (mOpponentTileBytes[index + 0] != -2 && mOpponentTileBytes[index + 1] != -2 && mOpponentTileBytes[index + 2] != -2) {
						t = new Terrain(mOpponentTileBytes, index, boardI, boardJ, CyvasseGameFragment.this);
					}
					else {
						t = null;
					}
					if (mOpponentTileBytes[index + 3] != -2 && mOpponentTileBytes[index + 4] != -2 && mOpponentTileBytes[index + 5] != -2) {
						u = new Unit(mOpponentTileBytes, index + 3, CyvasseGameFragment.this, boardI, boardJ);
						numPieces[u.player]++;
					}
					else {
						u = null;
					}
					board[boardI][boardJ] = new Tile(u, t);
				}
			}

			//Clear the done text, curtains, staging tiles, dialog
			drawCurtains(3);

			detachChildRunnable(doneText);
			scene.unregisterTouchArea(doneText);
			doneText = null;

			// Just in case
			for (CyvasseSprite s : stagingTiles_p0) {
				((CyvasseGameFragment) mFragment).detachChildRunnable(s);
			}
			for (CyvasseSprite s : stagingTiles_p1) {
				((CyvasseGameFragment) mFragment).detachChildRunnable(s);
			}
			stagingTiles_p0.clear();
			stagingTiles_p1.clear();

			dismissDialog();

			// Draw moves text
			
			drawMovesText();
		}
	}

	public void onPeerLeft() {
		showDialog(PLAYER_LEFT_DIALOG);
	}
	*/
}