package com.internet.ui;

import airtactics.com.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.internet.XMPPClient;
import com.internet.XMPPService;
import com.users.User;

public class CreateUser extends Activity implements OnClickListener{

	EditText editUsername, editPassword;
	Button createButton, loginButton;
	SharedPreferences mPrefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.create_user);
		editUsername = (EditText) findViewById(R.id.editUsername);
		editPassword = (EditText) findViewById(R.id.editPassword);
		createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(this);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		firstRunPreferences();
	}
	
	public void setUsernamePref(String username) {
	    SharedPreferences.Editor edit = mPrefs.edit();
	    edit.putString("username", username);
	    edit.commit();
	 }
	
	public void setPasswordPref(String password) {
	    SharedPreferences.Editor edit = mPrefs.edit();
	    edit.putString("password", password);
	    edit.commit();
	 }
	
	 /**
	 * setting up preferences storage
	 */
	 public void firstRunPreferences() {
	    Context mContext = this.getApplicationContext();
	    mPrefs = mContext.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
	 }
	
	private void createAccount() 
	{
		try
		{
			XMPPService.getInstance().createAccount(editUsername.getText().toString(), editPassword.getText().toString());
			//XMPPService.getInstance().login(editUsername.getText().toString(), editPassword.getText().toString());
			setUsernamePref(editUsername.getText().toString());
			setPasswordPref(editPassword.getText().toString());
			User.getInstance().setUser(editUsername.getText().toString(), editPassword.getText().toString());
			startActivity(new Intent(this, GameRoom.class));
			finish();
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "A player with that username already exists!!!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void login() 
	{
		//XMPPService.getInstance().login(editUsername.getText().toString(), editPassword.getText().toString());
		setUsernamePref(editUsername.getText().toString());
		setPasswordPref(editPassword.getText().toString());
		User.getInstance().setUser(editUsername.getText().toString(), editPassword.getText().toString());
		startActivity(new Intent(this, XMPPClient.class));
		finish();
	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.createButton:
				createAccount();
				break;
			case R.id.loginButton:
				login();
		}
		
	}

}
