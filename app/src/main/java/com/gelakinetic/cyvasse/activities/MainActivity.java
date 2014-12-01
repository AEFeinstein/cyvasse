//TODO wrap calls to games client methods with mSignedIn

//TODO exchange screen size info before launching fragment

package com.gelakinetic.cyvasse.activities;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gelakinetic.cyvasse.R;
import com.gelakinetic.cyvasse.fragments.CyvasseGameFragment;
import com.gelakinetic.cyvasse.fragments.MainFragment;
/* TODO multi
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantUtils;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig.Builder;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BasePlayGameActivity;
*/

/* TODO multi
public class MainActivity extends BasePlayGameActivity implements RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener,
OnInvitationReceivedListener {*/
public class MainActivity extends FragmentActivity {


	/* TODO multi
	// State Enum
	public enum OnlineState {
		NOT_ONLINE, ONLINE_SETUP, ONLINE_PLAYING
	}

	OnlineState									mOnlineState;

	private final static int		RC_SELECT_PLAYERS				= 10000;
	private final static int		RC_INVITATION_INBOX			= 10001;
	private final static int		RC_WAITING_ROOM					= 10002;
	public static final String	STAGING_AREA_TILES_KEY	= "STAGING_AREA_TILES";
	public static final String	PLAYER_NUM_KEY					= "PLAYER_NUM";

	// achievements and scores we're pending to push to the cloud
	// (waiting for the user to sign in, for instance)
	AccomplishmentsOutbox				mOutbox									= new AccomplishmentsOutbox();

	private Room								mRoom;
	private int									mTheirStagingAreaTiles	= -1;
	private int									mStagingAreaTiles				= -1;
	*/
	

	public static final String	MAIN_FRAGMENT_TAG	= "main";
	public static final String	CYVASSE_FRAGMENT_TAG	= "cyvasse";
	public static final String	PREFS_FRAGMENT_TAG	= "prefs";
	public static final String	NOTES_FRAGMENT_TAG	= "notes";

