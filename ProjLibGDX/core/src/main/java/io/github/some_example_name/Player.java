package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Player {
    public Sprite sprite;
    public Rectangle bounds;

    public Player(Texture texture, Vector2 position, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(position.x, position.y);
        bounds = new Rectangle(position.x, position.y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

   /* public void updateBounds() {
        bounds.setPosition(sprite.getX(), sprite.getY());
    }

    public void move(float x) {
        sprite.setX(x);
        updateBounds();
    }
*/
    public void clampToWorld(float worldWidth) {
        float clampedX = Math.max(0, Math.min(sprite.getX(), worldWidth - sprite.getWidth()));
        sprite.setX(clampedX);
        //updateBounds();
    }
}
