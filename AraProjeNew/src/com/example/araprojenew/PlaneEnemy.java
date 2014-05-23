package com.example.araprojenew;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;

import android.app.Activity;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
//TODO plane i�inde halledilmeliydi
public class PlaneEnemy extends AnimatedSprite{
	public Body body; //public for hud can get plane vars
	//public AnimatedSprite planeSpriteLeft,planeSpriteRight;
	private boolean isGas;
	private boolean isBreak;
	private final int maxSpeed=20;
	public ArrayList<Body> shots;
	public Sprite shotSprite;
	public int shotIndex=0;
	public int maxShot=35;
	public boolean isShot=false;
	public FixtureDef planefix;
	public int health=70;
	private AnimatedSprite explosionSprite;
	boolean animationFlagForPlaneCrush = true;
	public int shotType;
	
	public PlaneEnemy(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
    {
        super(pX, pY, ResourcesManager.getInstance().plane_regions[0], vbo);
        createPhysics(camera, physicsWorld);
        animate(1);
        shots = new ArrayList<Body>();
        createShots(physicsWorld,vbo,camera);
        explosionSprite = new AnimatedSprite(0, 0, ResourcesManager.getInstance().explosion_region, vbo);
    }
	

	
	private void createShots(PhysicsWorld physicsWorld,VertexBufferObjectManager vbo,final Camera camera) {		
		for(int i=0;i<maxShot;i++){
			shotSprite = new Sprite(0,9999,ResourcesManager.getInstance().shot_region,vbo);
			shots.add(PhysicsFactory.createBoxBody(physicsWorld, shotSprite, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(0, 0, 0)));
			shots.get(i).setUserData(shotSprite);
			shots.get(i).getMassData().mass = 0f;  
			shots.get(i).setBullet(true);

			physicsWorld.registerPhysicsConnector(new PhysicsConnector(shotSprite, shots.get(i), true, true){
				@Override
		        public void onUpdate(float pSecondsElapsed)
		        {
		            super.onUpdate(pSecondsElapsed);
		            camera.onUpdate(0.1f);
		            
		        }
			});
			}
			}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{   
	    body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	    body.setUserData("planeEnemy");
	    for(int i=0; i<body.getFixtureList().size();i++){
	        this.body.getFixtureList().get(i).setSensor(true);
	    }
	    physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body,true , true)
	    {
	        @Override
	        public void onUpdate(float pSecondsElapsed)
	        {
	        	PlaneEnemy.this.body.setTransform((PlaneEnemy.this.getX()+PlaneEnemy.this.getWidth()/2)/32f, (PlaneEnemy.this.getY()+PlaneEnemy.this.getHeight()/2)/32f, 
	            		MathUtils.degToRad(PlaneEnemy.this.getRotation()));
	        	if(isShot){
	        		if(shotType == 0){
	        			shoot();
	        		}
	        		else if(shotType == 2){
	        			doubleShot();
	        		}
	        		else if(shotType == 3){
	        			tripleShot();
	        		}
	        		isShot = false;
	        	}
	            
	            /*super.onUpdate(pSecondsElapsed);
	            camera.onUpdate(0.1f);
	            body.setTransform((body.getPosition().x+50) % 50, body.getPosition().y, body.getAngle());

	            body.setLinearVelocity((float)(body.getLinearVelocity().len()*Math.cos(body.getAngle())), (float)(body.getLinearVelocity().len()*Math.sin(body.getAngle())));
	            if(isGas)  body.applyForce((float) (10*Math.cos(body.getAngle())), (float) (10*Math.sin(body.getAngle())), mShapeHalfBaseWidth, mShapeHalfBaseHeight);
	            else if(isBreak && body.getLinearVelocity().len() > 0) body.applyForce((float) (-7*Math.cos(body.getAngle())), 0, mShapeHalfBaseWidth, mShapeHalfBaseHeight);
	            body.applyForce((float) (-1*Math.cos(body.getAngle())), (float) (-1*Math.sin(body.getAngle())), mShapeHalfBaseWidth, mShapeHalfBaseHeight);
	            if(body.getLinearVelocity().len()>maxSpeed) body.setLinearVelocity((float)Math.cos(body.getAngle())*maxSpeed, (float)Math.sin(body.getAngle())*maxSpeed); 
	           
	            if(body.getLinearVelocity().len() < 5) {
	            	body.setTransform(body.getPosition().x, body.getPosition().y+0.1f, body.getAngle());
	            }
	            else if(body.getLinearVelocity().len() < 10) {
	            	 body.setTransform(body.getPosition().x, body.getPosition().y+0.05f, body.getAngle());
	            }
	            else if(body.getLinearVelocity().len() < 15) {
	            	 body.setTransform(body.getPosition().x, body.getPosition().y+0.01f, body.getAngle());
	            }
	            //planeSpriteLeft.setPosition(getX()-GameScene.WORLD_WIDTH,getY());
	            //planeSpriteRight.setPosition(getX()+GameScene.WORLD_WIDTH,getY());
	            /* planeSpriteLeft.setPosition(getX()-150,getY());
	            planeSpriteRight.setPosition(getX()+150,getY());*/
	        }
	    });
	    
	}

	public void gasShip(Boolean status) {
		isGas = status;
	}


	public void breakShip(Boolean status) {
		isBreak = status;	
	}



	public void shoot() {
		float angle = body.getAngle();
		float x = body.getPosition().x+1*(float)Math.cos(angle);
		float y = body.getPosition().y+1*(float)Math.sin(angle);
		shots.get(shotIndex).setTransform(x,y, angle);
		shots.get(shotIndex).setLinearVelocity(35*(float)Math.cos(shots.get(shotIndex).getAngle()), 35*(float)Math.sin(shots.get(shotIndex).getAngle()));
		shotIndex = (shotIndex+1)% maxShot;		
		ResourcesManager.getInstance().fireSound.play();
	}
	
	public void shoot(float dx,float dy,float dAngle) {
		float angle = body.getAngle()+dAngle;
		float x = body.getPosition().x+dx+1*(float)Math.cos(angle);
		float y = body.getPosition().y+dy+1*(float)Math.sin(angle);
		shots.get(shotIndex).setTransform(x,y, angle);
		shots.get(shotIndex).setLinearVelocity(35*(float)Math.cos(shots.get(shotIndex).getAngle()), 35*(float)Math.sin(shots.get(shotIndex).getAngle()));
		shotIndex = (shotIndex+1)% maxShot;	
		ResourcesManager.getInstance().fireSound.play();
	}
	
