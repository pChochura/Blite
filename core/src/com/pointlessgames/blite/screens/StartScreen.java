package com.pointlessgames.blite.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.pointlessgames.blite.models.Alpha;
import com.pointlessgames.blite.models.Particle;
import com.pointlessgames.blite.models.Perk;
import com.pointlessgames.blite.models.Player;
import com.pointlessgames.blite.models.Star;
import com.pointlessgames.blite.models.Wall;
import com.pointlessgames.blite.renderers.FilledShapeRenderer;
import com.pointlessgames.blite.utils.ExtendedActor;
import com.pointlessgames.blite.utils.Settings;
import com.pointlessgames.blite.utils.Utils;

import java.util.ArrayList;

import static com.pointlessgames.blite.utils.Settings.ratio;
import static com.pointlessgames.blite.utils.Settings.screenHeight;

public class StartScreen implements Screen {

	private BitmapFont font250, font150, font75, font30;
	private ArrayList<Particle> particles;
	private Preferences preferences;
	private FilledShapeRenderer sR;
	private SpriteBatch batch;
	private Sound hitSound;
	private Sound collectSound;
	private Sound crashSound;
	private Sound pickSound;
	private Sound pick2Sound;
	private Sound powerupSound;

	private int[] goals = new int[]{10, 25, 50, 100, 250, 500, 1000};
	private Player player;
	private Wall wall;
	private Star star;
	private Star comboStar;

	private boolean questDone;
	private int currentGoal;
	private boolean started;
	private int highScore;
	private int score;
	private int stars;
	private int combo;
	private float timer;
	private float scoreTextSize;
	private float starsTextSize;
	private int selectedPerk;
	private ExtendedActor starsText;
	private ExtendedActor perkText;
	private ExtendedActor questText;
	private ArrayList<ExtendedActor> actors;
	private StretchViewport viewport;

	@Override
	public void show() {
		float aspectRatio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		viewport = new StretchViewport(screenHeight / aspectRatio, screenHeight, new OrthographicCamera(screenHeight / aspectRatio, screenHeight));

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		sR = new FilledShapeRenderer();
		batch = new SpriteBatch();

		hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_sound.wav"));
		crashSound = Gdx.audio.newSound(Gdx.files.internal("sounds/crash_sound.wav"));
		collectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collect_sound.wav"));
		pickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_sound.wav"));
		pick2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick_2_sound.wav"));
		powerupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup_sound.wav"));

		configureFonts();
		configureGame();
		configurePreferences();

		timer = 0;
		started = false;

		handleInput();
	}

	private void configurePreferences() {
		preferences = Gdx.app.getPreferences(Settings.SAVE);
		highScore = preferences.getInteger(Settings.HIGH_SCORE, 0);
		stars = preferences.getInteger(Settings.STARS, 0);
		questDone = true;//preferences.getBoolean(Settings.QUEST);

		Perk.loadPerks(preferences);
	}

	private void configureGame() {
		scoreTextSize = new GlyphLayout(font250, String.valueOf(score)).height / 2;
		starsTextSize = new GlyphLayout(font75, String.valueOf(stars)).height / 2;
		particles = new ArrayList<>();
		actors = new ArrayList<>();

		wall = new Wall(true, Settings.startHeight, Settings.colorDark);
		player = new Player(new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150 * ratio));
		star = new Star(1, MathUtils.random(360f));
		comboStar = new Star(1, 126);
		comboStar.pos = new Vector2(Gdx.graphics.getWidth() - 100 * ratio, Gdx.graphics.getHeight() - 100 * ratio);
		comboStar.scale = 0.75f;
		comboStar.fillFraction = 3 / 5f;

		selectedPerk = -1;

		starsText = new ExtendedActor();
		starsText.setPosition(Gdx.graphics.getWidth() - 275 * ratio, Gdx.graphics.getHeight() - 100 * ratio + starsTextSize);
		starsText.setColor(Settings.colorText.cpy());
		starsText.setName("");

		actors.add(starsText);

		perkText = new ExtendedActor();
		perkText.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		perkText.setColor(Settings.colorText.cpy().add(0, 0, 0, -1));
		perkText.setName(Perk.getText(selectedPerk));

		actors.add(perkText);

		questText = new ExtendedActor();
		questText.setPosition(Gdx.graphics.getWidth() / 2, 200);
		questText.setColor(Settings.colorText.cpy().add(0, 0, 0, -1));
		questText.setName("Dzienne wyzwanie Odbij się 20 razy zmieniając kolor tylko raz");

