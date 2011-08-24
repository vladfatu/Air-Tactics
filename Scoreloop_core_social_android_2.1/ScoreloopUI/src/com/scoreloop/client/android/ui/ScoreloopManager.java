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

package com.scoreloop.client.android.ui;

import java.util.List;

import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.core.model.AwardList;

/**
 * The ScoreloopManager is the general managing class for ScoreloopUI. A single instance of 
 * ScoreloopManager is shared by all activities in the application. The ScoreloopManager
 * should be instantiated from within the <b>Android application</b> class. The shared
 * ScoreloopManager instance should persist for the lifecycle of the game. The 
 * ScoreloopManagerSingleton class is used to intialize instances of ScoreloopManager. 
 * 
 * Basic Usage:\n
 * -# Use the ScoreloopManagerSingleton class to initialize the ScoreloopManager.
 * -# Access the ScoreloopManager using ScoreloopManagerSingleton.get().
 * -# Invoke the desired ScoreloopManager method. 
 *
 *  \sa ScoreloopManagerSingleton
 */
public interface ScoreloopManager {
	
	/**
	 * This method is used to "unlock" an award and to automatically set it
	 * as having been achieved by the session user. The method can be used
	 * to specify whether a toast message should be displayed and also
	 * whether the achieved award should be submitted to the Scoreloop
	 * servers straight away. Note that {@link #loadAchievements()} must first have
	 * been called before this method is invoked, otherwise a
	 * <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">
	 * java.lang.illegalStateException</a> will be thrown.
	 * @param awardId a valid award identifier as specified on the developer site.
	 * @param showToast @c true if you want the ScoreloopUI show a toast when the award gets achieved.
	 * @param submitNow @c true if you want to submit the new achievement immediately. You might not want to do this during gameplay as it results in a communication being built up.
	 */
	void achieveAward(String awardId, boolean showToast, boolean submitNow);

	/**
	 * This method returns the Achievement object
	 * for a given Award identifier. The award
	 * identifier is chosen by the developer and
	 * configured on https://developer.scoreloop.com.
	 * 
	 * Note that the {@link loadAchievements()} method
	 * must first be called, before this method is invoked
	 * otherwise a <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">java.lang.IllegalStateException</a> will be thrown.
	 *
	 * @param awardId An award identifier as specified by the developer on https://developer.scoreloop.com
	 * @return An Achievement object
	 */
	Achievement getAchievement(String awardId);

	/**
	 * This method Returns the list of all achievements
	 * that have been requested from the server by calling
	 * {@link loadAchievements()}. 
	 * 
	 * If this method is called before {@link loadAchievements()}
	 * then a <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">java.lang.IllegalStateException</a> will be thrown.
	 * @return <a href="http://download.oracle.com/javase/6/docs/api/java/util/List.html">
	 * java.util.List<Achievement></a> A list of Achievement objects.
	 */
	List<Achievement> getAchievements();

	/**
	 * Accessor to the award list of this game.
	 * @return An AwardList object
	 */
	AwardList getAwardList();

	/**
	 * This method is used to check whether the list of achievements
	 * have been successfully requested from the server. 
	 * @return @c true if the list of achievements were successfully loaded, otherwise @c false.
	 */
	boolean hasLoadedAchievements();

	/**
	 * This method is used to query whether an award with a given id
	 * has been achieved or not. The award identifier is defined
	 * by the developer and configured on https://developer.scoreloop.com.
	 * Note that {@link #loadAchievements()} must have first been called
	 * before this method can be invoked, otherwise a 
	 * <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">
	 * java.lang.IllegalStateException</a> will be thrown.
	 * @param awardId The identifier for the award specified on the developer web site.
	 * @return @c true if the award has been achieved, otherwise @c false.
	 */
	boolean isAwardAchieved(String awardId);

	/**
	 * This method loads a list of Scoreloop
	 * achievement objects from the server. Once this method 
	 * has been called (and after a successful repsonse has been
	 * received from the server) the following methods can be used:
	 * - {@link #hasLoadedAchievements()}, (to check whether the load
	 * operation was successful),
	 * - getAchievements(), (to access the returned list),
	 * - getAchievement(String), (to access a single named achievement),
	 * - isAwardAchieved(String), (to check whether a specific award has been achieved),
	 * - achieveAward(String).
	 * 
	 * @param continuation A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Runnable.html">java.lang.Runnable</a> that gets called when
	 * the loading of achievements completes with or without errors. 
	 * This can be  be @c null.
	 */
	void loadAchievements(Runnable continuation);

	/**
	* This method is used to submit a score to Scoreloop. 
	* It should be called once the game play
	* activity has ended. This method will automatically
	* check whether a challenge is underway and, if so, submit
	* the score as part of the challenge. The method will also 
	* automatically detect whether the score should be submitted
	* on behalf of the challenge contender or the challenge contestant. 
	* 
	* If no challenge is currently underway, the score will be submitted
	* to Scoreloop on behalf of the session user in the standard way.
	* 
	* After submitting the score, {@link OnScoreSubmitObserver.onScoreSubmit() onScoreSubmit()} will be called.
	* 
	* \sa @link scoreloopui-integratescores Submitting Scores to Scoreloop@endlink
	* 
	* \param scoreResult A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Double.html">java.lang.Double</a> object representing the
	* score result obtained by the user in the game. 
	* \param mode A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Integer.html">java.lang.Integer</a> representing the mode at which the score was obtained. If the game does not support modes, pass @c null here instead. 
	 */
	void onGamePlayEnded(Double scoreResult, Integer mode);
	
	/**
	 * This method correctly sets an OnCanStartGamePlayObserver
	 * in ScoreloopManager. This observer must be set if the challenges feature has been
	 * enabled in the game.
	 * 
	 * @param observer A valid observer. Pass @c null to remove
	 * the observer.
	 * \sa OnCanStartGamePlayObserver
	 */
	void setOnCanStartGamePlayObserver(OnCanStartGamePlayObserver observer);

	/**
	 * This method correctly sets an OnScoreSubmitObserver in
	 * ScoreloopManager.
	 * Set this observer to get informed about score submissions,
	 * (if the submission to the server succeeded or failed).
	 *
	 * @param observer A valid observer. Pass @c null to remove 
	 * the observer.
	 * \sa OnScoreSubmitObserver
	 */
	void setOnScoreSubmitObserver(OnScoreSubmitObserver observer);

	/**
	 * This method correctly sets an OnStartGamePlayRequestObserver
	 * in ScoreloopManager.
	 * This observer must be set if the challenges feature has been 
	 * enabled in the game. 
	 *  
	 * @param observer a valid observer. Pass @c null to remove the observer.
	 */
	void setOnStartGamePlayRequestObserver(OnStartGamePlayRequestObserver observer);

	/**
	 * This method displays a "welcome back" toast showing
	 * the user's Scoreloop display name.
	 * If no display name exists for the user, then no toast is shown. 
	 * The display name is set after the ScoreloopUI is finished with an
	 * authenticated user.
	 * 
	 * @param delay A time interval in milliseconds after which the toast should be shown. To show the toast immediately, pass zero here.  
	 */
	void showWelcomeBackToast(long delay);

	/**
	 * This method is used to submit achievements to the Scoreloop server. 
	 * This method will implicitly call the  {@link loadAchievements()}
	 * method if it has not already been called. 
	 * 
	 * @param continuation A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Runnable.html">java.lang.Runnable</a> that will be invoked when all achievements have been submitted. 
	 * This value can be @c null. 
	 */
	void submitAchievements(Runnable continuation);

}
