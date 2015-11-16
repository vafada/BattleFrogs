package com.mojang.mojam.entities;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Random;

public class Reactor extends Entity {
    public static final int CONSOLE_ACTIVATION_TIME = 1000;
    private final Image radiationImage;
    private float radiation = 1;
    private float radiationPulse = 0;
    private final Rectangle consoleHitbox = new Rectangle(418, 440, 290, 225);
    private long enteredConsoleTime = 0;

    public Reactor(World world, Point startingPoint) throws SlickException {
        super(world, new Image("graphics/empty.png"), startingPoint, 1, 1, Team.SYSTEM);
        radiationImage = new Image("graphics/radiation.png");
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        if (frame % 5 == 0) {
            Random random = new Random();
            long life = gameContainer.getTime() - lifeStart;
            radiationPulse = (float) (random.nextGaussian() * Math.sin(life) * 0.05f);
        }

        if (radiation > 0) {
            List<Entity> entities = world.getCollidingEntities(getRadiationHitbox());
            Vector2f ourPos = new Vector2f(getPosition().getCenterX(), getPosition().getCenterY());
            float max = radiationImage.getWidth() * radiation * 3 / 2f;

            for (Entity entity : entities) {
                Vector2f entityPos = new Vector2f(entity.getPosition().getCenterX(), entity.getPosition().getCenterY());
                float amount = entityPos.distance(ourPos);
                if (amount > max) amount = max;
                amount /= max;
                amount = 1 - amount;

                entity.radiate(this, amount);
            }
        } else if (world.getState() == World.State.RADIATION_CLEARED) {
            List<Entity> entities = world.getCollidingEntities(consoleHitbox);
            boolean foundPlayer = false;

            for (Entity entity : entities) {
                if (entity instanceof Player) {
                    foundPlayer = true;
                    break;
                }
            }

            if (enteredConsoleTime > 0 && !foundPlayer) {
                enteredConsoleTime = 0;
            } else if (enteredConsoleTime == 0 && foundPlayer) {
                enteredConsoleTime = gameContainer.getTime();
            } else if (enteredConsoleTime > 0 && foundPlayer) {
                long delta = gameContainer.getTime() - enteredConsoleTime;
                if (delta >= CONSOLE_ACTIVATION_TIME) {
                    enteredConsoleTime = 0;

                    if (world.hasPirates()) {
                        world.setState(World.State.ENGINES_ON);
                    } else {
                        world.setState(World.State.WIN);
                    }
                }
            }
        }
    }

    public void shrinkRadiation() {
        radiation -= 0.35f;

        if (radiation <= 0) {
            radiation = 0;
            world.setState(World.State.RADIATION_CLEARED);
        }
    }

    public Circle getRadiationHitbox() {
        float width = radiationImage.getWidth();
        width *= radiation * 3;

        return new Circle(position.getX(), position.getY(), width / 2f);
    }

    @Override
    public void render(Camera camera, GameContainer gameContainer) {
        float x = position.getX();
        float y = position.getY();
        Graphics graphics = gameContainer.getGraphics();

        graphics.pushTransform();
        graphics.translate(x, y);
        graphics.scale(radiation * 3 + radiationPulse, radiation * 3 + radiationPulse);
        if (BattleToads.ALLOW_DEBUGGING) graphics.setColor(Color.yellow);
        if (BattleToads.ALLOW_DEBUGGING) graphics.draw(new Circle(0, 0, 250));
        radiationImage.draw(-radiationImage.getWidth() / 2, -radiationImage.getHeight() / 2);
        graphics.popTransform();
        if (BattleToads.ALLOW_DEBUGGING) graphics.setColor(Color.red);
        if (BattleToads.ALLOW_DEBUGGING) graphics.draw(getRadiationHitbox());
    }

    @Override
    public boolean ignoreCollision() {
        return true;
    }
}
