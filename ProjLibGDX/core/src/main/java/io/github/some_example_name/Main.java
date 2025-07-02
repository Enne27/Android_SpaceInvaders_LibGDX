package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {



    /*private SpriteBatch batch;
    FitViewport viewport;
    Sound shotSound;
    Music music;
    Texture naveTexture;
    Texture backgroundTexture;
    Texture dropTexture;
    Sprite naveSprite;
    Vector2 touchPos;
    Array<Sprite> dropSprites;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    BitmapFont scoreText;
    FreeTypeFontGenerator generator;
    float dropTimer;
    int points;

    FileHandle fileHandle;


    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        // Cargamos las variables con nuestras imágenes y sonido deseados.
        naveTexture = new Texture("ace.png");
        backgroundTexture = new Texture("ace_fondo.jpeg");
        dropTexture = new Texture("mera_mera_no_mi.png");

        naveSprite = new Sprite(naveTexture);
        naveSprite.setSize(1, 1);

        shotSound = Gdx.audio.newSound(Gdx.files.internal("dropping_books.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("ado_new_genesis_uta.mp3"));
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();

        touchPos = new Vector2();
        dropSprites = new Array<>();

        bucketRectangle = new Rectangle(); // Colliders
        dropRectangle = new Rectangle();

        points = 0;

        try {
            //generator = new FreeTypeFontGenerator(Gdx.files.internal("one_piece.ttf"));
            fileHandle = Gdx.files.internal("one_piece.ttf");

            generator = new FreeTypeFontGenerator(fileHandle);

            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.color = Color.WHITE;

            scoreText = generator.generateFont(parameter);
            scoreText.setUseIntegerPositions(false); // IMPORTANTÍSIMO
            generator.dispose();

        } catch (Exception e) {
            Gdx.app.error("FONT", "Error cargando fuente: " + e.getMessage());
            scoreText = new BitmapFont();
            scoreText.setColor(1, 1, 1, 1); // Podemos poner el color que queramos
            scoreText.setUseIntegerPositions(false);
        }
        scoreText.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight() * 10);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // El equivalente al update.
        // Separamos el dibujo de la lógica y también de los inputs de usuario.
        input();
        logic();
        draw();
    }

    private void input(){
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { // No son las teclas normales, porque no van.
            naveSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            naveSprite.translateX(-speed * delta);
        }

        if (Gdx.input.isTouched()) { // El ratón y el input de móvil supongo
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            naveSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = naveSprite.getWidth();
        float bucketHeight = naveSprite.getHeight();

        naveSprite.setX(MathUtils.clamp(naveSprite.getX(), 0, worldWidth - bucketWidth));

        float delta = Gdx.graphics.getDeltaTime();
        bucketRectangle.set(naveSprite.getX(), naveSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i); // Sobrepasa el límite de la pantalla
            else if (bucketRectangle.overlaps(dropRectangle)) { // Si choca con el cubo
                dropSprites.removeIndex(i);
                shotSound.play();
                points++;
            }
        }

        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.setColor(1,1,1,0.7f);
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        naveSprite.draw(batch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(batch);
        }

        if (scoreText != null) {
            scoreText.draw(batch, "Points: " + points, 0.5f, worldHeight - 0.2f);
        } else {
            Gdx.app.error("FONT", "No se pudo cargar la fuente.");
        }

        batch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));;
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        naveTexture.dispose();
        scoreText.dispose();
    }*/
}