	/****************
	 * LIFECYCLE
	 ****************/
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			ft.add(R.id.fragment_container, new MainFragment(), MainActivity.MAIN_FRAGMENT_TAG);
			ft.commit();
		}
		/* TODO multi
		enableDebugLog(true, "game");

		mOnlineState = OnlineState.NOT_ONLINE;
		this.setSignInMessages("Signing in", "Signing out");
		*/
	}
	/* TODO multi
	@Override
	public void onPause() {
		super.onPause();
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (f instanceof CyvasseGameFragment) {
			if(((CyvasseGameFragment)f).mIsOnline) {
				getSupportFragmentManager().popBackStack();
				((CyvasseGameFragment)f).dismissDialog();
			}
		}
		if(mOnlineState != OnlineState.NOT_ONLINE) {
			// If we're online and quitting, pop the game fragment
			endOnlineGame();
		}
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		if (request == RC_SELECT_PLAYERS) {
			if (response != Activity.RESULT_OK) {
				// user canceled
				return;
			}

			// get the invitee list
			final ArrayList<String> invitees = data.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);

			// get automatch criteria
			Bundle autoMatchCriteria = null;
			int minAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
			int maxAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

			if (minAutoMatchPlayers > 0) {
				autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			}
			else {
				autoMatchCriteria = null;
			}

			// create the room and specify a variant if appropriate
			RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
			roomConfigBuilder.addPlayersToInvite(invitees);
			if (autoMatchCriteria != null) {
				roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
			}
			RoomConfig roomConfig = roomConfigBuilder.build();
			getGamesClient().createRoom(roomConfig);

			// prevent screen from sleeping during handshake
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		else if (request == RC_WAITING_ROOM) {
			if (response == Activity.RESULT_OK) {
				// startTheGame();
			}
			else if (response == Activity.RESULT_CANCELED) {
				// Waiting room was dismissed with the back button. The meaning of this
				// action is up to the game. You may choose to leave the room and cancel
				// the
				// match, or do something else like minimize the waiting room and
				// continue to connect in the background.

				// in this example, we take the simple approach and just leave the room:
				endOnlineGame();
			}
			else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
				// player wants to leave the room.
				endOnlineGame();
			}
		}
		else if (request == RC_INVITATION_INBOX) {
			if (response != Activity.RESULT_OK) {
				// canceled
				return;
			}

			// get the selected invitation
			Bundle extras = data.getExtras();
			Invitation invitation = extras.getParcelable(GamesClient.EXTRA_INVITATION);

			// accept it!
			RoomConfig roomConfig = makeBasicRoomConfigBuilder().setInvitationIdToAccept(invitation.getInvitationId()).build();
			getGamesClient().joinRoom(roomConfig);

			// prevent screen from sleeping during handshake
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// startTheGame();
		}
	}
*/
	
	/*********************************
	 * GameHelper.GameHelperListener *
	 *********************************/
	/* TODO multi
	public void onSignInFailed() {
		// Sign in has failed. So show the user the sign-in button.

		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (f instanceof MainFragment) {
			((MainFragment) f).setLoggedIn(false);
		}
	}

	public void onSignInSucceeded() {
		// show sign-out button, hide the sign-in button
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (f instanceof MainFragment) {
			((MainFragment) f).setLoggedIn(true);
		}

		// if we have accomplishments to push, push them
		if (!mOutbox.isEmpty()) {
			pushAccomplishments();
			Toast.makeText(this, "your_progress_will_be_uploaded", Toast.LENGTH_LONG).show();
		}

		// Check for rooms or something (coming from a notification)
		if (getInvitationId() != null) {
			Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
			roomConfigBuilder.setInvitationIdToAccept(getInvitationId());
			getGamesClient().joinRoom(roomConfigBuilder.build());
		}

		getGamesClient().registerInvitationListener(this);
	}
*/
	/**********************
	 * RoomUpdateListener *
	 **********************/
	/* TODO multi
	public void onRoomCreated(int statusCode, Room room) {
		mRoom = room;
		if (statusCode != GamesClient.STATUS_OK) {
			// let screen go to sleep
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// show error message, return to main screen.
			Toast.makeText(this, "Error: " + statusCode, Toast.LENGTH_LONG).show();
			if (statusCode == GamesClient.STATUS_CLIENT_RECONNECT_REQUIRED) {
				getGamesClient().reconnect();
			}
			return;
		}
		// get waiting room intent
		Intent i = getGamesClient().getRealTimeWaitingRoomIntent(room, 2);
		startActivityForResult(i, RC_WAITING_ROOM);
	}

	public void onLeftRoom(int arg0, String arg1) {
		// TODO don't join another room until this is called!!!
		mOnlineState = OnlineState.NOT_ONLINE;
	}

	public void onJoinedRoom(int statusCode, Room room) {
		mRoom = room;
		if (statusCode != GamesClient.STATUS_OK) {
			// let screen go to sleep
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// show error message, return to main screen.
			Toast.makeText(this, "Error: " + statusCode, Toast.LENGTH_LONG).show();

			if (statusCode == GamesClient.STATUS_CLIENT_RECONNECT_REQUIRED) {
				getGamesClient().reconnect();
			}
			return;
		}

		// get waiting room intent
		Intent i = getGamesClient().getRealTimeWaitingRoomIntent(room, 2);
		startActivityForResult(i, RC_WAITING_ROOM);
	}

	public void onRoomConnected(int statusCode, Room room) {
		mRoom = room;
		if (statusCode != GamesClient.STATUS_OK) {
			// let screen go to sleep
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// show error message, return to main screen.
			Toast.makeText(this, "Error: " + statusCode, Toast.LENGTH_LONG).show();
			if (statusCode == GamesClient.STATUS_CLIENT_RECONNECT_REQUIRED) {
				getGamesClient().reconnect();
			}
		}
		startTheGame();
	}
*/
	/****************************
	 * RoomStatusUpdateListener *
	 ****************************/
	/* TODO multi
	public void onConnectedToRoom(Room arg0) {
		// TODO Auto-generated method stub
		//mConnected  = true;
	}

	public void onDisconnectedFromRoom(Room room) {
		mRoom = room;
		// leave the room
		getGamesClient().leaveRoom(this, mRoom.getRoomId());

		// clear the flag that keeps the screen on
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void onPeerDeclined(Room room, List<String> arg1) {
		// peer declined invitation -- see if game should be cancelled
		if (mOnlineState != OnlineState.ONLINE_PLAYING && shouldCancelGame(room)) {
			endOnlineGame();
		}
	}

	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	public void onPeerJoined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

	public void onPeerLeft(Room room, List<String> arg1) {
		// peer left -- see if game should be cancelled
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (f instanceof CyvasseGameFragment) {
			((CyvasseGameFragment) f).onPeerLeft();
		}
	}

	// returns whether there are enough players to start the game
	boolean shouldStartGame(Room room) {
		int connectedPlayers = 0;
		for (Participant p : room.getParticipants()) {
			if (p.isConnectedToRoom())
				++connectedPlayers;
		}
		return connectedPlayers == 2;
	}

	public void onPeersConnected(Room room, List<String> arg1) {
		mRoom = room;
		if (mOnlineState == OnlineState.ONLINE_PLAYING) {
			// add new player to an ongoing game
		}
		else if (shouldStartGame(room)) {

			// TODO Start the game, but adding a fragment ANRs
		}
	}

	public void onPeersDisconnected(Room room, List<String> arg1) {
		if (mOnlineState == OnlineState.ONLINE_PLAYING) {
			// do game-specific handling of this -- remove player's avatar
			// from the screen, etc. If not enough players are left for
			// the game to go on, end the game and leave the room.
		}
		else if (shouldCancelGame(room)) {
			// cancel the game
			endOnlineGame();
		}

	}

	public void onRoomAutoMatching(Room arg0) {
		// TODO Auto-generated method stub

	}

	public void onRoomConnecting(Room arg0) {
		// TODO Auto-generated method stub

	}
*/
	/***********************************
	 * RealTimeMessageReceivedListener *
	 ***********************************/
	/* TODO multi
	public void onRealTimeMessageReceived(RealTimeMessage arg0) {
		// Pass the message into the game fragment
		switch (mOnlineState) {
			case NOT_ONLINE:
				break;
			case ONLINE_PLAYING:
				Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
				if (f instanceof CyvasseGameFragment) {
					((CyvasseGameFragment) f).receiveRealTimeMessage(arg0);
				}
				break;
			case ONLINE_SETUP:
				String[] msg = new String(arg0.getMessageData()).split(":");
				if (msg[0].equals(STAGING_AREA_TILES_KEY)) {
					mTheirStagingAreaTiles = Integer.parseInt(msg[1]);
					checkStagingAreaTiles();
				}
				break;
		}
	}

	private void checkStagingAreaTiles() {
		if (mStagingAreaTiles != -1 && mTheirStagingAreaTiles != -1) {
			mOnlineState = OnlineState.ONLINE_PLAYING;

			int stagingAreaTiles;
			if (mStagingAreaTiles < mTheirStagingAreaTiles) {
				stagingAreaTiles = mStagingAreaTiles;
			}
			else {
				stagingAreaTiles = mTheirStagingAreaTiles;
			}

			Bundle args = new Bundle();
			args.putBoolean("isOnline", true);
			args.putInt(STAGING_AREA_TILES_KEY, stagingAreaTiles);

			String mParticipantId = ParticipantUtils.getParticipantId(mRoom.getParticipants(), getGamesClient().getCurrentPlayerId());
			String mTheirParticipantId = null;
			for (Participant p : mRoom.getParticipants()) {
				if (!p.getParticipantId().equals(mParticipantId)) {
					mTheirParticipantId = p.getParticipantId();
				}
			}
			if (mTheirParticipantId == null) {
				return;
			}
			if (mParticipantId.compareTo(mTheirParticipantId) > 0) {
				args.putInt(PLAYER_NUM_KEY, 0);
			}
			else {
				args.putInt(PLAYER_NUM_KEY, 1);
			}

			CyvasseGameFragment cvf = new CyvasseGameFragment();
			cvf.setArguments(args);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_container, cvf, "CyvasseGameFragment");
			ft.addToBackStack(null);
			ft.commitAllowingStateLoss();
		}
	}
*/
	/********************************
	 * OnInvitationReceivedListener *
	 ********************************/
	/* TODO multi
	public void onInvitationReceived(Invitation arg0) {
		// TODO Prompt a user with a cancelable dialog
		showInvitationInbox();
	}
*/
	/*******************
	 * Fragment access *
	 *******************/
	/* TODO multi
	public void beginUserInitiatedSignInFromFragment() {
		this.beginUserInitiatedSignIn();
	}

	public void signOutFromFragment() {
		this.signOut();
	}

	public void showInvitationInbox() {
		// launch the intent to show the invitation inbox screen
		Intent intent = getGamesClient().getInvitationInboxIntent();
		startActivityForResult(intent, RC_INVITATION_INBOX);
	}

	public void startQuickGame() {
		// automatch criteria to invite 1 random automatch opponent.
		// You can also specify more opponents (up to 3).
		Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

		// build the room config:
		RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
		roomConfigBuilder.setAutoMatchCriteria(am);
		RoomConfig roomConfig = roomConfigBuilder.build();

		// create room:
		getGamesClient().createRoom(roomConfig);

		// prevent screen from sleeping during handshake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void invitePlayers() {
		// launch the player selection screen
		// minimum: 1 other player; maximum: 1 other players
		Intent intent = getGamesClient().getSelectPlayersIntent(1, 1);
		startActivityForResult(intent, RC_SELECT_PLAYERS);
	}

	public void sendRealTimeMessage(byte[] msg) {
		String mParticipantId = ParticipantUtils.getParticipantId(mRoom.getParticipants(), getGamesClient().getCurrentPlayerId());
		for (Participant p : mRoom.getParticipants()) {
			if (!p.getParticipantId().equals(mParticipantId)) {
				getGamesClient().sendReliableRealTimeMessage(null, msg, mRoom.getRoomId(), p.getParticipantId());
			}
		}
	}
*/
	/*********
	 * OTHER *
	 *********/
	/* TODO multi
	// Returns whether the room is in a state where the game should be cancelled.
	boolean shouldCancelGame(Room room) {

		// TODO: Your game-specific cancellation logic here. For example, you might
		// decide to
		// cancel the game if enough people have declined the invitation or left the
		// room.
		// You can check a participant's status with Participant.getStatus().
		// (Also, your UI should have a Cancel button that cancels the game too)
		return true;
	}

	private void startTheGame() {
		if (mOnlineState != OnlineState.ONLINE_PLAYING) {
			mOnlineState = OnlineState.ONLINE_SETUP;
			// prevent screen from sleeping during handshake
			// TODO clear this after the game
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int cameraHeight = displaymetrics.heightPixels;
			int cameraWidth = displaymetrics.widthPixels;
			int numTiles = 10; // standard for online

			mStagingAreaTiles = (int) Math.floor((cameraHeight - cameraWidth) / (cameraWidth / numTiles));
			if (mStagingAreaTiles % 2 == 1) {
				mStagingAreaTiles--;
			}

			checkStagingAreaTiles();

			// Negotiate the playing field
			String msg = STAGING_AREA_TILES_KEY + ":" + mStagingAreaTiles;
			sendRealTimeMessage(msg.getBytes());
		}
	}
*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				// Send the search key to the leftmost fragment
				Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
				if (f instanceof CyvasseGameFragment) {
					return !((CyvasseGameFragment) f).onInterceptBackKey() || super.onKeyDown(keyCode, event);
				}
			default:
				return super.onKeyDown(keyCode, event);
		}
	}
	/* TODO multi
	private RoomConfig.Builder makeBasicRoomConfigBuilder() {
		return RoomConfig.builder(this).setMessageReceivedListener(this).setRoomStatusUpdateListener(this);
	}

	void pushAccomplishments() {
		if (!isSignedIn()) {
			// can't push to the cloud, so save locally
			mOutbox.saveLocal(this);
			return;
		}
		if (mOutbox.mPrimeAchievement) {
			getGamesClient().unlockAchievement(getString(R.string.achievement_prime));
			mOutbox.mPrimeAchievement = false;
		}
		if (mOutbox.mArrogantAchievement) {
			getGamesClient().unlockAchievement(getString(R.string.achievement_arrogant));
			mOutbox.mArrogantAchievement = false;
		}
		if (mOutbox.mHumbleAchievement) {
			getGamesClient().unlockAchievement(getString(R.string.achievement_humble));
			mOutbox.mHumbleAchievement = false;
		}
		if (mOutbox.mLeetAchievement) {
			getGamesClient().unlockAchievement(getString(R.string.achievement_leet));
			mOutbox.mLeetAchievement = false;
		}
		if (mOutbox.mBoredSteps > 0) {
			getGamesClient().incrementAchievement(getString(R.string.achievement_really_bored), mOutbox.mBoredSteps);
			getGamesClient().incrementAchievement(getString(R.string.achievement_bored), mOutbox.mBoredSteps);
		}
		if (mOutbox.mEasyModeScore >= 0) {
			getGamesClient().submitScore(getString(R.string.leaderboard_easy), mOutbox.mEasyModeScore);
			mOutbox.mEasyModeScore = -1;
		}
		if (mOutbox.mHardModeScore >= 0) {
			getGamesClient().submitScore(getString(R.string.leaderboard_hard), mOutbox.mHardModeScore);
			mOutbox.mHardModeScore = -1;
		}
		mOutbox.saveLocal(this);
	}
	
	public void endOnlineGame(){
		mOnlineState = OnlineState.NOT_ONLINE;
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		try{
			getGamesClient().leaveRoom(this, mRoom.getRoomId());					
		}
		catch(Exception e) {
			// probably wern't connected. there should be an easier way of figuring if one should leave the room
		}
	}
	*/
	
	/*
	 * Dialog Management
	 */

	public static void setUpDialog(Dialog d, Typeface tf, String title, String message, String positive, String negative, String neutral, boolean showProgress) {
		if(title != null) {
			((TextView) d.findViewById(R.id.dialog_title)).setText(title);
			((TextView) d.findViewById(R.id.dialog_title)).setTypeface(tf);
		}
		else {
			d.findViewById(R.id.dialog_title).setVisibility(View.GONE);
		}
		
		if(message != null) {
			// Format the text and linkify it
			((TextView) d.findViewById(R.id.dialog_message)).setText(Html.fromHtml(message));
			((TextView) d.findViewById(R.id.dialog_message)).setMovementMethod(LinkMovementMethod.getInstance());
			((TextView) d.findViewById(R.id.dialog_message)).setTypeface(tf);
		}
		else {
			d.findViewById(R.id.dialog_message).setVisibility(View.GONE);
		}
		
		if(positive != null) {
			((TextView) d.findViewById(R.id.dialog_positive)).setText(positive);
			((TextView) d.findViewById(R.id.dialog_positive)).setTypeface(tf);
		}
		else {
			d.findViewById(R.id.dialog_positive).setVisibility(View.GONE);
		}
		
		if(negative != null) {
			((TextView) d.findViewById(R.id.dialog_negative)).setText(negative);
			((TextView) d.findViewById(R.id.dialog_negative)).setTypeface(tf);
		}
		else {
			d.findViewById(R.id.dialog_negative).setVisibility(View.GONE);
		}
		
		if(neutral != null) {
			((TextView) d.findViewById(R.id.dialog_neutral)).setText(neutral);
			((TextView) d.findViewById(R.id.dialog_neutral)).setTypeface(tf);
		}
		else {
			d.findViewById(R.id.dialog_neutral).setVisibility(View.GONE);
		}
		
		if(!showProgress) {
			d.findViewById(R.id.dialog_progress_spinner).setVisibility(View.GONE);
		}
		else {
			d.findViewById(R.id.dialog_message_scrollview).setVisibility(View.GONE);
			d.findViewById(R.id.dialog_buttons_layout).setVisibility(View.GONE);
			
		}
	}
}
