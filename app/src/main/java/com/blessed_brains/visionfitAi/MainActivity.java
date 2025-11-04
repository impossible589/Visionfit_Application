package com.blessed_brains.visionfitAi;

import static com.blessed_brains.visionfitAi.TFLiteClassifier.actiondetected;
import static com.blessed_brains.visionfitAi.TFLiteClassifier.countincurrentseason;
import static com.blessed_brains.visionfitAi.TFLiteClassifier.countview;
import static com.blessed_brains.visionfitAi.TFLiteClassifier.isfirstTime;
import static com.blessed_brains.visionfitAi.TFLiteClassifier.prevlabel;
import static com.blessed_brains.visionfitAi.TFLiteClassifier.totalcount;
import static com.blessed_brains.visionfitAi.webpagedownloader.staticurl;
import static com.blessed_brains.visionfitAi.PushupTrackerActivity.target;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.interop.Camera2Interop;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    List<String> landmarkswithcoordlist = new ArrayList<>();
    private ExecutorService cameraExecutor;
    ImageAnalysis imageAnalysis;
    Bitmap tb;
    boolean isStopped = false;
    boolean isbooting = true;
    static boolean isfrontcamera = true;
    static int Appversion = 1;
    boolean isframeavailable = false;
    static final String locker = "";
    static boolean isSoundenabled = true;
    RewardAnimationView rewardAnimation ;
    HashMap<String, String> pushuplogdictionary = new HashMap<>();
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    static Map<String, Integer> pushupData = new HashMap<>();
    private PreviewView previewView;
    PreviewView p;
    ProcessCameraProvider cameraProvider;
    private SurfaceView sview;
    static ImageProxy imagepxyuni;
    Matrix matrix;
    static TFLiteClassifier classifier = null;

    static long lastcp;
    Preview preview;
    Size bestsuitableres;
    LinearLayout placeholder;
    CameraSelector cameraSelector2;

    CardView flipbtn;

    CardView soundbtn;
    CardView calnderlay;
    mediapipeimagetoskeleton mediapipeimagetoskeleton1;
    static Context context;
    static boolean isttsReady = false;
    static MainActivity mainActivity1;
    static TextToSpeech tts;
    List<NormalizedLandmark> poseLandmarks;
    private final String TAG = "TFLitePredict";
    static Thread thread ;
    SharedPreferences prefs;
    SharedPreferences targetpref;
    Preview.SurfaceProvider surface;
    boolean ispermissiongranted= false;
    // Your dataset class names
    private final String[] classNames = {"pushdown", "pushup", "rubbish"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetpref = getSharedPreferences("target",MODE_PRIVATE);
        prefs = getSharedPreferences("pushup_data", MODE_PRIVATE);
        pinger();
        boolean tutorialShown = prefs.getBoolean(KEY_FIRST_LAUNCH, false);
        loaddictfromfile(this);
        monthverifier();


        if (!tutorialShown) {
            showTutorialDialog();
            // Mark as shown
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, true).apply();
        }
        if(targetpref.getString("target", null) == null){
            showCustomInputDialog();
        }
        //showTutorialDialog();

        // Animate logo with fade-in effect
       /* appLogo.animate()
                .alpha(1f)
                .setDuration(1000)
                .start();

        // Animate app name with fade-in effect and delay
        appName.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(300)
                .start();*/
       // requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set fullscreen flags
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this,
                                "Language not supported", Toast.LENGTH_SHORT).show();
                    }else{
                        isttsReady = true;

                    }
                } else {
                    isttsReady = false;
                    Toast.makeText(MainActivity.this,
                            "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setContentView(R.layout.testing2);
        lastcp = System.currentTimeMillis();
        //Log.d("gouravhttp", Thread.currentThread().getName());
        actiondetected = System.currentTimeMillis();
        //enableEdgeToEdge();

        flipbtn = findViewById(R.id.cameraflipbtnl);
        soundbtn = findViewById(R.id.soundbtnl);
        calnderlay = findViewById(R.id.calenderl);
        p = (PreviewView) findViewById(R.id.previewView);
        placeholder = (LinearLayout) findViewById(R.id.cameraPlaceholder);
        ViewGroup rootLayout = findViewById(android.R.id.content);

        rewardAnimation = new RewardAnimationView(this, rootLayout);
        handleWindowInsets();
        webpagedownloader webpagedownloader = new webpagedownloader();
        webpagedownloader.Isupdateavailable();
        //findViewById()
        mainActivity1=this;
        flipbtn.setVisibility(CardView.INVISIBLE);
        soundbtn.setVisibility(CardView.INVISIBLE);
        previewView = findViewById(R.id.previewView);
        surface = previewView.getSurfaceProvider();
        //sview = findViewById(R.id.poseOverlay);
        context = getApplicationContext();

        try {
            classifier = new TFLiteClassifier(getAssets(), "rubbishmodel.tflite");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediapipeimagetoskeleton1 = new mediapipeimagetoskeleton();

       /* mediapipeimagetoskeleton1 = new mediapipeimagetoskeleton(){
            @Override
              public void returnLivestreamResult(PoseLandmarkerResult poseLandmarkerResult, MPImage mpImage) {
                int x;
                int y;
                int c;


                List<Float> stringList = new ArrayList<>();
                //List<List<NormalizedLandmark>> normalizedLandmarks = poseLandmarkerResult.landmarks();
                if(poseLandmarkerResult.landmarks().size()>0) {
                    poseLandmarks = poseLandmarkerResult.landmarks().get(0);


                    //Log.d("gouravhttp", "returnLivestreamResult" +String.valueOf(poseLandmarks.size()));
                    for (int id = 0; id < poseLandmarks.size(); id++) {
                        NormalizedLandmark landmark = poseLandmarks.get(id);
                        stringList.add(Float.valueOf(id));
                        stringList.add(landmark.x());
                        stringList.add(landmark.y());

                    }
                    BodyDrawer bodyDrawer = new BodyDrawer();
                    List<Float> copy = new ArrayList<>(stringList);
                    stringList.clear();

                    Bitmap bitmap1 = bodyDrawer.circleConnectStarter(copy);


                    try {
                        String prediction = classifier.classify(bitmap1);
                    } catch (InterruptedException e) {
                    Log.d("gouravhttperror", String.valueOf(e));
                    }
                }else{
                    imagepxyuni.close();
                }







            }
        };*/

        mediapipeimagetoskeleton1.init();
        /*if (allPermissionsGranted()) {
           ispermissiongranted= true;
        } else {


            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }*/
       /* Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/syllab/p11.jpeg");
         //saveBitmap(context, rotateBitmap(bitmap,90), "camera_framerotated");

        mediapipeimagetoskeleton1.processBitmap(bitmap);
        mediapipeimagetoskeleton1.processBitmap(bitmap);
        mediapipeimagetoskeleton1.processBitmap(bitmap);*/
        // Replace with your actual dataset labels

        //Bitmap bitmap =BitmapFactory.decodeFile("/storage/emulated/0/syllab/p1.jpg");// your input image (from gallery/camera)
        //mediapipeimagetoskeleton1.processBitmap(bitmap);
       /* long start =  System.currentTimeMillis();
        try {
            TFLiteClassifier classifier = new TFLiteClassifier(getAssets(), "rubbishmodel.tflite");
            String prediction = classifier.classify(bitmap);
            long end = System.currentTimeMillis();

            Toast.makeText(this, String.valueOf(prediction), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, String.valueOf(prediction), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, String.valueOf(end-start), Toast.LENGTH_SHORT).show();
            // Example: print probabilities
            //System.out.println("Class 0: " + prediction[0]);
            // System.out.println("Class 1: " + prediction[1]);

        } catch (IOException e) {
            e.printStackTrace();
        }*/





       // bestsuitableres =  getNearestSupportedSize(new Size(1400,1400));
        //bestsuitableres = new Size(2000,2000);
      //  AccuratePoseDetectorOptions options =
           //     new AccuratePoseDetectorOptions.Builder()
             //           .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
              //          .build();
       // poseDetector = PoseDetection.getClient(options);


        // Base pose detector with streaming frames, when depending on the pose-detection sdk

        // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk






    }


  //  public void updatecanceled(View v) {
      // setContentView(R.layout.testing);
  //  }
    public void speakText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
      public void pinger(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Connect to the website
                    Document doc = Jsoup.connect("https://boterofficial.blogspot.com/2025/10/pinger.html").get();

                    // Extract only text (no HTML tags)


                    // Print plain text content

                } catch (Exception e) {



                }
            }
        }).start();
          // Log.d("gouravhttp", "version" );

      }

    private void showCustomInputDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        EditText etInput = dialogView.findViewById(R.id.etInput);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("Submit", (dialog, which) -> {

            String text = etInput.getText().toString();

            if(text.isEmpty()){}else {
                target = Integer.parseInt(text);

                saveTarget(target);
            }
            //Toast.makeText(this, "You entered: " + text, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    private void saveTarget(int target) {
        SharedPreferences.Editor editor = targetpref.edit();
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(target));
        editor.putString("target", sb.toString());
        editor.apply();
    }

    public void monthverifier() {
        if(pushupData.size()==0){

        }else {
            Map.Entry<String, Integer> firstEntry = pushupData.entrySet().iterator().next();
            String[] keysplit = firstEntry.getKey().split("-");
            if (keysplit.length > 1) {
                String month = keysplit[1];
                Calendar tempcal = Calendar.getInstance();
                String key = getDateKey(tempcal);
                key = key.split("-")[1];
                if (!month.equals(key)) {
                    pushupData.clear();
                    dictfilesaver();
                }
            }
        }
    }


    public void manualdictappend(int date,int count){
    Calendar tempcal = Calendar.getInstance();

    tempcal.set(Calendar.DAY_OF_MONTH, date);
    String key = getDateKey(tempcal);


    dictappend(key,count);
}
    public int getTodaysdate(){
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return Integer.parseInt(formattedDate.split("/")[0]);


    }
    private String getDateKey(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }

    public void resetclicked(View v){
        //Log.d("gouravhttp", "resetclicked");
       //TextView countview = (TextView) findViewById(R.id.pushupCount);
        if (prevlabel=="pushdown") {
            totalcount = -0.5f;
        }else{
            totalcount = 0f;
        }
        countview.setText("0");

    }
    public void increasecounter(){

        rewardAnimation.showReward(1);

    }
    public void startbtnclicked2(View v){
        if(!ispermissiongranted){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }else{
            startbtnclicked();
        }



    }