public void alternateShoot(){
		ResourcesManager.getInstance().alternateFireSound.play();
	}
	
	public void doubleShot(){
		shoot(shotSprite.getWidth()/32,shotSprite.getHeightScaled()/32,0);
		shoot(-shotSprite.getWidth()/32,-shotSprite.getHeightScaled()/32,0);
	}
	
	public void tripleShot(){
		shoot(0,0,0.5f);
		shoot(0,0,0);
		shoot(0,0,-0.5f);
	}

	public void crush(){
		try{
			this.getParent().attachChild(explosionSprite);
		}
		catch(Exception e){
			
		}
		if( animationFlagForPlaneCrush){
    		explosionSprite.setPosition(this);
    		explosionSprite.setVisible(true);
    		this.setVisible(false);
    		//explosionSprite.animate(100,false);
    		explosionSprite.animate(100,false, new IAnimationListener() { 		
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,int pInitialLoopCount) {}					
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,int pRemainingLoopCount, int pInitialLoopCount) {
												}								
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,int pOldFrameIndex, int pNewFrameIndex) {}
				
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {

				explosionSprite.setVisible(false);
				respawn();
				}
			});
    	
    		animationFlagForPlaneCrush= false;
    	}
	}
	public void respawn(){
		this.setVisible(true);
		animationFlagForPlaneCrush = true;
		this.body.setTransform(0,0,0);
		health = 100;
		
	}
}
