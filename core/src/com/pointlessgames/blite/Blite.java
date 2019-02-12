package com.pointlessgames.blite;

import com.badlogic.gdx.Game;
import com.pointlessgames.blite.screens.StartScreen;

public class Blite extends Game {

	@Override
	public void create() {
		this.setScreen(new StartScreen());
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
