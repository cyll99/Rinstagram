package com.example.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
@Parcel(analyze = Post.class)

public class Post extends ParseObject {

    public Post(){}

    public static final String KEY_DESCRIPTION="desciption";
    public static final String KEY_IMAGE="image";
    public static final String KEY_USER="user";
    public static final String CREATED_AT="createdAt";
    public static final String KEY_LIKERS="likers";
    public static final String KEY_NUM_LKES="numLikes";
    public static final String KEY_COMMENT="comments";



    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
            put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }


    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public JSONArray getLikers(){
        return getJSONArray(KEY_LIKERS);
    }
    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }
    public void setListLikers(ParseUser userLike){add(KEY_LIKERS, userLike);}
    public void removeItemListLikers(List<String> listUserLike){
        remove(KEY_LIKERS);
        put(KEY_LIKERS, listUserLike);
    }

    public void setNumLikes(int num){
        put(KEY_NUM_LKES, num);
    }
    public  int getNumLikes(){
        return getInt(KEY_NUM_LKES);
    }
    public JSONArray getListComment(){
        return getJSONArray(KEY_COMMENT);
    }


    public void setListComment(ParseObject comment){add(KEY_COMMENT, comment);}

    public static ArrayList<String> fromJsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> likers = new ArrayList<String>();

        try {
            for (int i = 0; i < jsonArray.length(); i++){
                likers.add(jsonArray.getJSONObject(i).getString("objectId"));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return likers;
    }


}
