package com.gelakinetic.cyvasse.fragments;

import com.gelakinetic.cyvasse.R;
import com.robobunny.SeekBarPreference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;


public class PrefsFragment extends PreferenceFragment  implements OnSharedPreferenceChangeListener {

	private ListPreference		mListPreference[]	= new ListPreference[14];
	private String						keys[]						= new String[14];
	private SeekBarPreference	p1ColorTitle;
	private SeekBarPreference	p2ColorTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Load the preferences from an XML resource */
		addPreferencesFromResource(R.xml.preferences);
		
		PreferenceGroup pg = getPreferenceScreen();

		keys[0] = this.getString(R.string.num_rabble_key);
		mListPreference[0] = (ListPreference) pg.findPreference(keys[0]);
		keys[1] = this.getString(R.string.num_spearmen_key);
		mListPreference[1] = (ListPreference) pg.findPreference(keys[1]);
		keys[2] = this.getString(R.string.num_crossbowmen_key);
		mListPreference[2] = (ListPreference) pg.findPreference(keys[2]);
		keys[3] = this.getString(R.string.num_light_horse_key);
		mListPreference[3] = (ListPreference) pg.findPreference(keys[3]);
		keys[4] = this.getString(R.string.num_heavy_horse_key);
		mListPreference[4] = (ListPreference) pg.findPreference(keys[4]);
		keys[5] = this.getString(R.string.num_elephant_key);
		mListPreference[5] = (ListPreference) pg.findPreference(keys[5]);
		keys[6] = this.getString(R.string.num_catapult_key);
		mListPreference[6] = (ListPreference) pg.findPreference(keys[6]);
		keys[7] = this.getString(R.string.num_trebuchet_key);
		mListPreference[7] = (ListPreference) pg.findPreference(keys[7]);
		keys[8] = this.getString(R.string.num_dragon_key);
		mListPreference[8] = (ListPreference) pg.findPreference(keys[8]);
		keys[9] = this.getString(R.string.num_king_key);
		mListPreference[9] = (ListPreference) pg.findPreference(keys[9]);
		keys[10] = this.getString(R.string.num_mountain_key);
		mListPreference[10] = (ListPreference) pg.findPreference(keys[10]);
		keys[11] = this.getString(R.string.num_river_key);
		mListPreference[11] = (ListPreference) pg.findPreference(keys[11]);
		keys[12] = this.getString(R.string.board_size_key);
		mListPreference[12] = (ListPreference) pg.findPreference(keys[12]);
		keys[13] = this.getString(R.string.num_moves_key);
		mListPreference[13] = (ListPreference) pg.findPreference(keys[13]);

		p1ColorTitle = (SeekBarPreference) pg.findPreference(getString(R.string.p1_red_key));
		p2ColorTitle = (SeekBarPreference) pg.findPreference(getString(R.string.p2_red_key));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		// Setup the initial values

		for (int i = 0; i < keys.length; i++) {
			mListPreference[i].setSummary(mListPreference[i].getEntry());
		}

		setTitleColors(PreferenceManager.getDefaultSharedPreferences(this.getActivity()));

		// Set up a listener whenever a key changes
		sp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		for (int i = 0; i < keys.length; i++) {
			if (key.equals(keys[i])) {
				mListPreference[i].setSummary(mListPreference[i].getEntry());
			}
		}

		setTitleColors(sharedPreferences);
	}

	private void setTitleColors(SharedPreferences sharedPreferences) {
		p1ColorTitle.setTitleColor(Color.rgb(sharedPreferences.getInt(getString(R.string.p1_red_key), 0),
				sharedPreferences.getInt(getString(R.string.p1_green_key), 0), sharedPreferences.getInt(getString(R.string.p1_blue_key), 0)));
		p2ColorTitle.setTitleColor(Color.rgb(sharedPreferences.getInt(getString(R.string.p2_red_key), 0),
				sharedPreferences.getInt(getString(R.string.p2_green_key), 0), sharedPreferences.getInt(getString(R.string.p2_blue_key), 0)));
	}
}