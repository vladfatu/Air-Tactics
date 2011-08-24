package com.scoreloop.client.android.ui.component.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scoreloop.client.android.ui.R;
import com.scoreloop.client.android.ui.framework.BaseDialog;

public class FieldEditDialog extends BaseDialog {

	public static final int BUTTON_OK 		= 0;
	public static final int BUTTON_CANCEL	= 1;

	private String _title;
	private String _currentLabel;
	private String _currentText;
	private String _newLabel;
	private String _newText;
	private EditText _editText;
	private TextView _hint;

	public FieldEditDialog(final Context context,
			final String title, final String currentLabel, final String currentText,
			final String newLabel, final String newText)
	{
		super(context);
		_title = title;
		_currentLabel = currentLabel;
		_currentText = currentText;
		_newLabel = newLabel;
		_newText = newText;
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_profile_edit;
	}

	public void onClick(final View v) {
		if (_listener != null) {
			switch (v.getId()) {
			case R.id.sl_button_ok:
				_listener.onAction(this, BUTTON_OK);
				break;
			case R.id.sl_button_cancel:
				_listener.onAction(this, BUTTON_CANCEL);
			}
		}
	}

	public String getEditText() {
		return _editText.getText().toString();
	}

	public void setHint(String text) {
		_hint.setText(text);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Button okButton = (Button) findViewById(R.id.sl_button_ok);
		okButton.setOnClickListener(this);
		final Button cancelButton = (Button) findViewById(R.id.sl_button_cancel);
		cancelButton.setOnClickListener(this);
		final TextView tvTitle = (TextView) findViewById(R.id.sl_title);
		tvTitle.setText(_title);
		final TextView tvCurrentLabel = (TextView) findViewById(R.id.sl_user_profile_edit_current_label);
		tvCurrentLabel.setText(_currentLabel);
		final TextView tvCurrentText = (TextView) findViewById(R.id.sl_user_profile_edit_current_text);
		tvCurrentText.setText(_currentText);
		final TextView tvNewLabel = (TextView) findViewById(R.id.sl_user_profile_edit_new_label);
		tvNewLabel.setText(_newLabel);
		_editText = (EditText) findViewById(R.id.sl_user_profile_edit_new_text);
		_editText.setText(_newText);
		_hint = (TextView) findViewById(R.id.sl_dialog_hint);
	}
}
