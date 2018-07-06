
package com.mygdx.game.GameSets;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Screens.*;

public class GGame extends Game {

  	private SpriteBatch batch;
	private BitmapFont font;

private LoadingScreen loadingScreen;
private SettingsScreen settingsScreen;
private AppPreferences preferences;
private MenuScreen menuScreen;
private BattleScreen battleScreen;
private LoginScreen loginScreen;
private RegisterScreen registerScreen;
private CharacterSelectScreen characterSelectScreen;
 


private final static int LOGIN = 0;
public int getLogin(){return LOGIN;}

private final static int REGISTER = 1;
public int getRegister(){return  REGISTER;}

private final static int MENU = 2;
public int getMenu(){
    return MENU;
}

private final static int SETTINGS = 3;
public int getSettings(){ return SETTINGS;}

private final static int CHARACTERSELECT = 4;
public int getCharacterSelect(){ return CHARACTERSELECT;}


private final static int BATTLE = 5;
public int getBattle(){return BATTLE;}
        
//PREFERENCES - preferencessScreen - settingScreen
//APPLICATION - mainScreen - gameScreen
//MENU - menuScreen - menuScreen

public void changeScreen(int screen){
	switch(screen){
		case LOGIN:
			if(loginScreen == null) loginScreen = new LoginScreen(this);
					this.setScreen(loginScreen);
			break;
		case REGISTER:
			if (registerScreen == null) registerScreen = new RegisterScreen(this);
					this.setScreen(registerScreen);
			break;
		case MENU:
			if(menuScreen == null) menuScreen = new MenuScreen(this);
					this.setScreen(menuScreen);
			break;
			//тут измененно
		case SETTINGS:
			if(settingsScreen == null) settingsScreen = new SettingsScreen(this);
					this.setScreen(settingsScreen);
			break;
		case CHARACTERSELECT:
			if(characterSelectScreen == null) characterSelectScreen = new CharacterSelectScreen(this);
			this.setScreen(characterSelectScreen);
			break;

		case BATTLE:
			if(battleScreen == null) battleScreen = new BattleScreen(this);
					this.setScreen(battleScreen);
			break;

	}
}

	public AppPreferences getPreferences(){
		return this.preferences;
	}

          @Override
	public void create() {

		preferences = new AppPreferences();
		batch = new SpriteBatch();
		// Use LibGDX's default Arial font.
		font = new BitmapFont();
                //this.setScreen(new )
			  battleScreen = new BattleScreen(this);
                setScreen(battleScreen);
//			  characterSelect = new CharacterSelect(this);
//			  setScreen(characterSelect);
	}

          @Override
	public void render() {
		super.render();
	}
        
        class TextInput implements Input.TextInputListener{
            @Override
            public void input(String text) {
            }

            @Override
            public void canceled() {
            }
    }

          @Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}
