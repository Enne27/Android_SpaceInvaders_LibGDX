package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Stage stage;
    private final Skin skin;

    private final Slider volumeSlider;
    private final Slider musicSlider;

    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final GlyphLayout layout;

    private Music music;
    private Sound sfxClick; long idSfx;

    private final Texture backgroundTexture;

    public SettingsScreen(final MainGame game) {
        stage = new Stage(new ScreenViewport());
        batch = game.batch;
        this.layout = new GlyphLayout();
        Gdx.input.setInputProcessor(stage);

        // Skin básico para fuentes y botones
        skin = new Skin();
        BitmapFont uiFont = new BitmapFont(); // Fuente básica para los labels y botones
        skin.add("default-font", uiFont);

        this.music = game.playMusic("audio/music/tot_musica_uta.mp3");
        sfxClick = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/button_pressed.mp3"));

        /*game.assetManager.load("backgrounds/luffy_background.png", Texture.class);
        game.assetManager.finishLoading();*/
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/luffy_background.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/one_piece.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleFontParam.size = 120;
        titleFont = generator.generateFont(titleFontParam);

        FreeTypeFontGenerator.FreeTypeFontParameter uiFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        uiFontParam.size = 60;
        uiFont = generator.generateFont(uiFontParam);
        generator.dispose();

        // Cargar texturas
        Texture sliderBgTexture = new Texture(Gdx.files.internal("UI/slider_background.png"));
        Texture sliderKnobTexture = new Texture(Gdx.files.internal("UI/knob_slider.png"));

        Slider.SliderStyle customSliderStyle = new Slider.SliderStyle();
        customSliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));
        customSliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));

        // Sliders
        volumeSlider = new Slider(0f, 1f, 0.01f, false, customSliderStyle);
        musicSlider = new Slider(0f, 1f, 0.01f, false, customSliderStyle);

        volumeSlider.setValue(game.sfxVolume);
        musicSlider.setValue(game.musicVolume);

        volumeSlider.setPosition(Gdx.graphics.getWidth() / 2f - 250, 550);
        musicSlider.setPosition(Gdx.graphics.getWidth() / 2f - 250, 350);

        volumeSlider.setSize(1000, 50);
        musicSlider.setSize(1000, 50);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = uiFont;

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = uiFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("UI/button1.png"))));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("UI/button1.png"))));
        skin.add("default", buttonStyle);

        Label volumeLabel = new Label("SFX Volume", labelStyle);
        Label musicLabel = new Label("Music Volume", labelStyle);

        volumeLabel.setPosition(Gdx.graphics.getWidth() / 2f - 500, 550);
        musicLabel.setPosition(Gdx.graphics.getWidth() / 2f - 500, 350);

        // Botón de volver
        TextButton backButton = new TextButton("Back", skin);
        backButton.setSize(300, 200);
        backButton.setPosition(Gdx.graphics.getWidth() / 2f - 150, 50);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                idSfx = sfxClick.play();
                game.saveSettings();
                game.setScreen(new MainMenu(game));
            }
        });

        // Listeners para sliders
        volumeSlider.addListener(change -> {
            game.sfxVolume = volumeSlider.getValue();
            game.saveSettings();
            long sfxVolumeLong = (long) game.sfxVolume;
            sfxClick.setVolume(idSfx, sfxVolumeLong);
            return false;
        });

        musicSlider.addListener(event -> {
            game.musicVolume = musicSlider.getValue();
            game.saveSettings();
            music.setVolume(game.musicVolume);
            return false;
        });

        // Añadir todo al stage
        stage.addActor(volumeLabel);
        stage.addActor(volumeSlider);
        stage.addActor(musicLabel);
        stage.addActor(musicSlider);
        stage.addActor(backButton);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Título
        layout.setText(titleFont, "VOLUME CONFIGURATION");
        titleFont.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, 925);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        music.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        titleFont.dispose();
        music.dispose();
        sfxClick.dispose();
    }
}
