package com.blessed_brains.visionfitAi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyDrawer {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    // color_map {id: color}
    private static final Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>() {{
        /*put(1,Color.rgb(255, 0, 0) );
        put(2,Color.rgb(0, 255, 0) );
        put(3, Color.rgb(0, 0, 25));
        put(4, Color.rgb(0, 255, 255));
        put(5,Color.rgb(255, 255, 0));
        put(6,Color.rgb(255, 0, 255));
        put(7, Color.rgb(128, 0, 0));   // Dark Blue
        put(8, Color.rgb(0, 128, 0));   // Dark Green
        put(9, Color.rgb(0, 0, 128));   // Dark Red
        put(10, Color.rgb(128, 128, 0));// Olive
        put(11, Color.rgb(128, 0, 128));// Purple
        put(12, Color.rgb(0, 128, 128));// Teal
        put(13, Color.rgb(192, 192, 192));   // Dark Blue
        put(14, Color.rgb(128, 128, 128));   // Dark Green
        put(15, Color.rgb(0, 69, 255));   // Dark Red
        put(16, Color.rgb(147, 20, 255));// Olive
        put(17, Color.rgb(19, 69, 139));// Purple
        put(18, Color.rgb(139, 0, 139));// Teal
        put(19, Color.rgb(0, 140, 255));   // Dark Green
        put(20, Color.rgb(60, 179, 113));// Olive, 255));   // Dark Red
        put(21, Color.rgb(255, 105, 180));// Olive
        put(22, Color.rgb(70, 130, 180));// Purple
        put(23, Color.rgb(154, 205, 50));// Teal
        put(24, Color.rgb(0, 215, 255));   // Dark Green
        put(25, Color.rgb(0, 191, 255));   // Dark Red
        put(26, Color.rgb(34, 139, 34));// Olive
        put(27, Color.rgb(255, 20, 147));// Purple
        put(28, Color.rgb(106, 90, 205));// Teal
        put(29, Color.rgb(210, 105, 30));
        put(30, Color.rgb(240, 230, 140));
        put(31, Color.rgb(173, 216, 230));
        put(32, Color.rgb(152, 251, 152));*/





        put(1,  Color.rgb(0, 0, 255));     // BGR(255,0,0) -> RGB(0,0,255)
        put(2,  Color.rgb(0, 255, 0));     // BGR(0,255,0) -> RGB(0,255,0)
        put(3,  Color.rgb(25, 0, 0));      // BGR(0,0,25)  -> RGB(25,0,0)
        put(4,  Color.rgb(255, 255, 0));   // BGR(0,255,255)-> RGB(255,255,0)
        put(5,  Color.rgb(0, 255, 255));   // BGR(255,255,0)-> RGB(0,255,255)
        put(6,  Color.rgb(255, 0, 255));   // BGR(255,0,255)-> RGB(255,0,255)
        put(7,  Color.rgb(0, 0, 128));     // Dark Blue
        put(8,  Color.rgb(0, 128, 0));     // Dark Green
        put(9,  Color.rgb(128, 0, 0));     // Dark Red
        put(10, Color.rgb(0, 128, 128));   // Olive
        put(11, Color.rgb(128, 0, 128));   // Purple
        put(12, Color.rgb(128, 128, 0));   // Teal
        put(13, Color.rgb(192, 192, 192)); // Silver
        put(14, Color.rgb(128, 128, 128)); // Gray
        put(15, Color.rgb(255, 69, 0));    // BGR(0,69,255)-> RGB(255,69,0)
        put(16, Color.rgb(255, 20, 147));  // BGR(147,20,255)-> RGB(255,20,147)
        put(17, Color.rgb(139, 69, 19));   // BGR(19,69,139)-> RGB(139,69,19)
        put(18, Color.rgb(139, 0, 139));   // Magenta
        put(19, Color.rgb(255, 140, 0));   // BGR(0,140,255)-> RGB(255,140,0)
        put(20, Color.rgb(113, 179, 60));  // BGR(60,179,113)-> RGB(113,179,60)
        put(21, Color.rgb(180, 105, 255)); // BGR(255,105,180)-> RGB(180,105,255)
        put(22, Color.rgb(180, 130, 70));  // BGR(70,130,180)-> RGB(180,130,70)
        put(23, Color.rgb(50, 205, 154));  // BGR(154,205,50)-> RGB(50,205,154)
        put(24, Color.rgb(255, 215, 0));   // BGR(0,215,255)-> RGB(255,215,0)
        put(25, Color.rgb(255, 191, 0));   // BGR(0,191,255)-> RGB(255,191,0)
        put(26, Color.rgb(34, 139, 34));   // Forest Green
        put(27, Color.rgb(147, 20, 255));  // BGR(255,20,147)-> RGB(147,20,255)
        put(28, Color.rgb(205, 90, 106));  // BGR(106,90,205)-> RGB(205,90,106)
        put(29, Color.rgb(30, 105, 210));  // BGR(210,105,30)-> RGB(30,105,210)
        put(30, Color.rgb(140, 230, 240)); // BGR(240,230,140)-> RGB(140,230,240)
        put(31, Color.rgb(230, 216, 173)); // BGR(173,216,230)-> RGB(230,216,173)
        put(32, Color.rgb(152, 251, 152)); //

    }};

    // targetedbody list (like Python list)
    private static final int[] targetedBody = {
            8,12, 7,11, 12,24, 11,23, 24,26,
            23,25, 26,28, 25,27, 12,14, 14,16,
            11,13, 13,15
    };

    /**
     * Main method: replicate circleconnectstarter(list1, outputfolder, file)
     *
     * @param list1   input list [id, x, y, id, x, y, ...]
     * @return Bitmap with circles + lines drawn
     */
    public Bitmap circleConnectStarter(List<Float> list1) {
        Bitmap image = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.WHITE);
        //Log.d("gouravhttp", "circleConnectStarter:");
        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(3f);
        linePaint.setAntiAlias(true);

        // track found ids
        List<Integer> bodyIdFound = new ArrayList<>();
        //Log.d("gouravhttp", String.valueOf(list1.size()));
        // draw circles
        for (int i = 0; i < list1.size(); i += 3) {
            int idd = Math.round(list1.get(i));
            if (isInTargetedBody(idd)) {
                float x = list1.get(i + 1) * WIDTH;
                float y = list1.get(i + 2) * HEIGHT;

                int color = colorMap.containsKey(idd) ? colorMap.get(idd) : Color.BLACK;

                //Log.d("gouravhttp", "Color::" + color);
                circlePaint.setColor(color);
                canvas.drawCircle(x, y, 5, circlePaint);

                bodyIdFound.add(idd);
            }
        }

        // draw lines
        for (int i = 0; i < targetedBody.length; i += 2) {
            int id1 = targetedBody[i];
            int id2 = targetedBody[i + 1];

            if (bodyIdFound.contains(id1) && bodyIdFound.contains(id2)) {
                int index1 = list1.indexOf((float) id1);
                int index2 = list1.indexOf((float) id2);

                if (index1 != -1 && index2 != -1) {
                    float x1 = list1.get(index1 + 1) * WIDTH;
                    float y1 = list1.get(index1 + 2) * HEIGHT;
                    float x2 = list1.get(index2 + 1) * WIDTH;
                    float y2 = list1.get(index2 + 2) * HEIGHT;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }
            }
        }

        //saveBitmap(context,image,String.valueOf(System.currentTimeMillis()));

        return image;
    }

    // helper to check if id is in targetedBody[]
    private static boolean isInTargetedBody(int id) {
        for (int val : targetedBody) {
            if (val == id) return true;
        }
        return false;
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


}
