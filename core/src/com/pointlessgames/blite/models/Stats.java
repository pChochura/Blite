package com.pointlessgames.blite.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;

import static com.pointlessgames.blite.utils.Settings.ratio;

public class Stats {

	public final int[] goals = new int[]{10, 25, 50, 100, 250, 500, 1000};

	public Player player;
	public Wall wall;
	public Star star;

	public int currentGoal;
	public int highScore;
	public int score;
	public int stars;
	public int combo;
	public float timer;

	public boolean started;

	private Preferences savePrefs;

	public Stats() {
		wall = new Wall(true, Settings.startHeight, Colors.colorDark);
		player = new Player(new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150 * ratio));
		star = new Star(1, MathUtils.random(360f));

		savePrefs = Gdx.app.getPreferences(Settings.SAVE);

		highScore = savePrefs.getInteger(Settings.HIGH_SCORE, highScore);
		stars = savePrefs.getInteger(Settings.STARS, highScore);

		Perk.loadPerks();
	}

	public void resetGame() {
		if(score > highScore)
			savePrefs.putInteger(Settings.HIGH_SCORE, highScore = score).flush();
		started = false;
		player.reset();
		wall.reset();
		wall.random(true);
		star.pos = null;
		combo = 0;
		timer = 0;
	}

	public void updateTimer(float dt, Runnable timeOut) {
		timer = MathUtils.clamp(timer - 0.33f * dt, 0, 1);
		if(timer == 0) timeOut.run();
	}
}
