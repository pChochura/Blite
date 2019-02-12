package com.pointlessgames.blite.models;

import com.badlogic.gdx.math.Vector2;

public class FloatingText {
	private Vector2 startPos;
	private Alpha startAlpha;
	private String startText;
	private boolean startVisible;

	public Vector2 pos;
	public Alpha alpha;
	public String text;
	public boolean visible;

	public FloatingText(float x, float y, int a, String text) {
		this.pos = new Vector2(x, y);
		this.alpha = new Alpha(a);
		this.text = text;
		this.visible = true;

		this.startPos = this.pos.cpy();
		this.startAlpha = this.alpha;
		this.startVisible = this.visible;
		this.startText = this.text;
	}

	public void reset() {
		pos.set(startPos);
		alpha.a = startAlpha.a;
		text = startText;
		visible = startVisible;
	}
}
