package net.stratplus.my_snap_sdk;


import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import com.miteksystems.misnap.params.MiSnapApi;
import com.miteksystems.misnap.params.CameraApi;


import org.json.JSONException;
import org.json.JSONObject;


/** MySnapSdkPlugin */
public class MySnapSdkPlugin implements FlutterPlugin, MethodCallHandler {
  private static final long PREVENT_DOUBLE_CLICK_TIME_MS = 1000;
  private long mTime;
  private static int mUxWorkflow;
  private static int mGeoRegion;
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "my_snap_sdk");
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "my_snap_sdk");
    channel.setMethodCallHandler(new MySnapSdkPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    String tes = MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT;
    Log.d("MYTAG", tes);

    startMiSnapWorkflow(MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT);

    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


  private void startMiSnapWorkflow(String docType) {
    // Prevent multiple MiSnap instances by preventing multiple button presses
    if (System.currentTimeMillis() - mTime < PREVENT_DOUBLE_CLICK_TIME_MS) {
      Log.e("UxStateMachine", "Double-press detected");
      return;
    }

    mTime = System.currentTimeMillis();
    JSONObject misnapParams = new JSONObject();
    try {
      misnapParams.put(MiSnapApi.MiSnapDocumentType, docType);


      // Here you can override optional API parameter defaults
      misnapParams.put(CameraApi.MiSnapAllowScreenshots, 1);
      // e.g. misnapParams.put(MiSnapApi.AppVersion, "1.0");
      // Workflow parameters are now put into the same JSONObject as MiSnap parameters
      //misnapParams.put(WorkflowApi.MiSnapTrackGlare, WorkflowApi.TRACK_GLARE_ENABLED);
      //misnapParams.put(CameraApi.MiSnapFocusMode, CameraApi.PARAMETER_FOCUS_MODE_HYBRID_NEW);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Intent intentMiSnap;
    if (mUxWorkflow == 1) {
      intentMiSnap = new Intent(this, MiSnapWorkflowActivity.class);
    } else {
      intentMiSnap = new Intent(this, MiSnapWorkflowActivity_UX2.class);
    }
    intentMiSnap.putExtra(MiSnapApi.JOB_SETTINGS, misnapParams.toString());
    startActivityForResult(intentMiSnap, MiSnapApi.RESULT_PICTURE_CODE);
  }
}
