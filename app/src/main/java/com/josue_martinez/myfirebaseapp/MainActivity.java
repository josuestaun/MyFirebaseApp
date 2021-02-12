package com.josue_martinez.myfirebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josue_martinez.myfirebaseapp.model.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private List<Persona> listPerson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    FloatingActionButton fabAdd;
    FloatingActionButton fabDel;
    EditText nombre, apellidos, correo, contraseña;
    ListView listaPersonas;

    //parte del firebase (bases de datos)
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);
        fabDel = findViewById(R.id.fabDel);
        nombre = findViewById(R.id.txt_nombrePersona);
        apellidos = findViewById(R.id.txt_appPersona);
        correo = findViewById(R.id.txt_correoPersona);
        contraseña = findViewById(R.id.txt_passwordPersona);

        listaPersonas = findViewById(R.id.lv_datosPersonas);

        inicializarFirebase();
        //Los metodos para trabajar con la base de datos deben ir despues de iniciarla
        listarDatos();

        listaPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);
                nombre.setText(personaSeleccionada.getNombre());
                apellidos.setText(personaSeleccionada.getApellidos());
                correo.setText(personaSeleccionada.getEmail());
                contraseña.setText(personaSeleccionada.getContraseña());
            }
        });

        //Añadir
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persona p = new Persona();
                p.setUid(UUID.randomUUID().toString());
                p.setNombre(nombre.getText().toString());
                p.setApellidos(apellidos.getText().toString());
                p.setEmail(correo.getText().toString());
                p.setContraseña(contraseña.getText().toString());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(MainActivity.this,
                        "AGREGADO", Toast.LENGTH_LONG).show();
                limpiarCajas();
            }
        });

        //Eliminar
        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persona p = new Persona();
                p.setUid(personaSeleccionada.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(MainActivity.this,
                        "BORRADO", Toast.LENGTH_LONG).show();
                limpiarCajas();
            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void limpiarCajas() {
        nombre.setText("");
        apellidos.setText("");
        correo.setText("");
        contraseña.setText("");
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                //recorro la tabla "Persona" con un for
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPerson.add(p);

                    //simple_list_item_1 es un layout que incorpora propiamente android
                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listaPersonas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}