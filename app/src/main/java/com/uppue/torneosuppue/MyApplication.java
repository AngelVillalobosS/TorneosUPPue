package com.uppue.torneosuppue;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicialización global de Firebase
        FirebaseApp.initializeApp(this);

        // Puedes agregar otras inicializaciones globales aquí
        // Por ejemplo: Analytics, Crashlytics, etc.
    }
}