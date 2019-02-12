package com.pointlessgames.blite.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pointlessgames.blite.renderers.FilledShapeRenderer;
import com.pointlessgames.blite.utils.Settings;

import java.util.Random;

public class Wall extends Position {
	private float startHeight;
	public Vector2 size;
	public Color color;
	public boolean isFreeze;

	public Wall(boolean left, float startHeight, Color color) {
		this.startHeight = startHeight;
		this.size = new Vector2(Settings.borderWidth, startHeight);
		this.pos = new Vector2(left ? Gdx.graphics.getWidth() - size.x : 0, 0);
		this.color = color;
		this.isFreeze = false;
		random();
	}

	public void random() {
		pos = new Vector2(pos.x == 0 ? Gdx.graphics.getWidth() - size.x : 0, new Random().nextFloat() * (Gdx.graphics.getHeight() - size.y));
		switchColor();
	}

	public void random(boolean left) {
		if(left) pos.x = 1;
		random();
	}

	public void switchColor() {
		color = color == Settings.colorDark ? Settings.colorBright : Settings.colorDark;
	}

	public void draw(FilledShapeRenderer sR) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(color);
		sR.rect(pos.x, pos.y, size.x, size.y);
		sR.end();
	}

	public void update(Player player) {
		if(!isFreeze) pos.y -= player.speed.y;
		pos.y = MathUtils.clamp(pos.y, 0, Gdx.graphics.getHeight() - size.y);
	}

	public void decreaseSize() {
		float pSize = size.y;
		size.y *= 0.97f;
		pos.y -= (pSize - size.y) / 2;
	}

	public void reset() {
		size.y = startHeight;
		isFreeze = false;
	}
}
