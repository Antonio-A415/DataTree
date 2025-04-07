package com.datatree.fragments.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datatree.R;
import com.datatree.databinding.FragmentHomeBinding;
import com.datatree.infraestructure.adapters.CultivosAdapter;
import com.datatree.infraestructure.dataclass.Itemsplants;
import com.datatree.infraestructure.dataclass.Resultslands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;

    private FragmentHomeBinding binding;

    private ArrayList<Itemsplants> list;
    private ArrayList<Resultslands> results;

    private boolean ban = true;

    private ArrayList<Integer> listimge = new ArrayList<>(Arrays.asList(
            R.drawable.cilantro,
            R.drawable.habanero,
            R.drawable.hortalizas,
            R.drawable.chayote,
            R.drawable.rabano
    ));

    private String[] listimge2 = {
            "hola", "Tomas", "Maria", "Juan", "Rosa", "Esteban"
    };


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new HomeViewModel();
        List<Itemsplants> list = new ArrayList<>();
        list.add(new Itemsplants(0, "VEGETALES", R.drawable.frutas));
        //list.add(new Itemsplants(1, "Rabano", "Raphanus sativus", R.drawable.rabano));
        list.add(new Itemsplants(1, "Tomate", "Solanum lycopersicum", R.drawable.tomate));
        list.add(new Itemsplants(1, "Habanero", "Capsicum chinense Habanero Group", R.drawable.habanero));
        list.add(new Itemsplants(0, "HORTALIZAS", R.drawable.hortalizas));
        list.add(new Itemsplants(1, "Cilantro", "Coriandrum sativum", R.drawable.cilantro));
        list.add(new Itemsplants(1, "Epazote", "Dysphania ambrosioides", R.drawable.epazote));
        list.add(new Itemsplants(1, "Tomate", "Solanum lycopersicum", R.drawable.tomate));

        CultivosAdapter adapter = new CultivosAdapter(list, requireContext());
        binding.recc.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recc.setAdapter(adapter);

        listenersIcons();
        listenerClickImageDone();

    }

    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenerClickImageDone(){
        binding.done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //mostrar un toast
                showToast("Solicitud en proceso");
            }
        });
    }

    public void listenersIcons(){
        binding.editQuery.addTextChangedListener(new TextWatcher() {
            //Antes de que el texto cambie
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //Cuando el texto cambie

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //binding.done.setVisibility(View.VISIBLE);
                if(!binding.editQuery.getText().toString().isEmpty()){ //verificar si el edtitquery esta vacio
                    binding.done.setVisibility(View.VISIBLE); //mostrar el button de done
                }else{
                    binding.done.setVisibility(View.GONE); //ocultar el button de gone
                }
            }
            //Cuando el texto se termine de escribir
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        binding=null;
    }

}