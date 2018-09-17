package com.alexzanderavila.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
    //ShapeRenderer shapeRenderer;
    Texture gameOver;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    BitmapFont font;

	int gameState = 0;
	float gravity = 2;

    Texture topTube;
    Texture bottomTube;

    float gap = 405;
    float maxTubeOffset;
    Random randomGenerator;

    float tubeVelocity = 6;
    int numberofTubes = 4;
    float[] tubeX = new float[numberofTubes];
    float[] tubeOffset = new float[numberofTubes];

    float tubeDistance;

    int score = 0;
    int scoringTube = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        background = new Texture("bg.png");
        gameOver = new Texture("gameover.png");
        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
        randomGenerator = new Random();
        tubeDistance = Gdx.graphics.getWidth() * 3 / 4;
        topTubeRectangles = new Rectangle[numberofTubes];
        bottomTubeRectangles = new Rectangle[numberofTubes];

        startGame();
	}

	@Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(gameState == 1) {

            if(Gdx.input.justTouched()) {
                velocity = -30;

            }
            for(int i = 0; i < numberofTubes; i++) {

                if(tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberofTubes * tubeDistance;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                } else {
                    tubeX[i] -= tubeVelocity;
                    if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2) {
                        score++;
                        if(scoringTube < numberofTubes - 1) {
                            scoringTube++;
                        } else {
                            scoringTube = 0;
                        }
                    }
                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }
            if(birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            } else {
                gameState = 2;
            }
        } else if (gameState == 0){

            if(Gdx.input.justTouched()) {
                gameState = 1;
            }
        }
         else if(gameState == 2) {
            if(birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            }
            batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
            if(Gdx.input.justTouched()) {
                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;

            }
        }
        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }


        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);
        //shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for(int i = 0; i < numberofTubes; i++)
        {
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                gameState = 2;
            }
        }

        //shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		birds[0].dispose();
		birds[1].dispose();
		topTube.dispose();
		bottomTube.dispose();
	}

	public void startGame() {
        birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
        for(int i = 0; i < numberofTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i*tubeDistance;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }
}
