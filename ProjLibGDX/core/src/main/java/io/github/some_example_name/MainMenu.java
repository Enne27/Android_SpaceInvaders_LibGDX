package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenu implements Screen {
    private final MainGame game;
    private final Music menuMusic; private  final Sound sfxClick;
    private final Texture background;

    private final GlyphLayout layout;
    private BitmapFont font;
    private final SpriteBatch batch;

    private final TextButton playButton;
    private final TextButton settingsButton;

    private Stage stage; // Para la UI
    private Skin skin; //Necesario para los botones

    @Override
    public void show() {
        menuMusic.setVolume(game.musicVolume);
    }

    public MainMenu(final MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.font = game.font;
        this.layout = new GlyphLayout();

        this.menuMusic = game.playMusic("audio/music/ado_new_genesis_uta.mp3");
        sfxClick = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/button_pressed.mp3"));


        /*game.assetManager.load("backgrounds/background_hat.png", Texture.class);
        game.assetManager.finishLoading();
        this.background = game.assetManager.get("backgrounds/background_hat.png", Texture.class);*/
        this.background = new Texture("backgrounds/background_hat.png");

        Gdx.app.log("DEBUG", "Volumen de SFX: " + game.sfxVolume);
        Gdx.app.log("DEBUG", "Volumen de música: " + menuMusic.getVolume());

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/one_piece.ttf"));

        // Fuente para los botones
        FreeTypeFontGenerator.FreeTypeFontParameter buttonFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonFontParam.size = 80;
        BitmapFont buttonFont = generator.generateFont(buttonFontParam);

        // Fuente para el título
        FreeTypeFontGenerator.FreeTypeFontParameter titleFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleFontParam.size = 30;
        BitmapFont titleFont = generator.generateFont(titleFontParam);

        generator.dispose();

        skin.add("default-font", buttonFont);
        skin.add("button", new Texture(Gdx.files.internal("UI/button1.png")));

        // Crear el estilo de los botones
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");

        // Asegurarnos de que el color del texto sea visible
        buttonStyle.fontColor = Color.WHITE;  // El color blanco debería funcionar bien

        // Configurar los botones para que tengan un fondo adecuado
        buttonStyle.up = skin.newDrawable("button", Color.WHITE);
        buttonStyle.down = skin.newDrawable("button", Color.GRAY);

        // Registrar el estilo con el nombre "default"
        skin.add("default", buttonStyle);

        // Usar la fuente grande para el título
        this.font = titleFont;

        // Crear botones
        playButton = new TextButton("PLAY", skin);
        settingsButton = new TextButton("SETTINGS", skin);

        // Posicionar botones
        playButton.setPosition(Gdx.graphics.getWidth() / 2f - 250, 300);
        settingsButton.setPosition(Gdx.graphics.getWidth() / 2f - 250, 120);
        playButton.setSize(500, 200);
        settingsButton.setSize(500, 200);


        // Agregar listener
        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                sfxClick.play(game.sfxVolume);
                menuMusic.stop();
                game.setScreen(new GameScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                sfxClick.play(game.sfxVolume);
                game.setScreen(new SettingsScreen(game));
                MainMenu.this.dispose();
            }
        });

        // Añadir al stage
        stage.addActor(playButton);
        stage.addActor(settingsButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Título
        font.getData().setScale(10f);
        layout.setText(font, "SPACE INVADERS");
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, 925);

        /*playButton.draw(batch, 1f);
        settingsButton.draw(batch, 1f);*/

        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        //Gdx.input.setInputProcessor(null);
        menuMusic.stop();
    }

    @Override
    public void dispose() {
        menuMusic.dispose();
        skin.dispose();
        stage.dispose();
        sfxClick.dispose();
    }
}
