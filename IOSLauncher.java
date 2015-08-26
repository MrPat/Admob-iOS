package com.example.mainpackage;

import java.util.ArrayList;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.bindings.admob.GADAdSize;
import org.robovm.bindings.admob.GADBannerView;
import org.robovm.bindings.admob.GADInterstitial;
import org.robovm.bindings.admob.GADInterstitialDelegateAdapter;
import org.robovm.bindings.admob.GADRequest;
import org.robovm.bindings.admob.GADRequestError;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;

public class IOSLauncher extends IOSApplication.Delegate implements AdController {
	private GADBannerView banner;
	private GADInterstitial interstitial;
	private boolean isInterstitialLoaded = false;
	private boolean isInterstitialFailed = false;
	private static final String BANNER_ID = "{YOUR BANNER AD ID}";
	private static final String INTERSTITIAL_ID = "{YOUR INTERSTITIAL AD ID}";
	private static ArrayList<String> testDevices;
	private IOSApplication iosApp;
	private boolean bannerInitialized = false;
	
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = false;
        config.orientationPortrait = true;
        iosApp = new IOSApplication(new DeckBuilder(this), config);
        initAds();
        return iosApp;
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

	@Override
	public void initAds() {
		String simID = GADRequest.GAD_SIMULATOR_ID;
		testDevices = new ArrayList<String>();
		testDevices.add(simID);
	}

	@Override
	public void initBanner() {
		if (!bannerInitialized) {
	        // Initialize the banner
			bannerInitialized = true;
			banner = new GADBannerView(GADAdSize.smartBannerPortrait());
			banner.setAdUnitID(BANNER_ID);
		}
	}

	@Override
	public void showBanner() {
		try {
			initBanner();

			// Add it to the screen
			banner.setRootViewController(iosApp.getUIViewController());
			iosApp.getUIViewController().getView().addSubview(banner);

			// Make it start requesting ads
			GADRequest request = new GADRequest();
			request.setTestDevices(testDevices);
			banner.loadRequest(request);

			// Set the size of the view we want for the banner
			// First get the size of the screen
			CGSize screenSize = UIScreen.getMainScreen().getBounds().getSize();
			double screenWidth = screenSize.getWidth();
			double screenHeight = screenSize.getHeight();

			// Get the size of the ad
			CGSize adSize = banner.getBounds().getSize();
			double adHeight = adSize.getHeight();
			double adWidth = adSize.getWidth();

			// Calculate the size of the view for the banner
			float bannerWidth = (float) screenWidth;
			float bannerHeight = (float) (bannerWidth / adWidth * adHeight);

			// Set the coords and size of the banner
			banner.setFrame(new CGRect(screenWidth / 2 - adWidth / 2, 0, bannerWidth, bannerHeight));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public Vector2 getBannerSize() {
		// Sometimes the iPhone/iPad scales the app's display to account for retina graphics. This is how I account for that.
		CGSize screenSize = UIScreen.getMainScreen().getBounds().getSize();
		
		// You need some way to get the screen size of your device from the Gdx class
		// Utilities is a singleton class I made that can give me access to Gdx functions 
		// from anywhere in the project. You will have to make your own class for this
		// Or find a way to get a reference to your main Gdx class in here
		Vector2 appSize = Utilities.getScreenSize();
		
		// Calculate how much the screen is scaled and apply that to the banner size to get 
		// its actual size in pixels on the device screen
		double screenXScale = appSize.x / screenSize.getWidth();
		double screenYScale= appSize.y / screenSize.getHeight();
		
		return new Vector2((float) (GADAdSize.smartBannerPortrait().toCGSize().getWidth() * screenXScale), (float) (GADAdSize.smartBannerPortrait().toCGSize().getHeight() * screenYScale));
	}

	@Override
	public void initInterstitial() {
		interstitial = new GADInterstitial();
		interstitial.setAdUnitID(INTERSTITIAL_ID);
	}

	@Override
	public void loadInterstitial() {
		try {
			initInterstitial();
			
			// I poll these values from my AdHelper class to get the status of the loading process
			isInterstitialLoaded = false;
			isInterstitialFailed = false;
			
			GADRequest request = new GADRequest();
			request.setTestDevices(testDevices);
			interstitial.loadRequest(request);
			
			// These are just event listeners for the interstitial, nothing scary
			interstitial.setDelegate(new GADInterstitialDelegateAdapter() {

				@Override
				public void didReceiveAd(GADInterstitial ad) {
					// This is called when the interstitial is loaded
					isInterstitialLoaded = true;
				}

				@Override
				public void didFailToReceiveAd(GADInterstitial ad,
						GADRequestError error) {
					// This is called when the interstitial fails
					isInterstitialFailed = true;
				}

				// I don't use these but they have to be defined here
				@Override
				public void willPresentScreen(GADInterstitial ad) {	}
				@Override
				public void willDismissScreen(GADInterstitial ad) {}
				@Override
				public void didDismissScreen(GADInterstitial ad) {}
				@Override
				public void willLeaveApplication(GADInterstitial ad) {}			
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void showInterstitial() {
		// If the interstitial is ready, show it and reset the loaded flag
		if (interstitial.isReady()) {
			interstitial.present(iosApp.getUIViewController());
			isInterstitialLoaded = false;
		}
	}

	@Override
	public boolean isInterstitialLoaded() {
		return isInterstitialLoaded;
	}

	@Override
	public boolean isInterstitialFailed() {
		return isInterstitialFailed;
	}

	// I don't think this is necessary but I loop in my main class until this is true 
	// and then I load my banner. Pretty sure it is always true by the time I check it.
	@Override
	public boolean isUIViewLoaded() {
		return iosApp.getUIViewController().isViewLoaded();
	}
}