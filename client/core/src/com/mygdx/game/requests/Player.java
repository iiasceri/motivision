package com.mygdx.game.requests;

import com.mygdx.game.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player {
    private	int	id;
    private LinkedHashMap<Integer, String>	characterList;
    private static Logger log = new Logger();
    private Player(int id) {
        this.id = id;
        this.characterList = null;
    }

    public ArrayList<String>     getCharactersName() {
        ArrayList<String> charName;

        if (characterList == null)
            return null;
        charName = new ArrayList<String>();
        charName.addAll(characterList.values());
        Collections.sort(charName);
        return charName;
    }

    public int getProfileId(String name) {
        Set<Integer>    keySet;
        String          profileName;

        if (characterList == null) {
            JsonHandler.errorMessage = "No characters finded";
            return -1;
        }
        if (characterList.containsValue(name) == false) {
            JsonHandler.errorMessage = "Character not found";
            return -1;
        }
        keySet = characterList.keySet();
        for (Integer key: keySet) {
            profileName = characterList.get(key);
            if (profileName.equals(name))
                return key;
        }
        return -1;
    }

    public void     updateAllCharacters() throws IOException, JSONException {
        String      urlParameters;
        String		url;
        JSONObject  jsonObject;
        int         characterId;
        String      characterName;

        characterList = null;
        url = JsonHandler.domain + "/get_player_characters";
        urlParameters = "playerId=" + this.id;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "GET");
        if (jsonObject.has("characters") == false)
            return;
        if (jsonObject.getString("characters").equals("null"))
            return;
        JSONArray arr = jsonObject.getJSONArray("characters");
        for (int i = 0; i < arr.length(); i++)
        {
            characterId = arr.getJSONObject(i).getInt("id");
            characterName = arr.getJSONObject(i).getString("nickname");
            addCharacter(characterName, characterId);
        }
    }

    public void	addCharacter(String name, int id) {
        if (name == null)
            return;
        if (characterList == null)
            characterList = new LinkedHashMap<Integer, String>();
        characterList.put(id, name);
        System.out.println("Added " + name);
    }

    public int	getId() {
        return id;
    }

    public void	spendPoints(int points) {
        // Send an URL request to spend points

    }

    public static boolean	loginExists(String login) throws IOException, JSONException {
        String      urlParameters;
        String		url;
        JSONObject  jsonObject;
        String		result;

        url = JsonHandler.domain + "/player_exist";
        urlParameters = "login=" + login;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "GET");
        if (jsonObject == null)
            return true;
        result = jsonObject.getString("message");
        if (result.equals("false"))
            return false;
        return true;
    }

    private static void	setErrorMessage(String message) {
        JsonHandler.errorMessage = message;
    }

    private static Player	getPlayerFromUrl(String url, String urlParameters, String requestMethod) throws JSONException, IOException {
        Player      player;
        String		field;
        String      characterName;
        JSONObject	jsonObject;
        int			id;
        int         characterId;

        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, requestMethod);
        if (jsonObject == null)
            return null;
        try {
            field = jsonObject.getString("id");
            id = Integer.parseInt(field);
        } catch (NumberFormatException e) {
            setErrorMessage("Invalid number format for \"id\" field");
            return null;
        }
        player = new Player(id);
        if (jsonObject.has("characters") == false)
            return player;
        if (jsonObject.getString("characters").equals("null"))
            return player;
        if (jsonObject.isNull("characters"))
            return player;

        JSONArray arr = jsonObject.getJSONArray("characters");

        for (int i = 0; i < arr.length(); i++)
        {
            field = arr.getJSONObject(i).getString("id");
            characterId = Integer.parseInt(field);
            characterName = arr.getJSONObject(i).getString("nickname");
            player.addCharacter(characterName, characterId);
        }
        return player;
    }

    public static Player	loginPlayer(String login, String password) throws IOException, JSONException {
        String  url;
        Player  player;
        if (loginExists(login) == false) {
            log.warn("Player doesn't exist");
            setErrorMessage("Player does not exist");
            return null;
        }

        url = JsonHandler.getDomain() + "/login";
        String urlParameters = "login=" + login + "&password=" + password;
        player = getPlayerFromUrl(url, urlParameters, "POST");
        log.info("Player found");
        return player;
    }

    public static Player	registerNewPlayer(String login, String password) throws IOException, JSONException {
        String  url;
        Player  player;

        if (loginExists(login)) {
            setErrorMessage("Login already exist");
            return null;
        }
        url = JsonHandler.domain + "/register_player";
        String urlParameters = "login=" + login + "&password=" + password;
        player = getPlayerFromUrl(url, urlParameters, "POST");
        return player;
    }

    public boolean      deletePlayer() throws IOException, JSONException {
        String      url;
        String      status;
        String      urlParameters;
        JSONObject  jsonObject;

        if (id == -1)
            return false;
        url = JsonHandler.domain + "/delete_player";
        urlParameters = "playerId=" + id;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "DELETE");
        //System.out.println(url + "?" + urlParameters);
        if (jsonObject == null)
            return false;
        status = jsonObject.getString("status");
        if (status.equals("success"))
            return true;
        return false;
    }
}

