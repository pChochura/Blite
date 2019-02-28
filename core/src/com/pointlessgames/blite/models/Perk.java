package com.pointlessgames.blite.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.Utils;

import java.util.Locale;

public class Perk {
	public final static int FREEZE = 0;
	public final static int SPEED = 1;
	public final static int SIZE = 2;

	public static int[][] prize = {{50, 100, 200, 450, 1000, 1650, 2500, 3500, 5000},
			{15, 50, 100, 250, 550, 1050, 1750, 2500, 3350},
			{35, 80, 125, 300, 750, 1150, 1700, 2300, 3050}};
	public static int[][] amount = {{2, 3, 4, 5, 6, 7, 8, 9, 10},
			{10, 13, 18, 21, 26, 29, 34, 37, 42},
			{50, 75, 125, 170, 230, 295, 360, 430, 500}};
	public static int[] level = {0, 0, 0};
	public static int maxLevel = prize[0].length;

	public static void drawPerk(CustomShapeRenderer sR, SpriteBatch batch, BitmapFont font, float x, float y, float r, int type) {
		if(type == FREEZE) drawFreezePerk(sR, batch, font, x, y, r);
		if(type == SPEED) drawSpeedPerk(sR, batch, font, x, y, r);
		if(type == SIZE) drawSizePerk(sR, batch, font, x, y, r);
	}

	private static void drawFreezePerk(CustomShapeRenderer sR, SpriteBatch batch, BitmapFont font, float x, float y, float r) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(Colors.colorText);
		y += r / 15;
		sR.rect(x + r / 4 - r / 20, y - r / 3, r / 10, r / 1.2f);
		sR.reversedArrow(x - r / 4, y - r / 3 + r / 2.4f, r / 4.8f, 0);
		sR.reversedArrow(x - r / 4, y - r / 3 + r / 2.4f, r / 4.8f, MathUtils.degRad * 60);
		sR.reversedArrow(x - r / 4, y - r / 3 + r / 2.4f, r / 4.8f, MathUtils.degRad * -60);
		y -= r / 15;
		batch.begin();
		font.draw(batch, String.format(Locale.getDefault(), "+%d", amount[0][level[0]]), x - r / 2, y - r / 2, r, Align.center, false);
		batch.end();
		sR.end();
	}

	private static void drawSpeedPerk(CustomShapeRenderer sR, SpriteBatch batch, BitmapFont font, float x, float y, float r) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(Colors.colorText);
		y += r / 15;
		sR.circle(x + r / 8, y + r / 15, r / 8);
		y -= r / 15;
		batch.begin();
		font.draw(batch, String.format(Locale.getDefault(), "-%d%%", amount[1][level[1]]), x - r / 2, y - r / 2, r, Align.center, false);
		batch.end();
		sR.end();
	}

	private static void drawSizePerk(CustomShapeRenderer sR, SpriteBatch batch, BitmapFont font, float x, float y, float r) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(Colors.colorText);
		y += r / 15;
		sR.rect(x + r / 4 - r / 20, y - r / 3, r / 10, r / 1.2f);
		sR.rectLine(x - r / 4, y - r / 3, x - r / 4, y - r / 3 + r / 1.2f, 2);
		sR.rectLine(x - r / 4, y - r / 3, x - r / 4 - r / 8, y - r / 3 + r / 8, 1);
		sR.rectLine(x - r / 4, y - r / 3, x - r / 4 + r / 8, y - r / 3 + r / 8, 1);
		sR.rectLine(x - r / 4, y - r / 3 + r / 1.2f, x - r / 4 - r / 8, y - r / 3 + r / 1.2f - r / 8, 1);
		sR.rectLine(x - r / 4, y - r / 3 + r / 1.2f, x - r / 4 + r / 8, y - r / 3 + r / 1.2f - r / 8, 1);
		y -= r / 15;
		batch.begin();
		font.draw(batch, String.format(Locale.getDefault(), "+%d", amount[2][level[2]]), x - r / 2, y - r / 2, r, Align.center, false);
		batch.end();
		sR.end();
	}

	public static void applyPerk(int selectedPerk, Player player, Wall wall) {
		wall.isFreeze = false;
		switch(selectedPerk) {
			case FREEZE:
				wall.isFreeze = true;
				break;
			case SPEED:
				player.speed.scl(1 - amount[SPEED][level[SPEED]] / 100f, 0);
				break;
			case SIZE:
				wall.size.y += amount[SIZE][level[SIZE]];
				wall.pos.y -= amount[SIZE][level[SIZE]] / 2;
				break;
		}
	}

	public static void savePerks() {
		Preferences preferences = Gdx.app.getPreferences(Settings.SAVE);
		for(int i = 0; i < level.length; i++)
			preferences.putInteger(Settings.LEVEL + i, level[i]);
		preferences.flush();
	}

	public static void loadPerks() {
		Preferences preferences = Gdx.app.getPreferences(Settings.SAVE);
		for(int i = 0; i < level.length; i++)
			level[i] = preferences.getInteger(Settings.LEVEL + i);
	}

	public static String getText(int selectedPerk) {
		switch(selectedPerk) {
			case FREEZE: return Settings.FREEZE;
			case SPEED: return Settings.SPEED;
			case SIZE: return Settings.SIZE;
		}
		return "";
	}
}
