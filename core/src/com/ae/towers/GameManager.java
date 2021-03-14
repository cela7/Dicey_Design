package com.ae.towers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class GameManager {
    private int state;
    private int gameType;
    private float scale;
    public static int TOTAL_BLOCKS = 7;
    public static int GAME_TYPES = 4;
    private int[] blocksAvailable;
    private Texture selectionUI;
    private Texture meterStick;
    private Texture dot;
    private Texture play;
    private Texture left;
    private Texture right;
    private Texture smallLeft;
    private Texture smallRight;
    private Texture button;
    private Texture pressedButton;
    private Texture fullHeart;
    private Texture emptyHeart;
    private Texture finish;
    private Texture back;
    private Texture arrow;
    private Texture about;
    private int hearts;
    private int MAX_HEARTS = 3;
    private int immunityTimer;
    private BitmapFont font;
    GlyphLayout layout;
    private float leftBrickLoc = -6.8f;
    private float brickSpacing = 5.33333f;
    private int selectedBlock = -1;
    private boolean freeBuild = false;
    private boolean wind = false;
    private float windAngle = 0;
    private float windForce = 0;
    private int blockPage = 0;
    private boolean gameOver = false;
    private boolean reset = false;
    private int winTimer = -1;
    private boolean win = false;

    private float density = 10f;
    private float friction = 0.6f;
    private float restitution = 0.1f;

    public GameManager()
    {
        state = 0;
        gameType = 0;
        scale = 2f;
        hearts = 3;
        blocksAvailable = new int[3];
        selectionUI = new Texture("selectionUI.png");
        meterStick = new Texture("meterstick.png");
        dot = new Texture("dot.png");
        play = new Texture("play.png");
        left = new Texture("left.png");
        right = new Texture("right.png");
        smallLeft = new Texture("smallLeft.png");
        smallRight = new Texture("smallRight.png");
        button = new Texture("button.png");
        pressedButton = new Texture("pressedButton.png");
        fullHeart = new Texture("fullHeart.png");
        emptyHeart = new Texture("emptyHeart.png");
        finish = new Texture("finish.png");
        back = new Texture("back.png");
        arrow = new Texture("arrow.png");
        about = new Texture("about.png");
        font = new BitmapFont(Gdx.files.internal("verdana.fnt"),
                Gdx.files.internal("verdana.png"), false);
        font.getData().setScale(0.7f);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        generateNewBlocks();
    }

    public int getState()
    {
        return state;
    }
    public int getGameType()
    {
        return gameType;
    }

    public ArrayList<FixtureDef> getFixtureDef(int type)
    {
        PolygonShape shape = new PolygonShape();
        ArrayList<FixtureDef> fixtures = new ArrayList<FixtureDef>();

        // Square
        if(type == 0)
        {
            shape.setAsBox(2 * scale / 2, 2 * scale / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            fixtures.add(fixtureDef);
            //shape.dispose();
            return fixtures;
        }
        // Line
        else if(type == 1)
        {
            shape.setAsBox(4 * scale / 2, 1 * scale / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            fixtures.add(fixtureDef);
            //shape.dispose();
            return fixtures;
        }
        // J
        else if(type == 2)
        {
            shape.set(new Vector2[] {new Vector2(-1.5f * scale, 2 * scale),
                                     new Vector2(1.5f * scale, 2 * scale),
                                     new Vector2(1.5f * scale, 1 * scale),
                                     new Vector2(-1.5f * scale, 1 * scale)});
            PolygonShape shape2 = new PolygonShape();
            shape2.set(new Vector2[] {new Vector2(-0.5f * scale, 0),
                                      new Vector2(-0.5f * scale, 1 * scale),
                                      new Vector2(-1.5f * scale, 1 * scale),
                                      new Vector2(-1.5f * scale, 0)});
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            FixtureDef fixtureDef2 = new FixtureDef();
            fixtureDef2.shape = shape2;
            fixtureDef2.density = density;
            fixtureDef2.friction = friction;
            fixtureDef2.restitution = restitution;
            fixtures.add(fixtureDef);
            fixtures.add(fixtureDef2);
            //shape.dispose();
            //shape2.dispose();
            return fixtures;
        }
        // L
        else if(type == 3)
        {
            shape.set(new Vector2[] {new Vector2(-1.5f * scale, 1 * scale),
                    new Vector2(1.5f * scale, 1 * scale),
                    new Vector2(1.5f * scale, 0 * scale),
                    new Vector2(-1.5f * scale, 0 * scale)});
            PolygonShape shape2 = new PolygonShape();
            shape2.set(new Vector2[] {new Vector2(-0.5f * scale, 2 * scale),
                    new Vector2(-0.5f * scale, 1 * scale),
                    new Vector2(-1.5f * scale, 1 * scale),
                    new Vector2(-1.5f * scale, 2 * scale)});
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            FixtureDef fixtureDef2 = new FixtureDef();
            fixtureDef2.shape = shape2;
            fixtureDef2.density = density;
            fixtureDef2.friction = friction;
            fixtureDef2.restitution = restitution;
            fixtures.add(fixtureDef);
            fixtures.add(fixtureDef2);
            //shape.dispose();
            //shape2.dispose();
            return fixtures;
        }
        // T
        else if(type == 4)
        {
            shape.set(new Vector2[] {new Vector2(-1.5f * scale, 1 * scale),
                    new Vector2(1.5f * scale, 1 * scale),
                    new Vector2(1.5f * scale, 0 * scale),
                    new Vector2(-1.5f * scale, 0 * scale)});
            PolygonShape shape2 = new PolygonShape();
            shape2.set(new Vector2[] {new Vector2(-0.5f * scale, 2 * scale),
                    new Vector2(-0.5f * scale, 1 * scale),
                    new Vector2(0.5f * scale, 1 * scale),
                    new Vector2(0.5f * scale, 2 * scale)});
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            FixtureDef fixtureDef2 = new FixtureDef();
            fixtureDef2.shape = shape2;
            fixtureDef2.density = density;
            fixtureDef2.friction = friction;
            fixtureDef2.restitution = restitution;
            fixtures.add(fixtureDef);
            fixtures.add(fixtureDef2);
            //shape.dispose();
            //shape2.dispose();
            return fixtures;
        }
        // S
        else if(type == 5)
        {
            shape.set(new Vector2[] {new Vector2(-1.5f * scale, 1 * scale),
                    new Vector2(0.5f * scale, 1 * scale),
                    new Vector2(0.5f * scale, 0 * scale),
                    new Vector2(-1.5f * scale, 0 * scale)});
            PolygonShape shape2 = new PolygonShape();
            shape2.set(new Vector2[] {new Vector2(-0.5f * scale, 2 * scale),
                    new Vector2(-0.5f * scale, 1 * scale),
                    new Vector2(1.5f * scale, 1 * scale),
                    new Vector2(1.5f * scale, 2 * scale)});
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            FixtureDef fixtureDef2 = new FixtureDef();
            fixtureDef2.shape = shape2;
            fixtureDef2.density = density;
            fixtureDef2.friction = friction;
            fixtureDef2.restitution = restitution;
            fixtures.add(fixtureDef);
            fixtures.add(fixtureDef2);
            //shape.dispose();
            //shape2.dispose();
            return fixtures;
        }
        // Z
        else if(type == 6)
        {
            shape.set(new Vector2[] {new Vector2(1.5f * scale, 1 * scale),
                    new Vector2(-0.5f * scale, 1 * scale),
                    new Vector2(-0.5f * scale, 0 * scale),
                    new Vector2(1.5f * scale, 0 * scale)});
            PolygonShape shape2 = new PolygonShape();
            shape2.set(new Vector2[] {new Vector2(0.5f * scale, 2 * scale),
                    new Vector2(0.5f * scale, 1 * scale),
                    new Vector2(-1.5f * scale, 1 * scale),
                    new Vector2(-1.5f * scale, 2 * scale)});
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            FixtureDef fixtureDef2 = new FixtureDef();
            fixtureDef2.shape = shape2;
            fixtureDef2.density = density;
            fixtureDef2.friction = friction;
            fixtureDef2.restitution = restitution;
            fixtures.add(fixtureDef);
            fixtures.add(fixtureDef2);
            //shape.dispose();
            //shape2.dispose();
            return fixtures;
        }
        else
            return null;
    }

    public void drawMenu(SpriteBatch batch)
    {
        batch.draw(play, -4, -12, 8, 4);
        batch.draw(left, -8, -12, 4, 4);
        batch.draw(right, 4, -12, 4, 4);
        batch.draw(about, -8, 12, 16, 4);
        if(freeBuild)
            batch.draw(pressedButton, -8, -16, 8, 4);
        else
            batch.draw(button, -8, -16, 8, 4);
        if(wind)
            batch.draw(pressedButton, 0, -16, 8, 4);
        else
            batch.draw(button, 0, -16, 8, 4);
    }
    public void drawText(SpriteBatch batch, OrthographicCamera camera)
    {
        if(state == 0)
        {
            font.getData().setScale(4f);
            layout = new GlyphLayout(font, "Dicey Design");
            font.draw(batch, "Dicey Design", 0 - layout.width / 2, 600 - layout.height / 2);
            font.getData().setScale(2f);
            layout = new GlyphLayout(font, "About");
            font.draw(batch, "About", 0 - layout.width / 2, 945 - layout.height / 2);
            font.getData().setScale(2f);
            layout = new GlyphLayout(font, "Free Build");
            font.draw(batch, "Free Build", -250 - layout.width / 2, -850 - layout.height / 2);
            font.getData().setScale(2f);
            layout = new GlyphLayout(font, "Wind");
            font.draw(batch, "Wind", 250 - layout.width / 2, -850 - layout.height / 2);
            switch(gameType) {
                case 0:
                    font.getData().setScale(3f);
                    layout = new GlyphLayout(font, "Classic");
                    font.draw(batch, "Classic", 0 - layout.width / 2, 140 - layout.height / 2);
                    break;
                case 1:
                    font.getData().setScale(3f);
                    layout = new GlyphLayout(font, "Split");
                    font.draw(batch, "Split", 0 - layout.width / 2, 140 - layout.height / 2);
                    break;
                case 2:
                    font.getData().setScale(3f);
                    layout = new GlyphLayout(font, "Small");
                    font.draw(batch, "Small", 0 - layout.width / 2, 140 - layout.height / 2);
                    break;
                case 3:
                    font.getData().setScale(3f);
                    layout = new GlyphLayout(font, "Barrier");
                    font.draw(batch, "Barrier", 0 - layout.width / 2, 140 - layout.height / 2);
                    break;
                default:
                    font.getData().setScale(3f);
                    layout = new GlyphLayout(font, "Unknown");
                    font.draw(batch, "Unknown", 0 - layout.width / 2, 140 - layout.height / 2);
                    break;
            }
        }
        else if(state == 1)
        {
            font.getData().setScale(1.5f);
            layout = new GlyphLayout(font, Integer.toString((int)Math.round(camera.position.y / 8) * 10));
            font.draw(batch, Integer.toString((int)Math.round(camera.position.y / 8) * 10), -477 - layout.width / 2, -670 - layout.height / 2 + camera.position.y * 8);

            if(wind)
            {
                font.getData().setScale(1.5f);
                layout = new GlyphLayout(font, Float.toString(windForce));
                font.draw(batch, Float.toString(windForce), 440 - layout.width / 2, 580 - layout.height / 2 + camera.position.y * 8);
            }

            if(gameOver)
            {
                font.getData().setScale(3f);
                layout = new GlyphLayout(font, "Game Over");
                font.draw(batch, "Game Over", -layout.width / 2, 200 - layout.height / 2 + camera.position.y * 8);
            }
            if(winTimer > 0)
            {
                font.getData().setScale(2f);
                layout = new GlyphLayout(font, Integer.toString(winTimer / 60 + 1));
                font.draw(batch, Integer.toString(winTimer / 60 + 1), -layout.width / 2, 200 - layout.height / 2 + camera.position.y * 8);
            }
            if(win)
            {
                font.getData().setScale(3f);
                layout = new GlyphLayout(font, "You Win");
                font.draw(batch, "You Win", -layout.width / 2, 200 - layout.height / 2 + camera.position.y * 8);
            }
        }
        else if(state == 2)
        {
            font.getData().setScale(2f);
            font.draw(batch, "Dicey Design", -450, 950);
            font.getData().setScale(1.5f);
            font.draw(batch, "Dicey Design is a game designed to\nprovide a quick distraction during our\ntime in quarantine and teach people\nbasic physics concepts in a fun way.", -450, 850);
            font.getData().setScale(1.5f);
            font.draw(batch, "Tap here to watch our video", -450, 600);
            font.getData().setScale(2f);
            font.draw(batch, "How to Play", -450, 475);
            font.getData().setScale(1.5f);
            font.draw(batch, "There are many different options to\nselect before you play. Press the left\nor right arrow to choose a stage to\nplay on, and press the play button\nto start. Selecting free build allows\nyou to select any block at any time,\nall with no risk of losing. Select wind\nto play with a constant strong wind\nthat will blow over your pieces.\nTo play the game, simply drag the\nblock you want from the top of the\nscreen and gently place it where you\nwant it to go. You can rotate a block\nyou are holding by tapping the\nscreen while holding the block.\nTower up 1000 meters to win!", -450, 375);
            font.getData().setScale(2f);
            font.draw(batch, "References", -450, -650);
            font.getData().setScale(1.5f);
            font.draw(batch, "Made with libGDX (Java), with\nBox2D for physics", -450, -750);
        }
    }
    public boolean updateMenu(Vector3 touchCoords)
    {
        if(touchCoords.x < 4 && touchCoords.x > -4 && touchCoords.y < -8 && touchCoords.y > -12)
        {
            state = 1;
            generateNewBlocks();
            if(wind)
            {
                windAngle = (float)((Math.PI / 180) * (Math.random() * 30 - 15));
                if(Math.random() < 0.5)
                    windAngle += (float)Math.PI;
                windForce = (float)Math.round((Math.random() * 20) + 20);
            }
            return true;
        }
        else if(touchCoords.x < -4 && touchCoords.x > -8 && touchCoords.y < -8 && touchCoords.y > -12)
        {
            gameType--;
        }
        else if(touchCoords.x < 8 && touchCoords.x > 4 && touchCoords.y < -8 && touchCoords.y > -12)
        {
            gameType++;
        }
        // free build
        if(touchCoords.x < 0 && touchCoords.x > -8 && touchCoords.y < -12 && touchCoords.y > -16)
        {
            freeBuild = !freeBuild;
        }
        if(touchCoords.x > 0 && touchCoords.x < 8 && touchCoords.y < -12 && touchCoords.y > -16)
        {
            wind = !wind;
        }
        if(touchCoords.y < 16 && touchCoords.y > 12)
        {
            state = 2;
        }

        if(gameType >= GAME_TYPES)
            gameType = 0;
        else if(gameType < 0)
            gameType = GAME_TYPES - 1;
        return false;
    }

    public void drawUI(SpriteBatch batch, ArrayList<Texture> blocks, OrthographicCamera camera)
    {
        batch.draw(selectionUI, -8, 12 + camera.position.y / 8, 16, 4);
        batch.draw(meterStick, -8, -10 + camera.position.y / 8, 1, 20);
        batch.draw(dot, -8, Math.min(9.6f + camera.position.y / 8, -10.4f + camera.position.y / 40 + camera.position.y / 8), 1, 1);
        batch.draw(finish, -8, 100, 16, 0.25f);
        batch.draw(back, 5.5f, -16 + camera.position.y / 8, 3, 3);
        if(wind)
        {
            batch.draw(arrow, 5.8f, 9.5f + camera.position.y / 8, 1, 0.75f, 2, 1.5f, 1, 1, (float)(windAngle * 180/Math.PI), 0, 0, 24, 16, false, false);
        }
        for (int i = 0; i < blocksAvailable.length; i++) {
            if(blocksAvailable[i] < blocks.size())
                batch.draw(blocks.get(blocksAvailable[i]), i * brickSpacing + leftBrickLoc + Block.getDrawX(blocksAvailable[i]), 13 + camera.position.y / 8, blocks.get(blocksAvailable[i]).getWidth() / 32, blocks.get(blocksAvailable[i]).getHeight() / 32);
        }
        if(freeBuild)
        {
            batch.draw(smallLeft, -6, 10.5f + camera.position.y / 8, 6, 1.5f);
            batch.draw(smallRight, 0, 10.5f + camera.position.y / 8, 6, 1.5f);
        }
        else
        {
            for(int i = 1; i <= MAX_HEARTS; i++)
            {
                if(i <= hearts && immunityTimer % 30 < 15)
                {
                    batch.draw(fullHeart, (i - MAX_HEARTS / 2) * 3 - fullHeart.getWidth() / 8, -15 + camera.position.y / 8, 2, 2);
                }
                else if(immunityTimer % 30 < 15 || gameOver)
                {
                    batch.draw(emptyHeart, (i - MAX_HEARTS / 2) * 3 - fullHeart.getWidth() / 8, -15 + camera.position.y / 8, 2, 2);
                }
            }
        }
    }
    public void drawAbout(SpriteBatch batch)
    {
        batch.draw(back, 5.5f, -16, 3, 3);
    }
    public void updateGame()
    {
        if(immunityTimer > 0)
        {
            immunityTimer--;
        }
        if(hearts <= 0)
        {
            gameOver = true;
        }
        if(winTimer > 0)
            winTimer--;
        if(winTimer == 0)
            win = true;
        if(wind)
        {
            windAngle += Math.random() * 0.01 - 0.005;
            if(Math.random() * 100 < 0.5)
            {
                windForce += Math.floor(Math.random() * 3) - 1;
                if(windForce < 20)
                    windForce = 20;
                if(windForce > 40)
                    windForce = 40;
            }
        }
    }
    public boolean resetGame()
    {
        if(reset)
        {
            reset = false;
            return true;
        }
        return false;
    }

    public void takeDamage()
    {
        if(!freeBuild && immunityTimer == 0)
        {
            hearts--;
            immunityTimer = 180;
        }
    }
    public void handleFirstTouch(Vector3 touchCoords, OrthographicCamera camera)
    {
        if(!gameOver && !win)
        {
            if (touchCoords.y >= 12f + camera.position.y / 8) {
                if (touchCoords.x <= leftBrickLoc + brickSpacing) {
                    selectedBlock = blocksAvailable[0];
                } else if (touchCoords.x <= leftBrickLoc + brickSpacing * 2 && blocksAvailable[1] < TOTAL_BLOCKS) {
                    selectedBlock = blocksAvailable[1];
                } else if (blocksAvailable[2] < TOTAL_BLOCKS) {
                    selectedBlock = blocksAvailable[2];
                }
                generateNewBlocks();
            }
        }
        else
        {
            resetVars();
        }
        // free build
        if(freeBuild)
        {
            if(touchCoords.y > 10.5 + camera.position.y / 8 && touchCoords.y < 12 + camera.position.y / 8)
            {
                if(touchCoords.x < 0 && touchCoords.x > -6)
                {
                    blockPage--;
                    if(blockPage < 0)
                        blockPage = TOTAL_BLOCKS / 3;
                    generateNewBlocks();
                }
                else if(touchCoords.x > 0 && touchCoords.x < 6)
                {
                    blockPage++;
                    if(blockPage > TOTAL_BLOCKS / 3)
                        blockPage = 0;
                    generateNewBlocks();
                }
            }
        }
        if(state == 1 || state == 2)
        {
            //batch.draw(back, 5.5f, -16 + camera.position.y / 8, 3, 3);
            if(touchCoords.y > -16 + camera.position.y / 8 && touchCoords.y < -13 + camera.position.y / 8 && touchCoords.x > 5.5f && touchCoords.x < 8.5f)
            {
                resetVars();
            }
        }
        if(state == 2)
        {
            if(touchCoords.y < 11 && touchCoords.y > 8)
            {
                Gdx.net.openURI("https://www.youtube.com/watch?v=NZjb9JUy5ZY&feature=youtu.be");
            }
        }
    }

    public void resetVars()
    {
        state = 0;
        gameType = 0;
        hearts = 3;
        immunityTimer = 0;
        selectedBlock = -1;
        winTimer = -1;
        windAngle = 0;
        windForce = 0;
        gameOver = false;
        win = false;
        reset = true;
    }

    public void generateNewBlocks()
    {
        if(!freeBuild)
        {
            for (int i = 0; i < blocksAvailable.length; i++) {
                blocksAvailable[i] = (int) (Math.random() * TOTAL_BLOCKS);
            }
        }
        else
        {
            blocksAvailable[0] = blockPage * 3;
            blocksAvailable[1] = 1 + blockPage * 3;
            blocksAvailable[2] = 2 + blockPage * 3;
        }
    }
    public int getSelectedBlock()
    {
        return selectedBlock;
    }
    public void setSelectedBlock(int s)
    {
        selectedBlock = s;
    }

    public void startWinTimer()
    {
        if(winTimer == -1)
        {
            winTimer = 180;
        }
    }
    public void stopWinTimer()
    {
        winTimer = -1;
    }
    public int getWinTimer()
    {
        return winTimer;
    }
    public boolean getWind()
    {
        return wind;
    }
    public Vector2 getWindForce()
    {
        return new Vector2((float)Math.cos(windAngle) * windForce * 20, (float)Math.sin(windAngle) * windForce * 20);
    }

    public void dispose()
    {
        selectionUI.dispose();
        meterStick.dispose();
        dot.dispose();
        play.dispose();
        left.dispose();
        right.dispose();
        smallLeft.dispose();
        smallRight.dispose();
        button.dispose();
        pressedButton.dispose();
        fullHeart.dispose();
        emptyHeart.dispose();
        finish.dispose();
        back.dispose();
        arrow.dispose();
        about.dispose();
        font.dispose();
    }
}
