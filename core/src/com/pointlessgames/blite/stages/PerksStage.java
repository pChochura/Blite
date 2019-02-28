package com.pointlessgames.blite.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.pointlessgames.blite.models.Alpha;
import com.pointlessgames.blite.models.Particle;
import com.pointlessgames.blite.models.Perk;
import com.pointlessgames.blite.models.Star;
import com.pointlessgames.blite.models.Stats;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.screens.StartScreen;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.Utils;

import static com.pointlessgames.blite.screens.StartScreen.font;
import static com.pointlessgames.blite.utils.Settings.ratio;

public class PerksStage extends Stage {

	private final CustomShapeRenderer sR;
	private final SpriteBatch sB;
	private final Stats stats;

	private int selectedPerk;
	private float xOffset;
	private float yOffset;

	public PerksStage(CustomShapeRenderer sR, SpriteBatch sB, Stats stats) {
		this.sR = sR;
		this.sB = sB;
		this.stats = stats;

		selectedPerk = -1;
		xOffset = 120 * ratio * (1 + MathUtils.cos(MathUtils.PI2 / 6f)) + 20 * ratio;
		yOffset = 2 * 120 * ratio * MathUtils.cos(MathUtils.PI2 / 6f) + 20 * ratio;
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

	@Override public void draw() {
		if(!stats.started) {
			drawPerksButtons();
			drawPerksIcons();
		} if(selectedPerk != -1 && !stats.started)
			drawUpdatePrize();
	}

	@Override public void act(float delta) {

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
		if((perkSelection != -1 || perkSelection != selectedPerk) && (stats.timer != 0 || !stats.started)) {
			if(stats.timer == 0 || perkSelection != -1) {
				if(perkSelection != -1 && perkSelection == selectedPerk) {
					if(stats.timer != 0) stats.timer = 0.01f;
					int prize = Perk.prize[perkSelection][Perk.level[perkSelection]];
					if(!stats.started && stats.stars >= prize && Perk.level[perkSelection] < Perk.maxLevel) {
						stats.timer = 0.01f;
						stats.stars -= prize;
						Perk.level[perkSelection] = MathUtils.clamp(Perk.level[perkSelection] + 1, 0, Perk.maxLevel);
						Gdx.app.getPreferences(Settings.SAVE).putInteger(Settings.STARS, stats.stars).flush();
						Perk.savePerks();
					}
				}
				selectedPerk = perkSelection;
				return true;
			}
		}
		return !stats.started && selectedPerk != perkSelection && (selectedPerk = perkSelection) == -1;
	}
}
