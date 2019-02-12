package com.pointlessgames.blite.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ExtendedActor extends Actor {

	private boolean isReset;

	private boolean visible;
	private float x;
	private float y;
	private float rotation;
	private Color color;
	private String name;

	public void reset() {
		super.setVisible(visible);
		super.setPosition(x, y);
		super.setRotation(rotation);
		super.setColor(color);
		super.setName(name);

		this.isReset = true;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(this.visible = visible);
		this.isReset = false;
	}

	@Override
	public void setX(float x) {
		super.setX(this.x = x);
		this.isReset = false;
	}

	@Override
	public void setX(float x, int alignment) {
		super.setX(this.x = x, alignment);
		this.isReset = false;
	}

	@Override
	public void setY(float y) {
		super.setY(this.y = y);
		this.isReset = false;
	}

	@Override
	public void setY(float y, int alignment) {
		super.setY(this.y = y, alignment);
		this.isReset = false;
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(this.x = x, this.y = y);
		this.isReset = false;
	}

	@Override
	public void setPosition(float x, float y, int alignment) {
		super.setPosition(this.x = x, this.y = y, alignment);
		this.isReset = false;
	}

	@Override
	public void setRotation(float degrees) {
		super.setRotation(this.rotation = degrees);
		this.isReset = false;
	}

	@Override
	public void setColor(Color color) {
		super.setColor(this.color = color);
		this.isReset = false;
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		this.color = new Color(r, g, b, a);
		this.isReset = false;
	}

	@Override
	public void setName(String name) {
		super.setName(this.name = name);
		this.isReset = false;
	}

	public boolean isReset() {
		return isReset;
	}
}