public void calclicked(View view){
    startActivity(new Intent(this, PushupTrackerActivity.class));
}

    public void startbtnclicked(){


        Button b= (Button) findViewById(R.id.btnStart);

        String string = String.valueOf( b.getText());
        if(string.contains("TART")){

            flipbtn.setVisibility(CardView.VISIBLE);
            soundbtn.setVisibility(CardView.VISIBLE);
            calnderlay.setVisibility(CardView.INVISIBLE);
            ImageButton soundbtn = findViewById(R.id.soundbtn);
            try{
                p.removeView(placeholder);

            }catch (Exception e){

            }
            countincurrentseason=0;
            if(isbooting){
                isbooting = false;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                mainlooper();
                cameraExecutor = Executors.newSingleThreadExecutor();
                startCamera(CameraSelector.DEFAULT_FRONT_CAMERA);
            }else{
            cameraProvider.bindToLifecycle(this, cameraSelector2,imageAnalysis, preview);}
            b.setBackgroundResource(R.drawable.graycornerbtn);
            b.setText("STOP");

        }else{
            manualdictappend(getTodaysdate(),countincurrentseason);
            flipbtn.setVisibility(ImageButton.INVISIBLE);
            soundbtn.setVisibility(ImageButton.INVISIBLE);
            calnderlay.setVisibility(CardView.VISIBLE);
            cameraProvider.unbindAll();
            b.setText("START");
            b.setBackgroundResource(R.drawable.button_start);
            p.addView(placeholder);
            calclicked(new View(this));

        }


    }
    public void cameraflipclicked(View view){
        if(isfrontcamera) {
            isfirstTime = true;
            isfrontcamera = false;

            startCamera(CameraSelector.DEFAULT_BACK_CAMERA);
        }else{
            isfirstTime = true;
            isfrontcamera = true;
           // cameraExecutor = Executors.newSingleThreadExecutor();

            startCamera(CameraSelector.DEFAULT_FRONT_CAMERA);
        }
    }
    private void enableEdgeToEdge() {
        // Make the app draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Set status bar and navigation bar to transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        // Enable notch area usage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          //  getWindow().getAttributes().layoutInDisplayCutoutMode =
                  //  WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // Set status bar icons to light color (for dark background)
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(false);
            windowInsetsController.setAppearanceLightNavigationBars(false);
        }
    }

    /**
     * ✅ Handle window insets for notch and system bars
     */
    private void handleWindowInsets() {
        View rootView = findViewById(android.R.id.content);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            // Get insets for system bars
            androidx.core.graphics.Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            // Apply padding to root view to avoid overlap with system bars
            // But we want to draw behind status bar, so only apply to bottom
            v.setPadding(0, 0, 0, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


   public void updatebtnclicked(View view){

     openWebPage(staticurl);

   }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Try to set Chrome as target browser
        //intent.setPackage("com.android.chrome");
       // startActivity(intent);
        // Make sure there's an app that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No Browser Found !", Toast.LENGTH_SHORT).show();
            // Handle error - no browser found
            // You could show a Toast or log an error
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
             ispermissiongranted = true;
             startbtnclicked();

            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @OptIn(markerClass = ExperimentalCamera2Interop.class)
    private void startCamera(CameraSelector cameraSelector) {
        cameraSelector2 = cameraSelector;
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview.Builder previewBuilder = new Preview.Builder();
                Camera2Interop.Extender<Preview> previewExtender = new Camera2Interop.Extender<>(previewBuilder);
                previewBuilder.setTargetAspectRatio(AspectRatio.RATIO_4_3);


// Set manual shutter speed (exposure time in nanoseconds, e.g. 1/200s = 5,000,000 ns)

                preview = previewBuilder.build();

                preview.setSurfaceProvider(surface);

               // CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder().setResolutionStrategy(new ResolutionStrategy(bestsuitableres, ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER)).build();

                ImageAnalysis.Builder imageAnalysisBuilder = new ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST);


                Camera2Interop.Extender<ImageAnalysis> extender = new Camera2Interop.Extender<>(imageAnalysisBuilder);
                Range<Integer> fpsRange = new Range<>(30, 30);
                extender.setCaptureRequestOption(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange);
                if(imageAnalysis!= null){
                    imageAnalysis.clearAnalyzer();
                }
                imageAnalysis = imageAnalysisBuilder.build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {

                    imagepxyuni = image;
                    if(!isStopped){
                        //FLog.d("gouravhttps", "Image analysis triggered");
                    isframeavailable = true;
                    synchronized (locker) {
                        locker.notifyAll();
                    }}
                    //Log.d("gouravhttps", "timediff"+String.valueOf(System.currentTimeMillis() - actiondetected ));


                    //if ((System.currentTimeMillis() - actiondetected) > 500) {
                    // modelstarter(image);
                    // }else{
                    //    image.close();
                    // }


                    //image.close();
                    // Log.d("Gouravhttps", "Image analysis triggered");
                   //Log.d("gouravhttps", "Image format: " + image.getWidth()+".."+image.getHeight());

                });

                cameraProvider.unbindAll(); // Unbind previous use-cases
                //cameraProvider.bindToLifecycle(this, cameraSelector,imageAnalysis);
                cameraProvider.bindToLifecycle(this, cameraSelector,imageAnalysis, preview);

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Camera initialization failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, getMainExecutor());
    }

    private Size getNearestSupportedSize(Size targetSize) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0]; // back camera
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size[] supported = map.getOutputSizes(ImageFormat.YUV_420_888); // For analysis
            Size nearest = supported[0];
            int minDiff = Integer.MAX_VALUE;

            for (Size s : supported) {
               //Log.d("gouravhttp", "Supported size:"+s.getWidth()+".."+s.getHeight());
                int diff = Math.abs(s.getWidth() * s.getHeight() -
                        targetSize.getWidth() * targetSize.getHeight());
                if (diff < minDiff) {
                    minDiff = diff;
                    nearest = s;
                }
            }

            return nearest;

        } catch (Exception e) {
            e.printStackTrace();
            return targetSize; // fallback
        }
    }

