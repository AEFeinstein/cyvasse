package com.gelakinetic.cyvasse.fragments;

import java.io.IOException;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gelakinetic.cyvasse.R;
import com.gelakinetic.cyvasse.gameHelpers.Unit.UnitType;

public class NotesFragment extends Fragment {

	private Typeface	mCustomFont;

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
	    super.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_notes, root, false);

		mCustomFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MagicMedieval.ttf");
		TextView rulesText = ((TextView) view.findViewById(R.id.rules_text));
		rulesText.setText(Html.fromHtml(getString(R.string.all_the_rules)));
		rulesText.setTypeface(mCustomFont);
		((TextView) view.findViewById(R.id.rules_header)).setTypeface(mCustomFont);
		((TextView) view.findViewById(R.id.unit_stats_header)).setTypeface(mCustomFont);

		try {
			if (view.findViewById(R.id.left_col) != null) {
				LinearLayout lc = (LinearLayout) view.findViewById(R.id.left_col);
				LinearLayout rc = (LinearLayout) view.findViewById(R.id.right_col);
				lc.addView(createNotes(inflater, UnitType.RABBLE));
				rc.addView(createNotes(inflater, UnitType.SPEARMEN));
				lc.addView(createNotes(inflater, UnitType.CROSSBOWMEN));
				rc.addView(createNotes(inflater, UnitType.LIGHT_HORSE));
				lc.addView(createNotes(inflater, UnitType.HEAVY_HORSE));
				rc.addView(createNotes(inflater, UnitType.ELEPHANT));
				lc.addView(createNotes(inflater, UnitType.CATAPULT));
				rc.addView(createNotes(inflater, UnitType.TREBUCHET));
				lc.addView(createNotes(inflater, UnitType.DRAGON));
				rc.addView(createNotes(inflater, UnitType.KING));
			}
			else {
				LinearLayout ll = (LinearLayout) view.findViewById(R.id.units_col);
				ll.addView(createNotes(inflater, UnitType.RABBLE));
				ll.addView(createNotes(inflater, UnitType.SPEARMEN));
				ll.addView(createNotes(inflater, UnitType.CROSSBOWMEN));
				ll.addView(createNotes(inflater, UnitType.LIGHT_HORSE));
				ll.addView(createNotes(inflater, UnitType.HEAVY_HORSE));
				ll.addView(createNotes(inflater, UnitType.ELEPHANT));
				ll.addView(createNotes(inflater, UnitType.CATAPULT));
				ll.addView(createNotes(inflater, UnitType.TREBUCHET));
				ll.addView(createNotes(inflater, UnitType.DRAGON));
				ll.addView(createNotes(inflater, UnitType.KING));
			}
		}
		catch (IOException e) {
			Toast.makeText(this.getActivity(), e.toString(), Toast.LENGTH_LONG).show();
			getFragmentManager().popBackStack();
		}

		return view;
	}

	private View createNotes(LayoutInflater inflater, UnitType ut) throws IOException {
		View child = inflater.inflate(R.layout.unit_stats_row, null);

		TextView name = (TextView) child.findViewById(R.id.name);
		TextView strength = (TextView) child.findViewById(R.id.strength);
		TextView movement = (TextView) child.findViewById(R.id.movement);
		TextView penaltyMountain = (TextView) child.findViewById(R.id.penalty_mountain);
		TextView penaltyRiver = (TextView) child.findViewById(R.id.penalty_river);
		TextView minAttack = (TextView) child.findViewById(R.id.min_attack);
		TextView maxAttack = (TextView) child.findViewById(R.id.max_attack);

		((TextView) child.findViewById(R.id.strength_title)).setTypeface(mCustomFont);
		((TextView) child.findViewById(R.id.movement_title)).setTypeface(mCustomFont);
		((TextView) child.findViewById(R.id.penalty_mountain_title)).setTypeface(mCustomFont);
		((TextView) child.findViewById(R.id.penalty_river_title)).setTypeface(mCustomFont);
		((TextView) child.findViewById(R.id.min_attack_title)).setTypeface(mCustomFont);
		((TextView) child.findViewById(R.id.max_attack_title)).setTypeface(mCustomFont);

		Drawable d = null;

		name.setTypeface(mCustomFont);
		strength.setTypeface(mCustomFont);
		movement.setTypeface(mCustomFont);
		penaltyMountain.setTypeface(mCustomFont);
		penaltyRiver.setTypeface(mCustomFont);
		minAttack.setTypeface(mCustomFont);
		maxAttack.setTypeface(mCustomFont);

		switch (ut) {
			case RABBLE:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/rabble.png"), null);
				name.setText(getString(R.string.rabble_name));
				strength.setText(getString(R.string.rabble_strength));
				movement.setText(getString(R.string.rabble_movement));
				penaltyMountain.setText(getString(R.string.rabble_penalty_mountain));
				penaltyRiver.setText(getString(R.string.rabble_penalty_river));
				minAttack.setText(getString(R.string.rabble_min_attack_range));
				maxAttack.setText(getString(R.string.rabble_max_attack_range));
				break;
			case SPEARMEN:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/spearmen.png"), null);
				name.setText(getString(R.string.spearmen_name));
				strength.setText(getString(R.string.spearmen_strength));
				movement.setText(getString(R.string.spearmen_movement));
				penaltyMountain.setText(getString(R.string.spearmen_penalty_mountain));
				penaltyRiver.setText(getString(R.string.spearmen_penalty_river));
				minAttack.setText(getString(R.string.spearmen_min_attack_range));
				maxAttack.setText(getString(R.string.spearmen_max_attack_range));
				break;
			case CROSSBOWMEN:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/crossbowmen.png"), null);
				name.setText(getString(R.string.crossbowmen_name));
				strength.setText(getString(R.string.crossbowmen_strength));
				movement.setText(getString(R.string.crossbowmen_movement));
				penaltyMountain.setText(getString(R.string.crossbowmen_penalty_mountain));
				penaltyRiver.setText(getString(R.string.crossbowmen_penalty_river));
				minAttack.setText(getString(R.string.crossbowmen_min_attack_range));
				maxAttack.setText(getString(R.string.crossbowmen_max_attack_range));
				break;
			case LIGHT_HORSE:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/light_horse.png"), null);
				name.setText(getString(R.string.light_horse_name));
				strength.setText(getString(R.string.light_horse_strength));
				movement.setText(getString(R.string.light_horse_movement));
				penaltyMountain.setText(getString(R.string.light_horse_penalty_mountain));
				penaltyRiver.setText(getString(R.string.light_horse_penalty_river));
				minAttack.setText(getString(R.string.light_horse_min_attack_range));
				maxAttack.setText(getString(R.string.light_horse_max_attack_range));
				break;
			case HEAVY_HORSE:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/heavy_horse.png"), null);
				name.setText(getString(R.string.heavy_horse_name));
				strength.setText(getString(R.string.heavy_horse_strength));
				movement.setText(getString(R.string.heavy_horse_movement));
				penaltyMountain.setText(getString(R.string.heavy_horse_penalty_mountain));
				penaltyRiver.setText(getString(R.string.heavy_horse_penalty_river));
				minAttack.setText(getString(R.string.heavy_horse_min_attack_range));
				maxAttack.setText(getString(R.string.heavy_horse_max_attack_range));
				break;
			case ELEPHANT:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/elephant.png"), null);
				name.setText(getString(R.string.elephant_name));
				strength.setText(getString(R.string.elephant_strength));
				movement.setText(getString(R.string.elephant_movement));
				penaltyMountain.setText(getString(R.string.elephant_penalty_mountain));
				penaltyRiver.setText(getString(R.string.elephant_penalty_river));
				minAttack.setText(getString(R.string.elephant_min_attack_range));
				maxAttack.setText(getString(R.string.elephant_max_attack_range));
				break;
			case CATAPULT:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/catapult.png"), null);
				name.setText(getString(R.string.catapult_name));
				strength.setText(getString(R.string.catapult_strength));
				movement.setText(getString(R.string.catapult_movement));
				penaltyMountain.setText(getString(R.string.catapult_penalty_mountain));
				penaltyRiver.setText(getString(R.string.catapult_penalty_river));
				minAttack.setText(getString(R.string.catapult_min_attack_range));
				maxAttack.setText(getString(R.string.catapult_max_attack_range));
				break;
			case TREBUCHET:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/trebuchet.png"), null);
				name.setText(getString(R.string.trebuchet_name));
				strength.setText(getString(R.string.trebuchet_strength));
				movement.setText(getString(R.string.trebuchet_movement));
				penaltyMountain.setText(getString(R.string.trebuchet_penalty_mountain));
				penaltyRiver.setText(getString(R.string.trebuchet_penalty_river));
				minAttack.setText(getString(R.string.trebuchet_min_attack_range));
				maxAttack.setText(getString(R.string.trebuchet_max_attack_range));
				break;
			case DRAGON:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/dragon.png"), null);
				name.setText(getString(R.string.dragon_name));
				strength.setText(getString(R.string.dragon_strength));
				movement.setText(getString(R.string.dragon_movement));
				penaltyMountain.setText(getString(R.string.dragon_penalty_mountain));
				penaltyRiver.setText(getString(R.string.dragon_penalty_river));
				minAttack.setText(getString(R.string.dragon_min_attack_range));
				maxAttack.setText(getString(R.string.dragon_max_attack_range));
				break;
			case KING:
				d = Drawable.createFromStream(getActivity().getAssets().open("gfx/units/king.png"), null);
				name.setText(getString(R.string.king_name));
				strength.setText(getString(R.string.king_strength));
				movement.setText(getString(R.string.king_movement));
				penaltyMountain.setText(getString(R.string.king_penalty_mountain));
				penaltyRiver.setText(getString(R.string.king_penalty_river));
				minAttack.setText(getString(R.string.king_min_attack_range));
				maxAttack.setText(getString(R.string.king_max_attack_range));
				break;
		}

		// Holo light blue
		d.setColorFilter(Color.rgb(0, 136, 191), PorterDuff.Mode.MULTIPLY);
		name.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);

		return child;
	}
}
