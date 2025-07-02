package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean active = true;

    public Enemy(Texture texture, float x, float y, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta, float speed, float worldWith) {
        //sprite.translateY(-speed * delta / 10); // vertical

       /* if (movingRight) { // horizontal
            sprite.translateX(speed * delta);
        } else {
            sprite.translateX(-speed * delta);
        }

        // choque borde
        if (sprite.getX() <= 0 || sprite.getX() + sprite.getWidth() >= worldWith) {
            movingRight = !movingRight;
            sprite.translateY(-sprite.getHeight());
        }*/

        bounds.setPosition(sprite.getX(), sprite.getY());

        /*if (sprite.getY() < -sprite.getHeight()) {
            active = false;
        }*/
    }
}