public void mainlooper(){
      Thread thread1 =   new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (isframeavailable){
                        isframeavailable=false;
                        try {

                            modelstarter(imagepxyuni);
                            //Log.d("gouravhttps", "Image analysis triggered");
                        } catch (IOException e) {
                            Log.d("gouravhttp", String.valueOf(e));
                        } catch (InterruptedException e) {
                            Log.d("gouravhttp", String.valueOf(e));
                        }
                    }else{
                            try {
                            synchronized (locker) {
                                locker.wait();
                            }
                        } catch (InterruptedException e) {
                            Log.d("gouravhttp", String.valueOf(e));
                        }
                    }
                }
            }
});

         thread1.start();

}


    public void modelstarter(ImageProxy imagepxy ) throws IOException, InterruptedException {



        //String file = "/storage/emulated/0/Pictures/image.jpg";
        // FileOutputStream out = new FileOutputStream(file);
        //Bitmap bitmap = toBitmap(imagepxy);
        // if (bitmap != null) {
        //   //  saveBitmap(getApplicationContext(), bitmap, "camera_frame");
        //  }


            //saveBitmap(context,toBitmap(imagepxy), String.valueOf(System.currentTimeMillis()));

           // InputImage image = InputImage.fromFilePath(context, uri);

          //xx  InputImage image =
                  // xxInputImage.fromMediaImage(mediaImage, imagepxy.getImageInfo().getRotationDegrees());

           // image.
           /* try {
                image = InputImage.fromFilePath(context,uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
            //InputImage image =
                    //InputImage.fromMediaImage(mediaImage, 0);
       // Log.d("gouravhttp", Thread.currentThread().getName()
        // thread = new Thread(new Runnable() {
          //  @Override
           // public void run() {
        //Log.d("gouravhttp", Thread.currentThread().getName());

               //saveBitmap(getApplicationContext(), bimage, String.valueOf(System.currentTimeMillis()));

                //Log.d("gouravhttp", String.valueOf(imagepxy.getImageInfo().getRotationDegrees()));
                mediapipeimagetoskeleton1.processBitmap(imagetorotatedtoBitmap(imagepxy,imagepxy.getImageInfo().getRotationDegrees()));


            ///}
        //});
         //if(shouldexec) {
          //   thread.start();
         //}


    }


    private void showTutorialDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.tutoriall);
        dialog.setCancelable(false);

        ImageView tutorialImage = dialog.findViewById(R.id.tutorialImage);
        Button btnGotIt = dialog.findViewById(R.id.btnGetStarted);

        tutorialImage.setImageResource(R.drawable.phone);

        btnGotIt.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    public  void createTextFile(Context context, String fileName, String content) {
        FileOutputStream fos = null;
        try {
            // MODE_PRIVATE will create or overwrite the file
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.flush();
            Log.d("gouravhttp", "File created successfully at:");
            System.out.println("File created successfully at: " + context.getFilesDir() + "/" + fileName);
        } catch (IOException e) {
            Log.d("gouravhttp", "Filenotfoundwhilecreatingfile");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void dictfilesaver() {
        String dictstr = "";
        for (Map.Entry<String, Integer> entry : pushupData.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            dictstr= dictstr+key+":"+value+"\n";


            // Do something with the key-value pair}
        }

        createTextFile(getApplicationContext(), "dictdata.txt", dictstr);

    }

    public void dictappend(String full,int count){
        if (pushupData.containsKey(full.toString())){

            pushupData.put(full.toString(),pushupData.get(full.toString())+count);

        }else{

            pushupData.put(full.toString(),count);
        }
        dictfilesaver();
    }
    public Bitmap imagetorotatedtoBitmap(ImageProxy imageProxy,float rotationdegrees) {
       // Image mediaImage = imageProxy.getImage();
       tb =imageProxy.toBitmap();
      /*  if (mediaImage == null) return null;

        // Convert YUV to NV21 byte array
        ByteBuffer yBuffer = mediaImage.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = mediaImage.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = mediaImage.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize); // V first
        uBuffer.get(nv21, ySize + vSize, uSize); // U second

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, imageProxy.getWidth(), imageProxy.getHeight()), 100, out);
        byte[] jpegBytes = out.toByteArray();
*/
        //Bitmap tb =  BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);

        return rotateBitmap(tb, rotationdegrees);
    }


    public static void saveBitmap(Context context, Bitmap bitmap, String filename) {

        File file = new File("/storage/emulated/0/Pictures/"+ filename + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //Toast.makeText(context, "Saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Bitmap imageProxyToBitmap(Image image) {

        if (image == null) return null;

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private Bitmap drawLandmarks(Bitmap bitmap, List<PointF> points) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10f);

        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, 8f, paint); // Draw dot
        }
        //Log.d("gouravhttp", "Saving bitmap");
        //saveBitmap(context, mutableBitmap, "camera_frame");
        return mutableBitmap;
    }

