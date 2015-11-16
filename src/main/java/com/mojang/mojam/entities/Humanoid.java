package com.mojang.mojam.entities;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.ai.Behavior;
import com.mojang.mojam.ai.BehaviorComparator;
import com.mojang.mojam.ai.actions.Action;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

import java.util.Set;
import java.util.TreeSet;

public abstract class Humanoid extends Entity {
    protected final Set<Behavior> behaviors = new TreeSet<Behavior>(new BehaviorComparator());

    public Humanoid(World world, Image image, Point startingPoint, int width, int height, Team team) {
        super(world, image, startingPoint, width, height, team);
    }

    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    private void applyCurrentBehavior() {
        for (Behavior behavior : behaviors) {
            Action action = behavior.getAction();
            if (action.isConditionMet(this)) {
                action.performAction(this);
                break;
            }
        }
    }

    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        applyCurrentBehavior();
        velocity.x += horizontalSpeed * 0.85f;
        move();
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public boolean isReadyForAttack() {
        return false;
    }
}
