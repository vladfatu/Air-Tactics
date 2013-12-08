package com.airtactics;


import android.app.Application;

import com.scoreloop.client.android.ui.OnCanStartGamePlayObserver;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;

public class MainAPP extends Application implements OnCanStartGamePlayObserver {

	static enum GamePlaySessionStatus {
		CHALLENGE, NONE, NORMAL
	}

	static private Integer _gamePlaySessionMode; // in case of no modes in the game, this is not needed
	static private GamePlaySessionStatus _gamePlaySessionStatus;
	final static String EXTRA_MODE = "extraMode";

	static Integer getGamePlaySessionMode() {
		return _gamePlaySessionMode;
	}

	static GamePlaySessionStatus getGamePlaySessionStatus() {
		return _gamePlaySessionStatus;
	}

	static void setGamePlaySessionMode(final Integer mode) {
		_gamePlaySessionMode = mode;
	}

	static void setGamePlaySessionStatus(final GamePlaySessionStatus status) {
		_gamePlaySessionStatus = status;
	}

	//@Override
	public boolean onCanStartGamePlay() {
		// ScoreloopUI knows whether a challenge game is ongoing,
		// therefore here we only need to care about normal games

		//return policy1();
		return policy2();
	}

	@Override
	public void onCreate() {
		super.onCreate();
 
		ScoreloopManagerSingleton.init(this);
		
		ScoreloopManagerSingleton.get().setOnCanStartGamePlayObserver(this);

		_gamePlaySessionStatus = GamePlaySessionStatus.NONE;
		_gamePlaySessionMode = null;
	}

	@SuppressWarnings("unused")
	private boolean policy1() { // if a normal game is ongoing, cancel it, and make room for a challenge game
		if (_gamePlaySessionStatus == GamePlaySessionStatus.NORMAL) {
			_gamePlaySessionStatus = GamePlaySessionStatus.NONE;
			_gamePlaySessionMode = null;
			// do whatever other cleanup is required when canceling a gameplay
		}
		return true;
	}

	private boolean policy2() { // if a normal game is ongoing, we keep it and reject the request to start a challenge game
		if (_gamePlaySessionStatus == GamePlaySessionStatus.NORMAL) {
			return false;
		} else {
			return true;
		}
	}
}

