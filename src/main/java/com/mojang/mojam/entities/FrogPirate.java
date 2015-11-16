package com.mojang.mojam.entities;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.ai.Behavior;
import com.mojang.mojam.ai.actions.enemy.*;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

public class FrogPirate extends Enemy {
    public static final int ANIMATION_TYPE_RUN = 0;
    public static final int ANIMATION_TYPE_ATTACK = 1;
    public static final int ANIMATION_TYPE_IDLE = 2;
    public static final int ANIMATION_COUNT = 3;

    public static final int[] ANIMATION_FRAME_COUNT = new int[] {
            7, // run
            10, // attack
            1, // idle
    };

    public static final int[] ANIMATION_FRAME_RATE = new int[] {
            83, // run
            83, // attack
            83, // idle
    };

    private static final int STALKING_SPEED = 6;
    private static final int MELEE_DAMAGE = 5;

    protected final SpriteSheet attackSheet;
    private final Point guardPoint;
    protected int animType = 0;
    protected Animation[] animations = new Animation[ANIMATION_COUNT];

    private final Sound landing;

    public FrogPirate(World world, Point point, Point guardPoint, Team opposingTeam) throws SlickException {
        super(world, new Image("graphics/frog_normal.png"), point, 388, 191);

        attackSheet = new SpriteSheet("graphics/frog_normal.png", 554, 191);

        for (int type = 0; type < ANIMATION_COUNT; type++) {
            SpriteSheet sheet = type == ANIMATION_TYPE_ATTACK ? attackSheet : animationSheet;
            Animation anim = new Animation(sheet, 0, type, ANIMATION_FRAME_COUNT[type] - 1, type, true, ANIMATION_FRAME_RATE[type], true);
            animations[type] = anim;
        }

        addBehavior(new Behavior(1, new MeleeAttack(opposingTeam, MELEE_DAMAGE)));
        addBehavior(new Behavior(2, new TongueAttack(opposingTeam)));
        addBehavior(new Behavior(5, new StalkEntity(opposingTeam, STALKING_SPEED)));
        this.guardPoint = guardPoint;
        addBehavior(new Behavior(25, new ReturnToGuardPoint(this.guardPoint, 12)));
        addBehavior(new Behavior(50, new Patrol(4, 400)));

        landing = new Sound("sounds/Frog_Landing.wav");
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        animations[animType].update(deltaTime);
        super.update(gameContainer, deltaTime);
    }

    @Override
    protected void cycleAttackAnimation() {
        animations[animType].restart();
    }

    @Override
    protected void stopAttackAnimation() {
        animType = ANIMATION_TYPE_IDLE;
        animations[animType].restart();
    }

    @Override
    protected long getAttackOffsetTime() {
        return 0;
    }

    @Override
    protected long getAttackTime() {
        return ANIMATION_FRAME_RATE[ANIMATION_TYPE_ATTACK] * ANIMATION_FRAME_COUNT[ANIMATION_TYPE_ATTACK];
    }

    @Override
    protected void updateAnimation() {
        if (isAttacking) {
            animType = ANIMATION_TYPE_ATTACK;
            if (!wasAttacking) animations[animType].restart();
        } else if (wasMoving && !isMoving) {
            animType = ANIMATION_TYPE_IDLE;
            animations[animType].restart();
        } else if (!wasMoving && isMoving) {
            animType = ANIMATION_TYPE_RUN;
            animations[animType].restart();
        }
    }

    @Override
    public void render(Camera camera, GameContainer gameContainer) {
        super.render(camera, gameContainer);

        if (BattleToads.ALLOW_DEBUGGING) gameContainer.getGraphics().setColor(Color.magenta);
        if (BattleToads.ALLOW_DEBUGGING) gameContainer.getGraphics().draw(getCollisionHitbox(position));
    }

    @Override
    public Rectangle getCollisionHitbox(Rectangle position) {
        Rectangle result = new Rectangle(position.getX(), position.getY(), position.getWidth(), position.getHeight());
        int cut = 50;

        if (facing == FACING_RIGHT) {
            result.setX(result.getX() + cut);
        }

        result.setWidth(result.getWidth() - cut);

        return result;
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

    @Override
    protected void onLanding() {
        SoundManager.getInstance().playSoundEffect(landing);
    }

    @Override
    public void radiate(Reactor reactor, float radiation) throws SlickException {
        if (world.getNumberOfBosses() == 0) {
            reactor.shrinkRadiation();
            world.setNumberOfBosses(1);

            world.removeEntity(this);
            world.addEntity(new FrogBoss(world, new Point(position.getX(), position.getY()), Team.THE_FRENCH));
        }
    }

    @Override
    protected void die() throws SlickException {
        super.die();

        System.out.println("state: " + world.getState() + " - pirates? " + world.hasPirates());
        if (world.getState() == World.State.ENGINES_ON && !world.hasPirates()) {
            world.setState(World.State.WIN);
        }
    }
}