//


    private void displayOnSurfaceView(SurfaceView surfaceView, Bitmap bitmap) {
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLACK); // Clear previous frame
            Rect dest = new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
            canvas.drawBitmap(bitmap, null, dest, null);
            surfaceView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void processAndDisplay(Image image, List<PointF> landmarkPoints, SurfaceView surfaceView) {
       // Log.d("gouravhttp", "Saving bitmap");
        Bitmap bitmap = imageProxyToBitmap(image);
        if (bitmap != null) {
            Bitmap withDots = drawLandmarks(bitmap, landmarkPoints);
            displayOnSurfaceView(surfaceView, withDots);
        }
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError")
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(
                nv21,
                ImageFormat.NV21,
                image.getWidth(),
                image.getHeight(),
                null
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public Bitmap rotateBitmap(Bitmap source, float degrees) {
        matrix = new Matrix();



        matrix.postRotate(degrees);
        return Bitmap.createBitmap(
                source,  // original bitmap
                0, 0,    // x, y of source region (usually 0,0)
                source.getWidth(),   // width of source region
                source.getHeight(),  // height of source region
                matrix,              // transformation matrix
                false                // filter — use true for smoother result
        );
    }


    public void soundclicked(View view) {

        if(isSoundenabled){
            isSoundenabled = false;
            ImageButton bt = (ImageButton) findViewById(R.id.soundbtn);
            bt.setImageDrawable(getDrawable(R.drawable.mute));

        }else{
            isSoundenabled = true;
            ImageButton bt = (ImageButton) findViewById(R.id.soundbtn);
            bt.setImageDrawable(getDrawable(R.drawable.sound));
        }

    }



    public void loaddictfromfile(Context context){

        // MODE_PRIVATE will create or overwrite the file

        try {
            FileInputStream fis = context.openFileInput("dictdata.txt");


            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\n","");
                String [] strings = line.split(":");
                String key = strings[0];
                String value = strings[1];
                Log.d("gouravhttp", "Key: " + key + ", Value: " + value);
                // datadict.put(key,value");
                pushupData.put(key,Integer.parseInt(value));

            }
        } catch (IOException e) {
            Log.d("gouravhttp", "Error reading file: " + e);
        }
    }

    public String todaysdate(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int year = calendar.get(Calendar.YEAR);

        String today = day + "/" + month + "/" + year;
        return today;

    }




}