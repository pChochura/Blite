package com.pointlessgames.blite.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.DataCallback;
import com.pointlessgames.blite.utils.Settings;

import static com.pointlessgames.blite.utils.Settings.ratio;

public class Player extends Acceleration {
	private Vector2 startPos;
	public Color color;

	public Player(Vector2 pos) {
		this.pos = pos;
		this.startPos = pos.cpy();
		this.color = Colors.colorBright;
		this.speed = new Vector2(Settings.startSpeed, 0);
		this.acc = new Vector2(0, Settings.gravity);
	}

	public void draw(CustomShapeRenderer sR) {
		float r = Settings.playerDiameter;
		sR.begin(ShapeRenderer.ShapeType.Filled);

		Polygon polygon = new Polygon(new float[] {
				0, -r / 2 - 20,
				-r / 2, r / 2 - 20,
				-3 * r / 8, r / 2 - 20 + r / 4,
				-r / 4, r / 2 - 20,
				r / 4, r / 2 - 20,
				3 * r / 8, r / 2 - 20 + r / 4,
				r / 2, r / 2 - 20});
		polygon.rotate(speed.angle() + 90);
		polygon.translate(pos.x, pos.y);

		sR.setColor(color);
		sR.polygon(polygon.getTransformedVertices());
		sR.setColor(color == Colors.colorDark ? Colors.colorBright : Colors.colorDark);
		sR.ellipse(pos.x - r / 6, pos.y - r / 6, r / 3, r / 3);

		sR.end();
	}

	public void update(float dt, DataCallback<Vector2> collided) {
		speed.mulAdd(acc, dt);
		pos.add(speed);

		if(pos.x + Settings.playerDiameter / 2 >= Gdx.graphics.getWidth() ||
				pos.x - Settings.playerDiameter / 2 <= 0) {
			speed.x *= -1;
			collided.run(pos.cpy());
		}
	}

	public void switchColor() {
		color = color == Colors.colorDark ? Colors.colorBright : Colors.colorDark;
	}

	public void increaseSpeed() {
		speed.x += Math.signum(speed.x) * 0.5f * ratio;
	}

	public void reset() {
		color = Colors.colorBright;
		pos = startPos.cpy();
		speed.set(Settings.startSpeed, 0);
	}
}
