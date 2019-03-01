package com.pointlessgames.blite.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.pointlessgames.blite.models.Alpha;
import com.pointlessgames.blite.models.FloatingText;
import com.pointlessgames.blite.models.Particle;
import com.pointlessgames.blite.models.Perk;
import com.pointlessgames.blite.models.Stats;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.SoundManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.pointlessgames.blite.screens.StartScreen.font;
import static com.pointlessgames.blite.utils.Settings.ratio;

public class GameStage extends Stage {

	private final CustomShapeRenderer sR;
	private final SpriteBatch sB;
	private final Stats stats;

	private ArrayList<Particle> particles;
	private ArrayList<Actor> floatingTexts;

	public GameStage(CustomShapeRenderer sR, SpriteBatch sB, Stats stats) {
		this.sR = sR;
		this.sB = sB;
		this.stats = stats;

		particles = new ArrayList<>();
		floatingTexts = new ArrayList<>();
	}

	private void checkCurrentGoal() {
		if(stats.score >= stats.goals[stats.currentGoal])
			stats.currentGoal++;
	}

	private void wallCollision() {
		stats.score += stats.wall.isFreeze ? (Perk.amount[Perk.FREEZE][Perk.level[Perk.FREEZE]]) : 1;

		if(Settings.soundsOn)
			SoundManager.hit.play(0.5f);

		addWallCollisionParticles();

		checkCurrentGoal();

		stats.wall.random();
		stats.player.increaseSpeed();
		stats.wall.decreaseSize();
		stats.star.random(stats.player, stats.wall);
	}

	private void addWallCollisionParticles() {
		Color first = stats.wall.color.cpy();
		Color second = first.toString().equals(Colors.colorDark.toString()) ? Colors.colorBright : Colors.colorDark;
		boolean isLeft = stats.wall.pos.x == 0;
		for(int i = 0; i < 20; i++) {
			float lerp = MathUtils.random(0f, 1f);
			float size = MathUtils.random(3f, 10f);
			float angle = isLeft ? MathUtils.random(80, 90) : MathUtils.random(90, 100);
			if(MathUtils.randomBoolean()) angle -= isLeft ? 160 : 200;
			Vector2 speed = new Vector2(1, 0).setAngle(angle).setLength(MathUtils.random(1f * ratio, 3f * ratio));
			Vector2 acc = new Vector2(speed.cpy().scl(-MathUtils.random(0f, 1f)));
			Particle p = new Particle(new Vector2(stats.wall.pos.x + stats.wall.size.x * (isLeft ? 1.5f : -0.5f), stats.player.pos.y), first.cpy().lerp(second, lerp), new Alpha(1), size)
					.colorful(first.cpy().lerp(second, lerp))
					.shrinking(MathUtils.random(0f, 3f * ratio))
					.fading(new Alpha(0))
					.living(1f);
			p.speed = speed;
			p.acc = acc;
			particles.add(p);
		}
	}

	private void addComboText(Vector2 pos) {
		FloatingText fT = new FloatingText(font).setText(String.format(Locale.getDefault(), "+%d", Settings.starSpikes)).setTextSize(0.3f);
		fT.setColor(Colors.colorText.cpy());
		fT.setPosition(pos.x - fT.getTargetWidth() / 2, pos.y);
		fT.addAction(Actions.sequence(
				Actions.parallel(Actions.moveBy(0, 25 * ratio, Settings.duration, Interpolation.exp5Out), Actions.alpha(0, Settings.duration, Interpolation.exp5Out)),
				Actions.run(() -> floatingTexts.remove(fT))));

		floatingTexts.add(fT);
	}

	private void checkStartCollision() {
		if(stats.star.pos != null && stats.player.pos.dst(stats.star.pos) < (Settings.playerDiameter + Settings.starRadius1) / 2) {
			stats.stars++;
			stats.score++;
			stats.combo++;
			if(stats.combo == Settings.starSpikes) {
				stats.timer = 1;
				stats.stars += stats.combo;
				stats.score += stats.combo;

				addComboText(stats.star.pos);
			}

			if(Settings.soundsOn)
				SoundManager.collect.play(0.5f);

			checkCurrentGoal();

			Gdx.app.getPreferences(Settings.SAVE).putInteger(Settings.STARS, stats.stars).flush();
			stats.star.pos = null;
		}
	}

	private void playerCollided(Vector2 pos) {
		if(stats.player.color == stats.wall.color && pos.y >= stats.wall.pos.y && pos.y < stats.wall.pos.y + stats.wall.size.y)
			wallCollision();
		else {
			if(Settings.soundsOn)
				SoundManager.crash.play(0.5f);
			particles.addAll(Particle.getExplosionParticles(pos, 10 * ratio, 30 * ratio));
			stats.resetGame();
		}
	}

	@Override public void draw() {
		stats.wall.draw(sR);
		stats.star.draw(sR);
		stats.player.draw(sR);

		for(Particle p : particles) p.draw(sR);
		for(Actor a : floatingTexts) a.draw(sB, 1);
	}

	@Override public void act(float delta) {
		for(int i = particles.size() - 1; i >= 0; i--) {
			particles.get(i).update(delta);
			if(!particles.get(i).isActive()) particles.remove(i);
		}

		if(stats.started && stats.timer == 0) {
			stats.wall.update(stats.player);
			stats.player.update(delta, this::playerCollided);
		}

		checkStartCollision();

		for(Actor a : floatingTexts) a.act(delta);
	}

	@Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(stats.timer == 0) {
			if(!stats.started) {
				stats.started = true;
				stats.score = 0;
				stats.currentGoal = 0;
			} else stats.player.switchColor();
		}
		if(stats.player.acc.y < 0) stats.player.acc.y *= -1;
		return true;
	}

	@Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(stats.player.acc.y > 0) stats.player.acc.y *= -1;
		return true;
	}
}