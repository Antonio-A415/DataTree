package com.datatree;


import android.animation.Animator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final MutableLiveData<Integer> frameLiveData =new MutableLiveData<>();

    private Timer timer;

    @Inject
    public MainViewModel(){}

    public LiveData<Integer> getFrameLiveData(){
        return frameLiveData;
    }

    public void reproducirAnimacion(LottieAnimationView viewLottie, int frameFinal, int intervaloTiempo) {
        if (timer != null) {
            timer.cancel();  // Detener el timer si ya está en ejecución
        }

        timer = new Timer();

        // Timer para reiniciar la animación cada 8 segundos
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Reiniciar la animación
                viewLottie.post(() -> {
                    viewLottie.setFrame(0); // Reiniciar al frame 0
                    viewLottie.playAnimation(); // Reproducir la animación
                });
            }
        }, 0, intervaloTiempo); // intervaloTiempo = 8000 ms (8 segundos)

        // Listener para detectar cuando la animación termina
        viewLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // No es necesario hacer nada aquí
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Cuando la animación termina, retroceder secuencialmente hasta frameFinal
                retrocederSecuencialmente(viewLottie, frameFinal);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // No es necesario hacer nada aquí
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // No es necesario hacer nada aquí
            }
        });
    }

    // Método para retroceder secuencialmente hasta frameFinal
    private void retrocederSecuencialmente(LottieAnimationView viewLottie, int frameFinal) {
        final int[] frameActual = {viewLottie.getFrame()}; // Obtener el frame actual

        // Timer para retroceder frame por frame
        Timer retrocesoTimer = new Timer();
        retrocesoTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                viewLottie.post(() -> {
                    if (frameActual[0] > frameFinal) {
                        frameActual[0]--; // Retroceder un frame
                        viewLottie.setFrame(frameActual[0]); // Establecer el frame
                    } else {
                        retrocesoTimer.cancel(); // Detener el retroceso cuando se alcanza frameFinal
                        viewLottie.pauseAnimation(); // Pausar la animación en frameFinal
                    }
                });
            }
        }, 0, 26); // 16 ms por frame (aproximadamente 60 fps)
    }
}
