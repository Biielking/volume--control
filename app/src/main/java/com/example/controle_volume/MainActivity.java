package com.example.controle_volume;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
//import android.widget.Switch;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.media.AudioManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView alturaTextView;
    private SeekBar alturaSeekBar;
    private SwitchCompat switchMute;
    private RadioGroup radioGroupMode;
    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alturaTextView = findViewById(R.id.alturaTextView);
        alturaSeekBar = findViewById(R.id.alturaSeekBar);
        switchMute = findViewById(R.id.switchMute);
        radioGroupMode = findViewById(R.id.radioGroupMode);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sharedPreferences = getSharedPreferences("VolumePrefs", MODE_PRIVATE);

        verificarPermissaoDND();
        configurarSeekBar();
        configurarSwitch();
        configurarRadioGroup();
        atualizarUI();
    }

    /**
     * Verifica se o usuário concedeu permissão para alterar o modo "Não Perturbe"
     */
    private void verificarPermissaoDND() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(this, "É necessário conceder permissão para alterar o modo de som!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    /**
     * Configura a SeekBar para ajustar o volume do sistema em tempo real
     */
    private void configurarSeekBar() {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        alturaSeekBar.setMax(maxVolume);
        alturaSeekBar.setProgress(currentVolume);
        alturaTextView.setText(String.valueOf(currentVolume));

        alturaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, AudioManager.FLAG_SHOW_UI);
                    switchMute.setChecked(progress == 0);
                    Toast.makeText(MainActivity.this, "Volume alterado para: " + progress, Toast.LENGTH_SHORT).show();
                }
                alturaTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                salvarPreferencias("volume", seekBar.getProgress());
            }
        });
    }

    /**
     * Configura o Switch de Mute para alternar o volume
     */
    private void configurarSwitch() {
        boolean isMuted = audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0;
        switchMute.setChecked(isMuted);

        switchMute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
                alturaSeekBar.setProgress(0);
            } else {
                int savedVolume = sharedPreferences.getInt("volume", audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) / 2);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, savedVolume, AudioManager.FLAG_SHOW_UI);
                alturaSeekBar.setProgress(savedVolume);
                Toast.makeText(MainActivity.this, "Modo Mudo Desativado", Toast.LENGTH_SHORT).show();
            }
            salvarPreferencias("isMuted", isChecked);
        });
    }

    /**
     * Configura os botões de modo de som e mantém a interface sincronizada
     */
    private void configurarRadioGroup() {
        int currentMode = audioManager.getRingerMode();
        definirModoSom(currentMode); // Atualiza a UI com o estado inicial

        radioGroupMode.setOnCheckedChangeListener((group, checkedId) -> {
            int mode = AudioManager.RINGER_MODE_NORMAL;

            if (checkedId == R.id.radioSilent) {
                mode = AudioManager.RINGER_MODE_SILENT;
            } else if (checkedId == R.id.radioVibrate) {
                mode = AudioManager.RINGER_MODE_VIBRATE;
            }

            definirModoSom(mode);
            salvarPreferencias("soundMode", mode);
        });
    }

    /**
     * Altera o modo de som e atualiza a interface
     */
    private void definirModoSom(int mode) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (mode == AudioManager.RINGER_MODE_SILENT || mode == AudioManager.RINGER_MODE_VIBRATE) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Toast.makeText(this, "Permissão necessária para alterar o modo de som", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                return;

            }

        }
        audioManager.setRingerMode(mode);

        String mensagem = "Modo alterado para: " +
                (mode == AudioManager.RINGER_MODE_SILENT ? "Silencioso" :
                        mode == AudioManager.RINGER_MODE_VIBRATE ? "Vibrar" : "Som");
        Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
        atualizarUI();
    }

    /**
     * Mantém a interface sincronizada com o estado atual do sistema
     */
    private void atualizarUI() {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int mode = audioManager.getRingerMode();

        alturaSeekBar.setProgress(currentVolume);
        alturaTextView.setText(String.valueOf(currentVolume));
        switchMute.setChecked(currentVolume == 0);

        if (mode == AudioManager.RINGER_MODE_SILENT) {
            ((RadioButton) findViewById(R.id.radioSilent)).setChecked(true);
        } else if (mode == AudioManager.RINGER_MODE_VIBRATE) {
            ((RadioButton) findViewById(R.id.radioVibrate)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioSound)).setChecked(true);
        }
    }

    /**
     * Salva preferências de usuário para restaurar os estados ao reabrir o app
     */
    private void salvarPreferencias(String chave, int valor) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(chave, valor);
        editor.apply();
    }

    private void salvarPreferencias(String chave, boolean valor) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(chave, valor);
        editor.apply();
    }
}
