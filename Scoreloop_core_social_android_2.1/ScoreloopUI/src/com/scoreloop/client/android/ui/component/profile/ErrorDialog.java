package com.scoreloop.client.android.ui.component.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scoreloop.client.android.ui.R;
import com.scoreloop.client.android.ui.framework.BaseDialog;

public class ErrorDialog extends BaseDialog {

	private String _title;
	private String _text;

	public ErrorDialog(final Context context, final String title, final String text)
	{
		super(context);
		_title = title;
		_text = text;
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_error;
	}

	public void onClick(final View v) {
		if (v.getId() == R.id.sl_button_ok) {
			dismiss();
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Button okButton = (Button) findViewById(R.id.sl_button_ok);
		okButton.setOnClickListener(this);
		TextView tvTitle = (TextView) findViewById(R.id.sl_title);
		tvTitle.setText(_title);
		TextView tvErrorMessage = (TextView) findViewById(R.id.sl_error_message);
		tvErrorMessage.setText(_text);
	}
}
