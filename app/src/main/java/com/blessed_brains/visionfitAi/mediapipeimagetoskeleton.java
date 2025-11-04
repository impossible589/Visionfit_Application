package com.blessed_brains.visionfitAi;

import static com.blessed_brains.visionfitAi.MainActivity.classifier;
import static com.blessed_brains.visionfitAi.MainActivity.context;
import static com.blessed_brains.visionfitAi.MainActivity.imagepxyuni;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class mediapipeimagetoskeleton {
    List<NormalizedLandmark> poseLandmarks;
    private PoseLandmarker poseLandmarker;
    PoseLandmarkerResult poseLandmarkerResult;
    MPImage mpImage;
    BodyDrawer bodyDrawer;
    NormalizedLandmark landmark;
    List<Float> stringList;

    public void init() {
        loadmodel();
    }

    private void loadmodel() {
        try {
            String modelName = "pose.task";
            File modelFile = copyAssetToCache(context, modelName); // 'this' = Context (Activity/Application)

            BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath(modelFile.getAbsolutePath()) // ✅ This now works!
                    .build();

            PoseLandmarker.PoseLandmarkerOptions options = PoseLandmarker.PoseLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinPoseDetectionConfidence(0.5f)
                    .setMinTrackingConfidence(0.5f)
                    .setMinPosePresenceConfidence(0.5f)
                    .setNumPoses(1)
                    .setRunningMode(RunningMode.VIDEO)
                    //.setResultListener(this::returnLivestreamResult)
                    // .setErrorListener(this::returnLivestreamError)


                    .build();

            poseLandmarker = PoseLandmarker.createFromOptions(context, options);
            bodyDrawer = new BodyDrawer();
            //Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/gym.jpeg");


        } catch (IOException e) {
            Log.d("gouravhttp", "Error loading model:" + String.valueOf(e));
            // Handle error: e.g., show message, retry, or fallback
        }


    }

    public void returnLivestreamError(RuntimeException e) {
    }


    public void returnLivestreamResult(PoseLandmarkerResult poseLandmarkerResult, MPImage mpImage) {


    }


    public void processBitmap(Bitmap bitmap) {

        mpImage = new BitmapImageBuilder(bitmap).build();
        long frameTime = SystemClock.uptimeMillis();

        //PoseLandmarkerResult poseLandmarkerResult = poseLandmarker.detectForVideo(mpImage, frameTime);
        poseLandmarkerResult = poseLandmarker.detectForVideo(mpImage, frameTime);
        //Log.d("gouravhttp", String.valueOf(poseLandmarkerResult));

        int x;
        int y;
        int c;

        //Log.d("gouravhttp", Thread.currentThread().getName());
        stringList = new ArrayList<>();
        //List<List<NormalizedLandmark>> normalizedLandmarks = poseLandmarkerResult.landmarks();
        if (poseLandmarkerResult.landmarks().size() > 0) {
            poseLandmarks = poseLandmarkerResult.landmarks().get(0);


            //Log.d("gouravhttp", "returnLivestreamResult" +String.valueOf(poseLandmarks.size()));
            for (int id = 0; id < poseLandmarks.size(); id++) {
                landmark = poseLandmarks.get(id);
                stringList.add(Float.valueOf(id));
                stringList.add(landmark.x());
                stringList.add(landmark.y());

            }
            List<Float> copy = new ArrayList<>(stringList);
            stringList.clear();

            //Log.d("gouravhttp", Thread.currentThread().getName());
            // Log.d("gouravhttp", Thread.currentThread().getName());
            try {
                classifier.classify(bodyDrawer.circleConnectStarter(copy));
            } catch (InterruptedException e) {
                Log.d("gouravhttp", "Error loading model:" + String.valueOf(e));

            }
        } else {
            imagepxyuni.close();
        }


    }


    public static File copyAssetToCache(Context context, String assetFileName) throws IOException {
        File cacheFile = new File(context.getCacheDir(), assetFileName);

        // Skip copying if already exists
        if (cacheFile.exists()) {
            Log.d("gouravhttp", assetFileName + " already exists in cache.");
            return cacheFile;
        }

        try (InputStream inputStream = context.getAssets().open(assetFileName);
             FileOutputStream outputStream = new FileOutputStream(cacheFile)) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            Log.d("gouravhttp", "Copied " + assetFileName + " to " + cacheFile.getAbsolutePath());
        }

        return cacheFile;
    }


}
