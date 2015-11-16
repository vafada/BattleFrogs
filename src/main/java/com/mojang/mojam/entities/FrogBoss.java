package com.mojang.mojam.entities;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.ai.Behavior;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.ai.actions.enemy.MeleeAttack;
import com.mojang.mojam.ai.actions.enemy.StalkEntity;
import com.mojang.mojam.ai.actions.enemy.TadpoleAttack;
import com.mojang.mojam.ai.actions.enemy.TongueAttack;
import com.mojang.mojam.entities.projectiles.Tadpole;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

public class FrogBoss extends Enemy {
    public static final int ANIMATION_TYPE_IDLE = 0;
    public static final int ANIMATION_TYPE_ATTACK_START = 1;
    public static final int ANIMATION_TYPE_ATTACK_FINISH = 2;
    public static final int ANIMATION_COUNT = 3;

    public static final int[] ANIMATION_FRAME_COUNT = new int[] {
            6, // idle
            6, // attack_start
            2, // attack_finish
    };

    public static final int[] ANIMATION_FRAME_RATE = new int[] {
            166, // idle
            83, // attack_start
            83, // finish
    };

    public static final Point[] PROJECTILE_COORDINATES = new Point[] {
            new Point(442, 191),
            new Point(477, 243),
            new Point(449, 299),
            new Point(410, 252),
    };

    public static final int ATTACK_MISSILE_WAIT = 150;
    public static final int ATTACK_CONTINUE_TIME = ATTACK_MISSILE_WAIT * (PROJECTILE_COORDINATES.length + 1);

    private static final int STALKING_SPEED = 6;
    private static final int MELEE_DAMAGE = 5;

    protected int animType = 0;
    protected Animation[] animations = new Animation[ANIMATION_COUNT];
    protected int missileIndex = 0;

    private final Sound deathSound;

    public FrogBoss(World world, Point point, Team opposingTeam) throws SlickException {
        super(world, new Image("graphics/frog_boss.png"), point, 700, 500);
        fullHealth = 300;
        currentHealth = fullHealth;

        for (int type = 0; type < ANIMATION_COUNT; type++) {
            Animation anim = new Animation(animationSheet, 0, type, ANIMATION_FRAME_COUNT[type] - 1, type, true, ANIMATION_FRAME_RATE[type], true);
            animations[type] = anim;
        }

        animations[ANIMATION_TYPE_IDLE].setPingPong(true);
        animations[ANIMATION_TYPE_ATTACK_START].setLooping(false);
        animations[ANIMATION_TYPE_ATTACK_FINISH].setLooping(false);

        if (isAttacking && !wasAttacking) {
            missileIndex = 0;
        }

        addBehavior(new Behavior(1, new MeleeAttack(opposingTeam, MELEE_DAMAGE)));
        addBehavior(new Behavior(2, new TadpoleAttack(opposingTeam)));

        deathSound = new Sound("sounds/Frog_Boss_Exploding.wav");
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        animations[animType].update(deltaTime);
        super.update(gameContainer, deltaTime);

        if (isAttacking) {
            long delta = world.getGame().getGameContainer().getTime() - attackStart - getAttackOffsetTime();
            if (delta >= ATTACK_MISSILE_WAIT * missileIndex && missileIndex < PROJECTILE_COORDINATES.length) {
                Tadpole tadpole = new Tadpole(this, getTadpoleAttack());
                getWorld().addEntity(tadpole);
                missileIndex++;
            }
        }
    }

    protected TadpoleAttack getTadpoleAttack() {
        for (Behavior behavior : behaviors) {
            if (behavior.getAction() instanceof TadpoleAttack) return (TadpoleAttack) behavior.getAction();
        }

        return null;
    }

    @Override
    protected void cycleAttackAnimation() {
        animations[animType].restart();
        missileIndex = 0;
    }

    @Override
    protected void stopAttackAnimation() {
        animType = ANIMATION_TYPE_IDLE;
        animations[animType].restart();
        missileIndex = 0;
    }

    @Override
    protected long getAttackOffsetTime() {
        return ANIMATION_FRAME_RATE[ANIMATION_TYPE_ATTACK_START] * ANIMATION_FRAME_COUNT[ANIMATION_TYPE_ATTACK_START];
    }

    @Override
    protected long getAttackTime() {
        return (ANIMATION_FRAME_RATE[ANIMATION_TYPE_ATTACK_START] * ANIMATION_FRAME_COUNT[ANIMATION_TYPE_ATTACK_START]) +
                ATTACK_CONTINUE_TIME +
                (ANIMATION_FRAME_RATE[ANIMATION_TYPE_ATTACK_FINISH] * ANIMATION_FRAME_COUNT[ANIMATION_TYPE_ATTACK_FINISH]);
    }

    @Override
    protected void updateAnimation() {
        if (isAttacking) {
            long delta = world.getGame().getGameContainer().getTime() - attackStart;
            int oldAnim = animType;

            if (!hasAttackHappened) {
                animType = ANIMATION_TYPE_ATTACK_START;
            } else if (delta >= getAttackOffsetTime() + ATTACK_CONTINUE_TIME) {
                animType = ANIMATION_TYPE_ATTACK_FINISH;
            }

            if (oldAnim != animType) animations[animType].restart();
        } else if (!wasAttacking) {
            animType = ANIMATION_TYPE_IDLE;
            animations[animType].restart();
        }
    }

    @Override
    public void render(Camera camera, GameContainer gameContainer) {
        Graphics graphics = gameContainer.getGraphics();
        graphics.pushTransform();
        graphics.translate(0, 40f);
        super.render(camera, gameContainer);
        graphics.popTransform();

        if (BattleToads.ALLOW_DEBUGGING) graphics.setColor(Color.magenta);
        if (BattleToads.ALLOW_DEBUGGING) graphics.draw(getCollisionHitbox(position));
    }

    @Override
    protected int getImageX0(Image image) {
        return facing == FACING_LEFT ? image.getWidth() : 0;
    }

    @Override
    protected int getImageX1(Image image) {
        return facing == FACING_LEFT ? 0 : image.getWidth();
    }

    @Override
    protected Image getFrame() {
        return animations[animType].getCurrentFrame();
    }

    public int getMissileIndex() {
        return missileIndex;
    }

    @Override
    public int getHealthMax() {
        return super.getHealthMax();
    }

    @Override
    protected void die() throws SlickException {
        super.die();
        world.setNumberOfBosses(world.getNumberOfBosses() - 1);
        SoundManager.getInstance().playSoundEffect(deathSound);

        if (world.getState() == World.State.ENGINES_ON && !world.hasPirates()) {
            world.setState(World.State.WIN);
        }
    }
}
