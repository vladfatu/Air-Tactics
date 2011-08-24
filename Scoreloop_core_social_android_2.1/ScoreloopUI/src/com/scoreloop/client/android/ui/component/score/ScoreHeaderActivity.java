/*
 * In derogation of the Scoreloop SDK - License Agreement concluded between
 * Licensor and Licensee, as defined therein, the following conditions shall
 * apply for the source code contained below, whereas apart from that the
 * Scoreloop SDK - License Agreement shall remain unaffected.
 * 
 * Copyright: Scoreloop AG, Germany (Licensor)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.scoreloop.client.android.ui.component.score;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.scoreloop.client.android.ui.R;
import com.scoreloop.client.android.ui.component.base.ComponentHeaderActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class ScoreHeaderActivity extends ComponentHeaderActivity implements OnClickListener {

	private static final int	DIALOG_GAME_MODE	= 1000;

	public void onClick(final DialogInterface dialog, final int position) {
		final int mode = getModeForPosition(position);
		getScreenValues().putValue(Constant.MODE, mode);
	}

	@Override
	public void onClick(final View view) {
		showDialogSafe(DIALOG_GAME_MODE);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.sl_header_default);

		setCaption(getGame().getName());
		getImageView().setImageDrawable(getResources().getDrawable(R.drawable.sl_header_icon_leaderboards));
		setTitle(getString(R.string.sl_leaderboards));
		
		if (getGame().hasModes()) {
			showControlIcon(R.drawable.sl_button_more);
			updateUI();
			addObservedKeys(Constant.MODE);
		}
	}

	private void showControlIcon(int resId) {
		ImageView icon = (ImageView)findViewById(R.id.sl_control_icon);
		icon.setImageResource(resId);
		icon.setEnabled(true);
		icon.setOnClickListener(this);
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		final Dialog dialog = super.onCreateDialog(id);
		if (dialog != null) {
			return dialog;
		}

		switch (id) {
		case DIALOG_GAME_MODE:
			return new AlertDialog.Builder(this).setItems(getConfiguration().getModesResId(), this).create();
		default:
			return null;
		}
	}

	private void updateUI() {
		setSubTitle(getModeString(getScreenValues().<Integer> getValue(Constant.MODE)));
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (key.equals(Constant.MODE) && ((newValue != null) && !newValue.equals(oldValue))) {
			updateUI();
		}
	}
}
