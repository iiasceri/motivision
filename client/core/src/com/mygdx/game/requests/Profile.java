package com.mygdx.game.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

public class Profile implements Comparable<Profile> {
    public static final String PROFILE_ID = "characterId";
    public static final String TEAM_ID = "teamId";
    public static final String IS_ADMIN = "isAdmin";
    public static final String NAME = "characterName";
    public static final String HEAD_TYPE = "headType";
    public static final String BODY_TYPE = "bodyType";
    public static final String GENDER = "gender";
    public static final String POINTS = "points";
    public static final String PLAYER_ID = "playerId";

    private String name;
    private int id;
    private int headType;
    private int bodyType;
    private char gender;
    private int points;
    private int teamId;
    private int power;
    private boolean isAdmin;
    private ArrayList<Item> itemList;


    private Profile(int id, int teamId, boolean isAdmin) {
        this.id = id;
        this.name = null;
        this.headType = 0;
        this.bodyType = 0;
        this.gender = 'N';
        this.points = 0;
        this.power = 0;
        this.teamId = teamId;
        this.isAdmin = isAdmin;
        this.itemList = null;
    }

    public static boolean nameExist(String name) throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;
        String result;

        url = JsonHandler.domain + "/character_exist";
        urlParameters = NAME + "=" + name;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "POST");
        if (jsonObject == null)
            return true;
        result = jsonObject.getString("message");
        if (result.equals("false"))
            return false;
        return true;
    }

    public ArrayList<Item> getEquippedItems() {
        ArrayList<Item> equippedItems;

        equippedItems = new ArrayList<Item>();
        if (itemList == null) {
            return equippedItems;
        }
        power = 0;
        for (Item item : itemList) {
            if (item.isEquipped()) {
                power += item.getPrice();
                //power += Item.getItemPrice(item.getId());
                equippedItems.add(item);
            }
        }
        return equippedItems;
    }

    private static void setErrorMessage(String message) {
        JsonHandler.errorMessage = message;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public static Profile getProfileFromJson(JSONObject jsonObject) throws JSONException {
        Profile profile;
        String field;
        int characterId;
        int teamId;
        boolean isAdmin;
        int number;
        char gender = 'N';

        if (jsonObject == null)
            return null;
        try {
            characterId = jsonObject.getInt(PROFILE_ID);
            if (jsonObject.has(TEAM_ID))
                teamId = jsonObject.getInt(TEAM_ID);
            else
                teamId = -1;

            if (jsonObject.has(IS_ADMIN)) {
                field = jsonObject.getString(IS_ADMIN);
                isAdmin = field.equals("true");
            } else
                isAdmin = false;

            profile = new Profile(characterId, teamId, isAdmin);

            field = jsonObject.getString(NAME);
            profile.setName(field);

            field = jsonObject.getString(HEAD_TYPE);
            number = Integer.parseInt(field);
            profile.setHeadType(number);

            field = jsonObject.getString(BODY_TYPE);
            number = Integer.parseInt(field);
            profile.setBodyType(number);

            field = jsonObject.getString(GENDER);
            if (field.length() == 1)
                gender = field.toCharArray()[0];
            profile.setGender(gender);

            number = jsonObject.getInt(POINTS);
            profile.setPoints(number);

            profile.loadItemsFromServer(jsonObject);

            return profile;
        } catch (NumberFormatException e) {
            setErrorMessage("Invalid number format");
            return null;
        }
    }

    static Profile getProfileFromUrl(String url, String urlParameters, String requestMethod)
            throws JSONException, IOException {
        JSONObject jsonObject;

        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, requestMethod);
        System.out.println(url + "?" + urlParameters);
        return getProfileFromJson(jsonObject);
    }

    // Load items from server if they aren't loaded already
    public void loadItems() {
        if (itemList == null) {
            try {
                updateItems();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateItems() throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;

        itemList = null;
        url = JsonHandler.domain + "/get_items";
        urlParameters = PROFILE_ID + "=" + id;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "GET");
        if (jsonObject == null)
            return;
        loadItemsFromServer(jsonObject);
    }

    private void loadItemsFromServer(JSONObject jsonObject) throws JSONException {
        Item item;
        String field;
        String itemName;
        String itemType;
        String itemImage;
        int itemId;
        int itemPrice;
        boolean equipped;
        ArrayList<Item>  items;

        items = new ArrayList<Item>();
        if (jsonObject.has("items") == false)
            return;
        if (jsonObject.isNull("items"))
            return;

        if (jsonObject.get("items") == null)
            return;
        JSONArray arr;
        try {
            arr = jsonObject.getJSONArray("items");
        } catch (JSONException e) {
            return;
        }
        if (arr == null)
            return;

        for (int i = 0; i < arr.length(); i++) {
            itemId = arr.getJSONObject(i).getInt(Item.ITEM_ID);
            itemName = arr.getJSONObject(i).getString(Item.ITEM_NAME);
            itemImage = arr.getJSONObject(i).getString(Item.ITEM_IMAGE);
            itemPrice = arr.getJSONObject(i).getInt(Item.ITEM_PRICE);
            itemType = arr.getJSONObject(i).getString(Item.ITEM_TYPE);
            if (arr.getJSONObject(i).has(Item.ITEM_EQUIPPED))
                equipped = arr.getJSONObject(i).getBoolean(Item.ITEM_EQUIPPED);
            else
                equipped = true;
            item = new Item(itemId, itemName, itemImage, itemPrice, itemType, equipped);
            items.add(item);
        }
        itemList = items;
    }

    private static Profile createProfileWithId(int characterId, LinkedHashMap<String, String> profileParams) {
        try {
            Profile profile;
            String field;
            int teamId;
            int number;
            boolean isAdmin;
            char gender = 'N';

            field = profileParams.get(TEAM_ID);
            teamId = Integer.parseInt(field);

            field = profileParams.get(IS_ADMIN);
            isAdmin = field.equals("true");
            profile = new Profile(characterId, teamId, isAdmin);

            field = profileParams.get(NAME);
            profile.setName(field);

            field = profileParams.get(HEAD_TYPE);
            number = Integer.parseInt(field);
            profile.setHeadType(number);

            field = profileParams.get(BODY_TYPE);
            number = Integer.parseInt(field);
            profile.setBodyType(number);

            field = profileParams.get(GENDER);
            if (field.length() == 1)
                gender = field.toCharArray()[0];
            profile.setGender(gender);

            return profile;
        } catch (NumberFormatException e) {
            setErrorMessage("Invalid number format");
            return null;
        }
    }

    public static Profile createNewProfile(LinkedHashMap<String, String> profileParams) throws IOException, JSONException {
        Profile profile;
        String url;
        String urlParameters = null;
        String name;
        Set<String> keySet;
        JSONObject jsonObject;
        int characterId;

        name = profileParams.get(NAME);
        if (nameExist(name)) {
            setErrorMessage("Name already exist");
            return null;
        }
        url = JsonHandler.domain + "/create_character";
        keySet = profileParams.keySet();
        for (String key : keySet) {
            if (urlParameters == null)
                urlParameters = "";
            else
                urlParameters += "&";
            urlParameters += key + "=" + profileParams.get(key);
        }
        System.out.println(url + "?" + urlParameters);
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "POST");
        if (jsonObject == null)
            return null;
        characterId = jsonObject.getInt(PROFILE_ID);
        profile = createProfileWithId(characterId, profileParams);
        if (profile == null)
            System.out.println("Profile is null");
        else
            System.out.println("Profile is not null");
        return profile;
    }

    public static Profile getProfile(int profileId) throws IOException, JSONException {
        String url;
        Profile profile;
        String urlParameters;

        if (profileId == -1)
            return null;
        url = JsonHandler.domain + "/get_character";
        urlParameters = PROFILE_ID + "=" + profileId;
        System.out.println("Start get profile from URL");
        profile = getProfileFromUrl(url, urlParameters, "GET");
        return profile;
    }

