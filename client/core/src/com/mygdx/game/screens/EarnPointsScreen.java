package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.gameSets.GGame;
import com.mygdx.game.requests.Activity;
import com.mygdx.game.requests.PlayerAccount;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EarnPointsScreen implements Screen {

    private GGame parent;
    private Stage stage;
    private Skin skin;

    private Viewport viewport;
    private Camera camera;
    private Music loginMusic;

    private Table screenTable;


    private AnimationScreen animationScreen;
    private SpriteBatch batch;


    public AnimationScreenTest animationScreenTest;



    public EarnPointsScreen(GGame g) {
        parent = g;

        animationScreenTest = new AnimationScreenTest();
//        skin = new Skin(Gdx.files.internal("skin1/neon-ui.json"));
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));



        batch = new SpriteBatch();
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
        Texture texture = null;
        try {
            texture = PlayerAccount.getProfileTexture();
        } catch (IOException e) {
            texture = new Texture("default.png");
            e.printStackTrace();
        } catch (JSONException e) {
            texture = new Texture("default.png");
            e.printStackTrace();
        }
        Image image = new Image(texture);
        //create buttons Settings, Back and adding them listeners
        TextButton settingsButton = new TextButton("Settings", skin, "small");
        TextButton backButton = new TextButton("Back", skin, "small");

        // tables
        Table activitiesTable = new Table();
        screenTable = new Table();
        Table buttonTable = new Table();

        // scrollpane
        ScrollPane scrollPane = new ScrollPane(activitiesTable);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setScrollingDisabled(true, false);

        List<Activity> activities = PlayerAccount.getActivities();
        //fill table with buttons and labels
        for (final Activity activity: activities){
            //instead of PLACE_HOLDER there should be name of activity
            TextButton activityName = new TextButton(activity.getActivityName(), skin, "square");
            activityName.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor){
                    //here should go Yes No pop up screen
                }
            });

            activitiesTable.add(activityName).fillX().expandX();

            TextButton points = new TextButton(activity.getActivityReward().toString(), skin, "square");
            points.setTouchable(Touchable.disabled);
            activitiesTable.add(points).width(Value.percentWidth(0.2f, activitiesTable));
            activitiesTable.row();
        }

        buttonTable.add(settingsButton).fill().pad(0, 0, pad / 2, 0);
        buttonTable.add(backButton).fill().pad(0, 0, pad / 2, 0);
        buttonTable.row();
        buttonTable.add(scrollPane).fillX().expand().top().colspan(2).pad(pad / 2, 0, 0, 0);

        //create table for all screen and add into it everything
        screenTable.setFillParent(true);

        //lidl strategy

        //
        //lidlActor.setPosition(0,0);


        screenTable.add(animationScreenTest).fill().expand().uniform().pad(pad, pad, pad, pad / 2);
        screenTable.add(buttonTable).fill().expand().uniform().pad(pad, pad, pad, pad / 2);
        stage.addActor(screenTable);

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(parent.getSettings());
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                parent.changeScreen(parent.getCharacterProfile());
            }
        });

        Gdx.input.setInputProcessor(stage);
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
