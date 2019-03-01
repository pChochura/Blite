package com.pointlessgames.blite.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

	public static Sound collect = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.wav"));
	public static Sound crash = Gdx.audio.newSound(Gdx.files.internal("sounds/crash.wav"));
	public static Sound hit = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.wav"));
	public static Sound pick = Gdx.audio.newSound(Gdx.files.internal("sounds/pick.wav"));
	public static Sound pick2 = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_2.wav"));
	public static Sound powerup = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));

	public static void dispose() {
		collect.dispose();
		crash.dispose();
		hit.dispose();
		pick.dispose();
		pick2.dispose();
		powerup.dispose();
	}

	public static void loadSounds() {
		collect = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.wav"));
		crash = Gdx.audio.newSound(Gdx.files.internal("sounds/crash.wav"));
		hit = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.wav"));
		pick = Gdx.audio.newSound(Gdx.files.internal("sounds/pick.wav"));
		pick2 = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_2.wav"));
		powerup = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));
	}
}
