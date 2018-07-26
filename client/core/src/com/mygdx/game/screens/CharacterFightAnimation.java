package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Entity;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;
import com.mygdx.game.animation.ParallaxBackground;
import com.mygdx.game.loader.AssetsManager;

public class CharacterFightAnimation extends Image{

    SpriteBatch batch;
    ShapeRenderer renderer;
    Player player;
    Player enemy;

    Loader loader;
    Drawer drawer;
    Data data;

    private int alo = 0;
    private int currentPosition = 1400;

    private int monsterPosition = 800;
    private int playerPosition = 200;

    private int monsterHealth = 200;
    private int playerHealth = 250;


    AssetsManager assetsManager = AssetsManager.getInstance();
    Texture texture;
    Image image;
    ParallaxBackground parallaxBackground;

    public CharacterFightAnimation() {
        this.setZIndex(5);
        parallaxBackground = new ParallaxBackground(assetsManager.getLayers());
        texture = assetsManager.aManager.get("universalbg.png");
        image = new Image(texture);

        image.setBounds(0, 0, 800, 480);
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();

        SCMLReader reader = new SCMLReader(Gdx.files.internal("KekNew.scml").read());
        data = reader.getData();
        Entity humanEntity = data.getEntity(0);
        player = new Player(humanEntity);
        enemy = new Player(humanEntity);
        enemy.flip(true, false);

        setPosition();
        enemy.setAnimation("ATTACK");
        player.setTime(700);

        enemy.setTime(700);


//        player.characterMaps = new CharacterMap[1];
//
//        player.characterMaps[0] = charMaps[0];

        //player.
        // player.setAnimation();

        //Animation troll

    }

    public void init(String animation) {
        player.setAnimation(animation);
        //enemy.setAnimation(animation);
        loader = new LoaderImplementation(data);
        loader.load(Gdx.files.internal("").path());
        drawer = new DrawerImplementation((LoaderImplementation) loader, batch, renderer);
    }


    public void changeAnimation(String animation) {
        player.setAnimation(animation);
        enemy.setAnimation(animation);

    }

    public void storeInts() {
        ++alo;
    }

    public void zeroInts() {
        alo = 0;
    }

    public void setPosition() {
        playerPosition = 200;
        monsterPosition = 800;
    }

    @Override
    public void act(float delta) {

        player.setPosition(playerPosition, 150);
        enemy.setPosition(monsterPosition, 150);
        player.update();
        enemy.update();


        //first is y second is x



        playerPosition += +2;
        monsterPosition += -2;
        batch.begin();

        batch.draw(texture, 0, 0, 1300, 800);

        drawer.draw(player);
        drawer.draw(enemy);
        if (playerPosition > 450) {
            monsterPosition = 550;
            playerPosition = 450;
            if (monsterHealth > 0) {
                ;
                player.getAnimation();
                enemy.getAnimation();
            }
        }
//            if (currentPosition < 520) {
//                player.setAnimation("ATTACK");
            //drawer.draw(player);

            if (currentPosition < 450) {
                currentPosition = 450;
                player.setAnimation("ATTACK");
                enemy.setAnimation("DIE");

                //drawer.draw(enemy);
                if (enemy.getTime() == 600) {
                    --alo;
                    player.setAnimation("WALK");
                    enemy.setAnimation("WALK");
                    setPosition();

                    //drawer.draw(player);
                }
            }
            //    }

        batch.end();
    }
}