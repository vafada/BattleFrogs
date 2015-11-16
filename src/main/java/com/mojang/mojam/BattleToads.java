package com.mojang.mojam;

import com.mojang.mojam.entities.Player;
import com.mojang.mojam.entities.Reactor;
import com.mojang.mojam.entities.Rift;
import com.mojang.mojam.entities.obstacles.Door;
import com.mojang.mojam.entities.pickups.Croissant;
import com.mojang.mojam.entities.pickups.Weapon;
import com.mojang.mojam.screen.*;
import com.mojang.mojam.sound.*;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BattleToads extends BasicGame {
    public static final boolean ALLOW_DEBUGGING = false;
    public static final int GAME_WIDTH = 1280;
    public static final int GAME_HEIGHT = 720;

    private Player player;
    private World world;
    private Camera camera;
    private LevelEditor levelEditor;
    private Gui gui;
    private boolean playing = false;
    private boolean paused = false;
    private boolean hideGame = false;
    private Screen screen;
    private GameContainer gameContainer;
    private TrueTypeFont font;
    private long startTime;
    private KeySequenceReader keySequenceReader;

    private final List<EnvironmentSound> backgroundSounds = new ArrayList<EnvironmentSound>();
    private Rift rift;

    public BattleToads() {
        super("Battle Frogs");
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        this.gameContainer = gameContainer;

        keySequenceReader = new KeySequenceReader(this);

        font = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 30), true);
        camera = new Camera(this, new Vector2f(GAME_WIDTH, GAME_HEIGHT));
        levelEditor = new LevelEditor(this);
        gui = new Gui(this, new Image("graphics/health_full.png"), new Image("graphics/health_bite.png"), new Image("graphics/health_empty.png"));

        gameContainer.getInput().addMouseListener(levelEditor);
        gameContainer.getGraphics().setFont(font);

        setPlaying(true);
    }

    private void loadCollisions(World world, InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().length() < 1) continue;

                String[] coords = line.split(" ", 4);
                Rectangle rect = new Rectangle(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
                world.addCollision(rect);
            }
        } catch (IOException e) {
            System.out.println("Couldn't load collisions!");
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        gameContainer.getGraphics().setFont(font);

        if (gameContainer.getInput().isKeyPressed(Input.KEY_F10)) {
            SoundManager.getInstance().toggleMute();
        }

        if (isPlaying() && gameContainer.getInput().isKeyPressed(Input.KEY_F5)) {
            setPlaying(false);
            setPlaying(true);
        }

        if (playing && !paused) {
            world.update(gameContainer, deltaTime);
        }

        for (EnvironmentSound backgroundSound : backgroundSounds) {
            backgroundSound.update(player, world.getState());
        }

        if (screen != null) {
            screen.update(gameContainer, deltaTime);
        }
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
        graphics.setFont(font);

        if (playing) {
            graphics.pushTransform();
            graphics.translate(-camera.getX(), 0);
            world.render(gameContainer, camera);
            graphics.popTransform();

            gui.render(gameContainer);
        }

        if (screen != null) {
            screen.render(gameContainer);
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        super.mouseClicked(button, x, y, clickCount);
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);
        keySequenceReader.appendChar(c);
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new BattleToads());

        app.setDisplayMode(GAME_WIDTH, GAME_HEIGHT, false);
        app.setShowFPS(false);
        app.setTargetFrameRate(60);
        app.setMaximumLogicUpdateInterval(15);
        app.setMinimumLogicUpdateInterval(15);
        app.setAlwaysRender(true);
        app.start();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) throws SlickException {
        if (this.screen != null) {
            this.screen.onStop();
        }

        this.screen = screen;

        if (screen != null) {
            screen.onStart();
        }
    }

    public boolean isHideGame() {
        return hideGame;
    }

    public void setHideGame(boolean hideGame) {
        this.hideGame = hideGame;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) throws SlickException {
        this.playing = playing;

        if (playing) {
            startTime = gameContainer.getTime();
            world = new World(this, new Vector2f(14709, 720));
            player = new Player(world, new Image("graphics/player.png"), 152, 195);

            loadCollisions(world, ResourceLoader.getResourceAsStream("collisions.txt"));

            final ForegroundObject intactBakeryDoorForeground = new ForegroundObject(new Image("graphics/BakeryWall_door_Intact.png"), new Point(11375, 0), 306, 720);

            world.addEntity(new Door(world, "graphics/IntoRift_door_Intact.png", new Point(6030, 0), 313, 720, new Rectangle(6130, 400, 120, 320)) {

                @Override
                protected void onDestroyed() throws SlickException {
                    world.setState(World.State.CRYO_DOOR_BLOWN);
                    world.addForegroundObject(new ForegroundObject(new Image("graphics/IntoRift_door_Broken.png"), new Point(6030, 0), 313, 720));
                }
            });
            world.addEntity(new Door(world, "graphics/BakeryWall_door_Intact.png", new Point(11375, 0), 306, 720, new Rectangle(11375, 400, 120, 320)) {

                @Override
                protected void onDestroyed() throws SlickException {
                    world.removeForegroundObject(intactBakeryDoorForeground);
                    world.addForegroundObject(new ForegroundObject(new Image("graphics/BakeryWall_door_Broken.png"), new Point(11375, 0), 306, 720));
                }
            });
            world.addEntity(new Door(world, "graphics/Reactor_door_Intact.png", new Point(2135, 0), 502, 720, new Rectangle(2135, 400, 120, 320)) {

                @Override
                protected void onDestroyed() throws SlickException {
                    world.addForegroundObject(new ForegroundObject(new Image("graphics/Reactor_door_Broken.png"), new Point(2135, 0), 502, 720));
                }
            });

            world.addEntity(player);
            rift = new Rift(world, new Point(2805, 76), 2260, 4350);
            world.addEntity(rift);

            world.addEntity(new Croissant(world, new Point(10616, 449)));
            Reactor reactor = new Reactor(world, new Point(456, 307));
            world.addEntity(reactor);
            world.addEntity(new Weapon(world, new Point(14000, 430)));

            world.addFrogPirate(new Point(9800, 652)); // Cafe
            world.addFrogPirate(new Point(11031, 630)); // Cafe
            world.addFrogPirate(new Point(12022, 648)); // Armory

            world.addForegroundObject(new ForegroundObject(new Image("graphics/LeaveCryo_Door_Broken.png"), new Point(8040, 0), 211, 720));
            world.addForegroundObject(intactBakeryDoorForeground);

            backgroundSounds.clear();
            backgroundSounds.add(new FrenchPAMusic(new Point(11364, 0), 2500));
            backgroundSounds.add(new CryoChamberAmbience(new Point(7156, 0), 1020));
            backgroundSounds.add(new ReactorAmbience(reactor.getStartingPoint(), 4000));

            setScreen(new InstructionsScreen(this));
        }
    }

    public GameContainer getGameContainer() {
        return gameContainer;
    }

    public World getWorld() {
        return world;
    }

    public long getStartTime() {
        return startTime;
    }

    public void onWorldStateChanged() throws SlickException {
        World.State state = world.getState();
        System.out.println("Game state was changed to: " + state);
        if (state == World.State.WEAPON_PICKED_UP) {
            setScreen(new WeaponPickedUpScreen(this));
        } else if (state == World.State.GAME_OVER) {
            setScreen(new DeathScreen(this));
        } else if (state == World.State.CRYO_DOOR_BLOWN) {
            setScreen(new CryoDoorBlownScreen(this));
            rift.open();
        } else if (state == World.State.RADIATION_CLEARED) {
            setScreen(new RadiationClearedScreen(this));
        } else if (state == World.State.ENGINES_ON) {
            setScreen(new TextScreen(this, "The engines have been enabled, clean up the remaining pirates!"));
            rift.close();
        } else if (state == World.State.WIN) {
            setScreen(new WinScreen(this));
        }
    }

    public void scorePenalty(int penalty) {
        startTime -= penalty;
    }
}