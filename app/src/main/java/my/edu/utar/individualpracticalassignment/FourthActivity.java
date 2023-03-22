package my.edu.utar.individualpracticalassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FourthActivity extends AppCompatActivity {

    private int currentIndex = -1;
    private CountDownTimer timer;
    final List<TextView> tvList = new ArrayList<>();
    List<Integer> score_list = new ArrayList<>();
    List<String> name_list = new ArrayList<>();
    List<Integer> number = new ArrayList<>();
    boolean firstTry = true;
    boolean firstRead = true;
    private int highest = 0;
    private boolean flag = true;
    int score;
    EditText txt;
    boolean isGreater = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fourth_level);
        score = getIntent().getIntExtra("Score",0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        for (int i = 3; i<28; i++){
            String tvID = "textView" + i;
            int resID = getResources().getIdentifier(tvID,"id",getPackageName());
            TextView tv = (TextView)findViewById(resID);
            tv.setHeight(width/5);
            tvList.add(tv);
        }

        Button btn = (Button)findViewById(R.id.button4);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (flag) startGame();
            }
        });
    }

    private void startGame() {
        flag = false;
        for (TextView textView : tvList) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == tvList.get(number.get(currentIndex))){
                        next();
                    }
                }
            });
        }
        currentIndex = 0;

        if (firstTry){
            for (int i = 0; i<25;i++){
                number.add(i);
            }
            firstTry = false;
        }
        Collections.shuffle(number);
        tvList.get(number.get(currentIndex)).setBackgroundColor(Color.parseColor("#F2E34C"));
        TextView tv1 = (TextView)findViewById(R.id.textView2);
        TextView tv2 = (TextView)findViewById(R.id.textView45);
        timer = new CountDownTimer(25000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv1.setText("Timer: " + (millisUntilFinished+1000)/1000);
                Button btn = (Button)findViewById(R.id.button4);
                btn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish() {
                Button btn = (Button)findViewById(R.id.button4);
                btn.setVisibility(View.VISIBLE);
                for (TextView textView : tvList) {
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setOnClickListener(null);
                }
                tv1.setText("");
                if (currentIndex > highest) highest=currentIndex;
                tv2.setText("Highest: "+highest);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FourthActivity.this);
                builder.setTitle("Game Over!");
                builder.setMessage("Do you want to retry or back to Homepage?");
                builder.setCancelable(false);
                builder.setNeutralButton("Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String file_name = "game_data.txt";

                        //Read the locally stored scoreboard, see whether the latest score can enter the scoreboard
                        FileInputStream fis = null;
                        try {
                            fis = openFileInput(file_name);
                            InputStreamReader isr = new InputStreamReader(fis);
                            BufferedReader br = new BufferedReader(isr);
                            String text;
                            //Store into array
                            score_list.clear();
                            name_list.clear();
                            while ((text = br.readLine()) != null) {
                                if (isNumeric(text)) {
                                    score_list.add(Integer.parseInt(text));
                                } else {
                                    name_list.add(text);
                                }
                            }
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fis!= null){
                                try{
                                    fis.close();
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        for (int score : score_list) {
                            if (currentIndex + 1 > score) {
                                isGreater = true;
                                break;
                            }
                        }

                        //See whether can enter the scoreboard
                        if ((score_list.size() < 25 || isGreater) && currentIndex!=0){
                            android.app.AlertDialog.Builder inputName = new android.app.AlertDialog.Builder(FourthActivity.this);
                            final EditText editTextName1 = new EditText(FourthActivity.this);
                            inputName.setTitle("Enter your name");
                            inputName.setView(editTextName1);
                            inputName.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (score_list.size() >= 25 && isGreater){
                                        score_list.remove(24);
                                        name_list.remove(24);
                                        txt = editTextName1;
                                        name_list.add(txt.getText().toString());
                                        score_list.add(highest+score);
                                        dialog.cancel();
                                    }else {
                                        txt = editTextName1;
                                        name_list.add(txt.getText().toString());
                                        score_list.add(highest+score);
                                        dialog.cancel();
                                    }

                                    // create a map to associate each score with its corresponding name
                                    Map<Integer, String> scoreNameMap = new HashMap<>();
                                    for (int i = 0; i < score_list.size(); i++) {
                                        scoreNameMap.put(score_list.get(i), name_list.get(i));
                                    }

                                    // sort the scores array list in descending order
                                    Collections.sort(score_list, Collections.reverseOrder());

                                    // iterate through the sorted scores to get the corresponding names
                                    ArrayList<String> sortedNames = new ArrayList<>();
                                    for (Integer score : score_list) {
                                        sortedNames.add(scoreNameMap.get(score));
                                    }

                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < score_list.size(); i++) {
                                        sb.append("#").append(i+1).append("  ").append(score_list.get(i)).append("  ").append(sortedNames.get(i)).append("\n");
                                    }

                                    StringBuilder sb1 = new StringBuilder();
                                    for (int i = 0; i < score_list.size(); i++) {
                                        sb1.append(sortedNames.get(i)).append("\n").append(score_list.get(i)).append("\n");
                                    }

                                    //If the latest score is high enough to enter the scoreboard
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileOutputStream = openFileOutput(file_name,MODE_PRIVATE);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        fileOutputStream.write(sb1.toString().getBytes());
                                        fileOutputStream.flush();
                                        fileOutputStream.close();

                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    android.app.AlertDialog.Builder scoreboard = new android.app.AlertDialog.Builder(FourthActivity.this);
                                    scoreboard.setTitle("Scoreboard");
                                    scoreboard.setMessage(sb);
                                    scoreboard.setPositiveButton("Homepage", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(FourthActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            dialog.cancel();
                                        }
                                    });
                                    scoreboard.show();
                                }
                            });
                            //If user do not want to enter their name into the scoreboard
                            inputName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(FourthActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            });
                            inputName.show();
                        }else{
                            Intent intent = new Intent(FourthActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGame();
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                flag = true;
            }
        }.start();
    }

    private void next() {
        TextView tv1 = (TextView)findViewById(R.id.textView2);

        if (currentIndex < 24) {
            tvList.get(number.get(currentIndex)).setBackgroundColor(Color.parseColor("#AAFF00"));
            currentIndex++;
            tvList.get(number.get(currentIndex)).setBackgroundColor(Color.parseColor("#F2E34C"));
        } else {
            TextView tv2 = (TextView)findViewById(R.id.textView45);
            if (currentIndex > highest) highest=currentIndex+1;
            tv2.setText("Highest: "+highest);
            Button btn = (Button)findViewById(R.id.button4);
            btn.setVisibility(View.VISIBLE);
            timer.cancel();
            for (TextView textView : tvList) {
                textView.setBackgroundColor(Color.WHITE);
                textView.setOnClickListener(null);
            }
            tv1.setText("");
            flag = true;

            //After game pop out
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Level Passed!");
            builder.setMessage("Congratulations!");
            builder.setCancelable(false);
            builder.setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(FourthActivity.this, FifthActivity.class);
                    intent.putExtra("Score", currentIndex+1+score);
                    startActivity(intent);
                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Home", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    final String file_name = "game_data6.txt";

                    //Read the locally stored scoreboard, see whether the latest score can enter the scoreboard
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(file_name);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);
                        String text;
                        //Store into array
                        score_list.clear();
                        name_list.clear();
                        while ((text = br.readLine()) != null) {
                            if (isNumeric(text)) {
                                score_list.add(Integer.parseInt(text));
                            } else {
                                name_list.add(text);
                            }
                        }
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fis!= null){
                            try{
                                fis.close();
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    for (int score : score_list) {
                        if (currentIndex + 1 > score) {
                            isGreater = true;
                            break;
                        }
                    }
                    //See whether can enter the scoreboard
                    if (score_list.size() < 25){
                        android.app.AlertDialog.Builder inputName = new android.app.AlertDialog.Builder(FourthActivity.this);
                        final EditText editTextName1 = new EditText(FourthActivity.this);
                        inputName.setTitle("Enter your name");
                        inputName.setView(editTextName1);
                        inputName.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (score_list.size() >= 25 && isGreater){
                                    score_list.remove(24);
                                    name_list.remove(24);
                                    txt = editTextName1;
                                    name_list.add(txt.getText().toString());
                                    score_list.add(highest+score);
                                    dialog.cancel();
                                }else {
                                    txt = editTextName1;
                                    name_list.add(txt.getText().toString());
                                    score_list.add(highest+score);
                                    dialog.cancel();
                                }

                                // create a map to associate each score with its corresponding name
                                Map<Integer, String> scoreNameMap = new HashMap<>();
                                for (int i = 0; i < score_list.size(); i++) {
                                    scoreNameMap.put(score_list.get(i), name_list.get(i));
                                }

                                // sort the scores array list in descending order
                                Collections.sort(score_list, Collections.reverseOrder());

                                // iterate through the sorted scores to get the corresponding names
                                ArrayList<String> sortedNames = new ArrayList<>();
                                for (Integer score : score_list) {
                                    sortedNames.add(scoreNameMap.get(score));
                                }

                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < score_list.size(); i++) {
                                    sb.append("#").append(i+1).append("  ").append(score_list.get(i)).append("  ").append(sortedNames.get(i)).append("\n");
                                }

                                StringBuilder sb1 = new StringBuilder();
                                for (int i = 0; i < score_list.size(); i++) {
                                    sb1.append(sortedNames.get(i)).append("\n").append(score_list.get(i)).append("\n");
                                }

                                //If the latest score is high enough to enter the scoreboard
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = openFileOutput(file_name,MODE_PRIVATE);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    fileOutputStream.write(sb1.toString().getBytes());
                                    fileOutputStream.flush();
                                    fileOutputStream.close();

                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                                android.app.AlertDialog.Builder scoreboard = new android.app.AlertDialog.Builder(FourthActivity.this);
                                scoreboard.setTitle("Scoreboard");
                                scoreboard.setMessage(sb);
                                scoreboard.setPositiveButton("Homepage", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(FourthActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                });
                                scoreboard.show();
                            }
                        });
                        //If user do not want to enter their name into the scoreboard
                        inputName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(FourthActivity.this, MainActivity.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });
                        inputName.show();
                    }else{
                        Intent intent = new Intent(FourthActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}