//    private static Pixmap addItemOnPixmap(Pixmap pixmap, String item, int x, int y) {
//        Pixmap itemPixmap;
//
//        x += 40;
//        itemPixmap = new Pixmap(Gdx.files.internal("items/" + item + ".png"));
//        pixmap.drawPixmap(itemPixmap, x - itemPixmap.getWidth() / 2, y - itemPixmap.getHeight() / 2);
//        itemPixmap.dispose();
//        return pixmap;
//    }

//    private static Texture addItems(Map<String, String> itemImages, int headType, int bodyType) {
//        Texture mergedImage;
//        Pixmap pixmap;
//        String legginsType;
//        String armorType;
//
//        System.out.println("Start creating profile texture");
//        pixmap = new Pixmap(Gdx.files.internal("default.png"));
//        legginsType = itemImages.get("leggins");
//        legginsType = legginsType.split("_")[0];
//        armorType = itemImages.get("armor");
//        armorType = armorType.split("_")[0];
//        pixmap = addItemOnPixmap(pixmap, legginsType + "_left_leg", 227, 435);
//        pixmap = addItemOnPixmap(pixmap, armorType + "_left_arm", 286, 304);
////        if (armorType.equals("iron"))
////            pixmap = addItemOnPixmap(pixmap, itemImages.get("armor"), 174, 308);
//        pixmap = addItemOnPixmap(pixmap, legginsType + "_right_leg", 163, 436);
////        if (armorType.equals("iron") == false)
//        pixmap = addItemOnPixmap(pixmap, itemImages.get("armor"), 174, 308);
//        pixmap = addItemOnPixmap(pixmap, armorType + "_right_arm", 91, 320);
//        pixmap = addItemOnPixmap(pixmap, itemImages.get("sword"), 411, 280);
////        pixmap = addItemOnPixmap(pixmap, itemImages.get("fingers"), 219, 304);
//        if (itemImages.get("shield").equals("null") == false) {
//            pixmap = addItemOnPixmap(pixmap, itemImages.get("shield"), 137, 373);
//        }
//        pixmap = addItemOnPixmap(pixmap, "steel_helmet", 184, 139);
//        mergedImage = new Texture(pixmap);
////        mergedImage = changeFaceType(pixmap, headType, bodyType);
//        pixmap.dispose();
//        return mergedImage;
//    }

