package cl.json;

import android.content.Intent;
import android.content.ActivityNotFoundException;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Callback;

public class RNShareModule extends ReactContextBaseJavaModule {

    private Callback callback;

  private final ReactApplicationContext reactContext;

  public RNShareModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNShare";
  }

  @ReactMethod
  public void open(ReadableMap options, Callback callback) {
    Intent shareIntent = createShareIntent(options);
    Intent intentChooser = createIntentChooser(options, shareIntent);
      this.callback = callback;
    try {
      this.reactContext.startActivityForResult(intentChooser);

      //this.callback.invoke("OK");
    } catch (ActivityNotFoundException ex) {
      this.callback.invoke("not_available");
    }
  }

  /**
   * Creates an {@link Intent} to be shared from a set of {@link ReadableMap} options
   * @param {@link ReadableMap} options
   * @return {@link Intent} intent
   */
  private Intent createShareIntent(ReadableMap options) {
    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    intent.setType("text/plain");

    if (hasValidKey("share_text", options)) {
      intent.putExtra(Intent.EXTRA_SUBJECT, options.getString("share_text"));
    }

    if (hasValidKey("share_URL", options)) {
      intent.putExtra(Intent.EXTRA_TEXT, options.getString("share_URL"));
    }

    return intent;
  }

  /**
   * Creates an {@link Intent} representing an intent chooser
   * @param {@link ReadableMap} options
   * @param {@link Intent} intent to share
   * @return {@link Intent} intent
   */
  private Intent createIntentChooser(ReadableMap options, Intent intent) {
    String title = "Share";
    if (hasValidKey("title", options)) {
      title = options.getString("title");
    }

    Intent chooser = Intent.createChooser(intent, title);
    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    return chooser;
  }

    @Override
    protected void onActivityResult(int requestCode, int result, Intent data) {
        this.callback.invoke("RequestCode: " + requestCode + "\nResult: " + result + "\nIntentData: " + data.toString());
    }

  /**
   * Checks if a given key is valid
   * @param @{link String} key
   * @param @{link ReadableMap} options
   * @return boolean representing whether the key exists and has a value
   */
  private boolean hasValidKey(String key, ReadableMap options) {
    return options.hasKey(key) && !options.isNull(key);
  }

}
