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

package com.scoreloop.client.android.ui.component.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerException;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.model.User;
import com.scoreloop.client.android.ui.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.PackageManager;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.framework.BaseDialog;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class ProfileSettingsListActivity extends ComponentListActivity<BaseListItem> implements RequestControllerObserver, DialogInterface.OnDismissListener {

	class UserProfileListAdapter extends BaseListAdapter<BaseListItem> {

		public UserProfileListAdapter(final Context context) {
			super(context);
			// screen contains static main list items
			add(new CaptionListItem(context, null, getString(R.string.sl_manage_account)));
			add(_changePictureItem);
			add(_changeUsernameItem);
			if (getSessionUser().getEmailAddress() != null) {
				add(_changeEmailItem);
			}
		}
	}

	private ProfileListItem	_changeEmailItem;
	private ProfileListItem	_changePictureItem;
	private ProfileListItem	_changeUsernameItem;
	private UserController	_userController;
	private String			_restoreEmail;
	private String			_errorTitle;
	private String			_errorMessage;

	private User getUpdateUser() {
		_restoreEmail = getSessionUser().getEmailAddress();
		return getSessionUser();
	}

	private void restoreUpdateUser() {
		getSessionUser().setEmailAddress(_restoreEmail);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		default:
			return super.onCreateDialog(id);
		case Constant.DIALOG_PROFILE_CHANGE_USERNAME:
			return getChangeUsernameDialog();
		case Constant.DIALOG_PROFILE_CHANGE_EMAIL:
			return getChangeEmailDialog();
		case Constant.DIALOG_PROFILE_FIRST_TIME:
			return getFirstTimeDialog();
		case Constant.DIALOG_PROFILE_ERROR:
			return getErrorDialog();
		}
	};

	private Dialog getErrorDialog() {
		final ErrorDialog dialog = new ErrorDialog(this, _errorTitle, _errorMessage);
		dialog.setOnDismissListener(this);
		return dialog;
	}

	private Dialog getChangeEmailDialog() {
		final FieldEditDialog dialog = new FieldEditDialog(this,
				getString(R.string.sl_change_email),
				getString(R.string.sl_current),
				getSessionUser().getEmailAddress(),
				getString(R.string.sl_new), null);
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			public void onAction(BaseDialog dialog, int actionId) {
				if (actionId == FieldEditDialog.BUTTON_OK) {
					FieldEditDialog dlg = (FieldEditDialog)dialog;
					String newEmail = dlg.getEditText().trim();
					if(!isValidEmailFormat(newEmail)) {
						dlg.setHint(getString(R.string.sl_please_email_address));
						return;
					}
					else {
						User user = getUpdateUser();
						user.setEmailAddress(newEmail);
						updateUser(user);
					}
				}
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	private Dialog getChangeUsernameDialog() {
		final FieldEditDialog dialog = new FieldEditDialog(this,
				getString(R.string.sl_change_username),
				getString(R.string.sl_current),
				getSessionUser().getLogin(),
				getString(R.string.sl_new), null);
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			//@Override
			public void onAction(BaseDialog dialog, int actionId) {
				dialog.dismiss();
				if (actionId == FieldEditDialog.BUTTON_OK) {
					String newUsername = ((FieldEditDialog)dialog).getEditText().trim();
					User user = getUpdateUser();
					user.setLogin(newUsername);
					updateUser(user);
				}
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	private Dialog getFirstTimeDialog() {
		final FirstTimeDialog dialog = new FirstTimeDialog(this, getSessionUser().getLogin());
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			public void onAction(BaseDialog dialog, int actionId) {
				FirstTimeDialog dlg = (FirstTimeDialog)dialog;
				if (actionId == FirstTimeDialog.BUTTON_OK) {
					String newEmail = dlg.getEmailText().trim();
					String newUsername = dlg.getUsernameText().trim();
					if(!isValidEmailFormat(newEmail)) {
						dlg.setHint(getString(R.string.sl_please_email_address));
						return;
					}
					else {
						User user = getUpdateUser();
						user.setLogin(newUsername);
						user.setEmailAddress(newEmail);
						updateUser(user);
					}
				}
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		User user = getSessionUser();
		_userController = new UserController(this);
		_changePictureItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_change_picture),
				getString(R.string.sl_change_picture), getString(R.string.sl_change_picture_details));
		_changeUsernameItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_change_username),
				getString(R.string.sl_change_username), user.getLogin());
		_changeEmailItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_change_email),
				getString(R.string.sl_change_email), user.getEmailAddress());
		if (user.getLogin() == null || user.getEmailAddress() == null) {
			showSpinnerFor(_userController);
			_userController.loadUser();
		} else {
			setListAdapter(new UserProfileListAdapter(this));
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		hideFooter();
		if (!PackageManager.isScoreloopAppInstalled(this)) {
			showFooter(new StandardListItem<Void>(this, getResources().getDrawable(R.drawable.sl_icon_scoreloop),
					getString(R.string.sl_slapp_title), getString(R.string.sl_slapp_subtitle), null));
		}
	}

	@Override
	protected void onFooterItemClick(final BaseListItem footerItem) {
		hideFooter();
		PackageManager.installScoreloopApp(this);
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		if (item == _changeUsernameItem) {
			if (getSessionUser().getEmailAddress() == null) {
				showDialogSafe(Constant.DIALOG_PROFILE_FIRST_TIME, true);
			} else {
				showDialogSafe(Constant.DIALOG_PROFILE_CHANGE_USERNAME, true);
			}
		} else if (item == _changePictureItem) {
			display(getFactory().createProfileSettingsPictureScreenDescription(getSessionUser()));
		} else if (item == _changeEmailItem) {
			showDialogSafe(Constant.DIALOG_PROFILE_CHANGE_EMAIL, true);
		}
	}

	@Override
	public void onRefresh(final int flags) {
		_changeEmailItem.setSubTitle(getSessionUser().getEmailAddress());
		_changeUsernameItem.setSubTitle(getSessionUser().getLogin());
		if(getBaseListAdapter() != null) {
			getBaseListAdapter().notifyDataSetChanged();
		}
		getManager().persistSessionUserName();
	}

	@Override
	protected void requestControllerDidFailSafe(RequestController requestController, Exception exception) {
		hideSpinnerFor(requestController);
		if(exception instanceof RequestControllerException) {
			RequestControllerException requestException = (RequestControllerException)exception;
			if(requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_EMAIL_TAKEN)) {
				_errorTitle = getString(R.string.sl_error_title_email_already_taken);
				_errorMessage = getString(R.string.sl_error_message_email_already_taken);
			}
			else if(requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_INVALID_EMAIL)) {
				_errorTitle = getString(R.string.sl_error_title_invalid_email_format);
				_errorMessage = getString(R.string.sl_error_message_invalid_email);
			}
			else if(requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_USERNAME_TAKEN)) {
				_errorTitle = getString(R.string.sl_error_title_username_already_taken);
				_errorMessage = getString(R.string.sl_error_message_username_already_taken);
			}
			showDialogSafe(Constant.DIALOG_PROFILE_ERROR, true);
		} else super.requestControllerDidFailSafe(requestController, exception);
		restoreUpdateUser();
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController controller) {
		final ValueStore store = getUserValues();
		store.putValue(Constant.USER_NAME, getSessionUser().getDisplayName());
		store.putValue(Constant.USER_IMAGE_URL, getSessionUser().getImageUrl());
		setListAdapter(new UserProfileListAdapter(this));
		hideSpinnerFor(controller);
		setNeedsRefresh();
	}

	private boolean isValidEmailFormat(String email) {
		Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void updateUser(final User user) {
		getHandler().post(new Runnable() {
			public void run() {
				showSpinnerFor(_userController);
				_userController.setUser(user);
				_userController.submitUser();
			}
		});
	}

}
