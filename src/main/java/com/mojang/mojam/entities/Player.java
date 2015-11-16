package com.mojang.mojam.entities;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.ai.actions.enemy.PlayerAttack;
import com.mojang.mojam.entities.projectiles.Missile;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;

public class Player extends Entity {
    public static final int ANIMATION_TYPE_RUN = 0;
    public static final int ANIMATION_TYPE_JUMP = 1;
    public static final int ANIMATION_TYPE_IDLE = 2;
    public static final int ANIMATION_TYPE_WALK = 3;
    public static final int ANIMATION_TYPE_ATTACK = 4;
    public static final int ANIMATION_COUNT = 5;

    public static final int[] ANIMATION_FRAME_COUNT = new int[] {
            6, // run
            3, // jump
            3, // idle
            8, // walk
            7, // attack
    };

    public static final int[] ANIMATION_FRAME_RATE = new int[] {
            83, // run
            83, // jump
            166, // idle
            166, // walk
            83, // attack
    };

    public static final int ATTACK_TIME = ANIMATION_FRAME_RATE[ANIMATION_TYPE_ATTACK] * ANIMATION_FRAME_COUNT[ANIMATION_TYPE_ATTACK];
    public static final int ATTACK_MISSILE_TIME = ANIMATION_FRAME_RATE[ANIMATION_TYPE_ATTACK] * 3;
    public static final int STARTING_X = 6470;

    protected final SpriteSheet attackSheet;
    protected final PlayerAttack playerAttack = new PlayerAttack(Team.THE_FRENCH, 50, ATTACK_TIME);
    protected int animType = ANIMATION_TYPE_IDLE;
    protected boolean hasWeapon = false;
    protected boolean isAttacking = false;
    protected boolean hasAttackHappened = false;
    protected boolean wasAttacking = false;
    protected long attackStart = 0;
    protected Animation[] animations = new Animation[ANIMATION_COUNT];

    private final Sound[] walking;
    private final Sound[] shooting;
    private final Sound jumping;
    private final Sound landing;

    private long walkingSoundInterval = 750;
    private long runningSoundInterval = 200;
    private long lastMovingSound = 0;
    private float lastAutoHeal = 0;

    public Player(World world, Image image, int width, int height) throws SlickException {
        super(world, image, new Point(STARTING_X, World.FLOOR_LEVEL - height), width, height, Team.THE_FRENCH);

        attackSheet = new SpriteSheet(image, 200, height);
        horizontalSpeed = 8;
        fullHealth = 200;
        currentHealth = fullHealth;

        for (int type = 0; type < ANIMATION_COUNT; type++) {
            SpriteSheet sheet = type == ANIMATION_TYPE_ATTACK ? attackSheet : animationSheet;
            Animation anim = new Animation(sheet, 0, type, ANIMATION_FRAME_COUNT[type] - 1, type, true, ANIMATION_FRAME_RATE[type], true);
            animations[type] = anim;
        }

        animations[ANIMATION_TYPE_ATTACK].setLooping(false);
        animations[ANIMATION_TYPE_JUMP].setPingPong(true);

        jumping = new Sound("sounds/AnnaB_Jumping_Up.wav");
        landing = new Sound("sounds/AnnaB_Landing.wav");

        walking = new Sound[]{
            new Sound("sounds/AnnaB_footstep1.wav"),
            new Sound("sounds/AnnaB_footstep2.wav"),
            new Sound("sounds/AnnaB_footstep3.wav")
        };

        shooting = new Sound[]{
                new Sound("sounds/weapon_shot1.wav"),
                new Sound("sounds/weapon_shot2.wav"),
                new Sound("sounds/weapon_shot3.wav")
        };
    }

    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        Input input = gameContainer.getInput();

        wasAttacking = isAttacking;
        boolean cycledAttack = false;
        long attackDelta = gameContainer.getTime() - attackStart;

        if (isAttacking && attackDelta >= ATTACK_TIME) {
            isAttacking = false;
            cycledAttack = true;
        }

        if (hasWeapon && !wasJumping && !isJumping && !isAttacking && gameContainer.getInput().isKeyDown(Input.KEY_SPACE)) {
            attackStart = gameContainer.getTime();
            isAttacking = true;
            hasAttackHappened = false;
        }

