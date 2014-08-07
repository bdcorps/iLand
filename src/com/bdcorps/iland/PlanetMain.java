package com.bdcorps.iland;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.BatchedPseudoSpriteParticleSystem;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.view.ConfigChooser;
import org.andengine.opengl.view.EngineRenderer;
import org.andengine.opengl.view.IRendererListener;
import org.andengine.util.modifier.ease.EaseBackInCustom;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeather.SEARCH_MODE;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.opengl.GLES20;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

public class PlanetMain extends BaseLiveWallpaperService implements
		OnSharedPreferenceChangeListener, IOffsetsChanged,
		IOnSceneTouchListener ,YahooWeatherInfoListener,
	    YahooWeatherExceptionListener{
	// ===========================================================
	// Master TO-DOs
	// ===========================================================

	// ===========================================================
	// Constants
	// ===========================================================
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
	public static final String SHARED_PREFS_NAME = "livewallpaperservicesettings";

	private SharedPreferences mSharedPreferences;

	Display display;
	int rotation = 1;
	int oldRotation = 0;

	ZoomCamera zoomCamera;
	Scene scene;

	Sprite planet_1;
	BitmapTextureAtlas planet_1Texture;
	ITextureRegion planet_1Region;
	LoopEntityModifier planet_1Modifier;

	Sprite planet_2;
	BitmapTextureAtlas planet_2Texture;
	ITextureRegion planet_2Region;
	LoopEntityModifier planet_2Modifier;

	Sprite planet_3;
	BitmapTextureAtlas planet_3Texture;
	ITextureRegion planet_3Region;
	LoopEntityModifier planet_3Modifier;

	Sprite scene_1;
	BitmapTextureAtlas scene_1Texture;
	ITextureRegion scene_1Region;
	LoopEntityModifier scene_1Modifier;

	Sprite scene_2;
	BitmapTextureAtlas scene_2Texture;
	ITextureRegion scene_2Region;
	LoopEntityModifier scene_2Modifier;

	Sprite scene_3;
	BitmapTextureAtlas scene_3Texture;
	ITextureRegion scene_3Region;
	LoopEntityModifier scene_3Modifier;

	Sprite sun;
	BitmapTextureAtlas sunTexture;
	ITextureRegion sunRegion;
	LoopEntityModifier sunModifier;

	Sprite cloud;
	BitmapTextureAtlas cloudTexture;
	ITextureRegion cloudRegion;
	LoopEntityModifier cloudModifier;

	SequenceEntityModifier planet_move;

	Sprite bg;
	BitmapTextureAtlas bgTexture;
	ITextureRegion bgRegion;
	LoopEntityModifier bgModifier;

	Sprite rd;
	BitmapTextureAtlas rdTexture;
	ITextureRegion rdRegion;
	LoopEntityModifier rdModifier;

	Sprite snow;
	BitmapTextureAtlas snowTexture;
	ITextureRegion snowRegion;
	LoopEntityModifier snowModifier;

	Sprite lf;
	BitmapTextureAtlas lfTexture;
	ITextureRegion lfRegion;
	LoopEntityModifier lfModifier;

	String androidAssetPath = "android.png";
	boolean assetsCreated = false;

	Time time;
	String assetSuffix = "noon";
	BatchedPseudoSpriteParticleSystem leafSystem;
	BatchedPseudoSpriteParticleSystem rainSystem;
	BatchedPseudoSpriteParticleSystem snowSystem;
	
	private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pSharedPrefs,
			String pKey) {
		if (assetsCreated) {
			resetAndroidAsset();
		}
	}

	@Override
	public void offsetsChanged(float xOffset, float yOffset, float xOffsetStep,
			float yOffsetStep, int xPixelOffset, int yPixelOffset) {

		if (mEngine.getCamera() != null) {
			// mEngine.getCamera().setCenter(((480 * xOffset) + 240), 400);
		}

	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void loadOrientation() {
		display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
	}

	public void checkOrientation() {
		rotation = display.getOrientation();
		if (oldRotation != rotation) {
			oldRotation = rotation;
			if (rotation == Surface.ROTATION_90
					|| rotation == Surface.ROTATION_270) {
				mEngine.getCamera().setCenter(CAMERA_WIDTH / 2, 540);
				scene.setScaleX(.5f);
				scene.setScaleY(1.4f);
			} else {
				if (mEngine.getCamera().getCenterY() != CAMERA_HEIGHT / 2) {
					mEngine.getCamera().setCenter(((CAMERA_WIDTH)),
							CAMERA_HEIGHT);
				}
				scene.setScale(1);
			}
		}

	}

	public void initializePreferences() {
		mSharedPreferences = PlanetMain.this.getSharedPreferences(
				SHARED_PREFS_NAME, 0);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mSharedPreferences, null);
	}

	// ===========================================================
	// Overrides
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		zoomCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), zoomCamera);
		engineOptions.getRenderOptions().setDithering(true);
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		scene = new Scene();
		time = new Time();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		scene.registerUpdateHandler(new IUpdateHandler() {
			public void reset() {

			}

			public void onUpdate(float pSecondsElapsed) {
				update();
			}
		});

		initializePreferences();
		// scene.setScale(0.5f);

		// BG
		bgTexture = new BitmapTextureAtlas(this.getTextureManager(), 1300,
				1300, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		bgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bgTexture, this, "bg_" + assetSuffix + ".png", 0, 0);
		bg = new Sprite(0, 0, bgRegion, this.getVertexBufferObjectManager());
		bgTexture.load();
		scene.attachChild(bg);
		/*
		 * scene.setBackgroundEnabled(true);
		 * 
		 * RepeatingSpriteBackground mGrassBackground = new
		 * RepeatingSpriteBackground(1024, 1024, this.getTextureManager(),
		 * AssetBitmapTextureAtlasSource.create(this.getAssets(),
		 * "gfx/background_grass.png"), this.getVertexBufferObjectManager());
		 * scene.setBackground(mGrassBackground);
		 */

		// Scene 1
		scene_1Texture = new BitmapTextureAtlas(this.getTextureManager(), 700,
				700, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		scene_1Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				scene_1Texture, this, "scene_1_" + assetSuffix + ".png", 0, 0);
		scene_1 = new Sprite(0, 0, scene_1Region,
				this.getVertexBufferObjectManager());
		scene_1Texture.load();
		scene.attachChild(scene_1);

		scene_1Modifier = new LoopEntityModifier(new RotationModifier(200, 0,
				360));
		scene_1.registerEntityModifier(scene_1Modifier);

		// Planet 1
		planet_1Texture = new BitmapTextureAtlas(this.getTextureManager(), 700,
				700, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		planet_1Region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(planet_1Texture, this, "planet_1_"
						+ assetSuffix + ".png", 0, 0);
		planet_1 = new Sprite(0, 0, planet_1Region,
				this.getVertexBufferObjectManager());
		planet_1Texture.load();
		scene.attachChild(planet_1);

		planet_1Modifier = new LoopEntityModifier(new RotationModifier(200, 0,
				360));
		planet_1.registerEntityModifier(planet_1Modifier);

		// Scene 2
		scene_2Texture = new BitmapTextureAtlas(this.getTextureManager(), 700,
				700, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		scene_2Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				scene_2Texture, this, "scene_2_" + assetSuffix + ".png", 0, 0);
		scene_2 = new Sprite(0, 0, scene_2Region,
				this.getVertexBufferObjectManager());
		scene_2Texture.load();
		scene.attachChild(scene_2);

		scene_2Modifier = new LoopEntityModifier(new RotationModifier(150, 360,
				0));
		scene_2.registerEntityModifier(scene_2Modifier);

		// Planet 2
		planet_2Texture = new BitmapTextureAtlas(this.getTextureManager(),
				1800, 1800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		planet_2Region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(planet_2Texture, this, "planet_2_"
						+ assetSuffix + ".png", 0, 0);
		planet_2 = new Sprite(0, 0, planet_2Region,
				this.getVertexBufferObjectManager());
		planet_2Texture.load();
		scene.attachChild(planet_2);
		planet_2Modifier = new LoopEntityModifier(new RotationModifier(150,
				360, 0));
		planet_2.registerEntityModifier(planet_2Modifier);

		// Scene 3
		scene_3Texture = new BitmapTextureAtlas(this.getTextureManager(), 1800,
				1800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		scene_3Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				scene_3Texture, this, "scene_3_" + assetSuffix + ".png", 0, 0);
		scene_3 = new Sprite(0, 0, scene_3Region,
				this.getVertexBufferObjectManager());

		scene_3Texture.load();

		scene.attachChild(scene_3);

		scene_3Modifier = new LoopEntityModifier(new RotationModifier(200, 0,
				360));
		scene_3.registerEntityModifier(scene_3Modifier);

		// Planet 3
		planet_3Texture = new BitmapTextureAtlas(this.getTextureManager(),
				1800, 1800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		planet_3Region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(planet_3Texture, this, "planet_3_"
						+ assetSuffix + ".png", 0, 0);
		planet_3 = new Sprite(0, 0, planet_3Region,
				this.getVertexBufferObjectManager());
		planet_3Texture.load();
		scene.attachChild(planet_3);

		planet_3Modifier = new LoopEntityModifier(new RotationModifier(200, 0,
				360));
		planet_3.registerEntityModifier(planet_3Modifier);

		// Sun
		sunTexture = new BitmapTextureAtlas(this.getTextureManager(), 200, 200,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		sunRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				sunTexture, this, "sun_" + assetSuffix + ".png", 0, 0);
		sun = new Sprite(60, 60, sunRegion, this.getVertexBufferObjectManager());
		sunTexture.load();
		scene.attachChild(sun);

		sunModifier = new LoopEntityModifier(new RotationModifier(150, 0, 360));
		sun.registerEntityModifier(sunModifier);

		// Cloud
		cloudTexture = new BitmapTextureAtlas(this.getTextureManager(), 2100,
				2100, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		cloudRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				cloudTexture, this, "clouds_noon.png", 0, 0);
		cloud = new Sprite(10, 30, cloudRegion,
				this.getVertexBufferObjectManager());
		cloudTexture.load();
		// scene.attachChild(cloud);

		cloudModifier = new LoopEntityModifier(
				new RotationModifier(200, 0, 360));
		cloud.registerEntityModifier(cloudModifier);

		rdTexture = new BitmapTextureAtlas(this.getTextureManager(), 20, 20,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		rdRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				rdTexture, this, "raindrop.png", 0, 0);
		rdTexture.load();

rainSystem = new BatchedPseudoSpriteParticleSystem(
				new RectangleParticleEmitter(0f, 0f, CAMERA_HEIGHT,
						CAMERA_HEIGHT / 2), 1, 3, 100, rdRegion,
				this.getVertexBufferObjectManager());
		// rainSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		// rainSystem.addParticleInitializer(new
		// ColorParticleInitializer<Entity>(1, 1, 1));
		rainSystem
				.addParticleInitializer(new VelocityParticleInitializer<Entity>(
						90,100, 120, 135));
		// rainSystem.addParticleInitializer(new
		// AccelerationParticleInitializer<Entity>(5, 15, 5, 30));
		rainSystem
				.addParticleInitializer(new RotationParticleInitializer<Entity>(
						150.0f, 170.0f));
		// rainSystem.addParticleInitializer(new
		// ExpireParticleInitializer<Entity>(20f));

		rainSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(
				0.6f, 0.8f));
		rainSystem.addParticleModifier(new AlphaParticleModifier<Entity>(6f,
				10f, 1.0f, 0.0f));
		//scene.attachChild(rainSystem);

		snowTexture = new BitmapTextureAtlas(this.getTextureManager(), 20, 20,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		snowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				snowTexture, this, "snow.png", 0, 0);
		snowTexture.load();

snowSystem = new BatchedPseudoSpriteParticleSystem(
				new RectangleParticleEmitter(0f, 0f, CAMERA_HEIGHT,
						CAMERA_HEIGHT / 2), 2, 5, 100, snowRegion,
				this.getVertexBufferObjectManager());
		snowSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		snowSystem.addParticleInitializer(new ColorParticleInitializer<Entity>(
				1, 1, 1));
		snowSystem
				.addParticleInitializer(new VelocityParticleInitializer<Entity>(
						-3, 3, -20, 40));
		snowSystem
				.addParticleInitializer(new AccelerationParticleInitializer<Entity>(
						5, 15, 5, 30));
		snowSystem
				.addParticleInitializer(new RotationParticleInitializer<Entity>(
						0.0f, 360.0f));
		snowSystem
				.addParticleInitializer(new ExpireParticleInitializer<Entity>(
						20f));

		snowSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(
				0.2f, 0.5f));

		snowSystem.addParticleModifier(new AlphaParticleModifier<Entity>(6f,
				10f, 1.0f, 0.0f));
		// scene.attachChild(snowSystem);

		lfTexture = new BitmapTextureAtlas(this.getTextureManager(), 50, 34,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		lfRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				lfTexture, this, "leaf.png", 0, 0);
		lfTexture.load();

		leafSystem = new BatchedPseudoSpriteParticleSystem(
				new RectangleParticleEmitter(0f, 0f, CAMERA_HEIGHT,
						CAMERA_HEIGHT / 2), 1, 2, 70, lfRegion,
				this.getVertexBufferObjectManager());
		leafSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		leafSystem.addParticleInitializer(new ColorParticleInitializer<Entity>(
				1, 1, 1));
		/*
		 * leafSystem .addParticleInitializer(new
		 * VelocityParticleInitializer<Entity>( -9, 0, -8, 5));
		 */
		leafSystem
				.addParticleInitializer(new VelocityParticleInitializer<Entity>(
						-9, 0, -18, -9));
		leafSystem
				.addParticleInitializer(new AccelerationParticleInitializer<Entity>(
						20, 45, 20, 50));
		leafSystem
				.addParticleInitializer(new RotationParticleInitializer<Entity>(
						0.0f, 360.0f));
		leafSystem
				.addParticleInitializer(new ExpireParticleInitializer<Entity>(
						20f));

		leafSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(
				0.45f, 0.7f));

		leafSystem.addParticleModifier(new AlphaParticleModifier<Entity>(6f,
				10f, 1.0f, 0.75f));
		scene.attachChild(leafSystem);

		float planet_1Center[] = getCenter(planet_1.getWidth(),
				planet_1.getHeight());
		float planet_2Center[] = getCenter(planet_2.getWidth(),
				planet_2.getHeight());
		float planet_3Center[] = getCenter(planet_3.getWidth(),
				planet_3.getHeight());
		planet_1.setPosition(planet_1Center[0], planet_1Center[1] + 675);
		planet_2.setPosition(planet_2Center[0], planet_2Center[1] + 675);
		planet_3.setPosition(planet_3Center[0], planet_3Center[1] + 675);
		scene_1.setPosition(planet_1Center[0] + planet_1.getWidth() / 2
				- scene_1.getWidth() / 2,
				(planet_1Center[1] + planet_1.getHeight() / 2 - scene_1
						.getHeight() / 2) + 675);
		scene_2.setPosition(planet_2Center[0] + planet_2.getWidth() / 2
				- scene_2.getWidth() / 2,
				(planet_2Center[1] + planet_2.getHeight() / 2 - scene_2
						.getHeight() / 2) + 675);
		scene_3.setPosition(planet_3Center[0] + planet_3.getWidth() / 2
				- scene_3.getWidth() / 2,
				(planet_3Center[1] + planet_3.getHeight() / 2 - scene_3
						.getHeight() / 2) + 675);

		assetsCreated = true;

		loadOrientation();

        mYahooWeather.setExceptionListener(this);
		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	protected void update() {
		time.setToNow();
		String tempSuffix = assetSuffix;
		if (time.hour >= 4 && time.hour <= 10) {
			// dawn
			assetSuffix = "dawn";
		} else if (time.hour >= 10 && time.hour <= 16) {
			// noon
			assetSuffix = "noon";
		} else if (time.hour >= 16 && time.hour <= 20) {
			// dusk
			assetSuffix = "dusk";
		} else if ((time.hour >= 20 && time.hour <= 24)
				|| (time.hour >= 0 && time.hour <= 4)) {
			// night
			assetSuffix = "night";
		}
		if (!assetSuffix.equals(tempSuffix)) {
			Log.d("StripedLog", "TIME CHANGED BITCHES: " + assetSuffix);
			resetAndroidAsset();
			if (leafSystem != null) {
				if (assetSuffix.equals("noon")) {
					scene.attachChild(leafSystem);
				} else {
					scene.detachChild(leafSystem);
				}
			}
			if (sunModifier != null) {
				if (assetSuffix.equals("night")) {
					sun.unregisterEntityModifier(sunModifier);
				}
			}
		}
	}

	int a = 0;

	public float[] getCenter(float w, float h) {
		float centerW = CAMERA_WIDTH / 2 - w / 2;
		float centerH = CAMERA_HEIGHT - h;
		return new float[] { centerW, centerH };
	}

	public void resetAndroidAsset() {
		if (assetsCreated) { /*
							 * move(scene_3, "down", 10);
							 * scene_3.registerEntityModifier(planet_move);
							 * move(planet_3, "down", 10);
							 * planet_3.registerEntityModifier(planet_move);
							 * 
							 * move(scene_2, "down", 5);
							 * scene_2.registerEntityModifier(planet_move);
							 * move(planet_2, "down", 5);
							 * planet_2.registerEntityModifier(planet_move);
							 * 
							 * move(scene_1, "down", 5);
							 * scene_1.registerEntityModifier(planet_move);
							 * move(planet_1, "down", 5);
							 * planet_1.registerEntityModifier(planet_move);
							 */

			bgTexture.clearTextureAtlasSources();
			bgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					bgTexture, this, "bg_" + assetSuffix + ".png", 0, 0);

			planet_1Texture.clearTextureAtlasSources();
			planet_1Region = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(planet_1Texture, this, "planet_1_"
							+ assetSuffix + ".png", 0, 0);

			planet_2Texture.clearTextureAtlasSources();
			planet_2Region = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(planet_2Texture, this, "planet_2_"
							+ assetSuffix + ".png", 0, 0);

			planet_3Texture.clearTextureAtlasSources();
			planet_3Region = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(planet_3Texture, this, "planet_3_"
							+ assetSuffix + ".png", 0, 0);

			scene_1Texture.clearTextureAtlasSources();
			scene_1Region = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(scene_1Texture, this, "scene_1_"
							+ assetSuffix + ".png", 0, 0);

			scene_2Texture.clearTextureAtlasSources();
			scene_2Region = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(scene_2Texture, this, "scene_2_"
							+ assetSuffix + ".png", 0, 0);

			scene_3Texture.clearTextureAtlasSources();
			scene_3Region = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(scene_3Texture, this, "scene_3_"
							+ assetSuffix + ".png", 0, 0);

			sunTexture.clearTextureAtlasSources();
			sunRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					sunTexture, this, "sun_" + assetSuffix + ".png", 0, 0);

			/*
			 * 
			 * move(scene_3, "up", 15);
			 * scene_3.registerEntityModifier(planet_move); move(planet_3, "up",
			 * 15); planet_3.registerEntityModifier(planet_move);
			 * 
			 * move(scene_2, "up", 10);
			 * scene_2.registerEntityModifier(planet_move); move(planet_2, "up",
			 * 10); planet_2.registerEntityModifier(planet_move);
			 * 
			 * move(scene_1, "up", 5);
			 * scene_1.registerEntityModifier(planet_move); move(planet_1, "up",
			 * 5); planet_1.registerEntityModifier(planet_move);
			 */
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene arg0, TouchEvent pTouchEvent) {

		switch (pTouchEvent.getAction()) {
		case TouchEvent.ACTION_DOWN:
			Log.d("StripedLog", "down");
			return true;

		case TouchEvent.ACTION_UP:
			Log.d("StripedLog", "up");
			return true;

		case TouchEvent.ACTION_MOVE:
			Log.d("StripedLog", "move");
			return true;
		}
		return false;
	}

	private void move(final Sprite spr, String dir, int delay) {
		if (dir == "down") {
			planet_move = new SequenceEntityModifier(new DelayModifier(delay),
					new MoveYModifier(8, spr.getY(), spr.getY() + 400,
							EaseBackInCustom.getInstance()) {
						@Override
						protected void onModifierFinished(IEntity pItem) {
							// spr.unregisterEntityModifier(planet_move);
							// planet_move.reset();
							super.onModifierFinished(pItem);
						}
					});

		} else if (dir == "up") {
			planet_move = new SequenceEntityModifier(new DelayModifier(delay),
					new MoveYModifier(8, spr.getY(), spr.getY() - 400,
							EaseBackInCustom.getInstance()) {
						@Override
						protected void onModifierFinished(IEntity pItem) {
							// spr.unregisterEntityModifier(planet_move);
							// planet_move.reset();
							super.onModifierFinished(pItem);
						}
					});

		}
		// spr.setVisible(true);
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		move(scene_3, "up", 6);
		scene_3.registerEntityModifier(planet_move);
		move(planet_3, "up", 6);
		planet_3.registerEntityModifier(planet_move);

		move(scene_2, "up", 3);
		scene_2.registerEntityModifier(planet_move);
		move(planet_2, "up", 3);
		planet_2.registerEntityModifier(planet_move);

		move(scene_1, "up", 0);
		scene_1.registerEntityModifier(planet_move);
		move(planet_1, "up", 0);
		planet_1.registerEntityModifier(planet_move);

		this.mEngine.registerUpdateHandler(new FPSLogger() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
			}

			@Override
			public void reset() {
			}
		});

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public Engine onCreateEngine() {
		return new MyBaseWallpaperGLEngine(this);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		scene.setOnSceneTouchListener(this);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	protected class MyBaseWallpaperGLEngine extends GLEngine {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private EngineRenderer mEngineRenderer;
		private ConfigChooser mConfigChooser;

		// ===========================================================
		// Constructors
		// ===========================================================

		public MyBaseWallpaperGLEngine(final IRendererListener pRendererListener) {

			if (this.mConfigChooser == null) {
				PlanetMain.this.mEngine.getEngineOptions().getRenderOptions()
						.setMultiSampling(false);
				this.mConfigChooser = new ConfigChooser(PlanetMain.this.mEngine
						.getEngineOptions().getRenderOptions()
						.isMultiSampling());
			}
			this.setEGLConfigChooser(this.mConfigChooser);

			this.mEngineRenderer = new EngineRenderer(PlanetMain.this.mEngine,
					this.mConfigChooser, pRendererListener);
			this.setRenderer(this.mEngineRenderer);
			this.setRenderMode(GLEngine.RENDERMODE_CONTINUOUSLY);
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public Bundle onCommand(final String pAction, final int pX,
				final int pY, final int pZ, final Bundle pExtras,
				final boolean pResultRequested) {
			if (pAction.equals(WallpaperManager.COMMAND_TAP)) {
				Log.d("StripedLog", "tap");
				// PlanetMain.this.onTap(pX, pY);
			} else if (pAction.equals(WallpaperManager.COMMAND_DROP)) {
				// PlanetMain.this.onDrop(pX, pY);
				Log.d("StripedLog", "drop");
			}

			return super.onCommand(pAction, pX, pY, pZ, pExtras,
					pResultRequested);
		}

		@Override
		public void onResume() {
			super.onResume();
			PlanetMain.this.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
			PlanetMain.this.onPause();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			this.mEngineRenderer = null;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);

			final float touchX = event.getX();
			final float touchY = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

		        searchByGPS();
		        
				if (event.getY() < 0) {
					sun.setPosition(event.getX() - sun.getWidth() / 2, 0);
				} else if (event.getY() > CAMERA_HEIGHT / 2) {
					sun.setPosition(event.getX() - sun.getWidth() / 2,
							CAMERA_HEIGHT / 2);
				} else {
					sun.setPosition(event.getX() - sun.getWidth() / 2,
							event.getY() - sun.getHeight() / 2);
				}

				return;
			case MotionEvent.ACTION_MOVE:
				if (event.getY() < 0) {
					sun.setPosition(event.getX() - sun.getWidth() / 2, 0);
				} else if (event.getY() > CAMERA_HEIGHT / 2) {
					sun.setPosition(event.getX() - sun.getWidth() / 2,
							CAMERA_HEIGHT / 2);
				} else {
					sun.setPosition(event.getX() - sun.getWidth() / 2,
							event.getY() - sun.getHeight() / 2);
				}

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			default:
				return;
			}
		}
	}

	@Override
	public void onFailConnection(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailParsing(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailFindLocation(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotWeatherInfo(WeatherInfo weatherInfo) {
		   if (weatherInfo != null) {

   			if (leafSystem != null){scene.detachChild(leafSystem);}
			   
				if ((weatherInfo.getCurrentCode()>0&&weatherInfo.getCurrentCode()<=12)||(weatherInfo.getCurrentCode()==46))   {
					  //rain
					scene.attachChild(rainSystem);
		        Log.d("StripedLog", 
		        		String.valueOf(weatherInfo.getCurrentCode()));
		        	}else if ((weatherInfo.getCurrentCode()>=15&&weatherInfo.getCurrentCode()<=18)||(weatherInfo.getCurrentCode()>=41&&weatherInfo.getCurrentCode()<=43)){//snow

						scene.attachChild(snowSystem);	}else 
		        		{
		        			if (leafSystem != null){scene.detachChild(leafSystem);}
		        			if (rainSystem != null){scene.detachChild(rainSystem);}
		        			if (snowSystem != null){scene.detachChild(snowSystem);}}
		   }
	}
	
	private void searchByGPS() {
		mYahooWeather.setSearchMode(SEARCH_MODE.GPS);
		mYahooWeather.queryYahooWeatherByGPS(getApplicationContext(), this);
	}
}