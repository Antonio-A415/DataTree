package com.datatree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.datatree.activitys.cultivos.CultivosActivity;
import com.datatree.activitys.profile.Profile;
import com.datatree.activitys.visor.VisorActivity;
import com.datatree.fragments.analytics.AnalyticsFragment;
import com.datatree.fragments.chatsfragment.ChatsFragment;
import com.datatree.fragments.feedfragment.FeedFragment;
import com.datatree.infraestructure.adapters.ChatAdapter;
import com.datatree.databinding.ActivityMainBinding;
import com.datatree.infraestructure.dataclass.DataMessage;
import com.datatree.fragments.home.HomeFragment;
import com.datatree.fragments.settings.FragmentSettings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint  //agregar al anotacion para usar el ViewModel inyectado.
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
//para configurar el Drawer layout  con la toolbar
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;

    private ChatAdapter chatAdapter;
    private List<DataMessage> messageList;
    private EditText inputMessage;
    private ImageView sendButton;
    private Dialog chatDialog;

    /*inyectar automaticamente el ViewModel
    @Inject
    MainViewModel viewModel;
    */
    //Usamos la delegacion de ViewModelProvider en java
    private MainViewModel viewModel;

    public void Init(){
        recyclerView = findViewById(R.id.recyclerViewMessages);
        inputMessage = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        loadSampleMessages();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputMessage.getText().toString().trim();
                if (!text.isEmpty()) {
                    messageList.add(new DataMessage(text, true)); // Mensaje del usuario
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);

                    // Respuesta automática del bot
                    botReply();
                    inputMessage.setText(""); // Limpiar campo de texto
                }
            }
        });
    }

    //Es en este metodo donde se agregan items al recyclerview como respuesta del modelo de gpt

    private void botReply() {
        messageList.add(new DataMessage("Hola, soy un bot. ¿En qué puedo ayudarte?", false));
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void loadSampleMessages() {
        // Mensajes de ejemplo predefinidos sobre análisis de datos agrícolas
        messageList.add(new DataMessage("¡Hola! ¿Necesitas ayuda con tus cultivos?", false)); // Mensaje del bot
        messageList.add(new DataMessage("Sí, quiero analizar los datos de humedad del suelo.", true)); // Mensaje del usuario
        messageList.add(new DataMessage("Perfecto. ¿Tienes los registros más recientes?", false)); // Mensaje del bot
        messageList.add(new DataMessage("Sí, los datos de esta semana ya están cargados.", true)); // Mensaje del usuario
        messageList.add(new DataMessage("Según los datos, el nivel de humedad está por debajo del óptimo. Se recomienda riego en las próximas 24 horas.", false)); // Mensaje del bot
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageList.size() - 1); // Desplazarse al último mensaje

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurar la tooolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar );
        setSupportActionBar(toolbar);

        //inicializar la lista

        drawerLayout = findViewById(R.id.drawer_layout);

        //usamos binding en lugar del metodo findViewById y la libreria R
        //drawerLayout = binding.drawerLayout;

        NavigationView navigationView = findViewById(R.id.nav_view);
        //NavigationView navigationView = binding.nav_view;

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.opne_nav,R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //instanciar el viewModel despues de que la acitivy se ha cargado completamente:
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //animacion del icono de notificacion de lottie
        LottieAnimationView notification= findViewById(R.id.notificacion);
        notification.setFrame(38);  // esta es la posicion cuando se queda quieta, puede revisar el frame enm el siguiente link: https://app.lottiefiles.com/share/7e7815c1-4e70-4304-9365-acc91bfb49fa

        //observar cambios en el frame y actualizar Lottie
        viewModel.getFrameLiveData().observe(this, frame->notification.setFrame(frame));
        //INICIAR la animacion
        viewModel.reproducirAnimacion(notification,38,8000);

        notification.setOnClickListener(lambda -> {
            if(!notification.isAnimating())
            {
                notification.playAnimation();
                Toast.makeText(this,"Notificacion recibida",Toast.LENGTH_SHORT).show();

                notification.addAnimatorUpdateListener(animation ->{
                    if(notification.getFrame() >= 38){
                        notification.pauseAnimation();

                        //evitar que siga escuchando listeners
                        //notification.removeAllUpdateListeners();

                    }
                });

            }
        });

        Timer timer= new Timer();

        messageList = new ArrayList<>();

        chatAdapter = new ChatAdapter(messageList);

        //boton de icono de asistente para mostrar el BottomSheet
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottom();
            }
        });


    }

    //para hacer la transicion entre fragmentos o activities
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }
        //sirve para ir al fragmento donde se pueden ver los datos de los sensores.
        if (item.getItemId() == R.id.nav_tools){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new AnalyticsFragment())
                    .commit();
        }

        if(item.getItemId() == R.id.nav_chats){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new ChatsFragment())
                    .commit();
        }

        if(item.getItemId() == R.id.nav_feed){
            //Toast.makeText(getApplicationContext(), "Cambiando a la vista de alertas", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new FeedFragment())
                    .commit();
        }

        if(item.getItemId() == R.id.nav_estadisticas){
         Intent IrACultivos = new Intent(this, CultivosActivity.class);
         startActivity(IrACultivos);
        }

        //Abrir la activity del perfi.
        if(item.getItemId() == R.id.nav_profile){
            Intent IrAProfile = new Intent(this, Profile.class);
            startActivity(IrAProfile);
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
        chatDialog= new Dialog(this);

        chatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chatDialog.setContentView(R.layout.bottom_sheet_chat);
        ImageView buttonSend = chatDialog.findViewById(R.id.buttonSend);

        // Inicializar vistas del chat
        recyclerView = chatDialog.findViewById(R.id.recyclerViewMessages);
        inputMessage = chatDialog.findViewById(R.id.editTextMessage);
        sendButton = chatDialog.findViewById(R.id.buttonSend);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        loadSampleMessages();

        // Configurar botón de enviar
        sendButton.setOnClickListener(v -> {
            String text = inputMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                messageList.add(new DataMessage(text, true));
                chatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageList.size() - 1);

                botReply();
                inputMessage.setText("");
            }else{
                messageList.add(new DataMessage(text, false));
                chatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
                inputMessage.setText("");
            }
        });

        /*
        buttonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                chatDialog.dismiss();
                Toast.makeText(MainActivity.this,"En proceso de desarrollo",Toast.LENGTH_LONG).show();

            }
        });

        */

        chatDialog.show();
        chatDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        chatDialog.getWindow().getAttributes().windowAnimations= R.style.AnimationDialog;
        chatDialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //switc solo acepta constantes en tiempo de compilacion, asi es mejor usar
        //una serie de if else
        if(item.getItemId() == R.id.action_add){
            //invocar las funciones que abren la seccion de registro de planta

            Intent AgregarNuevaPlanta = new Intent(this, VisorActivity.class);
            startActivity(AgregarNuevaPlanta);

            return true;
        } else if(item.getItemId() == R.id.action_search){
            //abrir la vista para busqueda de plantas
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void asignarAnimacion(FloatingActionButton floatingActionButton){
        LottieAnimationView lottieView=new LottieAnimationView(MainActivity.this);
        lottieView.setAnimation(R.raw.icon_chat);

    }

}
