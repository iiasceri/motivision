package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.gameSets.GGame;

import java.util.ArrayList;

public class CharacterProfileScreen implements Screen {

    private GGame parent;
    private Stage stage;
    private Skin skin;

    private Table itemTable;
    private Texture textureImage;
    private TextureRegion textureRegion;
    private TextureRegionDrawable textureRegionDrawable;

    private Viewport viewport;
    private Camera camera;
    private Music loginMusic;

    public CharacterProfileScreen(GGame g) {
        parent = g;

        skin = new Skin(Gdx.files.internal("skin1/neon-ui.json"));

        stage = new Stage();
        viewport = new StretchViewport(800, 480, stage.getCamera());
        stage.setViewport(viewport);

    }

    @Override
    public void show() {
        stage.clear();
//        stage.setDebugAll(true);
        float pad = 5;

        // Character Sprite
        Texture texture = new Texture("default.png");
        Image image = new Image(texture);

        //create text buttons and give them listeners
        TextButton earnPointsButton = new TextButton("Earn Points", skin);
        TextButton teamMembersButton = new TextButton("Team Members", skin);
        TextButton lastBattleButton = new TextButton("Last battle", skin);
        TextButton manageTeamButton = new  TextButton("Manage Team", skin);

        ImageButton imageButton;

        earnPointsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(parent.getEarnPoints());
            }
        });

        teamMembersButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(parent.getTeamMembers());
            }
        });

        lastBattleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(parent.getLastBattle());
            }
        });

        manageTeamButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(parent.getAdmin());
            }
        });

        //take number of points from DB
        //here is just random number
        int pointsNumber = 777;

        //Create label wich represents points
        Label pointsLabel = new Label("Points: " + pointsNumber, skin);

        // add item list
        ArrayList<Integer> numberOfItems = new ArrayList<Integer>();

        //here should go i< activities and activities should be taken from Data Base
        for (int i = 1; i <13; i++) {
            numberOfItems.add(i);
        }

        /*
        for (int i = 0; i < 10; i++) {
                numberOfActivities.add("Activity: " + activity);
            }
        */

        //create and fill table with buttons and labels
        itemTable = new Table();
        itemTable.add(earnPointsButton, teamMembersButton, lastBattleButton);
        itemTable.row();
        itemTable.add(pointsLabel);
        itemTable.row();

        Table imageTable = new Table();
        for (Integer e : numberOfItems) {

            imageButton = new ImageButton(addImage()); //Set the button up
            //add listener to image button, so item will replace already equipped item
            imageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    //here should go Yes No pop up screen
                }
            });

            imageTable.add(imageButton).fill().expand();//.pad(pad, pad, pad, pad);
            if(e%3 == 0) {
                imageTable.row();
            }
        }
        //here should go if statement, if user is admin of team
        //if{ }
        itemTable.add(imageTable).fill().expand().colspan(3);
        itemTable.row();
        itemTable.add();
        itemTable.add();
        itemTable.add(manageTeamButton);

        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(image).fill().expand().uniform().pad(pad, pad, pad, pad / 2);
        screenTable.add(itemTable).fill().expand().uniform().pad(pad, pad / 2, pad, pad);
        stage.addActor(screenTable);

        Gdx.input.setInputProcessor(stage);
    }

    public TextureRegionDrawable addImage(){
        textureImage = new Texture(Gdx.files.internal("supaimage.png"));
        textureRegion = new TextureRegion(textureImage);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        return textureRegionDrawable;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
