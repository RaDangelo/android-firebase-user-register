package br.com.rafaeldangelobergami.cadastrofotoutilizandofirebase;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView nomeTextView, foneTextView, emailTextView;
    private ImageView exibeFotoImageView;

    private FirebaseDatabase db;
    StorageReference storageRootReference;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nomeTextView = (TextView) findViewById(R.id.nomeTextView);
        foneTextView = (TextView) findViewById(R.id.foneTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        exibeFotoImageView = (ImageView) findViewById(R.id.exibeFotoImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEditarUsuario);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditarUsuarioActivity.class);
                startActivity(intent);
            }
        });
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("usuario");
        storageRootReference = FirebaseStorage.getInstance().getReference();
        baixarFoto();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                Usuario.getInstance().setNome(usuario.getNome());
                Usuario.getInstance().setFone(usuario.getFone());
                Usuario.getInstance().setEmail(usuario.getEmail());
                configurarValoresCamposVisuais();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void baixarFoto() {
        StorageReference fotoReference = storageRootReference.child("img/foto.png");
        try {
            File temp = File.createTempFile("img", "foto.png");
            fotoReference.getFile(temp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Usuario.getInstance().setFoto(BitmapFactory.decodeFile(temp.getPath()));
                    Toast.makeText(MainActivity.this, getString (R.string.ok_baixou), Toast.LENGTH_SHORT)
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarValoresCamposVisuais();
    }

    private void configurarValoresCamposVisuais(){
        nomeTextView.setText(Usuario.getInstance().getNome());
        foneTextView.setText(Usuario.getInstance().getFone());
        emailTextView.setText(Usuario.getInstance().getEmail());
        if (Usuario.getInstance().getFoto() != null) {
            exibeFotoImageView.setImageBitmap(Usuario.getInstance().getFoto());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
