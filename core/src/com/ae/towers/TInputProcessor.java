package com.ae.towers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class TInputProcessor implements InputProcessor {

    private Vector3 touch = new Vector3();
    private int rotate = 0;

    public Vector3 getTouch()
    {
        return touch;
    }
    public int getRotate()
    {
        return rotate;
    }
    public void setRotate(int rotate)
    {
        this.rotate = rotate;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if(pointer == 0)
        {
            touch.x = x;
            touch.y = y;
            touch.z = 1;
        }
        else
            rotate = 1;
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if(pointer == 0)
        {
            touch.x = x;
            touch.y = y;
            touch.z = 3;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if(pointer == 0)
        {
            touch.x = x;
            touch.y = y;
            touch.z = 2;
        }
        return true;
    }

    //@Override
    public boolean touchMoved(int x, int y) {
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
}
