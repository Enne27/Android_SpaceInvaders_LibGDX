package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FinalScreen implements Screen {
    BitmapFont font;
    private final BitmapFont buttonFont;

    private final MainGame game;
    private final Music music;
    private final SpriteBatch batch;
    private final Texture background;
    private final GlyphLayout layout;

    private final Stage stage;
    FitViewport viewport;
    private final OrthographicCamera camera;
    private final Skin skin;
    private final TextButton exitButton, restartButton;

    public FinalScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/one_piece.ttf"));

        // Fuente para el título, tamaño más grande
        FreeTypeFontGenerator.FreeTypeFontParameter titleFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleFontParam.size = 48;  // tamaño mayor para que se vea bien
        this.font = generator.generateFont(titleFontParam);
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Fuente para botones
        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParam.size = 24;
        buttonFont = generator.generateFont(buttonParam);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        generator.dispose();

        this.background = new Texture("backgrounds/luffy_background.png");
        this.layout = new GlyphLayout();
        this.music = game.playMusic("audio/music/ado_new_genesis_uta.mp3");

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);  // viewport con tamaño lógico grande
        stage = new Stage(viewport);

        skin = new Skin();
        skin.add("default-font", buttonFont);
        Texture buttonTexture = new Texture(Gdx.files.internal("UI/button1.png"));
        NinePatch ninePatch = new NinePatch(buttonTexture, 24, 24, 24, 24);
        skin.add("button", ninePatch);

        // Crear el estilo de los botones
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = skin.newDrawable("button", Color.WHITE);
        buttonStyle.down = skin.newDrawable("button", Color.GRAY);
        skin.add("default", buttonStyle);

        exitButton = new TextButton("EXIT", skin);
        exitButton.pack();
        exitButton.setSize(exitButton.getWidth() * 0.4f, exitButton.getHeight() * 0.4f);
        exitButton.setScale(0.5f);

        exitButton.setPosition((viewport.getWorldWidth() - exitButton.getWidth()) / 2, 180);

        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        restartButton = new TextButton("RESTART", skin);
        restartButton.pack();
        restartButton.setSize(restartButton.getWidth() * 0.4f, restartButton.getHeight() * 0.4f);
        restartButton.setScale(0.5f);

        restartButton.setPosition((viewport.getWorldWidth() - restartButton.getWidth()) / 2, 80);

        restartButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

        stage.addActor(exitButton);
        stage.addActor(restartButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Texto grande y centrado
        if(!game.winwin) {
            layout.setText(font, "GAME OVER");
        } else {
            layout.setText(font, "VICTORY!");
        }
        float x = (viewport.getWorldWidth() - layout.width) / 2;
        float y = viewport.getWorldHeight() - 80;  // un poco debajo del tope

        font.draw(batch, layout, x, y);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        this.font.dispose();
        buttonFont.dispose();
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}
