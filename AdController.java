package com.example.mainpackage;

import com.badlogic.gdx.math.Vector2;

public interface AdController {
	// All of these functions must be implemented in your IOSLauncher class
	
	public abstract void initAds();
	
	public abstract void initBanner();
	
	public abstract void showBanner();
	
	public abstract void hideBanner();
	
	public abstract Vector2 getBannerSize();
	
	public abstract void initInterstitial();
	
	public abstract void loadInterstitial();
	
	public abstract void showInterstitial();
	
	public abstract boolean isInterstitialLoaded();
	
	public abstract boolean isInterstitialFailed();
	
	public abstract boolean isUIViewLoaded();
}
