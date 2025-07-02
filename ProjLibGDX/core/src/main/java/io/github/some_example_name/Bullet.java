package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean active = true;

    public Bullet(Texture texture, float x, float y, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta, float speed) {
        sprite.translateY(speed * delta);
        bounds.setPosition(sprite.getX(), sprite.getY());

        if (sprite.getY() > 10f) { // fuera de la pantalla
            active = false;
        }
    }
}
