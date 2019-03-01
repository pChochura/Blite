package com.pointlessgames.blite.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.pointlessgames.blite.models.Stats;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;

import static com.pointlessgames.blite.utils.Settings.ratio;
import static com.pointlessgames.blite.screens.StartScreen.font;

public class BackgroundStage extends Stage {

	private final CustomShapeRenderer sR;
	private final SpriteBatch sB;
	private final Stats stats;

	public BackgroundStage(CustomShapeRenderer sR, SpriteBatch sB, Stats stats) {
		this.sR = sR;
		this.sB = sB;
		this.stats = stats;
	}

	private void drawBackground() {
		int[] goals = stats.goals;
		int score = stats.score;
		int currentGoal = stats.currentGoal;
		float fraction = (float)score / goals[currentGoal];
		if(currentGoal > 0) fraction = (float) (score - goals[currentGoal - 1]) / (float) (goals[currentGoal] - goals[currentGoal - 1]);

		sR.begin(ShapeRenderer.ShapeType.Filled);

		sR.setColor(Colors.colorHex2);
		sR.hexagon(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio, Settings.hexagonSize);

		sR.setColor(Colors.colorHex1);
		sR.hexagon(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio, Settings.hexagonSize, fraction);

		if(!stats.started) {
			sR.setColor(Colors.colorHex2);
			sR.hexagon(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150 * ratio, Settings.playerDiameter);
		} else if(stats.timer != 0) {
			sR.setColor(Colors.colorHex1);
			sR.set(ShapeRenderer.ShapeType.Line);
			sR.hexagon(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio, Settings.hexagonSize + Settings.timerThickness * 0.5f, stats.timer);
		}

		sR.end();
	}

	private void drawBorders() {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(com.pointlessgames.blite.utils.Colors.colorBorder);
		sR.rect(0, 0, stats.wall.size.x, Gdx.graphics.getHeight());
		sR.rect(Gdx.graphics.getWidth() - stats.wall.size.x, 0, stats.wall.size.x, Gdx.graphics.getHeight());
		sR.end();
	}

	private void drawScore() {
		sB.begin();
		font.getData().setScale(1);
		font.setColor(Colors.colorText);
		font.draw(sB, String.valueOf(stats.score), 0, Gdx.graphics.getHeight() - 400 * ratio + font.getData().lineHeight * 0.35f, Gdx.graphics.getWidth(), Align.center, false);
		if(!stats.started) {
			font.getData().setScale(0.3f);
			font.draw(sB, String.valueOf(stats.highScore), 0, Gdx.graphics.getHeight() - 400 * ratio - font.getData().lineHeight * 1.5f, Gdx.graphics.getWidth(), Align.center, false);
		}
		sB.end();
	}

	private void drawStars() {
		if(!stats.started) {
			sB.begin();
			font.getData().setScale(0.3f);
			font.setColor(Colors.colorText);
			font.draw(sB, String.valueOf(stats.stars), Gdx.graphics.getWidth() - 275 * ratio, Gdx.graphics.getHeight() - 100 * ratio + font.getData().lineHeight * 0.3f, 100 * ratio, Align.right, false);
			sB.end();
		}
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(stats.started ? Colors.colorDark : Colors.colorStar);
		sR.star(Gdx.graphics.getWidth() - 100 * ratio, Gdx.graphics.getHeight() - 100 * ratio, Settings.starRadius1, Settings.starRadius2, Settings.starSpikes, 126, 0.75f, 1);
		sR.setColor(Colors.colorStar);
		float fillFraction = (float) stats.combo / Settings.starSpikes;
		sR.star(Gdx.graphics.getWidth() - 100 * ratio, Gdx.graphics.getHeight() - 100 * ratio, Settings.starRadius1, Settings.starRadius2, Settings.starSpikes, 126, 0.75f, fillFraction);
		sR.end();
	}

	@Override public void draw() {
		drawBackground();
		drawBorders();
		drawScore();
		drawStars();
	}
}