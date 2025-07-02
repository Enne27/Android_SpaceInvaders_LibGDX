package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    final MainGame game;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final Music music;
    private final Sound shotSound, dieSound, killerSound;

    private final Player player;
    private final Texture playerTexture, bulletTexture, enemyTexture, megaBossTexture, backgroundTexture, bulletEnemy, meatShieldTexture;

    private final Array<Bullet> bullets;
    private final Array<Enemy> enemies;
    private Enemy megaBoss; private int megaBossLives = 10; boolean megaBossTime = false;
    private final Array<BossBullet> bossBullets;

    private PowerUp shieldPowerUp;
    private boolean shieldActive = false;
    private float shieldTimer = 0f;

    Vector2 touchPos;

    private float enemyTimer, gameOverTimer;
    private final Array<EnemyBullet> enemyBullets;
    private float enemyShootTimer = 0f;
    private final int maxEnemies = 20;
    private int instancedEnemies = 0;
    private int enemiesDestroyed = 0;
    private int points;
    private int lives;
    private  float shootTimer = 0f;
    float bossShootTimer = 0f;
    float bossShootCooldown = 2f; // cada 2 segundos

    private boolean doubleShot = false;
    private boolean powerUpSpawned = false;
    boolean powerUpShieldSpawned = false;
    private  PowerUp powerUp;
    private final Texture powerUpTexture;

    private BitmapFont font;
    FileHandle fileHandle;
    FreeTypeFontGenerator generator;
    private Stage stage;

    private boolean enemiesMovingRight = true;
    private final Texture bulletBossTexture;

    private boolean playerBlinking = false;
    private float blinkTimer = 0f;
    private float blinkStateTimer = 0f;
    private float rotateTimer = 0f;
    private boolean playerRotating = false;

    public GameScreen(final MainGame game) {
        this.game = game;
        this.batch = game.batch;

        viewport = new FitViewport(8, 5);

        playerTexture = new Texture("characters/ace.png");
        bulletTexture = new Texture("shots/mera_mera_no_mi.png");
        enemyTexture = new Texture("characters/garp.png");
        megaBossTexture = new Texture("characters/luffy_boss.png");
        backgroundTexture = new Texture("backgrounds/ace_fondo.jpeg");
        bulletBossTexture = new Texture("shots/luffy_carne.jpg");

        float playerWidth = 1f;
        float playerHeight = 1f;
        player = new Player(playerTexture, new com.badlogic.gdx.math.Vector2(4 - playerWidth / 2, 0.2f), playerWidth, playerHeight);
        touchPos = new Vector2();
        player.sprite.setOriginCenter();

        meatShieldTexture = new Texture("shots/luffy_carne.jpg");
        bulletEnemy = new Texture("shots/cannon_ball.png");
        bossBullets = new Array<>();
        bullets = new Array<>();
        enemyBullets = new Array<>();
        enemies = new Array<>(maxEnemies);

        this.music = game.playMusic("audio/music/lullaby_uta.mp3");
        this.shotSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/gun_shot.mp3"));
        this.dieSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/game_die.mp3"));
        this.killerSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/cinematic_boom.mp3"));

        powerUpTexture = new Texture("shots/mera_mera_no_mi.png");

        points = 0;
        lives = 3;

        try {
            fileHandle = Gdx.files.internal("fonts/one_piece.ttf");

            generator = new FreeTypeFontGenerator(fileHandle);

            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.color = Color.WHITE;

            font = generator.generateFont(parameter);
            font.setUseIntegerPositions(false); // IMPORTANTÍSIMO
            generator.dispose();

        } catch (Exception e) {
            Gdx.app.error("FONT", "Error cargando fuente: " + e.getMessage());
            font = new BitmapFont();
            font.setColor(1, 1, 1, 1); // Podemos poner el color que queramos
            font.setUseIntegerPositions(false);
        }
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight() * 10);
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateLogic(delta);

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.setColor(1, 1, 1, 1); // Sin transparencia
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        player.sprite.draw(batch);

        for (Bullet b : bullets) b.sprite.draw(batch);
        for (Enemy e : enemies) e.sprite.draw(batch);
        for (EnemyBullet eb : enemyBullets) eb.sprite.draw(batch);


        if (powerUp != null && powerUp.active) {
            powerUp.sprite.draw(batch);
        }

        if(megaBossTime && megaBoss != null && megaBoss.active) {
            megaBoss.sprite.draw(batch);
            for (BossBullet bb : bossBullets) bb.sprite.draw(batch);
        }

        if (shieldPowerUp != null && shieldPowerUp.active) {
            shieldPowerUp.sprite.draw(batch);
        }
        if (shieldActive && megaBoss != null && megaBoss.active) {
            batch.setColor(1, 1, 1, 0.4f);
            batch.draw(meatShieldTexture, player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
            batch.setColor(1, 1, 1, 1);
        }

        font.draw(batch, "Score: " + points + "   Lives: " + lives, 0.5f, 4.8f);
        batch.end();
    }

    private void handleInput() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { // No son las teclas normales, porque no van.
            player.sprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.sprite.translateX(-speed * delta);
        }

        float shootCooldown = 0.4f;
        if (Gdx.input.isTouched()) { // El ratón y el input de móvil supongo
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            player.sprite.setCenterX(touchPos.x);

            shootTimer += delta;
            if(shootTimer >= shootCooldown){
                shootTimer = 0;

                float bulletY = player.sprite.getY() + player.sprite.getHeight();

                if (doubleShot) {
                    // Disparo doble (dos balas separadas horizontalmente)
                    float leftX = player.sprite.getX() + player.sprite.getWidth() * 0.25f - 0.2f;
                    float rightX = player.sprite.getX() + player.sprite.getWidth() * 0.75f - 0.2f;

                    bullets.add(new Bullet(bulletTexture, leftX, bulletY, 0.3f, 0.4f));
                    bullets.add(new Bullet(bulletTexture, rightX, bulletY, 0.3f, 0.4f));
                } else {
                    // Disparo simple (central)
                    float bulletX = player.sprite.getX() + player.sprite.getWidth() / 2 - 0.2f;
                    bullets.add(new Bullet(bulletTexture, bulletX, bulletY, 0.4f, 0.5f));
                }

                shotSound.play(game.sfxVolume);
            }
        } else {
            shootTimer = shootCooldown;
        }

        player.clampToWorld(viewport.getWorldWidth());
    }

    private void updateLogic(float delta) {
        for (Bullet bullet : bullets) bullet.update(delta, 5f);

        for (EnemyBullet enemyBullet : enemyBullets) enemyBullet.update(delta, 3f);

        // enemigos movimiento
        boolean shouldDescend = false;
        for (Enemy enemy : enemies) {
            if (!enemy.active) continue;
            if ((enemiesMovingRight && enemy.sprite.getX() + enemy.sprite.getWidth() >= viewport.getWorldWidth()) ||
                (!enemiesMovingRight && enemy.sprite.getX() <= 0)) {
                shouldDescend = true;
                enemiesMovingRight = !enemiesMovingRight;
                break;
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.active) continue;

            if (shouldDescend) {
                float enemyDescendStep = 0.05f;
                enemy.sprite.translateY(-enemyDescendStep); // baja todos
            } else {
                float dir = enemiesMovingRight ? 1 : -1;
                float enemyGroupSpeed = 1f;
                enemy.sprite.translateX(dir * delta * enemyGroupSpeed); // se mueven horizontalmente
            }

            enemy.bounds.setPosition(enemy.sprite.getX(), enemy.sprite.getY());

            if (enemy.sprite.getY() < -enemy.sprite.getHeight()) {
                enemy.active = false;
            }
        }

        // Limpiar balas inactivas
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).active) bullets.removeIndex(i);
        }

        // Limpiar enemigos inactivos
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).active) enemies.removeIndex(i);
        }

        // Limpiar balas enemigas inactivas
        for (int i = enemyBullets.size - 1; i >= 0; i--) {
            if (!enemyBullets.get(i).active) enemyBullets.removeIndex(i);
        }

        // Eliminar enemigos que se caen del escenario
        for (Enemy enemy : enemies) {
            if (enemy.sprite.getY() + enemy.sprite.getHeight() <= player.sprite.getHeight() && enemy.active) {
                enemy.active = false;
                enemiesDestroyed++;
                Gdx.app.log("ENEMY", "Un enemigo se cayó del escenario. Total eliminados: " + enemiesDestroyed);
            }
        }

        checkCollisions(delta);

        // Spawnear enemigos en grupos
        enemyTimer += delta;
        if (enemyTimer >= 1.5f) {
            enemyTimer = 0;
            if (instancedEnemies < maxEnemies) {
                int rows = 2;
                int cols = 5;
                float spacingX = 1.2f;
                float spacingY = 1f;
                float startX = 0.5f;
                float startY = viewport.getWorldHeight() - 1.5f;

                for (int row = 0; row < rows && instancedEnemies < maxEnemies; row++) {
                    for (int col = 0; col < cols && instancedEnemies < maxEnemies; col++) {
                        float x = startX + col * spacingX;
                        float y = startY - row * spacingY;

                        enemies.add(new Enemy(enemyTexture, x, y, 1f, 1f));
                        instancedEnemies++;
                    }
                }
            }
        }

        // Disparo enemigo aleatorio
        enemyShootTimer += delta;
        float enemyShootCooldown = 1f;
        if (enemyShootTimer >= enemyShootCooldown) {
            enemyShootTimer = 0f;
            if (enemies.size > 0) {
                Enemy randomEnemy = enemies.random();
                float bulletX = randomEnemy.sprite.getX() + randomEnemy.sprite.getWidth() / 2 - 0.1f;
                float bulletY = randomEnemy.sprite.getY();
                enemyBullets.add(new EnemyBullet(bulletEnemy, bulletX, bulletY, 0.5f, 0.5f));
            }
        }

        if (powerUp != null && powerUp.active) {
            powerUp.update(delta, 2f);

            if (powerUp.bounds.overlaps(player.getBounds())) {
                powerUp.active = false;
                doubleShot = true;
                Gdx.app.log("POWERUP", "¡Doble disparo activado!");
            }
        }

        if (shieldPowerUp != null && shieldPowerUp.active) {
            shieldPowerUp.update(delta, 2f);
            if (shieldPowerUp.bounds.overlaps(player.getBounds())) {
                shieldPowerUp.active = false;
                shieldActive = true;
                shieldTimer = 0f;
                Gdx.app.log("SHIELD", "¡Escudo del MegaBoss activado!");
            }
        }
        if (shieldActive) {
            shieldTimer += delta;
            float shieldDuration = 5f;
            if (shieldTimer >= shieldDuration) {
                shieldActive = false;
                Gdx.app.log("SHIELD", "Escudo del MegaBoss desactivado.");
            }
        }

        megaBoss(delta);

        if (playerBlinking) {
            blinkTimer += delta;
            blinkStateTimer += delta;

            float blinkInterval = 0.1f;
            if (blinkStateTimer >= blinkInterval) {
                blinkStateTimer = 0f;
                // Alternar entre visible/invisible
                player.sprite.setAlpha(player.sprite.getColor().a == 1 ? 0 : 1);
            }

            float blinkDuration = 1f;
            if (blinkTimer >= blinkDuration) {
                playerBlinking = false;
                player.sprite.setAlpha(1); // Asegurarse de que termine visible
            }
        }
        if (playerRotating) {
            rotateTimer += delta;
            float rotationSpeed = 180f;
            player.sprite.rotate(rotationSpeed * delta);
            // duración de la animación
            float rotateDuration = 2f;
            if (rotateTimer >= rotateDuration) {
                playerRotating = false;
                player.sprite.setRotation(0); // resetear
            }
        }
    }

    private void megaBoss(float delta){
        for (BossBullet bb : bossBullets) bb.update(delta, 0.7f);

        for (int i = bossBullets.size - 1; i >= 0; i--) {
            if (!bossBullets.get(i).active) {
                bossBullets.removeIndex(i);
            }
        }

        if (!megaBossTime && enemiesDestroyed >= maxEnemies) {
            megaBossTime = true;
            megaBoss = new Enemy(megaBossTexture, viewport.getWorldWidth() / 2, viewport.getWorldHeight() - 3, 3f, 3f);
            megaBoss.active = true;
            if(!powerUpShieldSpawned) {
                float x = MathUtils.random(1f, 6f);
                float y = viewport.getWorldHeight();
                shieldPowerUp = new PowerUp(powerUpTexture, x, y, 0.6f, 0.6f);
                powerUpShieldSpawned = true;
            }
        }
        if (megaBossTime && megaBoss != null && megaBoss.active) {
            float speed = 1.5f;
            megaBoss.sprite.translateX(speed * delta * (enemiesMovingRight ? 1 : -1));

// Rebota en los bordes del mundo
            if (megaBoss.sprite.getX() <= 0 || megaBoss.sprite.getX() + megaBoss.sprite.getWidth() >= viewport.getWorldWidth()) {
                enemiesMovingRight = !enemiesMovingRight;
            }
            float bossX = MathUtils.clamp(megaBoss.sprite.getX(), 0, viewport.getWorldWidth() - megaBoss.sprite.getWidth());
            megaBoss.sprite.setX(bossX);

            bossShootTimer += delta;
            if (bossShootTimer >= bossShootCooldown) {
                bossShootTimer = 0f;

                // Disparo en dos direcciones diagonales
                Vector2[] directions = {
                    new Vector2(-1, -1), // diagonal izquierda abajo
                    new Vector2(1, -1),  // diagonal derecha abajo
                    new Vector2(-0.5f, -1), // más centrado
                    new Vector2(0.5f, -1)
                };

                for (Vector2 dir : directions) {
                    float x = megaBoss.sprite.getX() + megaBoss.sprite.getWidth() / 2;
                    float y = megaBoss.sprite.getY() + megaBoss.sprite.getHeight() / 2;
                    bossBullets.add(new BossBullet(bulletBossTexture, x, y, 0.4f, 0.4f, dir, 3f));
                }
            }
        }
    }

    private void checkCollisions(float delta) {
        // Colisiones entre balas del jugador y enemigos
        for (Bullet b : bullets) {
            for (Enemy e : enemies) {
                if (b.active && e.active && b.bounds.overlaps(e.bounds)) {
                    b.active = false;
                    e.active = false;
                    points++;
                    enemiesDestroyed++;
                    killerSound.play(game.sfxVolume);

                    if (enemiesDestroyed == maxEnemies / 2 && !powerUpSpawned) {
                        float x = player.sprite.getX(); // Puedes poner posición aleatoria si prefieres
                        float y = viewport.getWorldHeight();
                        powerUp = new PowerUp(powerUpTexture, x, y, 0.6f, 0.6f);
                        powerUpSpawned = true;
                    }
                }
            }
        }

        // Colisiones entre balas de enemigos y el jugador
        for (EnemyBullet eBullet : enemyBullets) {
            if (eBullet.active && eBullet.bounds.overlaps(player.getBounds())) {
                eBullet.active = false;
                lives--;
                startPlayerBlink();
                dieSound.play(game.sfxVolume);
            }
        }

        // Colisiones entre enemigos y el jugador
        for (Enemy e : enemies) {
            if (e.active && e.bounds.overlaps(player.getBounds())) {
                e.active = false;
                if (!shieldActive) {
                    lives--;
                    startPlayerBlink();
                    dieSound.play(game.sfxVolume);
                }
            }
        }

        //balas boss contra jugador
        for (BossBullet bb : bossBullets) {
            if (bb.active && bb.bounds.overlaps(player.getBounds())) {
                bb.active = false;
                if (!shieldActive) {
                    lives--;
                    startPlayerBlink();
                    dieSound.play(game.sfxVolume);
                }
            }
        }

        if (lives <= 0) {
            game.winwin = false;
            game.setScreen(new FinalScreen(game));  // Mostrar pantalla final
        }

        if (megaBossTime && megaBoss != null && megaBoss.active) {
            for (Bullet b : bullets) {
                if (b.active && b.bounds.overlaps(megaBoss.bounds)) {
                    b.active = false;
                    megaBossLives--;
                    killerSound.play(game.sfxVolume);
                }
            }
        }

        if (megaBossLives <= 0) {
            if (megaBoss != null) {megaBoss.active = false;}
            playerRotating = true;
            rotateTimer = 0f;
            game.winwin = true;
            gameOverTimer += delta;
            if (gameOverTimer >= 2f) { // espera 2 segundos
                game.setScreen(new FinalScreen(game)); // o el que sea tu final
            }
        }
    }

    private void startPlayerBlink() {
        playerBlinking = true;
        blinkTimer = 0f;
        blinkStateTimer = 0f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }
    @Override public void hide() {music.stop();}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        playerTexture.dispose();
        bulletTexture.dispose();
        enemyTexture.dispose();
        backgroundTexture.dispose();
        shotSound.dispose();
        font.dispose();
        powerUpTexture.dispose();
        music.dispose();
        dieSound.dispose();
        killerSound.dispose();
        if (stage != null)stage.dispose();
    }
}
