package com.example.soundplayer;

public class Sound {

    private int redId;
    private String name;

    public Sound(String name, int redId){
        this.name = name;
        this.redId = redId;
    }

    public int getRedId() {
        return redId;
    }

    public String getName() {
        return name;
    }
}

enum Sounds{

    c("c", new Sound("c", R.raw.c)),
    d("d", new Sound("d", R.raw.d)),
    e("e", new Sound("e", R.raw.e)),
    f("f", new Sound("f", R.raw.f)),
    g("g", new Sound("g", R.raw.g)),
    a("a", new Sound("a", R.raw.a)),
    h("h", new Sound("h", R.raw.h)),
    c2("c2", new Sound("c2", R.raw.c2));

    private final String name;
    private final Sound soud;

    Sounds(final String name, final Sound sound){
        this.name = name;
        this.soud = sound;
    }

    public String getName(){
        return name;
    }

    public Sound getSoud(){
        return soud;
    }

    static Sound get(String name){
        for (final Sounds sounds : values()) {
            if (sounds.getName().equals(name)) {
                return sounds.soud;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}