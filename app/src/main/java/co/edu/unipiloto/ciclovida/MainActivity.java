package co.edu.unipiloto.ciclovida;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private List<Long> lapTimes = new ArrayList<>();
    private ArrayAdapter<String> lapTimesAdapter;
    private ListView listViewLapTimes;
    private boolean isRunning = false;
    private long startTime;
    private long elapsedTime;
    private Handler handler = new Handler();
    private TextView textViewElapsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewElapsedTime = findViewById(R.id.textViewElapsedTime);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button resetButton = findViewById(R.id.resetButton);
        Button lapButton = findViewById(R.id.lapButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap();
            }
        });
        lapTimesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewLapTimes = findViewById(R.id.listViewLapTimes);
        listViewLapTimes.setAdapter(lapTimesAdapter);
    }

    private void startTimer() {
        if (!isRunning) {
            isRunning = true;
            startTime = SystemClock.elapsedRealtime() - elapsedTime;
            handler.postDelayed(updateTimer, 0);
        }
    }

    private void stopTimer() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(updateTimer);
        }
    }

    private void resetTimer() {
        isRunning = false;
        elapsedTime = 0;
        lapTimes.clear();
        updateUI();
    }

    private void lap() {
        if (isRunning) {
            long currentTime = SystemClock.elapsedRealtime();
            long lapTime = currentTime - startTime;
            lapTimes.add(lapTime);
            updateLapTimes();
        }
    }

    private void updateLapTimes() {
        lapTimesAdapter.clear();
        for (int i = 0; i < lapTimes.size(); i++) {
            String lapTimeString = "Vuelta " + (i + 1) + ": " + formatTime(lapTimes.get(i));
            lapTimesAdapter.add(lapTimeString);
        }
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime;
                updateUI();
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void updateUI() {
        textViewElapsedTime.setText(formatTime(elapsedTime));
    }

    private String formatTime(long timeInMillis) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeInMillis),
                TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60);
    }
}