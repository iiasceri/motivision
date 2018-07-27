package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.gameSets.GGame;
import com.mygdx.game.music.GameSounds;
import com.mygdx.game.requests.PlayerAccount;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;

public class TeamMembersScreen implements Screen {

    private GGame parent;
    private Stage stage;
    private Skin skin;
    private GameSounds gameSounds = GameSounds.getInstance();
    private Viewport viewport;
    private Camera camera;
    private Music loginMusic;
    private GDXDialogs dialogs;
    private static String selectedName = null;
    private CharacterAnimation animation;
    private SettingsPopup settingsPopup;

    public TeamMembersScreen(GGame g) {
        if (selectedName == null) {
            selectedName = PlayerAccount.getProfileName();
        }
        parent = g;
        Pixmap pixmap;
        animation = new CharacterAnimation();
        animation.init("IDLE");
        animation.setZIndex(10);

        dialogs = GDXDialogsSystem.install();
        skin = new Skin(Gdx.files.internal("skin2/clean-crispy-ui.json"));
        stage = new Stage();
        viewport = new StretchViewport(800, 480, stage.getCamera());
        stage.setViewport(viewport);
        settingsPopup = new SettingsPopup();
    }

    @Override
    public void show() {
        stage.clear();
        float pad = 5;


        Gdx.input.setCatchBackKey(true);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
//                System.out.println("new keycode = " + keycode);
                if (keycode == Input.Keys.BACK /*|| keycode == Input.Keys.TAB*/) {
                    parent.changeScreen(parent.getCharacterProfile());
                }
                return false;
            }
        });

        // Character Sprite
        // label
        if (selectedName == null)
            selectedName = PlayerAccount.getProfileName();
        DrawerImplementation.characterName = selectedName;
        PlayerAccount.updateTeam();
        Label teamStats = new Label("", skin);
        teamStats.setText("Victories: " + PlayerAccount.getWins() + " Defeats: " + PlayerAccount.getLosses());
        teamStats.setAlignment(Align.center);

        String teamName = PlayerAccount.getTeamName();
        Label teamLabel = new Label(" Team: " + teamName, skin);

        // buttons
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton backButton = new TextButton("Back", skin);

        // tables
        Table screenTable = new Table();
        Table buttonTable = new Table();
        Table teamMembersTable = new Table();

        //create scrollPane
        ScrollPane scrollPane = new ScrollPane(teamMembersTable);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setScrollingDisabled(true, false);
        HashMap<String, Integer> teamMembers = PlayerAccount.getTeamMembersList();
        for (final String key : teamMembers.keySet()) {
            //instead of PLACE_HOLDER there should be name of character
            TextButton profileName = new TextButton(key, skin, "square");
            TextButton points = new TextButton(teamMembers.get(key).toString(), skin, "square");

            profileName.addListener(new SelectTeamMember(PlayerAccount.getProfileName(), key));
            teamMembersTable.add(profileName).fillX().expandX();

            //here are points of each teammate
            points.setTouchable(Touchable.disabled);
            teamMembersTable.add(points).width(Value.percentWidth(0.2f, teamMembersTable));

            if (PlayerAccount.isAdmin()) {

                TextButton xButton = new TextButton("X", skin, "red");

                xButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gameSounds.clickSound();
                        final Label kickLabel = new Label("Are you sure you want to kick \"" + key + "\" ?", skin, "big");
                        Dialog dialog = new Dialog("Confirmation", skin) {
                            @Override
                            public void result(Object obj) {
                                gameSounds.clickSound();
                                if (obj == "yes") {
                                    try {
                                        PlayerAccount.deleteTeamMember(key);
                                        if (selectedName.equals(key))
                                            selectedName = null;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    TeamMembersScreen.this.show();
                                }
                            }
                        };

                        dialog.getContentTable().row();
                        dialog.getContentTable().add(kickLabel);
                        dialog.button("Yes", "yes");
                        dialog.button("No", "no");
                        dialog.show(stage);
                    }
                });

                teamMembersTable.add(xButton).width(Value.percentWidth(0.2f, teamMembersTable));
                if (key.compareTo(PlayerAccount.getProfileName()) == 0) {
                    xButton.setDisabled(true);
                    xButton.setTouchable(Touchable.disabled);
                }
            }

            teamMembersTable.row();
        }

        buttonTable.add(settingsButton).fill().pad(0, 0, pad, 0);
        buttonTable.add(backButton).fill().pad(0, 0, pad, 0);
        buttonTable.row();
        //buttonTable.add(teamStats).fill().colspan(2).padBottom(pad);
        //buttonTable.row();
        buttonTable.add(scrollPane).fillX().expand().top().colspan(2).pad(pad / 2, 0, 0, 0);

        Table leftTable = new Table();
        leftTable.add(teamLabel);
        leftTable.top();
        leftTable.row();
        leftTable.add(animation).expand();


        screenTable.setFillParent(true);
        screenTable.add(leftTable).fill().expand().uniform().pad(pad, pad, pad, pad / 2);
        screenTable.add(buttonTable).fill().expand().uniform().pad(pad, pad, pad, pad / 2);
        stage.addActor(screenTable);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameSounds.clickSound();
                selectedName = null;
                parent.changeScreen(parent.getCharacterProfile());
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingsPopup.show(stage);
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    class SelectTeamMember extends ChangeListener {
        String name;

        public SelectTeamMember(String currentAcc, String selectName) {
            this.name = selectName;
        }

        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            System.out.println(GGame.SCREEN_NUMBER);
            gameSounds.clickSound();
            DrawerImplementation.characterName = getTeamMember();
        }
        private String getTeamMember() {
            selectedName = name;
            show();
            return selectedName;
        }
    }

    class DeleteMember extends ChangeListener {
        String name;

        public DeleteMember(String name) {
            this.name = name;
        }

        @Override
        public void changed(ChangeEvent changeEvent, Actor actor) {
            gameSounds.clickSound();
            if (name.equals(PlayerAccount.getProfileName()))
                denyPopUp();
            else {
                confirmPopUp();
                show();
            }
        }

        private void denyPopUp() {
            final GDXButtonDialog bDialog = dialogs.newDialog(GDXButtonDialog.class);
            bDialog.setTitle("Denied");
            bDialog.setMessage("Sorry, can't delete your own account here");
            bDialog.setClickListener(new ButtonClickListener() {
                @Override
                public void click(int button) {
                    gameSounds.clickSound();
                }
            });
            bDialog.addButton("Back");
            bDialog.build().show();
        }

        private void confirmPopUp() {

            final Label kickCharacterLabel = new Label("Are you sure you want to kick \"" + name + "\" ?", skin, "big");
            Dialog dialog = new Dialog("Confirmation", skin) {
                @Override
                public void result(Object obj) {
                    gameSounds.clickSound();
                    if (obj == "yes") {
                        try {
                            PlayerAccount.deleteTeamMember(name);
                            if (selectedName.equals(name))
                                selectedName = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        TeamMembersScreen.this.show();
                    }
                }
            };

            dialog.getContentTable().row();
            dialog.getContentTable().add(kickCharacterLabel);
            dialog.button("Yes", "yes");
            dialog.button("Yes", "no");
            dialog.show(stage);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    public static String getSelectedName() {
        return selectedName;
    }
}
