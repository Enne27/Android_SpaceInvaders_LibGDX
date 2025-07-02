package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class EnemyBullet {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean active = true;

    public EnemyBullet(Texture texture, float x, float y, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta, float speed) {
        sprite.translateY(-speed * delta); // Los disparos de los enemigos van hacia abajo
        bounds.setPosition(sprite.getX(), sprite.getY());

        if (sprite.getY() < 0) { // Si la bala sale de la pantalla
            active = false;
        }
    }
}
