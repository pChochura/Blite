package com.pointlessgames.blite.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.pointlessgames.blite.models.Perk;
import com.pointlessgames.blite.models.Stats;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Settings;

import static com.pointlessgames.blite.utils.Settings.ratio;

public class GameStage extends Stage {

	private final CustomShapeRenderer sR;
	private final SpriteBatch sB;
	private final Stats stats;

	public GameStage(CustomShapeRenderer sR, SpriteBatch sB, Stats stats) {
		this.sR = sR;
		this.sB = sB;
		this.stats = stats;
	}

	private void checkCurrentGoal() {
		if(stats.score >= stats.goals[stats.currentGoal])
			stats.currentGoal++;
	}

	private void wallCollision() {
		stats.score += stats.wall.isFreeze ? (Perk.amount[Perk.FREEZE][Perk.level[Perk.FREEZE]]) : 1;

		checkCurrentGoal();

		stats.wall.random();
		stats.player.increaseSpeed();
		stats.wall.decreaseSize();
		stats.star.random(stats.player, stats.wall);
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

				//TODO do this after
				stats.combo = 0;
			}

			checkCurrentGoal();

			Gdx.app.getPreferences(Settings.SAVE).putInteger(Settings.STARS, stats.stars).flush();
			stats.star.pos = null;
		}
	}

	private void playerCollided(Vector2 pos) {
		if(stats.player.color == stats.wall.color && pos.y >= stats.wall.pos.y && pos.y < stats.wall.pos.y + stats.wall.size.y)
			wallCollision();
		else stats.resetGame();
	}

	@Override public void draw() {
		stats.wall.draw(sR);
		stats.star.draw(sR);
		stats.player.draw(sR);
	}

	@Override public void act(float delta) {
		stats.wall.update(stats.player);
		if(stats.started)
			stats.player.update(delta, this::playerCollided);

		checkStartCollision();
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