package com.pointlessgames.blite.renderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.Utils;

public class CustomShapeRenderer extends ShapeRenderer {

	private EarClippingTriangulator ear = new EarClippingTriangulator();

	public void polygon(float[] vertices, int offset, int count) {
		if(getCurrentType() != ShapeType.Filled && getCurrentType() != ShapeType.Line)
			throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
		if(count < 6)
			throw new IllegalArgumentException("Polygons must contain at least 3 points.");
		if(count % 2 != 0)
			throw new IllegalArgumentException("Polygons must have an even number of vertices.");

		final float firstX = vertices[0];
		final float firstY = vertices[1];
		if(getCurrentType() == ShapeType.Line) {
			for(int i = offset, n = offset + count; i < n; i += 2) {
				final float x1 = vertices[i];
				final float y1 = vertices[i + 1];

				final float x2;
				final float y2;

				if(i + 2 >= count) {
					x2 = firstX;
					y2 = firstY;
				} else {
					x2 = vertices[i + 2];
					y2 = vertices[i + 3];
				}

				getRenderer().color(getColor());
				getRenderer().vertex(x1, y1, 0);
				getRenderer().color(getColor());
				getRenderer().vertex(x2, y2, 0);

			}
		} else {
			ShortArray arrRes = ear.computeTriangles(vertices);

			for(int i = 0; i < arrRes.size - 2; i = i + 3) {
				float x1 = vertices[arrRes.get(i) * 2];
				float y1 = vertices[(arrRes.get(i) * 2) + 1];

				float x2 = vertices[(arrRes.get(i + 1)) * 2];
				float y2 = vertices[(arrRes.get(i + 1) * 2) + 1];

				float x3 = vertices[arrRes.get(i + 2) * 2];
				float y3 = vertices[(arrRes.get(i + 2) * 2) + 1];

				this.triangle(x1, y1, x2, y2, x3, y3);
			}
		}
	}

	public void hexagon(float x, float y, float radius, float fillFraction) {
		if(getCurrentType() != ShapeType.Filled && getCurrentType() != ShapeType.Line)
			throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
		
		if(getCurrentType() == ShapeType.Filled) {
			if(fillFraction <= 0f) return;

			float[] vertices;
			
			if(fillFraction == 1f) {
				vertices = new float[14];
				for(float i = 0, j = 0; i < MathUtils.PI2; i += MathUtils.PI2 / 6f, j++) {
					vertices[(int) (2 * j)] = MathUtils.cos(i) * radius;
					vertices[(int) (2 * j + 1)] = MathUtils.sin(i) * radius;
				}
			} else {
				int corners = MathUtils.floor(fillFraction * 6);
				vertices = new float[(corners + 3) * 2];

				vertices[0] = 0;
				vertices[1] = 0;

				for(int i = 0; i <= corners; i++) {
					vertices[2 * i + 2] = MathUtils.cos(i * MathUtils.PI2 / 6f) * radius;
					vertices[2 * i + 3] = MathUtils.sin(i * MathUtils.PI2 / 6f) * radius;
				}

				float px = MathUtils.cos(corners * MathUtils.PI2 / 6f) * radius;
				float py = MathUtils.sin(corners * MathUtils.PI2 / 6f) * radius;
				float startT = corners / 6f;
				corners = MathUtils.ceil(fillFraction * 6);
				float dx = MathUtils.cos(corners * MathUtils.PI2 / 6f) * radius;
				float dy = MathUtils.sin(corners * MathUtils.PI2 / 6f) * radius;
				float endT = corners / 6f;
				float finalX = Utils.map(fillFraction, endT, startT, dx, px);
				float finalY = Utils.map(fillFraction, endT, startT, dy, py);

				vertices[vertices.length - 2] = startT == endT ? px : finalX;
				vertices[vertices.length - 1] = startT == endT ? py : finalY;
			}

			Polygon hex = new Polygon(vertices);
			hex.translate(x, y);

			polygon(hex.getTransformedVertices());
		} else {
			int corners = MathUtils.floor(fillFraction * 6);
			float px = MathUtils.cos(0) * radius, py = MathUtils.sin(0) * radius;
			for(float i = 1; i <= corners; i++) {
				float dx = MathUtils.cos(i * MathUtils.PI2 / 6f) * radius;
				float dy = MathUtils.sin(i * MathUtils.PI2 / 6f) * radius;
				rectLine(px + x, py + y, (px = dx) + x, (py = dy) + y, Settings.timerThickness);
			}

			px = MathUtils.cos(corners * MathUtils.PI2 / 6f) * radius;
			py = MathUtils.sin(corners * MathUtils.PI2 / 6f) * radius;
			float startT = corners / 6f;
			corners = MathUtils.ceil(fillFraction * 6);
			float dx = MathUtils.cos(corners * MathUtils.PI2 / 6f) * radius;
			float dy = MathUtils.sin(corners * MathUtils.PI2 / 6f) * radius;
			float endT = corners / 6f;
			float finalX = Utils.map(fillFraction, endT, startT, dx, px);
			float finalY = Utils.map(fillFraction, endT, startT, dy, py);
			rectLine(px + x, py + y, finalX + x, finalY + y, Settings.timerThickness);
		}
	}
	
	public void hexagon(float x, float y, float radius) {
		hexagon(x, y, radius, 1);
	}

	public void reversedArrow(float x, float y, float radius, float angle) {
		if(getCurrentType() != ShapeType.Filled && getCurrentType() != ShapeType.Line)
			throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");

		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float cx = x - cos * radius;
		float cy = y - sin * radius;
		rectLine(x - cos * radius, y - sin * radius, x + cos * radius, y + sin * radius, 2);
		rectLine(cx, cy, cx + MathUtils.cos(angle + MathUtils.degRad * 135) * radius / 2, cy + MathUtils.sin(angle + MathUtils.degRad * 135) * radius / 2, 1);
		rectLine(cx, cy, cx + MathUtils.cos(angle - MathUtils.degRad * 135) * radius / 2, cy + MathUtils.sin(angle - MathUtils.degRad * 135) * radius / 2, 1);
		cx = x + cos * radius;
		cy = y + sin * radius;
		rectLine(cx, cy, cx + MathUtils.cos(angle + MathUtils.degRad * 45) * radius / 2, cy + MathUtils.sin(angle + MathUtils.degRad * 45) * radius / 2, 1);
		rectLine(cx, cy, cx + MathUtils.cos(angle - MathUtils.degRad * 45) * radius / 2, cy + MathUtils.sin(angle - MathUtils.degRad * 45) * radius / 2, 1);
	}

	public void star(float x, float y, float radius1, float radius2, float spikes, float angle, float scale, float fillFraction) {
		if(getCurrentType() != ShapeType.Filled)
			throw new GdxRuntimeException("Must call begin(ShapeType.Filled)");

		float[] vertices = new float[24];
		for(float i = 0, j = 10; j >= 0; i += MathUtils.PI / spikes, j--) {
			float sx, sy;
			if(j % 2 == 1) {
				sx = MathUtils.cos(i) * radius1;
				sy = MathUtils.sin(i) * radius1;
			} else {
				sx = MathUtils.cos(i) * radius2;
				sy = MathUtils.sin(i) * radius2;
			}
			if(j <= 10 * fillFraction) {
				vertices[(int) (2 * j + 2)] = sx;
				vertices[(int) (2 * j + 3)] = sy;
			}
		}

		Polygon star = new Polygon(vertices);
		star.rotate(angle);
		star.setScale(scale, scale);
		star.translate(x, y);

		polygon(star.getTransformedVertices());
	}
}
