package com.blessed_brains.visionfitAi;

import static com.blessed_brains.visionfitAi.MainActivity.pushupData;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.prolificinteractive.materialcalendarview.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


    public class PushupTrackerActivity extends AppCompatActivity {
        static Map<String,String> datadict = new HashMap<>();
        GridLayout gridHeatmap;
        TextView tvSelectedDate;
        EditText etPushups;
        Button btnSave;

        SharedPreferences prefs;
        Calendar calendar = Calendar.getInstance();

        String selectedDate;
        static int target = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pushup_tracker);
            prefs = getSharedPreferences("target",MODE_PRIVATE);

            gridHeatmap = findViewById(R.id.gridHeatmap);

            target=loadtarget();




            btnSave = findViewById(R.id.btnSave);
            ImageButton btnMenu = findViewById(R.id.btnSave2);
            btnMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
                popup.getMenuInflater().inflate(R.menu.menuxml, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_settings) {

                        pushupData.clear();
                        //Log.d("gouravhttp",String.valueOf(pushupData));
                        dictfilesaver();

                        loaddictfromfile(getApplicationContext());

                        generateHeatmap();


                    } else if (item.getItemId()==R.id.action_about) {
                        showCustomInputDialog();


                    }
                    return true;
                });
                popup.show();
            });

            //loadData();
            generateHeatmap();
        }
        public void closebtnclicked(View view){
            finish();
        }
      
        public void editrecordclicked(View view){


            startActivity(new Intent(this, MainActivity.class));


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
                if(text.equals("")){

                }else {
                    target = Integer.parseInt(text);

                    saveTarget(target);
                    generateHeatmap();
                }
                //Toast.makeText(this, "You entered: " + text, Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }


        private void generateHeatmap() {
            gridHeatmap.removeAllViews();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar tempcal = Calendar.getInstance();
            int totalcount = 0;
            for (int i = 1; i <= daysInMonth; i++) {
                tempcal.set(Calendar.DAY_OF_MONTH, i);
                String key = getDateKey(tempcal);
                key = String.valueOf(key);
               // Log.d("gouravhttp", "Key1: " + key);
                int daycount = pushupData.getOrDefault(key,0);
                float daycountf = Float.parseFloat(String.valueOf(daycount));
                totalcount = totalcount+daycount;
                TextView cell = new TextView(this);
                final int day = i;
                cell.setWidth(120);
                cell.setHeight(120);
                cell.setGravity(android.view.Gravity.CENTER);
                cell.setText(String.valueOf(i));
                cell.setTextColor(Color.BLACK);

                cell.setBackground(new BitmapDrawable(background_gradient_bitmaper(Float.parseFloat(String.valueOf(daycountf*100/target)))));


                cell.setOnClickListener(v -> onDayClick(day,v));
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

    // ✅ Set margins (left, top, right, bottom)
                params.setMargins(dpToPx(8), dpToPx(8), dpToPx(20), dpToPx(64));

    // ✅ Apply LayoutParams
                cell.setLayoutParams(params);



                gridHeatmap.addView(cell);
            }
            TextView tv= (TextView) findViewById(R.id.tvTotal);
            tv.setText("Total: "+totalcount);
        }

        private int getColorForDay(int day) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String key = getDateKey(calendar);
            int count = pushupData.getOrDefault(key, 0);

            if (count == 0) return Color.parseColor("#222222");
            else if (count < 30) return Color.parseColor("#3355FF55");
            else if (count < 60) return Color.parseColor("#5599FF55");
            else if (count < 100) return Color.parseColor("#88CCFF55");
            else return Color.parseColor("#BB00FF00");
        }

        private void onDayClick(int day, View v1) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            selectedDate = getDateKey(calendar);

            tvSelectedDate.setText("Selected: " + selectedDate);
            etPushups.setText(String.valueOf(pushupData.getOrDefault(selectedDate, 0)));
            etPushups.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);

            btnSave.setOnClickListener(v -> {
                int count = Integer.parseInt(etPushups.getText().toString());
                dictappend(selectedDate,count);

                int daycount = pushupData.getOrDefault(selectedDate,0);
                float daycountf = Float.parseFloat(String.valueOf(daycount));
                v1.setBackground(new BitmapDrawable(background_gradient_bitmaper(Float.parseFloat(String.valueOf(daycountf/100)))));

               /* if (!pushupData.containsKey(selectedDate)) {

                } else {

                    pushupData.put(selectedDate, pushupData.get(selectedDate) + count);
                }*/
                dictfilesaver();
                //saveData();
                //generateHeatmap();
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            });
        }

        private String getDateKey(Calendar cal) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
        }

        private void saveTarget(int target) {
            SharedPreferences.Editor editor = prefs.edit();
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(target));
            editor.putString("target", sb.toString());
            editor.apply();
        }

        private int loadtarget() {
            String saved = prefs.getString("target", "");
            if (saved.equals("")) {
                return 100;
            }
            return Integer.parseInt(saved);
        }


        public Bitmap background_gradient_bitmaper(float percent) {
            percent = percent/100f;
            // Create a bitmap with view size
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();

            // Define the gradient with precise color stops
            LinearGradient gradient = null;

            float splitY = 100 * (1.0f - percent);

            // Top part: success green
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, 100, splitY, paint);

            // Bottom part: white
            paint.setColor(0xFF4CAF50);
            canvas.drawRect(0, splitY, 100, 100, paint);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                gradient = new LinearGradient(
                        0, 0, 0, 100, // vertical gradient
                        new int[]{
                                0xFFFFFFFF
                        , // start color (white)
                                0xFFFFFFFF
                        , // stays white till 80%
                                0xFF4CAF50  // end color
                        },
                        new float[]{
                                0f,   // start
                               1.0f- percent, // white till 80%
                                1f    // then fade to pink
                        },
                        Shader.TileMode.CLAMP
                );
            }

            //paint.setShader(gradient);

            //canvas.drawRect(0, 0, 100, 100, paint);
            /*paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setAntiAlias(true);
            canvas.drawText("20", 30, 30, paint);*/
          return bitmap;
        }
    public int dpToPx(int dp) {

        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
       return marginInPx;
    }
    public void dictappend(String full,int count){
     if (pushupData.containsKey(full.toString())){

         pushupData.put(full.toString(),pushupData.get(full.toString())+count);

     }else{

           pushupData.put(full.toString(),count);
     }
    }

        /*@Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menuxml, menu);
            return true;
        }*/

       /* @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_settings) {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_about) {
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }*/

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


        public  void createTextFile(Context context, String fileName, String content) {
            FileOutputStream fos = null;
            try {
                // MODE_PRIVATE will create or overwrite the file
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(content.getBytes());
                fos.flush();
                //Log.d("gouravhttp", "File created successfully at:");
                //System.out.println("File created successfully at: " + context.getFilesDir() + "/" + fileName);
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
              //  Log.d("gouravhttp", "Key: " + key + ", Value: " + value);
               // datadict.put(key,value");
               pushupData.put(key,Integer.parseInt(value));

            }
        } catch (IOException e) {
            Log.d("gouravhttp", "Error reading file: " + e);
        }
    }
    public void trackesettingclicked(View v){





    }
public int getTodaysdate(){
    LocalDate today = LocalDate.now();
    String formattedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    return Integer.parseInt(formattedDate.split("/")[0]);


}




    }

