package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BossBullet {
    public Sprite sprite;
    public Rectangle bounds;
    private Vector2 velocity;
    public boolean active = true;

    public BossBullet(Texture texture, float x, float y, float width, float height, Vector2 direction, float speed) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        bounds = new Rectangle(x, y, width, height);
        this.velocity = direction.nor().scl(speed);
    }

    public void update(float delta, float speed) {
        sprite.translate(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(sprite.getX(), sprite.getY());

        // Condición para desactivar si se va fuera de pantalla
        if (sprite.getY() < 0 || sprite.getY() > 10f || sprite.getX() < 0 || sprite.getX() > 10f) {
            active = false;
        }
    }
}
