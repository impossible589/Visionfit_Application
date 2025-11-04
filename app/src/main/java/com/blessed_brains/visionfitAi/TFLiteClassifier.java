package com.blessed_brains.visionfitAi;

import static com.blessed_brains.visionfitAi.MainActivity.imagepxyuni;
import static com.blessed_brains.visionfitAi.MainActivity.isSoundenabled;
import static com.blessed_brains.visionfitAi.MainActivity.isttsReady;
import static com.blessed_brains.visionfitAi.MainActivity.mainActivity1;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TFLiteClassifier {
    static final String locker2 = "";
    float[] floatarray;
    static int countincurrentseason= 0;
    String reqlabel;
    ByteBuffer inputBuffer;
    private Interpreter interpreter;
   static long actiondetected ;
   static float totalcount = 0f;
   static TextView countview;
   boolean isbufferallocated = false;
    TensorBuffer buffer;
   View v = null;
  static boolean isfirstTime = true;
   int currentlabelindex = -1;
   float currentframepower = 0f;
   int nextindextocheck =-1;
    TensorProcessor processor;
    FloatBuffer floatBuffer;
   static long countstarted;
   static String prevlabel = "pushdown";
   static String currentlabel = "";
    TensorImage tensorImage;
    ByteBuffer byteBuffer;
    TextView infotview;
    TextView pv;
    float maxrubbishthres;
    int[] intValues;
    Bitmap resized;
    private static final int IMAGE_SIZE = 224;   // input size for MobileNetV2
    private static final int NUM_CLASSES = 3;    // adjust if you have >2 classes
    private String[] labels = {"pushdown", "pushup", "rubbish"}; // replace with your dataset folder names
    //private String[] labels = {"pushdown", "pushup"};
    public TFLiteClassifier(AssetManager assetManager, String modelPath) throws IOException {


       interpreter = new Interpreter(loadModelFile(assetManager, modelPath));
       pv = mainActivity1.findViewById(R.id.pushupLabel);


        processor = new TensorProcessor.Builder().build();
        countview = mainActivity1.findViewById(R.id.pushupCount);
        infotview = mainActivity1.findViewById(R.id.informertext);



        // Update UI components here
                //Log.d("gouravhttp", "TFLiteClassifier");





    }

    // Load .tflite file from assets
    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Run inference on a Bitmap
    public String classify(Bitmap bitmap) throws InterruptedException {


        // Convert bitmap → ByteBuffer
        inputBuffer = bitmapToByteBuffer(bitmap);
        //Log.d("gouravhttp", "TFLiteClassifier");
        // Output array
        float[][] output = new float[1][NUM_CLASSES];

        // Run inference
       // long start = System.currentTimeMillis();
        interpreter.run(inputBuffer, output);
        //long end = System.currentTimeMillis();
        // Find predicted class
        int predictedClass = argMax(output[0]);
        float confidence = output[0][predictedClass];
        float pushupfv = (output[0][1]/5.0f);
        float pushdownfv =  (output[0][0]/5.5f);
        List<String> flabels = Arrays.asList("pushdown", "pushup");
        //saveBitmap(context,bitmap,String.valueOf(System.currentTimeMillis()));
        List<Float> fvaluelist = Arrays.asList(pushdownfv,pushupfv);

        float maxValue = Collections.max(fvaluelist);
        int maxIndex = fvaluelist.indexOf(maxValue);
        //String maxLabel = flabels.get(maxIndex);
        //
        // Log.d("gouravhttps", String.valueOf(maxLabel));
        if (isfirstTime){
            prevlabel = "pushdown";
            isfirstTime= false;
            currentlabelindex = 0;
        }else{
            //maxValue = fvaluelist.get(nextindextocheck);
        }

        if(currentlabelindex == (flabels.size()-1)){
            nextindextocheck = 0;

        }else{

            nextindextocheck = currentlabelindex+1;

        }


        maxValue = fvaluelist.get(nextindextocheck);

        reqlabel = flabels.get(nextindextocheck);
        //Log.d("gouravhttplabel", "oirgpushdown"+String.valueOf(output[0][0])+"origpushup"+String.valueOf(output[0][1])+"xy__"+String.valueOf(fvaluelist)+"xylabels__"+String.valueOf(flabels));
        //Log.d("gouravhttplabel", "currentrunindex--"+currentlabelindex+"reqlabel--" +reqlabel+"reqindex--"+nextindextocheck);
        //Log.d("gouravhttpcorspmaxv", String.valueOf(maxValue));
       // if(!isfrontcamera && reqlabel == "pushup"){
         //   maxValue = (maxValue*5.5f)/5.5f;
        //}
        if(reqlabel!=prevlabel){

        }else{
            maxValue= 0.0f;
        }
        if(reqlabel=="pushup"){
            maxrubbishthres = -3.6f;
        }else if (reqlabel=="pushdown"){
            maxrubbishthres = -2.0f;
        }else{
           maxrubbishthres= -4.0f;
        }

        //  Log.d("gouravhttp", String.valueOf(maxValue));
        //Log.d("gouravhttp", String.valueOf(isfrontcamera)+String.valueOf("fullpred") + "______" + String.valueOf(output[0][0])+"xx"+ String.valueOf(output[0][1])+ "xx"+String.valueOf(output[0][2]));

        //Log.d("gouravhttps", String.valueOf("timeelapsed")+String.valueOf(System.currentTimeMillis()-actiondetected));
        //actiondetected = System.currentTimeMillis();
        if(maxValue>1.0){
            //saveBitmap(context,bitmap,String.valueOf(System.currentTimeMillis()));
            prevlabel = reqlabel;
            //Log.d("gouravhttp", String.valueOf(isfrontcamera)+String.valueOf("fullpred") + "______" + String.valueOf(output[0][0])+"xx"+ String.valueOf(output[0][1])+ "xx"+String.valueOf(output[0][2]));
            //Log.d("gouravhttp", String.valueOf(Thread.currentThread().getName()));


            //synchronized (locker2) {
              // locker2.wait();
            //}

            imagepxyuni.close();
            mainActivity1.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    totalcount = totalcount+1f;
                    currentlabel = reqlabel;
                    isfirstTime = false;
                    currentlabelindex = flabels.indexOf(currentlabel);
                    //actiondetected = System.currentTimeMillis();
                   // Log.d("gouravhttps", String.valueOf("gotframeActionnnnnnnnnnnnnn"));
                    // String current = ((TextView) mainActivity1.findViewById(R.id.pushupLabel)).getText().toString();
                    //float count = Float.parseFloat(current);

                    if(reqlabel == "pushup"){
                        infotview.setText(String.valueOf("Move Down"));
                    }else if(reqlabel=="pushdown"){
                        infotview.setText(String.valueOf("Move Up"));
                    }


                    if((totalcount%2)==0){
                        countincurrentseason = countincurrentseason+1;
                        countview.setText(String.valueOf(totalcount/2));
                        mainActivity1.increasecounter();
                        if(isSoundenabled && isttsReady){
                        mainActivity1.speakText(String.valueOf(totalcount/2).split("\\.")[0]);}
                    }


                    //)
                    //countview.setText(String.valueOf(totalcount));




                }
            });

        }else{
            imagepxyuni.close();
        }


        //Log.d("gouravhttp", String.valueOf(("time_taken")) + "______" + String.valueOf(System.currentTimeMillis() - countstarted));
        //labels[predictedClass] == "pushup" && confidence > 6.0
        /*if (output[0][1] > 6.0) {
           // Log.d("gouravhttp", String.valueOf((labels[predictedClass])) + "______" + String.valueOf(confidence));
           // Log.d("gouravhttp", String.valueOf("fullpred") + "______" + String.valueOf(output[0][0])+"xx"+ String.valueOf(output[0][1])+ "xx"+String.valueOf(output[0][2]));

            mainActivity1.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    actiondetected = System.currentTimeMillis();
                   // String current = ((TextView) mainActivity1.findViewById(R.id.pushupLabel)).getText().toString();
                    //float count = Float.parseFloat(current);
                    TextView pv = mainActivity1.findViewById(R.id.pushupLabel) ;
                    pv.setText(String.valueOf("PUSH_UP"));
                   // mainActivity1.setContentView(R.layout.activity_main);
                    imagepxyuni.close();
                }
            });
           // saveBitmap(context,bitmap,String.valueOf(System.currentTimeMillis()));
        }
        else if (output[0][0] > 8.0) {
          //  Log.d("gouravhttp", String.valueOf((labels[predictedClass])) + "______" + String.valueOf(confidence));
          //  Log.d("gouravhttp", String.valueOf("fullpred") + "______" + String.valueOf(output[0][0])+"xx"+ String.valueOf(output[0][1])+"xx"+ String.valueOf(output[0][2]));

            // Toast.makeText(context, String.valueOf((labels[predictedClass]))+"Confidence"+String.valueOf(confidence), Toast.LENGTH_SHORT).show();
            mainActivity1.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    actiondetected = System.currentTimeMillis();
                    //mainActivity1.setContentView(R.layout.activity_down);
                    //String current = ((TextView) mainActivity1.findViewById(R.id.pushupCount)).getText().toString();
                    //float count = Float.parseFloat(current);
                    TextView pv = mainActivity1.findViewById(R.id.pushupLabel) ;
                    pv.setText(String.valueOf("Push_Down"));
                    imagepxyuni.close();

                }
            });

        }else  {
           // Log.d("gouravhttp", String.valueOf("rubbish"));
            imagepxyuni.close();
        }*/


        return labels[predictedClass];
    }



    // Convert Bitmap to ByteBuffer (with normalization /255.0)
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);



        resized.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE);
        byteBuffer.position(0);

        int pixelIndex = 0;
        int size = IMAGE_SIZE;
        int total = size * size;
        int[] values = intValues;  // local reference

        for (int i = 0; i < total; i++) {
            int pixel = values[i];

            // Extract in one go
            byteBuffer.putFloat((pixel >> 16) & 0xFF);
            byteBuffer.putFloat((pixel >> 8) & 0xFF);
            byteBuffer.putFloat(pixel & 0xFF);
        }

        return byteBuffer;
    }


    public @NonNull ByteBuffer bitmapToByteBuffer(Bitmap bitmap) {
       // Log.d("gouravhttp", "bitmapToByteBuffer");
             if(isbufferallocated){
                 byteBuffer.clear();
                 floatBuffer.clear();
             }

              resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, false);
              tensorImage = TensorImage.fromBitmap(resized);
            // NO-OP
             buffer = processor.process(tensorImage.getTensorBuffer());
             floatarray = buffer.getFloatArray();
             if(!isbufferallocated){
             byteBuffer = ByteBuffer.allocate(floatarray.length * 4);

                 byteBuffer.order(ByteOrder.nativeOrder());
                 floatBuffer = byteBuffer.asFloatBuffer();
                 isbufferallocated = true;

             }
            floatBuffer.put(floatarray);
          return byteBuffer;


// or

    }



    // Utility: find index of max probability
    private int argMax(float[] probs) {
        int maxIndex = 0;
        float maxProb = probs[0];
        for (int i = 1; i < probs.length; i++) {
            if (probs[i] > maxProb) {
                maxProb = probs[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
