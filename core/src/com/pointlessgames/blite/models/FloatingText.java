package com.pointlessgames.blite.models;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

public class FloatingText extends Actor {

	private BitmapFont font;
	private String text;
	private float textSize;
	private float targetWidth;

	public FloatingText(BitmapFont font) {
		this.font = font;
	}

	public FloatingText setText(String text) {
		this.text = text;
		this.targetWidth = new GlyphLayout(font, text).width;
		return this;
	}

	public FloatingText setTextSize(float textSize) {
		this.textSize = textSize;
		return this;
	}

	public FloatingText setTargetWidth(float width) {
		this.targetWidth = width;
		return this;
	}

	public float getTargetWidth() {
		return targetWidth;
	}

	@Override public void draw(Batch batch, float parentAlpha) {
		batch.begin();

		font.getData().setScale(textSize);
		font.setColor(getColor());
		font.draw(batch, text, getX(), getY(), targetWidth, Align.center, false);

		batch.end();
	}
}
