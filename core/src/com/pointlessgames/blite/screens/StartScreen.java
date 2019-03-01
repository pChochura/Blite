package com.pointlessgames.blite.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.pointlessgames.blite.models.Perk;
import com.pointlessgames.blite.models.Stats;
import com.pointlessgames.blite.renderers.CustomShapeRenderer;
import com.pointlessgames.blite.stages.BackgroundStage;
import com.pointlessgames.blite.stages.GameStage;
import com.pointlessgames.blite.stages.PerksStage;
import com.pointlessgames.blite.utils.Colors;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.SoundManager;

import static com.pointlessgames.blite.utils.Settings.ratio;

public class StartScreen extends BaseScreen {

	private CustomShapeRenderer sR;
	private SpriteBatch sB;
	private Stats stats;

	public static BitmapFont font;

	public StartScreen() {
		super(Settings.screenHeight, Colors.colorBackground);
	}

	@Override public void show() {
		super.show();

		sR = new CustomShapeRenderer();
		sB = new SpriteBatch();
		stats = new Stats();

		addStage(new BackgroundStage(sR, sB, stats));
		addStage(new GameStage(sR, sB, stats));
		addStage(new PerksStage(sR, sB, stats));

		SoundManager.loadSounds();

		configureFont();
	}

	private void configureFont() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arcon.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = (int) (250 * ratio);
		parameter.incremental = true;
		font = generator.generateFont(parameter, new FreeTypeFontGenerator.FreeTypeBitmapFontData());
		generator.dispose();
	}

	@Override public void dispose() {
		super.dispose();

		font.dispose();
		sR.dispose();
		sB.dispose();

		SoundManager.dispose();
	}
}