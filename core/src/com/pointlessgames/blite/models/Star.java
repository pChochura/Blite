package com.pointlessgames.blite.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.Utils;

public class Star extends Position {

	private float angle;
	public float fillFraction;
	public float scale;

	public Star(float fillFraction, float angle) {
		this.fillFraction = fillFraction;
		this.angle = angle;
		this.scale = 1;
	}

	public Rectangle getBoundingRectangle() {
		Rectangle rect = new Rectangle();
		float minX = Float.MIN_VALUE, minY = Float.MIN_VALUE, maxX = 0, maxY = 0;
		for(float i = 0; i < MathUtils.PI2; i += MathUtils.PI / Settings.starSpikes) {
			float x = MathUtils.cos(i) * Settings.starRadius1 * scale;
			float y = MathUtils.sin(i) * Settings.starRadius1 * scale;
			if(x > maxX) maxX = x;
			if(x < minX) minX = x;
			if(y > maxY) maxY = y;
			if(y < minY) minY = y;
		}
		return rect.set(minX, minY, maxX, maxY);
	}

	public void random(Player player, Wall wall) {
		if(MathUtils.randomBoolean(Settings.starProbability)) {
			float width = Gdx.graphics.getWidth();
			float dir = Math.signum(player.speed.x);
			float x = MathUtils.random(dir < 0 ? Settings.starWallDistance : width / 2 + Settings.starWallDistance, dir < 0 ? width / 2 - Settings.starWallDistance : width - Settings.starWallDistance);
			float d = Math.abs(x - wall.pos.x);
			float mid = (player.pos.y + Settings.playerDiameter / 2 + wall.pos.y + wall.size.y / 2) / 2;
			float offset = Utils.map(d, Settings.starWallDistance, width / 2 - Settings.starWallDistance, 0, wall.size.y / 2);
			float y = MathUtils.random(mid - offset, mid + offset);
			pos = new Vector2(x, y);
		}
	}

	public void draw(CustomShapeRenderer sR) {
		if(this.pos == null) return;
		sR.begin(ShapeRenderer.ShapeType.Filled);

		sR.setColor(Colors.colorDark);
		sR.star(pos.x, pos.y, Settings.starRadius1, Settings.starRadius2, Settings.starSpikes, angle, scale, fillFraction);
		sR.setColor(Colors.colorStar);
		sR.star(pos.x, pos.y, Settings.starRadius1, Settings.starRadius2, Settings.starSpikes, angle, scale, 1);

		sR.end();
	}
}