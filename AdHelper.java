package com.example.mainpackage;

import com.badlogic.gdx.math.Vector2;

public class AdHelper {
	private static AdController adController;
	private static boolean showAds = true;
	
	public static void initialize(AdController controller) {
		adController = controller;
	}
	
	public static void showBanner() {
		if (adController != null && showAds) adController.showBanner();
	}
	
	public static void initBanner() {
		if (adController != null) adController.initBanner();
	}

	public static boolean isShowAds() {
		return showAds;
	}

	public static void setShowAds(boolean showAds) {
		AdHelper.showAds = showAds;
	}
	
	public static Vector2 getBannerSize() {
		return adController.getBannerSize();
	}
	
	public static float getBannerHeight() {
		if (adController != null) return getBannerSize().y;
		else return 0;
	}
	
	public static void loadInterstitial() {
		if (adController != null) adController.loadInterstitial();
	}
	
	public static void showInterstitial() {
		if (adController != null) adController.showInterstitial();
	}
	
	public static boolean isInterstitialLoaded() {
		if (adController != null) return adController.isInterstitialLoaded();
		else return false;
	}
	
	public static boolean isInterstitialFailed() {
		if (adController != null) return adController.isInterstitialFailed();
		else return true;
	}
	
	public static boolean isUIViewLoaded() {
		if (adController != null) return adController.isUIViewLoaded();
		return true;
	}
}
