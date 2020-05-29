package net.stratplus.my_snap_sdk;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.app.Activity;

import androidx.annotation.NonNull;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;



import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.BinaryMessenger;
import com.miteksystems.misnap.misnapworkflow.params.WorkflowApi;
import com.miteksystems.misnap.misnapworkflow_UX2.MiSnapWorkflowActivity_UX2;
import com.miteksystems.misnap.params.MiSnapApi;
import com.miteksystems.misnap.params.CameraApi;
import com.miteksystems.misnap.misnapworkflow.MiSnapWorkflowActivity;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.lifecycle.Lifecycle;



/** MySnapSdkPlugin */
public class MySnapSdkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.

  private static final String CHANNEL = "my_snap_sdk";


  public static void registerWith(Registrar registrar) {
    Log.d("Before", "AFTER PLUGIN SETUP");

    if(registrar.activity() == null){
      return;
    }

    Activity activity = registrar.activity();

    MySnapSdkPlugin plugin = new MySnapSdkPlugin();
    plugin.setup(registrar.messenger(),activity,registrar.context(), registrar, null);
    Log.d("AFTER", "AFTER PLUGIN SETUP");
/*


    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);

    MySnapSdkPlugin myPlugin = new MySnapSdkPlugin(registrar.activity());
    registrar.addActivityResultListener(myPlugin);
    channel.setMethodCallHandler(myPlugin);*/
  }





  private static final long PREVENT_DOUBLE_CLICK_TIME_MS = 1000;
  private long mTime;
  private static int mUxWorkflow;
  private static int mGeoRegion;
  private Activity activity;
  private Context context;
  private Lifecycle lifecycle;
  private MySnapDelegate delegate;
  private MethodChannel channel;



  private FlutterPluginBinding pluginBinding;
  private ActivityPluginBinding activityBinding;




  public MySnapSdkPlugin() {
    this.activity = null;
    Log.d("MySnapSdkPlugin", "Constr:MySnapSdkPlugin");
  }


  public MySnapSdkPlugin(final MySnapDelegate delegate, Activity activity) {
    this.activity = activity;
    this.delegate = delegate;
    Log.d("MySnapSdkPlugin", "Constr:MySnapSdkPlugin22222222");

  }



  public void setup(final BinaryMessenger messenger,
               final Activity activity,
               final Context context,
               Registrar registrar,
               final ActivityPluginBinding activityBinding){
    Log.d("STEP", "SETUP!");

    this.activity = activity;
    this.delegate = new MySnapDelegate(activity, context);

    channel = new MethodChannel(messenger, CHANNEL);
    channel.setMethodCallHandler(this);
    activityBinding.addActivityResultListener(delegate);

  }

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity



  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    this.pluginBinding = binding;
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.context = applicationContext;
    channel = new MethodChannel(messenger, "my_snap_sdk");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    activityBinding = activityPluginBinding;
    Log.d("MYSNAP!!!!", "Atached to activity!!!!!!");
    this.activity = activityPluginBinding.getActivity();

    setup(
            pluginBinding.getBinaryMessenger(),
            this.activity,
            activityPluginBinding.getActivity(),
            null,
            activityPluginBinding);

  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    Log.d("MYSNAP!!!!", "onDetachedFromActivityForConfigChanges!!!!!!");
    // destroyed to change configuration.
    // This call will be followed by onReattachedToActivityForConfigChanges().
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    Log.d("MYSNAP!!!!", "onReattachedToActivityForConfigChanges!!!!!!");
    this.activity = activityPluginBinding.getActivity();
    // after a configuration change.
  }

  @Override
  public void onDetachedFromActivity() {
    Log.d("MYSNAP!!!!", "onDetachedFromActivity!!!!!!");
    // Clean up references.

  }





  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    String tes = MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT;
    Log.d("MYTAGMethod", call.method);

    if (call.method.equals("getPicture")) {
      //startMiSnapWorkflow(MiSnapApi.PARAMETER_DOCTYPE_ID_CARD_FRONT);
      //result.success("Android " + android.os.Build.VERSION.RELEASE);

      delegate.startMiSnapWorkflow(tes, result);
    } else {
      //result.notImplemented();
      Log.d("TAH", "NOT IMplemented");
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }








}