        if (isAttacking && !hasAttackHappened && attackDelta >= ATTACK_MISSILE_TIME) {
            Missile missile = new Missile(this, playerAttack);
            playShootingSound();
            world.addEntity(missile);
            hasAttackHappened = true;
        }

        if (wasAttacking && !isAttacking) {
            animType = ANIMATION_TYPE_IDLE;
            animations[animType].restart();
        } else if (cycledAttack && isAttacking) {
            animations[animType].restart();
        }

        animations[animType].update(deltaTime);

        if (!isAttacking && (input.isKeyDown(Input.KEY_RIGHT) || input.isKeyDown(Input.KEY_D))) {
            velocity.x += horizontalSpeed * getWalkingSpeed();
        } else if (!isAttacking && (input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_A))) {
            velocity.x -= horizontalSpeed * getWalkingSpeed();
        }
        if (!isAttacking && !isJumping && !wasJumping && (input.isKeyDown(Input.KEY_UP) || input.isKeyDown(Input.KEY_W))) {
            if (velocity.y == 0) {
                SoundManager.getInstance().playSoundEffect(jumping);
            }
            velocity.y -= jumpSpeed;
            isJumping = true;
            animType = ANIMATION_TYPE_JUMP;
            animations[animType].start();
            animations[animType].setCurrentFrame(0);
            animations[animType].stopAt(ANIMATION_FRAME_COUNT[animType] - 1);
        }

        if (gameContainer.getTime() - lastAutoHeal > 10000) {
            increaseHealth(1);
            lastAutoHeal = gameContainer.getTime();
        }

        playWalkingSound();

        move();
    }

    private void playShootingSound() {
        if (isAttacking) {
            SoundManager.getInstance().playRandomSoundEffect(shooting);
        }
    }

    private void playWalkingSound() {
        long now = System.currentTimeMillis();
        long soundInterval = hasWeapon ? runningSoundInterval : walkingSoundInterval;
        if (isMoving && !isJumping && now - soundInterval > lastMovingSound) {
            SoundManager.getInstance().playRandomSoundEffect(walking);
            lastMovingSound = now;
        }
    }

    @Override
    protected void updateAnimation() {
        if (wasJumping && !isJumping) {
            animType = isMoving ? getWalkingAnimation() : ANIMATION_TYPE_IDLE;
            animations[animType].restart();
        } else if (isJumping && velocity.y > 0) {
            animType = ANIMATION_TYPE_JUMP;
            animations[animType].setCurrentFrame(0);
            animations[animType].stop();
        } else if (isAttacking) {
            animType = ANIMATION_TYPE_ATTACK;
            if (!wasAttacking) animations[animType].restart();
        } else if (wasMoving && !isMoving) {
            animType = isJumping ? ANIMATION_TYPE_JUMP : ANIMATION_TYPE_IDLE;
            animations[animType].restart();
        } else if (!wasMoving && isMoving) {
            animType = getWalkingAnimation();
            animations[animType].restart();
        }
    }

    protected int getWalkingAnimation() {
        return hasWeapon ? ANIMATION_TYPE_RUN : ANIMATION_TYPE_WALK;
    }

    protected float getWalkingSpeed() {
        return hasWeapon ? 1.95f : 1.35f;
    }

    @Override
    protected Image getFrame() {
        return animations[animType].getCurrentFrame();
    }

    @Override
    public void decreaseHealth(int amount) throws SlickException {
        super.decreaseHealth(amount);
        System.out.println("Took damage: " + amount);
    }

    @Override
    protected void die() throws SlickException {
        world.setState(World.State.GAME_OVER);
    }

    @Override
    public void radiate(Reactor reactor, float radiation) throws SlickException {
        if (random.nextFloat() < radiation * 0.5f) {
            decreaseHealth(1);
        }
    }

    public void setHasWeapon(boolean hasWeapon) {
        this.hasWeapon = hasWeapon;
    }

    @Override
    protected void onLanding() {
        SoundManager.getInstance().playSoundEffect(landing);
    }
}
