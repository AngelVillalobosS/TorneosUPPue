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

        // Verificar autenticación y rol
        checkAuthAndRole();
    }

    private void checkAuthAndRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Usuario no autenticado, mostrar login
            showFragment(new LoginActivity());
            navigationView.setVisibility(View.GONE); // Ocultar menú
        } else {
            // Obtener rol del usuario
            fetchUserRole();
        }
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
                    setupNavigationMenu(); // Configurar menú según rol

                    // Mostrar fragmento inicial
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        showFragment(new TeamsFragment());
                        navigationView.setCheckedItem(R.id.nav_teams);
                    }
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
        menu.clear(); // Limpiar menú existente

        // Inflar menú según rol
        if ("admin".equals(userRole)) {
            navigationView.inflateMenu(R.menu.admin_nav_menu);
        } else if ("captain".equals(userRole)) {
            navigationView.inflateMenu(R.menu.captain_nav_menu);
        } else {
            // Rol por defecto si no está definido
            navigationView.inflateMenu(R.menu.default_nav_menu);
        }

        navigationView.setVisibility(View.VISIBLE); // Mostrar menú
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

        // Menú compartido por ambos roles
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
        }

        // Menú específico de admin
        else if (id == R.id.nav_manage_teams) {
            showFragment(new ManageTeamsFragment());
        }

        // Menú específico de capitán
        else if (id == R.id.nav_my_team) {
            showFragment(new MyTeamFragment());
        } else if (id == R.id.nav_report_result) {
            showFragment(new ReportResultFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Limpiar y mostrar login
        navigationView.setVisibility(View.GONE);
        navigationView.setCheckedItem(-1);
        showFragment(new LoginActivity());
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
        toggle.syncState();
    }
}