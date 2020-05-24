package net.stratplus.my_snap_sdk;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.BinaryMessenger;

import com.miteksystems.misnap.params.MiSnapApi;
import com.miteksystems.misnap.params.CameraApi;
import com.miteksystems.misnap.misnapworkflow.MiSnapWorkflowActivity;
import org.json.JSONException;
import org.json.JSONObject;


/** MySnapSdkPlugin */
public class MySnapSdkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private static final long PREVENT_DOUBLE_CLICK_TIME_MS = 1000;
  private long mTime;
  private static int mUxWorkflow;
  private static int mGeoRegion;
  private Activity activity;
  private Context context;

  public MySnapSdkPlugin() {
    this.activity = null;
  }

  public MySnapSdkPlugin(Activity activity) {
    this.activity = activity;
  }


  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  /*
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "my_snap_sdk");
    channel.setMethodCallHandler(this);
  }
  */


  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.context = applicationContext;
    channel = new MethodChannel(messenger, "my_snap_sdk");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity
    Log.d("MYSNAP!!!!", "Atached to activity!!!!!!");
    this.activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // TODO: the Activity your plugin was attached to was
    Log.d("MYSNAP!!!!", "onDetachedFromActivityForConfigChanges!!!!!!");
    // destroyed to change configuration.
    // This call will be followed by onReattachedToActivityForConfigChanges().
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to a new Activity
    Log.d("MYSNAP!!!!", "onReattachedToActivityForConfigChanges!!!!!!");
    this.activity = activityPluginBinding.getActivity();
    // after a configuration change.
  }

  @Override
  public void onDetachedFromActivity() {
    // TODO: your plugin is no longer associated with an Activity.
    Log.d("MYSNAP!!!!", "onDetachedFromActivity!!!!!!");
    // Clean up references.
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
    channel.setMethodCallHandler(new MySnapSdkPlugin(registrar.activity()));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    String tes = MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT;
    Log.d("MYTAG", tes);



    if (call.method.equals("getPlatformVersion")) {
      startMiSnapWorkflow(MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT);
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

    Log.d("MYTAG", "MyActividy::::" + activity.toString());
    Log.d("MYTAG", "MyContext::::" + context.toString());

    Intent intentMiSnap;
    intentMiSnap = new Intent(context, MiSnapWorkflowActivity.class);
    intentMiSnap.putExtra(MiSnapApi.JOB_SETTINGS, misnapParams.toString());
    activity.startActivityForResult(intentMiSnap, MiSnapApi.RESULT_PICTURE_CODE);
  }





}
