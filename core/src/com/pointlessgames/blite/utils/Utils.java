package com.pointlessgames.blite.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.pointlessgames.blite.renderers.FilledShapeRenderer;

public class Utils {

	public static float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

	public static void hexagon(FilledShapeRenderer sR, float x, float y, float r, float fraction, boolean selected) {
		if(fraction <= 0f) return;

		int corners = MathUtils.floor(fraction * 6);
		float vertices[] = new float[(corners + 3) * 2];

		vertices[0] = 0;
		vertices[1] = 0;

		for(int i = 0; i <= corners; i++) {
			vertices[2 * i + 2] = MathUtils.cos(i * MathUtils.PI2 / 6f) * r;
			vertices[2 * i + 3] = MathUtils.sin(i * MathUtils.PI2 / 6f) * r;
		}

		float px = MathUtils.cos(corners * MathUtils.PI2 / 6f) * r;
		float py = MathUtils.sin(corners * MathUtils.PI2 / 6f) * r;
		float startT = corners / 6f;
		corners = MathUtils.ceil(fraction * 6);
		float dx = MathUtils.cos(corners * MathUtils.PI2 / 6f) * r;
		float dy = MathUtils.sin(corners * MathUtils.PI2 / 6f) * r;
		float endT = corners / 6f;
		float finalX = Utils.map(fraction, endT, startT, dx, px);
		float finalY = Utils.map(fraction, endT, startT, dy, py);

		vertices[vertices.length - 2] = startT == endT ? px : finalX;
		vertices[vertices.length - 1] = startT == endT ? py : finalY;

		Polygon hex = new Polygon(vertices);
		hex.translate(x, y);

		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(selected ? Settings.colorHex1 : Settings.colorHex2);
		sR.polygon(hex.getTransformedVertices());
		sR.end();
	}

	public static void hexagon(FilledShapeRenderer sR, float x, float y, float r, boolean selected) {
		float vertices[] = new float[14];
		for(float i = 0, j = 0; i < MathUtils.PI2; i += MathUtils.PI2 / 6f, j++) {
			vertices[(int) (2 * j)] = MathUtils.cos(i) * r;
			vertices[(int) (2 * j + 1)] = MathUtils.sin(i) * r;
		}
		Polygon hex = new Polygon(vertices);
		hex.translate(x, y);

		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(selected ? Settings.colorHex1 : Settings.colorHex2);
		sR.polygon(hex.getTransformedVertices());
		sR.end();
	}

	public static void hexagon(FilledShapeRenderer sR, float x, float y, float r) {
		hexagon(sR, x, y, r, false);
	}

	public static void hexagonOutline(FilledShapeRenderer sR, float x, float y, float r, float fraction, boolean selected) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(selected ? Settings.colorHex2 : Settings.colorHex1);
		int corners = MathUtils.floor(fraction * 6);
		float px = MathUtils.cos(0) * r, py = MathUtils.sin(0) * r;
		for(float i = 1; i <= corners; i++) {
			float dx = MathUtils.cos(i * MathUtils.PI2 / 6f) * r;
			float dy = MathUtils.sin(i * MathUtils.PI2 / 6f) * r;
			sR.rectLine(px + x, py + y, (px = dx) + x, (py = dy) + y, Settings.timerThickness);
		}

		px = MathUtils.cos(corners * MathUtils.PI2 / 6f) * r;
		py = MathUtils.sin(corners * MathUtils.PI2 / 6f) * r;
		float startT = corners / 6f;
		corners = MathUtils.ceil(fraction * 6);
		float dx = MathUtils.cos(corners * MathUtils.PI2 / 6f) * r;
		float dy = MathUtils.sin(corners * MathUtils.PI2 / 6f) * r;
		float endT = corners / 6f;
		float finalX = Utils.map(fraction, endT, startT, dx, px);
		float finalY = Utils.map(fraction, endT, startT, dy, py);
		sR.rectLine(px + x, py + y, finalX + x, finalY + y, Settings.timerThickness);

		sR.end();
	}

	public static void hexagonOutline(FilledShapeRenderer sR, float x, float y, float r, float fraction) {
		hexagonOutline(sR, x, y, r, fraction, false);
	}

	public static void reversedArrow(FilledShapeRenderer sR, float sx, float sy, float r, float a) {
		float x = sx - MathUtils.cos(a) * r;
		float y = sy - MathUtils.sin(a) * r;
		sR.rectLine(sx - MathUtils.cos(a) * r, sy - MathUtils.sin(a) * r, sx + MathUtils.cos(a) * r, sy + MathUtils.sin(a) * r, 2);
		sR.rectLine(x, y, x + MathUtils.cos(a + MathUtils.degRad * 135) * r / 2, y + MathUtils.sin(a + MathUtils.degRad * 135) * r / 2, 1);
		sR.rectLine(x, y, x + MathUtils.cos(a - MathUtils.degRad * 135) * r / 2, y + MathUtils.sin(a - MathUtils.degRad * 135) * r / 2, 1);
		x = sx + MathUtils.cos(a) * r;
		y = sy + MathUtils.sin(a) * r;
		sR.rectLine(x, y, x + MathUtils.cos(a + MathUtils.degRad * 45) * r / 2, y + MathUtils.sin(a + MathUtils.degRad * 45) * r / 2, 1);
		sR.rectLine(x, y, x + MathUtils.cos(a - MathUtils.degRad * 45) * r / 2, y + MathUtils.sin(a - MathUtils.degRad * 45) * r / 2, 1);
	}
}
