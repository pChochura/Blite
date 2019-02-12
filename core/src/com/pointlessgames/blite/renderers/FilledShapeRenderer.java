package com.pointlessgames.blite.renderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;

public class FilledShapeRenderer extends ShapeRenderer {

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
}
