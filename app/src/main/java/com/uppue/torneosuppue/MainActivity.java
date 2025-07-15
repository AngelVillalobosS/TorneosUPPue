package com.uppue.torneosuppue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar el botón de hamburguesa
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Mostrar fragmento inicial
        if (savedInstanceState == null) {
            showFragment(new TeamsFragment());
            navigationView.setCheckedItem(R.id.nav_teams);
        }
    }

    // Método público para mostrar fragments
    public void showFragment(Fragment fragment) {
        Log.d("NAVIGATION", "Mostrando fragment: " + fragment.getClass().getSimpleName());
        // Limpiar back stack
        clearBackStack();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        // Solo añadir al back stack si no es el fragmento inicial
        if (!(fragment instanceof TeamsFragment)) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_teams) {
            showFragment(new TeamsFragment());
        } else if (id == R.id.nav_recent_matches) {
            showFragment(new RecentMatchesFragment());
        } else if (id == R.id.nav_upcoming_matches) {
            showFragment(new UpcomingMatchesFragment());
        } else if (id == R.id.nav_profile) {
            showFragment(new ProfileFragment());
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            showFragment(new LoginFragment());
            navigationView.setCheckedItem(-1); // Desmarcar todos los items
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
}