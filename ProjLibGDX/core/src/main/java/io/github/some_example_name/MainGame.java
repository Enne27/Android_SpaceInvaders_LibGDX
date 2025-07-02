package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    public float sfxVolume, musicVolume;
    public Preferences prefs;

    public boolean winwin;
    //public AssetManager assetManager; // Esta clase gestiona música, sonido, etc. y evita duplicados, como cosa interesante.


    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(12, 6);

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());


        //assetManager = new AssetManager();
        prefs = Gdx.app.getPreferences("SpaceInvadersSettings");
        loadSettings();
        this.setScreen(new MainMenu(this));
    }

    private void loadSettings() {
        musicVolume = prefs.getFloat("musicVolume", 1f);
        sfxVolume = prefs.getFloat("sfxVolume", 1f);
        Gdx.app.log("DEBUG", "Volumen de música cargado: " + musicVolume);
    }

    public void saveSettings(){
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putFloat("sfxVolume", sfxVolume);
        prefs.flush();
    }

    public Music playMusic(String path) {
        try {
            /*if (!assetManager.isLoaded(path)) {
                assetManager.load(path, Music.class);
                assetManager.finishLoading();
            }
            Music m = assetManager.get(path);*/
            Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
            m.setLooping(true);
            m.setVolume(musicVolume);
            m.play();
            return m;
        } catch (GdxRuntimeException e) {
            Gdx.app.error("Music Error", "No se pudo cargar: " + path, e);
            return null;
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();

        //assetManager.dispose();
        super.dispose();
    }
}
