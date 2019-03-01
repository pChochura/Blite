package com.pointlessgames.blite.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Settings {
	public static final String HIGH_SCORE = "highScore";
	public static final String STARS = "stars";
	public static final String LEVEL = "level_";
	public static final String SAVE = "Save";
	public static final String FREEZE = "FREEZE";
	public static final String SPEED = "SPEED";
	public static final String SIZE = "SIZE";
	public static final String MAX = "MAX";

	public static final float screenWidth = 1080f;
	public static final float screenHeight = 1920f;
	public static final float ratio = Gdx.graphics.getHeight() / screenHeight;

	public static final float duration = 0.3f;

	public static final float hexagonSize = 300 * ratio;
	public static final float timerThickness = 10 * ratio;
	public static final float borderWidth = 15 * ratio;
	public static final float startHeight = 750 * ratio;
	public static final float playerDiameter = 120 * ratio;
	public static final float startSpeed = -10 * ratio;
	public static final float gravity = -30 * ratio;
	public static final float starWallDistance = 150 * ratio;
	public static final float starRadius1 = 50 * ratio;
	public static final float starRadius2 = 30 * ratio;
	public static final float starProbability = 0.6f;
	public static final int starSpikes = 5;

	public static boolean soundsOn = true;
}