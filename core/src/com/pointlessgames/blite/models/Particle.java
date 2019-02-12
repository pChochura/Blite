package com.pointlessgames.blite.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.pointlessgames.blite.renderers.FilledShapeRenderer;
import com.pointlessgames.blite.utils.Settings;

import java.util.ArrayList;

import static com.pointlessgames.blite.utils.Settings.ratio;

public class Particle extends Acceleration {
	public static final Polygon RECTANGLE = getRectPolygon();

	private static Polygon getRectPolygon() {
		float[] vertices = new float[8];
		//TOp left
		vertices[0] = -0.5f;
		vertices[1] = -0.5f;
		//Bottom left
		vertices[2] = -0.5f;
		vertices[3] = 0.5f;
		//Bottom right
		vertices[4] = 0.5f;
		vertices[5] = 0.5f;
		//Top right
		vertices[6] = 0.5f;
		vertices[7] = -0.5f;
		return new Polygon(vertices);
	}

	private static final float DEFAULT_LIFE = 0.5f;
	private boolean shrinking;
	private boolean colorful;
	private boolean fading;
	private boolean active;

	private float life, startLife;
	private Alpha alpha, startAlpha, endAlpha;
	private Color color, startColor, endColor;
	private float size, startSize, endSize;
	private Polygon shape;

	public Particle(Vector2 pos, Color color, Alpha alpha, float size) {
		this.pos = pos;
		this.size = size;
		this.alpha = alpha;
		this.color = color;
		this.active = true;
		this.acc = new Vector2();
		this.speed = new Vector2();

		this.life = this.startLife = DEFAULT_LIFE;
	}

	public Particle shrinking(float endSize) {
		this.startSize = size;
		this.endSize = endSize;
		shrinking = true;
		return this;
	}

	public Particle colorful(Color endColor) {
		this.startColor = color.cpy();
		this.endColor = endColor;
		colorful = true;
		return this;
	}

	public Particle fading(Alpha endAlpha) {
		this.startAlpha = new Alpha(alpha.a);
		this.endAlpha = endAlpha;
		fading = true;
		return this;
	}

	public Particle living(float startLife) {
		this.life = this.startLife = startLife;
		return this;
	}

	public Particle ofShape(Polygon shape) {
		this.shape = shape;
		this.shape.setScale(size, size);
		return this;
	}

	public void update(float dt) {
		if(active) {
			speed.mulAdd(acc, dt);
			pos.add(speed);

			life -= dt;
			if(colorful) color = startColor.cpy().lerp(endColor, 1 - life / startLife);
			if(shrinking) size = MathUtils.lerp(startSize, endSize, 1 - life / startLife);
			if(fading) alpha.a = MathUtils.lerp(startAlpha.a, endAlpha.a, 1 - life / startLife);

			if(shape != null) {
				shape.setPosition(pos.x, pos.y);
				shape.setRotation(speed.angle());
				color = color.cpy().lerp(Settings.colorBackground, 1 - alpha.a);
			} else color.a = alpha.a;

			if(life <= 0) active = false;
		}
	}

	public void draw(FilledShapeRenderer sR) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(color);

		if(shape != null)
			sR.polygon(shape.getTransformedVertices());
		else sR.circle(pos.x, pos.y, size);

		sR.end();
	}

	public boolean isActive() {
		return active;
	}

	public static ArrayList<Particle> getExplosionParticles(Vector2 pos, float minSize, float maxSize) {
		return getExplosionParticles(pos, minSize, maxSize, MathUtils.random(15, 25) * ratio);
	}

	public static ArrayList<Particle> getExplosionParticles(Vector2 pos, float minSize, float maxSize, float amount) {
		ArrayList<Particle> particles = new ArrayList<>();

		Color dark = Settings.colorDark.cpy();
		for(int i = 0; i < amount; i++) {
			float lerp = MathUtils.random(0f, 1f);
			float size = MathUtils.random(minSize, maxSize);
			Vector2 speed = new Vector2(1, 0).setAngle(MathUtils.random(360f)).setLength(MathUtils.random(5f * ratio, 10f * ratio));
			Vector2 acc = new Vector2(speed.cpy().scl(-MathUtils.random(0f, 2f * ratio)));
			Particle p = new Particle(pos.cpy(), dark.cpy().lerp(Settings.colorBright, lerp), new Alpha(1), size)
					.colorful(dark.cpy().lerp(Settings.colorBright, lerp))
					.shrinking(0f)
					.fading(new Alpha(0))
					.living(1f);
			p.speed = speed;
			p.acc = acc;
			particles.add(p);
		}

		return particles;
	}
}