//    public Texture getProfileTexture() {
//        Map<String, String> itemImages;
//        Set<String> itemSet;
//
//        //System.out.println("procesing...");
//        itemImages = new LinkedHashMap<String, String>();
//        itemImages.put("sword", "default_sword");
//        itemImages.put("armor", "default_armor");
////        itemImages.put("fingers", "default_fingers");
//        itemImages.put("leggins", "default_leggins");
//        itemImages.put("shield", "null");
//        itemSet = itemImages.keySet();
//        if (itemList != null) {
//            for (Item item : itemList) {
//                if (item.isEquipped() == false)
//                    continue;
//                for (String itemType : itemSet) {
//                    System.out.println(item.getType());
//                    if (item.getType().equals("steel_axe")) {
//                        System.out.println("Axe added!");
//                        itemImages.put("sword", "steel_sword");
//                    }
//                    if (item.getType().contains(itemType)) {
//                        itemImages.put(itemType, item.getType());
//                    }
//                }
//            }
//        }
//        return addItems(itemImages, headType, bodyType);
//    }

    public boolean deleteProfile() throws IOException, JSONException {
        String url;
        String status;
        String urlParameters;
        JSONObject jsonObject;

        if (id == -1)
            return false;
        url = JsonHandler.domain + "/delete_character";
        urlParameters = PROFILE_ID + "=" + id;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "DELETE");
        if (jsonObject == null)
            return false;
        status = jsonObject.getString("status");
        if (status.equals("success"))
            return true;
        return false;
    }

    public int getId() {
        return id;
    }

    public String getProfileName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeadType(int headType) {
        this.headType = headType;
    }

    public void setBodyType(int bodyType) {
        this.bodyType = bodyType;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTeamId() {
        return teamId;
    }


//    public static Texture changeFaceType(Pixmap background, int headType, int bodyType) {
//        Texture mergedImage;
//        Pixmap itemPixmap;
//
//        if (headType <= 0 || headType > 2)
//            headType = 1;
//        if (bodyType <= 0 || bodyType > 3)
//            bodyType = 1;
//
//        itemPixmap = new Pixmap(Gdx.files.internal("head/body" + bodyType + ".png"));
//        background.drawPixmap(itemPixmap, 0, 0);
//        itemPixmap.dispose();
//        itemPixmap = new Pixmap(Gdx.files.internal("head/head" + headType + ".png"));
//        background.drawPixmap(itemPixmap, 0, 0);
//        itemPixmap.dispose();
//        mergedImage = new Texture(background);
//        System.out.println("Texture created");
//        return mergedImage;
//    }

//    public static Texture changeFaceType(int headType, int bodyType) {
//        Pixmap pixmap;
//
//        pixmap = new Pixmap(Gdx.files.internal("default.png"));
//        return changeFaceType(pixmap, headType, bodyType);
//    }

    public boolean buyItem(int id) throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;
        String result;

        url = JsonHandler.domain + "/buy_item";
        urlParameters = PROFILE_ID + "=" + this.id;
        urlParameters += "&" + Item.ITEM_ID + "=" + id;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "GET");
        if (jsonObject == null)
            return false;
        result = jsonObject.getString("status");
        return result.equals("success");
    }

    public int getItemStatus(int id) {
        if (itemList == null)
            return Item.STORE_ITEM;
        for (Item item : itemList) {
            if (item.getId() == id)
                return item.isEquipped() ? Item.EQUIPPED_ITEM : Item.UNEQUIPPED_ITEM;
        }
        return Item.STORE_ITEM;
    }

    public void unequipItem(int itemId) throws IOException, JSONException {
        String url;
        String urlParameters;

        for (Item item : itemList) {
            if (item.getId() == itemId) {
                item.unequip();
            }
        }
        url = JsonHandler.domain + "/unequip_item";
        urlParameters = PROFILE_ID + "=" + this.id;
        urlParameters += "&" + Item.ITEM_ID + "=" + itemId;
        JsonHandler.readJsonFromUrl(url, urlParameters, "GET");
    }

    public void equipItem(int itemId) throws IOException, JSONException {
        String url;
        String urlParameters;
        String type = null;

        for (Item item : itemList) {
            if (item.getId() == itemId) {
                type = item.getType();
                item.equip();
                System.out.println("Item found!");
                break;
            }

        }
        url = JsonHandler.domain + "/equip_item";
        urlParameters = PROFILE_ID + "=" + this.id;
        urlParameters += "&" + Item.ITEM_ID + "=" + itemId;
        JsonHandler.readJsonFromUrl(url, urlParameters, "GET");
        for (Item item : itemList) {
            if (item.getId() != itemId && item.getType().equals(type)) {
                item.unequip();
            }
        }
    }


    public boolean doActivity(int activityId) throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;
        String result;

        url = JsonHandler.domain + "/do_activity";
        urlParameters = Profile.PROFILE_ID + "=" + id;
        urlParameters += "&" + Activity.ACTIVITY_ID + "=" + activityId;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "POST");
        if (jsonObject == null)
            return false;
        this.points = jsonObject.getInt("points");
        return jsonObject.getString("status").equals("success");

    }

    public boolean updateActivity(int activityId, String activityName, int activityReward)
            throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;
        String result;

        url = JsonHandler.domain + "/update_activity";
        urlParameters = Team.TEAM_ID + "=" + teamId;
        urlParameters += "&" + Activity.ACTIVITY_ID + "=" + activityId;
        urlParameters += "&" + Activity.ACTIVITY_NAME + "=" + activityName;
        urlParameters += "&" + Activity.ACTIVITY_REWARD + "=" + activityReward;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "POST");
        if (jsonObject == null)
            return false;
        return jsonObject.getString("status").equals("success");
    }

    public boolean deleteActivity(int activityId)
            throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;
        String result;

        url = JsonHandler.domain + "/delete_activity";
        urlParameters = Team.TEAM_ID + "=" + teamId;
        urlParameters += "&" + Activity.ACTIVITY_ID + "=" + activityId;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "DELETE");
        if (jsonObject == null)
            return false;
        return jsonObject.getString("status").equals("success");
    }

    public boolean createActivity(String name) throws IOException, JSONException {
        String urlParameters;
        String url;
        JSONObject jsonObject;
        String result;

        url = JsonHandler.domain + "/create_activity";
        urlParameters = Team.TEAM_ID + "=" + teamId;
        urlParameters += "&" + Activity.ACTIVITY_NAME + "=" + name;
        urlParameters += "&" + Activity.ACTIVITY_REWARD + "=" + 0;
        jsonObject = JsonHandler.readJsonFromUrl(url, urlParameters, "POST");
        if (jsonObject == null)
            return false;
        return jsonObject.getString("status").equals("success");
    }

    @Override
    public int compareTo(Profile profile) {
        return name.compareTo(profile.getName());
    }

    public int getHeadNumber() {
        int headNumber;

        headNumber = 0;
        if (gender == 'F')
            headNumber += 6;
        if (headType < 1 || headType > 2)
            headType = 1;
        if (bodyType < 1 || bodyType > 3)
            bodyType = 1;
        if (headType == 2)
            headNumber += 3;
        headNumber += bodyType;
        return headNumber;
    }

    public int getPower() {
        return power;
    }
}