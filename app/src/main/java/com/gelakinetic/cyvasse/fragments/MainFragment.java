package com.gelakinetic.cyvasse.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gelakinetic.cyvasse.R;
import com.gelakinetic.cyvasse.activities.MainActivity;

import java.io.File;
/* TODO multi
 * import com.google.android.gms.common.SignInButton;
 */

public class MainFragment extends Fragment {
	Context mCtx;
	private TextView load_game_btn;
	private String DIALOG_TAG;
	private static final int ABOUT_DIALOG = 1;
	Typeface tf;

	/* TODO multi
	private SignInButton	signInButton;
	private LinearLayout	multiplayerButtons;
	private boolean	mIsLoggedIn = false;
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mainView = inflater.inflate(R.layout.fragment_main, container, false);
		mCtx = this.getActivity();
		/* TODO multi
		signInButton = (SignInButton)mainView.findViewById(R.id.sign_in_button);
		multiplayerButtons = (LinearLayout)mainView.findViewById(R.id.multiplayer_buttons);
		
		((SignInButton) mainView.findViewById(R.id.sign_in_button)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMainActivity().beginUserInitiatedSignInFromFragment();
			}
		});
		((Button) mainView.findViewById(R.id.sign_out_button)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMainActivity().signOutFromFragment();

				// show sign-in button, hide the sign-out button
				setLoggedIn(false);
			}
		});

		((Button) mainView.findViewById(R.id.invite_players)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMainActivity().invitePlayers();
			}
		});

		((Button) mainView.findViewById(R.id.show_invitations)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMainActivity().showInvitationInbox();
			}
		});

		((Button) mainView.findViewById(R.id.quick_game)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getMainActivity().startQuickGame();
			}
		});
*/

		tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MagicMedieval.ttf");
		((TextView) mainView.findViewById(R.id.title)).setTypeface(tf);

		/*
		 * Launch the game
		 */
		TextView new_game_btn = (TextView) mainView.findViewById(R.id.new_game_btn);
		new_game_btn.setTypeface(tf);
		new_game_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				File saveFile = new File(mCtx.getFilesDir(), "game.save");
				saveFile.delete();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, new CyvasseGameFragment(), MainActivity.CYVASSE_FRAGMENT_TAG);
				ft.addToBackStack(null);
				ft.commit();
			}
		});

		load_game_btn = (TextView) mainView.findViewById(R.id.load_game_btn);
		load_game_btn.setTypeface(tf);
		load_game_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, new CyvasseGameFragment(), MainActivity.CYVASSE_FRAGMENT_TAG);
				ft.addToBackStack(null);
				ft.commit();
			}
		});

		/*
		 * Access the settings
		 */
		TextView settings_btn = (TextView) mainView.findViewById(R.id.settings_btn);
		settings_btn.setTypeface(tf);
		settings_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, new PrefsFragment(), MainActivity.PREFS_FRAGMENT_TAG);
				ft.addToBackStack(null);
				ft.commit();
//				Intent myIntent = new Intent(getActivity(), CyvassePreferences.class);
//				getActivity().startActivity(myIntent);
			}
		});

		/*
		 * Read some notes
		 */
		TextView notes_btn = (TextView) mainView.findViewById(R.id.notes_btn);
		notes_btn.setTypeface(tf);
		notes_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, new NotesFragment(), MainActivity.NOTES_FRAGMENT_TAG);
				ft.addToBackStack(null);
				ft.commit();
			}
		});

		/*
		 * About this app
		 */
		TextView about_btn = (TextView) mainView.findViewById(R.id.about_btn);
		about_btn.setTypeface(tf);
		about_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(ABOUT_DIALOG);
			}
		});
		/* TODO multi
		setLoggedIn(mIsLoggedIn);
		*/
		return mainView;
	}

	/* TODO multi
	private MainActivity getMainActivity() {
		return (MainActivity) this.getActivity();
	}
*/
	@Override
	public void onResume() {
		super.onResume();

		File saveFile = new File(getActivity().getFilesDir(), "game.save");
		if (saveFile.exists()) {
			load_game_btn.setVisibility(View.VISIBLE);
		}
		else {
			load_game_btn.setVisibility(View.GONE);
		}
	}

	public void showDialog(final int id) {
		// CyvasseDialogFragment.show() will take care of adding the fragment
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
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setRetainInstance(true);
			}

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
					case ABOUT_DIALOG:
						final Dialog dialog = new Dialog(mCtx, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.dialog_styled);

						// Format the title and add the version string
						String dialogTitle;
						try {
							PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
							dialogTitle = mCtx.getString(R.string.about_title) + " " + getString(R.string.app_name) + " " + pInfo.versionName;
						} catch (NameNotFoundException e) {
							dialogTitle = mCtx.getString(R.string.about_title) + " " + getString(R.string.app_name);
						}

						MainActivity.setUpDialog(dialog, tf, dialogTitle, getString(R.string.about_body), null, null, getString(R.string.about_enjoy), false);

						// Format the button and add the onClickListener
						Button dialogButton = (Button) dialog.findViewById(R.id.dialog_neutral);
						dialogButton.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						return dialog;
					default:
						return null;
				}
			}

			@Override
			public void onDismiss(DialogInterface dialog) {
				super.onDismiss(dialog);
			}

		};
		newFragment.show(ft, DIALOG_TAG);
	}
	/* TODO multi
	public void setLoggedIn(boolean loggedIn) {
		mIsLoggedIn = loggedIn;
		if(loggedIn) {
			multiplayerButtons.setVisibility(View.VISIBLE);
			signInButton.setVisibility(View.GONE);
		}
		else {
			multiplayerButtons.setVisibility(View.GONE);
			signInButton.setVisibility(View.VISIBLE);
		}
	}
	*/
}
