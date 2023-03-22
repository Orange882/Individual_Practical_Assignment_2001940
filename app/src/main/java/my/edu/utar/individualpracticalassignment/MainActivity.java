package my.edu.utar.individualpracticalassignment;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    List<Integer> score_list = new ArrayList<>();
    List<String> name_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startBtn = (Button)findViewById(R.id.button1);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this,
                        FirstActivity.class);
                startActivity(intent);
            }
        });

        Button rankBtn = (Button)findViewById(R.id.button3);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String file_name = "game_data.txt";
                FileInputStream fis = null;
                try {
                    fis = openFileInput(file_name);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String text;
                //Store into array
                score_list.clear();
                name_list.clear();
                while (true) {
                    try {
                        if ((text = br.readLine()) == null) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (isNumeric(text)) {
                        score_list.add(Integer.parseInt(text));
                    } else {
                        name_list.add(text);
                    }
                }
                android.app.AlertDialog.Builder scoreboard =
                        new android.app.AlertDialog.
                                Builder(MainActivity.this);

                // create a map to associate each score
                // with its corresponding name
                Map<Integer, String> scoreNameMap = new HashMap<>();
                for (int i = 0; i < score_list.size(); i++) {
                    scoreNameMap.put(score_list.get(i), name_list.get(i));
                }

                // sort the scores array list in descending order
                Collections.sort(score_list, Collections.reverseOrder());

                // iterate through the sorted scores to
                // get the corresponding names
                ArrayList<String> sortedNames = new ArrayList<>();
                for (Integer score : score_list) {
                    sortedNames.add(scoreNameMap.get(score));
                }
                StringBuilder sb1 = new StringBuilder();
                for (int i = 0; i < score_list.size(); i++) {
                    sb1.append("#").append(i+1).append("  ")
                            .append(score_list.get(i)).append("  ")
                            .append(sortedNames.get(i)).append("\n");
                }
                scoreboard.setTitle("Scoreboard");
                scoreboard.setMessage(sb1);
                scoreboard.setPositiveButton("Back",
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                scoreboard.show();

            }
        });
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