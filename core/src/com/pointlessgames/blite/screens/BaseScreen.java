package com.pointlessgames.blite.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;

class BaseScreen implements Screen {

	private ArrayList<Stage> stages;
	private StretchViewport viewport;

	private float screenHeight;
	private Color backgroundColor;
	private InputMultiplexer inputMultiplexer;

	BaseScreen(float screenHeight, Color backgroundColor) {
		this.screenHeight = screenHeight;
		this.backgroundColor = backgroundColor;

		stages = new ArrayList<>();
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	public void addStage(int index, Stage stage) {
		stages.add(index, stage);
		inputMultiplexer.addProcessor(index, stage);
	}

	public void addStage(Stage stage) {
		stages.add(stage);
		inputMultiplexer.addProcessor(0, stage);
	}

	private void setScreenSize() {
		float aspectRatio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		viewport = new StretchViewport(screenHeight / aspectRatio, screenHeight, new OrthographicCamera(screenHeight / aspectRatio, screenHeight));
	}

	@Override public void show() {
		setScreenSize();
	}

	@Override public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewport.getCamera().update();

		for(Stage s : stages)
			s.act(delta);

		for(Stage s : stages)
			s.draw();
	}

	@Override public void resize(int width, int height) {
		setScreenSize();
	}

	@Override public void dispose() {
		for(Stage s : stages)
			s.dispose();
	}

	@Override public void pause() { }
	@Override public void resume() { }
	@Override public void hide() { }
}
