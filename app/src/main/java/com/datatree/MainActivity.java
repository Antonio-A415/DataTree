package com.datatree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.datatree.activitys.CultivosActivity;
import com.datatree.databinding.ActivityMainBinding;
import com.datatree.fragments.home.HomeFragment;
import com.datatree.fragments.settings.FragmentSettings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
binding = ActivityMainBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar );
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.opne_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottom();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }

        if (item.getItemId() == R.id.nav_settigs){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new FragmentSettings())
                    .commit();
        }

        if(item.getItemId() == R.id.nav_logout){
         Intent IrACultivos = new Intent(this, CultivosActivity.class);
         startActivity(IrACultivos);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();;
        }
    }
    //Mostar el bottom
    private void showBottom(){
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_chat);
        ImageView logo = dialog.findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialog.dismiss();
                Toast.makeText(MainActivity.this,"En proceso de desarrollo",Toast.LENGTH_LONG).show();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations= R.style.AnimationDialog;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void asignarAnimacion(FloatingActionButton floatingActionButton){
        LottieAnimationView lottieView=new LottieAnimationView(MainActivity.this);
        lottieView.setAnimation(R.raw.icon_chat);

    }

}