		actors.add(questText);

		currentGoal = 0;
	}

	private void configureFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arcon.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = (int) (250 * ratio);
		parameter.incremental = true;
		parameter.borderWidth = 1;
		parameter.borderColor = Settings.colorText;
		font250 = generator.generateFont(parameter, new FreeTypeFontGenerator.FreeTypeBitmapFontData());

		parameter.size = (int) (150 * ratio);
		font150 = generator.generateFont(parameter, new FreeTypeFontGenerator.FreeTypeBitmapFontData());

		parameter.size = (int) (75 * ratio);
		font75 = generator.generateFont(parameter, new FreeTypeFontGenerator.FreeTypeBitmapFontData());

		parameter.borderWidth = 0;
		parameter.size = (int) (30 * ratio);
		font30 = generator.generateFont(parameter, new FreeTypeFontGenerator.FreeTypeBitmapFontData());

		generator.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(Settings.colorBackground.r, Settings.colorBackground.g, Settings.colorBackground.b, Settings.colorBackground.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewport.getCamera().update();

		//Transitions
		for(ExtendedActor a : actors) {
			a.act(delta);
			if(a.getActions().isEmpty() && !a.isReset()) a.reset();
		}

		//Updating
		if(started && timer == 0) {
			player.update(delta, pos -> {
				if(player.color == wall.color && pos.y >= wall.pos.y && pos.y < wall.pos.y + wall.size.y)
					wallCollided();
				else {
					particles.addAll(Particle.getExplosionParticles(pos, 10 * ratio, 30 * ratio));
					crashSound.play(0.5f);
					resetGame();
				}
			});
			wall.update(player);

			addPlayerThrust();

			checkStars();
		} else if(timer > 0) {
			timer = MathUtils.clamp(timer - 0.33f * delta, 0, 1);
			if(timer == 0) {
				powerupSound.play(0.5f);
				if(started) {
					if(selectedPerk == -1) selectedPerk = MathUtils.random(Perk.FREEZE, Perk.SIZE);
					Perk.applyPerk(selectedPerk, player, wall);
				}
				perkText.setName(Perk.getText(selectedPerk));
				perkText.addAction(Actions.parallel(Actions.alpha(1), Actions.fadeOut(Settings.duration), Actions.moveBy(0, 50 * ratio, Settings.duration)));
				selectedPerk = -1;
			}
		}

		//Drawing
		drawBorders();
		drawBackground();
		drawScore();
		drawStars();

		//Particles
		for(int i = particles.size() - 1; i >= 0; i--) {
			particles.get(i).update(delta);
			particles.get(i).draw(sR);
			if(!particles.get(i).isActive()) particles.remove(i);
		}

		//More drawing
		batch.begin();
		font150.setColor(perkText.getColor());
		font150.draw(batch, perkText.getName(), 0, perkText.getY(), Gdx.graphics.getWidth(), Align.center, false);
		batch.end();

		wall.draw(sR);
		star.draw(sR);
		player.draw(sR);

		drawQuestDialog();
	}

	private void drawQuestDialog() {
		if(questText.getColor().a != 0) {
			sR.begin(ShapeRenderer.ShapeType.Filled);
			sR.setColor(Settings.colorBorder.cpy().mul(1, 1, 1, questText.getColor().a));
			float width = Gdx.graphics.getWidth() - 300 * ratio;
			float height = 400 * ratio;
			sR.rect(questText.getX() - width / 2, questText.getY(), width, height);
			sR.end();

			batch.begin();
			font75.setColor(questText.getColor());
			font75.draw(batch, questText.getName(), questText.getX() - width / 2, questText.getY() + height / 2, width, Align.center, false);
			batch.end();
		}
	}

	private void addPlayerThrust() {
		if(player.speed.len() < 20 || Gdx.graphics.getFrameId() % 15 != 0) return;
		for(int i = 0; i < Utils.map(player.speed.len(), 15, 50, 1, 10); i++) {
			float size = MathUtils.random(2f, 3f);
			float angle = 180 - player.speed.angle();
			Vector2 speed = new Vector2(1, 0).setAngle(angle).setLength(MathUtils.random(1f * ratio, 3f * ratio));
			Vector2 acc = new Vector2(speed.cpy().scl(-MathUtils.random(0f, 1f)));
			Vector2 pos = player.pos.cpy();
			pos.add(MathUtils.random(-Settings.playerDiameter, Settings.playerDiameter) / 3, MathUtils.random(-Settings.playerDiameter, Settings.playerDiameter) / 3);
			Particle p = new Particle(pos, player.color.cpy(), new Alpha(1), size)
					.fading(new Alpha(0))
					.shrinking(0f);
			p.speed = speed;
			p.acc = acc;
			particles.add(p);
		}
	}

	private void wallCollided() {
		addWallCollisionParticles();

		hitSound.play(0.5f);

		score += wall.isFreeze ? (Perk.amount[Perk.FREEZE][Perk.level[Perk.FREEZE]]) : 1;

		checkCurrentGoal();

		wall.random();
		player.increaseSpeed();
		wall.decreaseSize();
		star.random(player, wall);
	}

	private void checkCurrentGoal() {
		if(score >= goals[currentGoal]) {
			currentGoal++;

			//TODO Give something after reaching the goal
//			stars += 5;
//			preferences.putInteger(Settings.STARS, stars).flush();
		}
	}

	private void addWallCollisionParticles() {
		Color dark = Settings.colorDark.cpy();
		boolean isLeft = wall.pos.x == 0;
		for(int i = 0; i < 20; i++) {
			float lerp = MathUtils.random(0f, 1f);
			float size = MathUtils.random(3f, 10f);
			float angle = isLeft ? MathUtils.random(80, 90) : MathUtils.random(90, 100);
			if(MathUtils.randomBoolean()) angle -= isLeft ? 160 : 200;
			Vector2 speed = new Vector2(1, 0).setAngle(angle).setLength(MathUtils.random(1f * ratio, 3f * ratio));
			Vector2 acc = new Vector2(speed.cpy().scl(-MathUtils.random(0f, 1f)));
			Particle p = new Particle(new Vector2(wall.pos.x + wall.size.x * (isLeft ? 1.5f : -0.5f), player.pos.y), dark.cpy().lerp(Settings.colorBright, lerp), new Alpha(1), size)
					.colorful(dark.cpy().lerp(Settings.colorBright, lerp))
					.shrinking(MathUtils.random(0f, 3f * ratio))
					.fading(new Alpha(0))
					.living(1f);
			p.speed = speed;
			p.acc = acc;
			particles.add(p);
		}
	}

	private void resetGame() {
		if(score > highScore) {
			preferences.putInteger(Settings.HIGH_SCORE, highScore = score).flush();
			particles.addAll(Particle.getExplosionParticles(new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio), 20 * ratio, 50 * ratio, 100));
		}
		started = false;
		player.reset();
		wall.reset();
		wall.random(true);
		star.pos = null;
		combo = 0;
		selectedPerk = -1;
	}

	private void checkStars() {
		if(star.pos != null && player.pos.dst(star.pos) < (Settings.playerDiameter + Settings.starRadius1) / 2) {
			collectSound.play(0.5f);
			stars++;
			score++;
			combo++;
			if(combo == Settings.starSpikes) {
				timer = 1;
				stars += combo;
				score += combo;

				starsText.addAction(Actions.sequence(
						Actions.parallel(Actions.alpha(1), Actions.fadeOut(Settings.duration), Actions.moveBy(0, 25 * ratio, Settings.duration)),
						Actions.run(() -> {
							combo -= Settings.starSpikes;
							starsText.reset();
						})));
			}

			checkCurrentGoal();

			preferences.putInteger(Settings.STARS, stars).flush();
			star.pos = null;
		}
	}

	private void drawStars() {
		comboStar.fillFraction = (float) combo / Settings.starSpikes;
		if(!started || combo == Settings.starSpikes) {
			batch.begin();
			font75.setColor(starsText.getColor());
			font75.draw(batch, combo == Settings.starSpikes ? "+5" : String.valueOf(stars), starsText.getX(), starsText.getY(), 100 * ratio, Align.right, false);
			batch.end();

			comboStar.fillFraction = 1;
		}
		comboStar.draw(sR);
	}

	private void drawScore() {
		batch.begin();
		font250.setColor(Settings.colorText);
		font75.setColor(Settings.colorText);
		font250.draw(batch, String.valueOf(score), 0, Gdx.graphics.getHeight() - 400 * ratio + scoreTextSize, Gdx.graphics.getWidth(), Align.center, false);
		if(!started)
			font75.draw(batch, String.valueOf(highScore), 0, Gdx.graphics.getHeight() - 650 * ratio + scoreTextSize, Gdx.graphics.getWidth(), Align.center, false);
		batch.end();
	}

	private void drawBackground() {
		float fraction = (float) score / goals[currentGoal];
		if(currentGoal > 0)
			fraction = (float) (score - goals[currentGoal - 1]) / (float) (goals[currentGoal] - goals[currentGoal - 1]);

		Utils.hexagon(sR, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio, Settings.hexagonSize);
		Utils.hexagon(sR, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio, Settings.hexagonSize, fraction, true);
		if(!started) {
			Utils.hexagon(sR, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150 * ratio, Settings.playerDiameter);

			if(!questDone)
				drawQuestIcon(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150 * ratio, Settings.playerDiameter);
		} if(timer != 0)
			Utils.hexagonOutline(sR, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio, Settings.hexagonSize, timer);

		if(!started || timer != 0) {
			float xOffset = 120 * ratio * (1 + MathUtils.cos(MathUtils.PI2 / 6f)) + 20 * ratio;
			float yOffset = 2 * 120 * ratio * MathUtils.cos(MathUtils.PI2 / 6f) + 20 * ratio;

			Utils.hexagon(sR, Gdx.graphics.getWidth() / 2 - xOffset, 400 * ratio - yOffset, Settings.playerDiameter, selectedPerk == Perk.FREEZE);
			Utils.hexagon(sR, Gdx.graphics.getWidth() / 2, 400 * ratio, Settings.playerDiameter, selectedPerk == Perk.SPEED);
			Utils.hexagon(sR, Gdx.graphics.getWidth() / 2 + xOffset, 400 * ratio - yOffset, Settings.playerDiameter, selectedPerk == Perk.SIZE);

			Perk.drawPerk(sR, batch, font30, Gdx.graphics.getWidth() / 2 - xOffset, 400 * ratio - yOffset, Settings.playerDiameter, Perk.FREEZE);
			Perk.drawPerk(sR, batch, font30, Gdx.graphics.getWidth() / 2, 400 * ratio, Settings.playerDiameter, Perk.SPEED);
			Perk.drawPerk(sR, batch, font30, Gdx.graphics.getWidth() / 2 + xOffset, 400 * ratio - yOffset, Settings.playerDiameter, Perk.SIZE);

			//Add speed perk particles effect
			if(!started || timer != 0) {
				Vector2 ballPos = new Vector2(Gdx.graphics.getWidth() / 2f + Settings.playerDiameter / 8, (float) 400 * ratio + 2 * Settings.playerDiameter / 15);
				Color dark = Settings.colorHex2.cpy();
				for(int i = 0; i < 1; i++) {
					Vector2 pos = ballPos.cpy().add(new Vector2(Settings.playerDiameter / 8, 0).setAngle(-135f)).add(new Vector2(1, 0).setToRandomDirection().setLength(Settings.playerDiameter / 8));
					float lerp = MathUtils.random(0f, 1f);
					float size = MathUtils.random(2.5f * ratio, 3.5f * ratio);
					Vector2 speed = new Vector2(1, 0).setAngle(-135).setLength(MathUtils.random(1f * ratio, 3f * ratio));
					Vector2 acc = new Vector2(speed.cpy().scl(-MathUtils.random(0f, 1f)));
					Particle p = new Particle(pos, dark.cpy().lerp(Settings.colorHex1, lerp), new Alpha(0), size)
							.colorful(dark.cpy().lerp(Settings.colorHex1, lerp))
							.shrinking(MathUtils.random(0f, 2f * ratio))
							.fading(new Alpha(0))
							.living(0.2f);
					p.speed = speed;
					p.acc = acc;
					particles.add(p);
				}
			}

			if(selectedPerk != -1 && !started) {
				String prize = Perk.level[selectedPerk] < Perk.maxLevel ? String.valueOf(Perk.prize[selectedPerk][Perk.level[selectedPerk]]) : Settings.MAX;
				GlyphLayout fontGlyph = new GlyphLayout(font30, prize);
				float offset = 10;
				Star star = new Star(1, 126);
				star.scale = 0.4f;
				star.pos = new Vector2((Gdx.graphics.getWidth() + star.getBoundingRectangle().width + fontGlyph.width) / 2 + offset, 200 * ratio);
				star.draw(sR);

				batch.begin();
				font30.draw(batch, prize, (Gdx.graphics.getWidth() - star.getBoundingRectangle().width - fontGlyph.width) / 2 - offset, 200 * ratio + fontGlyph.height / 2, fontGlyph.width, Align.center, false);
				batch.end();
			}
		}
	}

	private void drawQuestIcon(int x, float y, float r) {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(Settings.colorIcon);
		sR.circle(x + r * 5 / 8f, y + r * 5 / 8f, r / 6f);
		sR.setColor(Settings.colorBackground);
		sR.rectLine(x + r * 5 / 8f, y + r * 5 / 8f + r / 12f, x + r * 5 / 8f, y + r * 5 / 8f - r / 40f, r / 40f);
		sR.circle(x + r * 5 / 8f, y + r * 5 / 8f - r / 16f, r / 40f);
		sR.end();
	}

	private void showQuest() {
		questText.addAction(Actions.parallel(Actions.alpha(1, Settings.duration), Actions.moveBy(0, -50, Settings.duration, new Interpolation.ExpOut(2, 3))));
	}

	private void drawBorders() {
		sR.begin(ShapeRenderer.ShapeType.Filled);
		sR.setColor(Settings.colorBorder);
		sR.rect(0, 0, wall.size.x, Gdx.graphics.getHeight());
		sR.rect(Gdx.graphics.getWidth() - wall.size.x, 0, wall.size.x, Gdx.graphics.getHeight());
		sR.end();
	}

	private void handleInput() {
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector2 pos = new Vector2(screenX, screenY);
//				if(!started && isPlayerClicked(pos)) {
//					showQuest();
//					return true;
//				}

				int perkSelection = checkPerkSelection(pos);
				if((perkSelection != -1 || perkSelection != selectedPerk) && (timer != 0 || !started)) {
					if(timer == 0 || perkSelection != -1) {
						if(perkSelection != -1 && perkSelection == selectedPerk) {
							if(timer != 0) timer = 0.01f;
							int prize = Perk.prize[perkSelection][Perk.level[perkSelection]];
							if(!started && stars >= prize && Perk.level[perkSelection] < Perk.maxLevel) {
								timer = 0.01f;
								stars -= prize;
								Perk.level[perkSelection] = MathUtils.clamp(Perk.level[perkSelection] + 1, 0, Perk.maxLevel);
								preferences.putInteger(Settings.STARS, stars).flush();
								Perk.savePerks(preferences);
							} else pick2Sound.play(0.5f);
						} else if(perkSelection != -1)
							pickSound.play(0.5f);
						selectedPerk = perkSelection;
						return false;
					}
				}
				if(timer == 0) {
					if(!started) {
						started = true;
						score = 0;
						currentGoal = 0;
					} else player.switchColor();
				}
				if(player.acc.y < 0) player.acc.y *= -1;
				return super.touchDown(screenX, screenY, pointer, button);
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if(player.acc.y > 0) player.acc.y *= -1;
				return super.touchUp(screenX, screenY, pointer, button);
			}
		});
	}

	private boolean isPlayerClicked(Vector2 pos) {
		return new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150 * ratio).dst(pos) <= Settings.playerDiameter;
	}

	private int checkPerkSelection(Vector2 pos) {
		float xOffset = 120 * ratio * (1 + MathUtils.cos(MathUtils.PI2 / 6f)) + 20 * ratio;
		float yOffset = 2 * 120 * ratio * MathUtils.cos(MathUtils.PI2 / 6f) + 20 * ratio;
		Vector2[] perks = new Vector2[3];
		perks[0] = new Vector2(Gdx.graphics.getWidth() / 2 - xOffset, Gdx.graphics.getHeight() - 400 * ratio + yOffset);
		perks[1] = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 400 * ratio);
		perks[2] = new Vector2(Gdx.graphics.getWidth() / 2 + xOffset, Gdx.graphics.getHeight() - 400 * ratio + yOffset);

		for(int i = 0; i < perks.length; i++)
			if(perks[i].dst(pos) <= Settings.playerDiameter) return i;
		return -1;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		font30.dispose();
		font75.dispose();
		font150.dispose();
		font250.dispose();
		batch.dispose();
		sR.dispose();
		hitSound.dispose();
		crashSound.dispose();
		collectSound.dispose();
		pickSound.dispose();
		pick2Sound.dispose();
		powerupSound.dispose();
	}
}