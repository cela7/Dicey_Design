package com.ae.towers;

import com.badlogic.gdx.physics.box2d.Body;

public class Block {
    private Body body;
    private int type;
    private int w;
    private int h;
    private int oy;
    private float drawX;
    public Block(Body body, int type)
    {
        this.body = body;
        this.type = type;
        this.oy = 0;
        this.drawX = 0;
        switch(type)
        {
            case 0:
                w = 2;
                h = 2;
                drawX = 0.5f;
                break;
            case 1:
                w = 4;
                h = 1;
                break;
            default:
                w = 3;
                h = 2;
                oy = 2;
                break;
        }
    }
    public Block(Body body, int width, int height)
    {
        this.body = body;
        this.w = width;
        this.h = height;
    }

    public int getType()
    {
        return type;
    }
    public Body getBody()
    {
        return body;
    }
    public int getWidth()
    {
        return w;
    }
    public int getHeight()
    {
        return h;
    }
    public int getOffsetY()
    {
        return oy;
    }
    public static float getDrawX(int t) {
        if(t == 0)
            return 0.5f;
        else
            return 0;
    }
}
