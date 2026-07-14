package com.tencentbyd.audiovisualizertest;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MainActivity extends Activity {

    private static final String TAG = "AudioVisualizerTest";
    private static final String METHODS_TAG = "AudioMethods";

    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(60, 40, 60, 40);

        TextView title = new TextView(this);
        title.setText("Audio Visualizer Test");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        resultView = new TextView(this);
        resultView.setText("Ready\nNo command has been sent.");
        resultView.setTextSize(18);
        resultView.setGravity(Gravity.CENTER);
        resultView.setPadding(0, 30, 0, 30);
        root.addView(resultView, matchWrap());

        Button start = new Button(this);
        start.setText("START AUDIO OUTPUT");
start.setOnClickListener(v ->
        invokeAudioMethodWithPackage(
                "startAudioOutput",
                "com.android.youtube.premium"
        )
);
        root.addView(start, matchWrap());

        Button stop = new Button(this);
        stop.setText("STOP AUDIO OUTPUT");
        stop.setOnClickListener(v -> invokeAudioMethod("stopAudioOutput"));
        root.addView(stop, matchWrap());

        Button youtube = new Button(this);
        youtube.setText("OPEN YOUTUBE PREMIUM");
        youtube.setOnClickListener(v -> openYouTube());
        root.addView(youtube, matchWrap());

        Button list = new Button(this);
        list.setText("LIST AUDIO METHODS");
        list.setOnClickListener(v -> listAudioMethods());
        root.addView(list, matchWrap());

        setContentView(root);
    }

    private LinearLayout.LayoutParams matchWrap() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        return params;
    }

    private void invokeAudioMethod(String methodName) {
        try {
            AudioManager manager =
                    (AudioManager) getSystemService(AUDIO_SERVICE);

            Method method = AudioManager.class.getMethod(methodName);
            Object response = method.invoke(manager);

            String message =
                    "SUCCESS\n"
                            + methodName
                            + " called\nResult: "
                            + response;

            resultView.setText(message);
            Log.i(TAG, message);

        } catch (InvocationTargetException error) {
            Throwable cause =
                    error.getCause() != null
                            ? error.getCause()
                            : error;

            String message =
                    "INVOCATION FAILED\n"
                            + cause.getClass().getName()
                            + "\n"
                            + String.valueOf(cause.getMessage());

            resultView.setText(message);
            Log.e(TAG, message, cause);

        } catch (Throwable error) {
            String message =
                    "FAILED\n"
                            + error.getClass().getName()
                            + "\n"
                            + String.valueOf(error.getMessage());

            resultView.setText(message);
            Log.e(TAG, message, error);
        }
    }

    private void openYouTube() {
        String[] packages = {
                "com.android.youtube.premium",
                "com.google.android.youtube"
        };

        for (String packageName : packages) {
            Intent intent =
                    getPackageManager()
                            .getLaunchIntentForPackage(packageName);

            if (intent != null) {
                startActivity(intent);
                return;
            }
        }

        resultView.setText("YouTube was not found.");
    }

    private void listAudioMethods() {
        StringBuilder output = new StringBuilder();

        Method[] methods =
                AudioManager.class.getDeclaredMethods();

        Arrays.sort(
                methods,
                (first, second) ->
                        first.getName().compareTo(second.getName())
        );

        for (Method method : methods) {
            StringBuilder line = new StringBuilder();

            line.append(method.getReturnType().getSimpleName())
                    .append(" ")
                    .append(method.getName())
                    .append("(");

            Class<?>[] parameters = method.getParameterTypes();

            for (int index = 0; index < parameters.length; index++) {
                line.append(parameters[index].getSimpleName());

                if (index < parameters.length - 1) {
                    line.append(", ");
                }
            }

            line.append(")");

            output.append(line).append("\n");
            Log.i(METHODS_TAG, line.toString());
        }

        resultView.setText(
                "Found "
                        + methods.length
                        + " AudioManager methods.\n"
                        + "Full list was written to Logcat."
        );

        Log.i(METHODS_TAG, "TOTAL_METHODS=" + methods.length);
    }
private void invokeAudioMethodWithPackage(
        String methodName,
        String packageName
) {
    try {
        AudioManager manager =
                (AudioManager) getSystemService(AUDIO_SERVICE);

        Method method =
                AudioManager.class.getMethod(
                        methodName,
                        String.class
                );

        Object response =
                method.invoke(manager, packageName);

        String message =
                "SUCCESS\n"
                        + methodName
                        + "(\""
                        + packageName
                        + "\") called\nResult: "
                        + response;

        resultView.setText(message);
        Log.i(TAG, message);

    } catch (InvocationTargetException error) {
        Throwable cause =
                error.getCause() != null
                        ? error.getCause()
                        : error;

        String message =
                "INVOCATION FAILED\n"
                        + cause.getClass().getName()
                        + "\n"
                        + String.valueOf(cause.getMessage());

        resultView.setText(message);
        Log.e(TAG, message, cause);

    } catch (Throwable error) {
        String message =
                "FAILED\n"
                        + error.getClass().getName()
                        + "\n"
                        + String.valueOf(error.getMessage());

        resultView.setText(message);
        Log.e(TAG, message, error);
    }
}
}

