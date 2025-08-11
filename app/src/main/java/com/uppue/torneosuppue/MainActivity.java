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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    String userRole; // Almacenará el rol del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificar autenticación antes de inicializar la UI
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            redirectToLogin();
            return;
        }

        // Configurar UI solo si el usuario está autenticado
        initializeUI();
        fetchUserRole();
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish(); // Cierra MainActivity para que no quede en el historial
    }

    private void initializeUI() {
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
    }

    private void fetchUserRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userRole = documentSnapshot.getString("role");
                        setupNavigationMenu();

                        // Mostrar fragmento inicial
                        showFragment(new TeamsFragment());
                        navigationView.setCheckedItem(R.id.nav_teams);
                    } else {
                        Toast.makeText(this, "Perfil de usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener rol: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    void setupNavigationMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();

        if ("admin".equals(userRole)) {
            navigationView.inflateMenu(R.menu.admin_nav_menu);
        } else if ("captain".equals(userRole)) {
            navigationView.inflateMenu(R.menu.captain_nav_menu);
        } else {
            navigationView.inflateMenu(R.menu.default_nav_menu);
        }

        navigationView.setVisibility(View.VISIBLE);
    }

    public void showFragment(Fragment fragment) {
        Log.d("NAVIGATION", "Mostrando fragment: " + fragment.getClass().getSimpleName());
        clearBackStack();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

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
            logout();
        } else if (id == R.id.nav_manage_teams) {
            showFragment(new ManageTeamsFragment());
        } else if (id == R.id.nav_my_team) {
            showFragment(new MyTeamFragment());
        } else if (id == R.id.nav_report_result) {
            showFragment(new ReportResultFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        redirectToLogin();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            toggle.syncState();
        }
    }
}