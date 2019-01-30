package com.example.android.news_app.NYTimes;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultimediaArrayAdapter extends TypeAdapter<Multimedia[]> {
    int counter= 0;
    Gson gson = new Gson();

    public void write(JsonWriter out, Multimedia[] value) throws IOException {
        String json = gson.toJson(value);
        out.value(json);
    }

    @Override
    public Multimedia[] read(JsonReader in) throws IOException {
        Multimedia[] arr = new Multimedia[0];
        List<Multimedia> list = new ArrayList<Multimedia>();
        Multimedia multimedia;
        JsonToken type = in.peek();

        if(!type.toString().equals("STRING")){
            in.beginArray();
            while(in.hasNext()){
                multimedia = getMultimedia(in);
                list.add(multimedia);
            }
            in.endArray();
            arr = listToArray(list);
        }else{
            in.nextString();
        }

        return arr;
    }

    public Multimedia getMultimedia(JsonReader in) throws IOException{
        Multimedia image = new Multimedia();
        JsonToken type;
        try{
            type = in.peek();
            in.beginObject();
            image = new Multimedia();
            type = in.peek();
            if(type.toString().equals("NAME")){
                if(in.nextName().equals("url")){

                    if(isRightType(in.peek().toString(),"STRING")){
                        image.url = in.nextString();
                    }else{
                        in.nextNull();
                    }

                    in.nextName();
                    if(isRightType(in.peek().toString(),"STRING")){
                        in.nextString();
                    }else{
                        in.nextNull();
                    }

                    in.nextName();
                    image.height = in.nextInt();
                    in.nextName();
                    image.width = in.nextInt();
                    in.nextName();
                    if(isRightType(in.peek().toString(),"STRING")){
                        in.nextString();
                    }else{
                        in.nextNull();
                    }

                    in.nextName();
                    if(isRightType(in.peek().toString(),"STRING")){
                        in.nextString();
                    }else{
                        in.nextNull();
                    }

                    in.nextName();
                    if(isRightType(in.peek().toString(),"STRING")){
                        image.caption = in.nextString();
                    }else{
                        in.nextNull();
                    }

                    in.nextName();
                    if(isRightType(in.peek().toString(),"STRING")){
                        image.copyright = in.nextString();
                    }else{
                        in.nextNull();
                    }
                }
            }
            in.endObject();
        }catch(JsonSyntaxException e){
            return null;
        }
        return image;
    }


    public boolean isRightType(String type, String expectedType){
        return type.equals(expectedType);
    }
    public Multimedia[] listToArray(List<Multimedia> list){
        int size = list.size();
        Multimedia[] arr = new Multimedia[size];

        for(int i = 0; i < size; i++){
            arr[i] = list.get(i);
        }
        return arr;
    }
}
