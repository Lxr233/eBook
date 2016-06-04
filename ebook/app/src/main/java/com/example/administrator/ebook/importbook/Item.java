package com.example.administrator.ebook.importbook;

/**
 * Created by Lxr on 2016/6/3.
 */
public class Item implements Comparable<Item>{
    private String name;
    private String path;
    private String type;
    private String msg;
    private boolean hasImport = false;

    public Item(String n,String m, String p, String t)
    {
        name = n;
        msg = m;
        path = p;
        type = t;

    }

    public Item(String n,String m, String p, String t,boolean h)
    {
        name = n;
        msg = m;
        path = p;
        type = t;
        hasImport = h;

    }
    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }
    public boolean getHasImport(){return hasImport;}
    public String getType(){
        return type;
    }
    public String getMsg(){
        return msg;
    }


    public int compareTo(Item o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
