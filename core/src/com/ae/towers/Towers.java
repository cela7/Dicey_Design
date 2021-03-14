package com.ae.towers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Towers extends ApplicationAdapter {
	private SpriteBatch batch;
	private SpriteBatch textBatch;
	private ArrayList<Texture> blocks;
	private World world;
	private OrthographicCamera camera;
	private float cameraTimer;
	private ArrayList<Block> blockList;
	private Texture purple;
	private ArrayList<Block> groundList;
	private GameManager gameManager;
	private Box2DDebugRenderer debugRenderer;
	private Matrix4 debugMatrix;
	private float accumulator;
	private float timeStep;
	private float scale = 8;
	private Vector3 touchCoords;
	private BitmapFont font;
	private int selectedBlock = -1;
	private float selectedBlockRotation = 0;
	private TInputProcessor inputProcessor;
	private int groundType = 1;
	private int winTimer = -1;
	private float[] colors;
	private int pauseWorldStep = 0;
	private ReentrantLock lock;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		textBatch = new SpriteBatch();
		blocks = new ArrayList<Texture>();
		blocks.add(new Texture("square.png"));
		blocks.add(new Texture("line.png"));
		blocks.add(new Texture("L.png"));
		blocks.add(new Texture("J.png"));
		blocks.add(new Texture("T.png"));
		blocks.add(new Texture("S.png"));
		blocks.add(new Texture("Z.png"));
		purple = new Texture("purple.png");
		world = new World(new Vector2(0f, -9.8f), false);
		camera = new OrthographicCamera(Gdx.graphics.getWidth()/scale, Gdx.graphics.getHeight()/scale);
		camera.viewportWidth = 135;
		camera.viewportHeight = 255.25f;
		lock = new ReentrantLock();

		cameraTimer = 0;
		//camera.translate(Gdx.graphics.getWidth()/scale/2, Gdx.graphics.getHeight()/scale/2);
		blockList = new ArrayList<Block>();
		groundList = new ArrayList<Block>();
		gameManager = new GameManager();
		font = new BitmapFont();
		font.getData().setScale(1f);
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		touchCoords = new Vector3(0, 0, 0);
		accumulator = 0f;
		timeStep = 1/60f;

		debugRenderer = new Box2DDebugRenderer();
		debugMatrix = new Matrix4(camera.combined);

		colors = new float[3];

		inputProcessor = new TInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		/*spawnBody(0,-3, 0);
		spawnBody(1,-3, 5);
		spawnBody(2,4, 10);
		spawnBody(0,5, 5);
		spawnBody(3,4, 0);
		spawnBody(4,-3, 10);
		spawnBody(5,4, 15);
		spawnBody(6,4, 20);*/
		//setupGame();
	}

	@Override
	public void render () {
		colors = calculateColors();
		Gdx.gl.glClearColor(colors[0], colors[1], colors[2], 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateCamera();
		debugMatrix = new Matrix4(camera.combined.cpy());
		debugMatrix.scale(scale, scale, 1);
		batch.setProjectionMatrix(debugMatrix);

		if(gameManager.resetGame())
		{
			selectedBlock = -1;
			selectedBlockRotation = 0;
			blockList.clear();
			groundList.clear();
			camera.position.y = 0;
			cameraTimer = 0;
			world = new World(new Vector2(0f, -9.8f), false);
		}

		batch.begin();

		if(gameManager.getState() == 0)
		{
			gameManager.drawMenu(batch);
		}
		else if(gameManager.getState() == 1)
		{
			drawBlocks(batch);
			gameManager.drawUI(batch, blocks, camera);
			gameManager.updateGame();
		}
		else if(gameManager.getState() == 2)
		{
			gameManager.drawAbout(batch);
		}
		batch.end();
		//debugRenderer.render(world, debugMatrix);

		textBatch.setProjectionMatrix(camera.combined.cpy().scl(1/scale));
		textBatch.begin();
		gameManager.drawText(textBatch, camera);
		textBatch.end();

		handleTouches();
		if(gameManager.getWind())
			addWind();
		removeWaste();
		checkWin();
		physicsStep(Gdx.graphics.getDeltaTime());
	}

	public float[] calculateColors()
	{
		if(blockList.size() > 0)
		{
			float r = Math.min(0.7f, camera.position.y / scale / 150f);
			float g = Math.min(0.3f, camera.position.y / scale / 350f);
			float b = Math.min(0.7f, camera.position.y / scale / 100f);
			return new float[] {r, g, b};
		}
		else
			return new float[] {0, 0, 0};
	}

	public void addWind()
	{
		for(Block b : blockList)
		{
			b.getBody().applyForceToCenter(gameManager.getWindForce(), false);
		}
	}

	public void checkWin()
	{
		if(selectedBlock == -1)
		{
			float highestY = Float.MIN_VALUE;
			for (int i = 0; i < blockList.size(); i++) {
				Block block = blockList.get(i);
				if (block.getBody().getPosition().y > highestY && Math.abs(block.getBody().getLinearVelocity().y) <= 8)
					highestY = block.getBody().getPosition().y;
			}
			if (highestY >= 100)
				gameManager.startWinTimer();
			else if (highestY < 100)
				gameManager.stopWinTimer();
		}
		else
			gameManager.stopWinTimer();
	}

	public void removeWaste()
	{
		int i = 0;
		while(i < blockList.size())
		{
			if(blockList.get(i).getBody().getPosition().y < -24) {
				world.destroyBody(blockList.get(i).getBody());
				blockList.remove(i);
				if(i == selectedBlock)
				{
					selectedBlock = -1;
					selectedBlockRotation = 0;
				}
				else if(i < selectedBlock)
				{
					selectedBlock--;
				}
				gameManager.takeDamage();
			}
			else
				i++;
		}
	}

	public void handleTouches()
	{
		// Game Touches
		if(gameManager.getState() == 0)
		{
			touchCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchCoords);
			touchCoords.scl(1 / scale);
			if(Gdx.input.justTouched())
			{
				if(gameManager.updateMenu(touchCoords))
					setupGame(gameManager.getGameType());
			}
		}
		if(gameManager.getState() == 1)
		{
			touchCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchCoords);
			touchCoords.scl(1 / scale);
			if (Gdx.input.justTouched() && inputProcessor.getTouch().z == 1) {
				gameManager.handleFirstTouch(touchCoords, camera);
				cameraTimer = 60;
				if (gameManager.getSelectedBlock() != -1) {
					spawnBody(gameManager.getSelectedBlock(), touchCoords.x, touchCoords.y);
					selectedBlock = blockList.size() - 1;
				}
			} else if (inputProcessor.getTouch().z == 2) {
				if (selectedBlock != -1) {
					Body body = blockList.get(selectedBlock).getBody();
					body.setLinearVelocity((touchCoords.x - body.getPosition().x) * 1 / Gdx.graphics.getDeltaTime() / 2, (touchCoords.y - (body.getPosition().y)) * 1 / Gdx.graphics.getDeltaTime() / 2);
					body.setTransform(body.getPosition(), 0);
					body.setAngularVelocity(0f);
				}
			} else if (inputProcessor.getTouch().z == 3) {
				if (selectedBlock != -1) {
					Body body = blockList.get(selectedBlock).getBody();
					body.setLinearVelocity(body.getLinearVelocity().x * 0.2f, body.getLinearVelocity().y * 0.2f);
					body.setAngularVelocity(0f);
					selectedBlock = -1;
					selectedBlockRotation = 0f;
					gameManager.setSelectedBlock(-1);
				}
			}

			if (inputProcessor.getRotate() != 0) {
				selectedBlockRotation += inputProcessor.getRotate() * 90;
				inputProcessor.setRotate(0);
			}
			if (selectedBlock != -1 && blockList.get(selectedBlock).getBody().getAngle() != selectedBlockRotation) {
				Body body = blockList.get(selectedBlock).getBody();
				body.setTransform(body.getPosition(), selectedBlockRotation * (float) Math.PI / 180f);
			}
		}
		else if(gameManager.getState() == 2)
		{
			touchCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchCoords);
			touchCoords.scl(1 / scale);
			if (Gdx.input.justTouched() && inputProcessor.getTouch().z == 1) {
				gameManager.handleFirstTouch(touchCoords, camera);
			}
		}
	}
	public void updateCamera()
	{
		float highestY = Float.MIN_VALUE;
		float x = 0;
		for(Block block : blockList)
		{
			if(block.getBody().getPosition().y > highestY)
				highestY = block.getBody().getPosition().y;
		}
		// check if x position should be updated
		/*if(selectedBlock != -1 && blockList.get(selectedBlock).getBody().getPosition().x < -6)
			x = -4 * scale;
		if(selectedBlock != -1 && blockList.get(selectedBlock).getBody().getPosition().x > 6)
			x = 4 * scale;*/
		if(cameraTimer > 0)
			cameraTimer--;
		Vector3 posCamera = new Vector3(x, highestY * scale, 0);
		if(cameraTimer <= 0) {
			camera.position.lerp(posCamera, 0.02f);
			camera.update();
		}
	}

	public void spawnBody(int type, float x, float y)
	{
		if(!world.isLocked()) {
			BodyDef def = new BodyDef();
			def.type = BodyDef.BodyType.DynamicBody;
			def.position.set(x, y);
			Body b = world.createBody(def);
			ArrayList<FixtureDef> fixtures = gameManager.getFixtureDef(type);

			for (int i = 0; i < fixtures.size(); i++) {
				if (fixtures.get(i) != null) {
					b.createFixture(fixtures.get(i));
					fixtures.get(i).shape.dispose();
				}
			}
			blockList.add(new Block(b, type));
		}
	}
	public void drawBlocks(SpriteBatch batch)
	{
		for(Block b : blockList)
		{
			batch.draw(blocks.get(b.getType()), b.getBody().getPosition().x - b.getWidth(), b.getBody().getPosition().y - b.getHeight() + b.getOffsetY(), b.getWidth(), b.getHeight() - b.getOffsetY(), b.getWidth() * 2, b.getHeight() * 2, 1, 1, b.getBody().getAngle() * 180/(float)Math.PI, 0, 0, blocks.get(b.getType()).getWidth(), blocks.get(b.getType()).getHeight(), false, false);
		}
		for(Block b : groundList)
		{
			batch.draw(purple, b.getBody().getPosition().x - b.getWidth() / 2, b.getBody().getPosition().y - b.getHeight() / 2, b.getWidth(), b.getHeight());
		}
	}

	public void physicsStep(float dt)
	{
		if(pauseWorldStep != 0) {
			float frameTime = Math.min(dt, 0.25f);
			accumulator += frameTime;
			while (accumulator > timeStep) {
				world.step(timeStep, 6, 2);
				accumulator -= timeStep;
			}
		}
		else
			pauseWorldStep--;
	}

	public void setupGame(int type)
	{
		// Classic
		if(type == 0)
		{
			BodyDef groundBodyDef = new BodyDef();
			groundBodyDef.position.set(new Vector2(0.1f, -14));

			Body groundBody = world.createBody(groundBodyDef);

			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox(camera.viewportWidth / scale / 2 - 0.2f, 2.0f);
			FixtureDef groundFixture = new FixtureDef();
			groundFixture.shape = groundBox;
			groundFixture.friction = 0.6f;
			groundFixture.density = 0f;
			groundBody.createFixture(groundFixture);

			groundList.add(new Block(groundBody, (int)(camera.viewportWidth / scale / 1 + 0.1f), 4));

			groundBox.dispose();
		}
		else if(type == 1)
		{
			BodyDef groundBodyDef = new BodyDef();
			groundBodyDef.position.set(new Vector2(-6.1f, -14));
			Body groundBody = world.createBody(groundBodyDef);
			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox(camera.viewportWidth / scale / 8 - 0.2f, 2.0f);
			FixtureDef groundFixture = new FixtureDef();
			groundFixture.shape = groundBox;
			groundFixture.friction = 0.6f;
			groundFixture.density = 0f;
			groundBody.createFixture(groundFixture);

			BodyDef groundBodyDef2 = new BodyDef();
			groundBodyDef2.position.set(new Vector2(6.1f, -14));
			Body groundBody2 = world.createBody(groundBodyDef2);
			groundBody2.createFixture(groundFixture);

			groundList.add(new Block(groundBody, (int)(camera.viewportWidth / scale / 4f), 4));
			groundList.add(new Block(groundBody2, (int)(camera.viewportWidth / scale / 4f), 4));

			groundBox.dispose();
		}
		else if(type == 2)
		{
			BodyDef groundBodyDef = new BodyDef();
			groundBodyDef.position.set(new Vector2(0.1f, -14));

			Body groundBody = world.createBody(groundBodyDef);

			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox(camera.viewportWidth / scale / 8, 2.0f);
			FixtureDef groundFixture = new FixtureDef();
			groundFixture.shape = groundBox;
			groundFixture.friction = 0.6f;
			groundFixture.density = 0f;
			groundBody.createFixture(groundFixture);

			groundList.add(new Block(groundBody, (int)(camera.viewportWidth / scale / 4), 4));

			groundBox.dispose();
		}
		else if(type == 3)
		{
			BodyDef groundBodyDef = new BodyDef();
			groundBodyDef.position.set(new Vector2(-4f, -14));
			Body groundBody = world.createBody(groundBodyDef);
			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox(camera.viewportWidth / scale / 16, 2.0f);
			FixtureDef groundFixture = new FixtureDef();
			groundFixture.shape = groundBox;
			groundFixture.friction = 0.6f;
			groundFixture.density = 0f;
			groundBody.createFixture(groundFixture);

			PolygonShape groundBox2 = new PolygonShape();
			groundBox2.setAsBox(camera.viewportWidth / scale / 8, 50.0f);
			groundFixture.shape = groundBox2;
			BodyDef groundBodyDef2 = new BodyDef();
			groundBodyDef2.position.set(new Vector2(9f, 2));
			Body groundBody2 = world.createBody(groundBodyDef2);
			groundBody2.createFixture(groundFixture);

			groundList.add(new Block(groundBody, (int)(camera.viewportWidth / scale / 8), 4));
			groundList.add(new Block(groundBody2, (int)(camera.viewportWidth / scale / 4), 100));

			groundBox.dispose();
			groundBox2.dispose();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		textBatch.dispose();
		for(Texture t : blocks)
			t.dispose();
		world.dispose();
		gameManager.dispose();
	}
}
