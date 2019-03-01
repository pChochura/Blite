package com.pointlessgames.blite.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.pointlessgames.blite.models.FloatingText;
import com.pointlessgames.blite.models.Perk;
import com.pointlessgames.blite.models.Star;
import com.pointlessgames.blite.models.Stats;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.SoundManager;

import java.util.ArrayList;

import static com.pointlessgames.blite.screens.StartScreen.font;
import static com.pointlessgames.blite.utils.Settings.ratio;

public class PerksStage extends Stage {

	private final CustomShapeRenderer sR;
	private final SpriteBatch sB;
	private final Stats stats;

	private int selectedPerk;
	private float xOffset;
	private float yOffset;

	private ArrayList<Actor> floatingTexts;

	public PerksStage(CustomShapeRenderer sR, SpriteBatch sB, Stats stats) {
		this.sR = sR;
		this.sB = sB;
		this.stats = stats;

		selectedPerk = -1;
		xOffset = 120 * ratio * (1 + MathUtils.cos(MathUtils.PI2 / 6f)) + 20 * ratio;
		yOffset = 2 * 120 * ratio * MathUtils.cos(MathUtils.PI2 / 6f) + 20 * ratio;

		floatingTexts = new ArrayList<>();
	}

	private void drawPerksButtons() {
		sR.begin(ShapeRenderer.ShapeType.Filled);

		sR.setColor(selectedPerk == Perk.FREEZE ? Colors.colorHex1 : Colors.colorHex2);
		sR.hexagon(Gdx.graphics.getWidth() / 2 - xOffset, 400 * ratio - yOffset, Settings.playerDiameter);

		sR.setColor(selectedPerk == Perk.SPEED ? Colors.colorHex1 : Colors.colorHex2);
		sR.hexagon(Gdx.graphics.getWidth() / 2, 400 * ratio, Settings.playerDiameter);

		sR.setColor(selectedPerk == Perk.SIZE ? Colors.colorHex1 : Colors.colorHex2);
		sR.hexagon(Gdx.graphics.getWidth() / 2 + xOffset, 400 * ratio - yOffset, Settings.playerDiameter);

		sR.end();
	}

	private void drawPerksIcons() {
		font.getData().setScale(0.12f);
		font.setColor(Colors.colorText);
		Perk.drawPerk(sR, sB, font, Gdx.graphics.getWidth() / 2 - xOffset, 400 * ratio - yOffset, Settings.playerDiameter, Perk.FREEZE);
		Perk.drawPerk(sR, sB, font, Gdx.graphics.getWidth() / 2, 400 * ratio, Settings.playerDiameter, Perk.SPEED);
		Perk.drawPerk(sR, sB, font, Gdx.graphics.getWidth() / 2 + xOffset, 400 * ratio - yOffset, Settings.playerDiameter, Perk.SIZE);
	}

	private void drawUpdatePrize() {
		font.getData().setScale(0.12f);
		font.setColor(Colors.colorText);
		String prize = Perk.level[selectedPerk] < Perk.maxLevel ? String.valueOf(Perk.prize[selectedPerk][Perk.level[selectedPerk]]) : Settings.MAX;
		GlyphLayout fontGlyph = new GlyphLayout(font, prize);
		float offset = 10 * ratio;
		Star star = new Star(1, 126);
		star.scale = 0.4f;
		star.pos = new Vector2((Gdx.graphics.getWidth() + star.getBoundingRectangle().width + fontGlyph.width) / 2 + offset, 200 * ratio);
		star.draw(sR);

		sB.begin();
		font.draw(sB, prize, (Gdx.graphics.getWidth() - star.getBoundingRectangle().width - fontGlyph.width) / 2 - offset, 200 * ratio + fontGlyph.height / 2, fontGlyph.width, Align.center, false);
		sB.end();
	}

	private void timeOut() {
		stats.combo -= Settings.starSpikes;
		if(stats.started) {
			if(selectedPerk == -1) selectedPerk = MathUtils.random(Perk.FREEZE, Perk.SIZE);
			Perk.applyPerk(selectedPerk, stats.player, stats.wall);

			addPerkText();

			if(Settings.soundsOn)
				SoundManager.powerup.play(0.5f);
		}
		selectedPerk = -1;
	}

	private void addPerkText() {
		FloatingText fT = new FloatingText(font).setText(Perk.getText(selectedPerk)).setTextSize(0.75f).setTargetWidth(Gdx.graphics.getWidth());
		fT.setColor(Colors.colorText.cpy());
		fT.setPosition(0, Gdx.graphics.getHeight() / 2f);
		fT.addAction(Actions.sequence(
				Actions.parallel(Actions.moveBy(0, 100 * ratio, Settings.duration, Interpolation.exp5Out), Actions.alpha(0, Settings.duration, Interpolation.exp5Out)),
				Actions.run(() -> floatingTexts.remove(fT))));

		floatingTexts.add(fT);
	}

	@Override public void draw() {
		if(!stats.started || stats.timer != 0) {
			drawPerksButtons();
			drawPerksIcons();
		} if(selectedPerk != -1 && !stats.started)
			drawUpdatePrize();

		for(Actor a : floatingTexts)
			a.draw(sB, 1);
	}

	@Override public void act(float delta) {
		if(stats.started && stats.timer > 0) stats.updateTimer(delta, this::timeOut);

		for(Actor a : floatingTexts)
			a.act(delta);
	}

	private int checkPerkSelection(Vector2 pos) {
		Vector2[] perks = new Vector2[3];
		perks[0] = new Vector2(Gdx.graphics.getWidth() / 2 - xOffset, Gdx.graphics.getHeight() - 400 * ratio + yOffset);
		perks[1] = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio);
		perks[2] = new Vector2(Gdx.graphics.getWidth() / 2 + xOffset, Gdx.graphics.getHeight() - 400 * ratio + yOffset);

		for(int i = 0; i < perks.length; i++)
			if(perks[i].dst(pos) <= Settings.playerDiameter) return i;
		return -1;
	}

	@Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 pos = new Vector2(screenX, screenY);
		int perkSelection = checkPerkSelection(pos);

		if(stats.started) {
			if(stats.timer == 0) return false;
			else if(perkSelection != -1) {
				if(perkSelection == selectedPerk) stats.timer = 0.01f;
				selectedPerk = perkSelection;
				if(Settings.soundsOn)
					SoundManager.pick.play(0.5f);
			} else return false;
		} else if(perkSelection == -1) {
			if(selectedPerk == -1) return false;
			else {
				selectedPerk = perkSelection;
				if(Settings.soundsOn)
					SoundManager.pick.play(0.5f);
			}
		} else {
			if(selectedPerk == perkSelection) {
				int prize = Perk.prize[perkSelection][Perk.level[perkSelection]];
				if(stats.stars >= prize && Perk.level[perkSelection] < Perk.maxLevel) {
					stats.stars -= prize;
					Perk.level[perkSelection] = MathUtils.clamp(Perk.level[perkSelection] + 1, 0, Perk.maxLevel);
					Gdx.app.getPreferences(Settings.SAVE).putInteger(Settings.STARS, stats.stars).flush();
					Perk.savePerks();

					addPerkText();
				} else if(Settings.soundsOn)
					SoundManager.pick2.play(0.5f);
			}
			selectedPerk = perkSelection;
			if(Settings.soundsOn)
				SoundManager.pick.play(0.5f);
		}

		return true;
	}
}
