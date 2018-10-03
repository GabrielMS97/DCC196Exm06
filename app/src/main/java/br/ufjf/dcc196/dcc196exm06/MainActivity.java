package br.ufjf.dcc196.dcc196exm06;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button btnInserir;
    private Button btnListar;
    private RecyclerView lstLivros;
    private BibliotecaDbHelper dbHelper;
    private LivroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new BibliotecaDbHelper(getApplicationContext());
        lstLivros = (RecyclerView) findViewById(R.id.lst_Livros);
        lstLivros.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LivroAdapter(getCursorLivrosPos1950());
        lstLivros.setAdapter(adapter);
        final Random rnd = new Random();
        btnInserir = (Button) findViewById(R.id.btn_Inserir);
        btnInserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues valores = new ContentValues();
                valores.put(BibliotecaContract.Livro.COLUMN_NAME_TITULO, "Livro " + rnd.nextInt(1000));
                valores.put(BibliotecaContract.Livro.COLUMN_NAME_AUTOR, "Autor " + rnd.nextInt(1000));
                valores.put(BibliotecaContract.Livro.COLUMN_NAME_ANO, 1900 + rnd.nextInt(118));
                long id = db.insert(BibliotecaContract.Livro.TABLE_NAME, null, valores);
                Log.i("DBINFO", "registro criado com id: " + id );
            }
        });
        btnListar = (Button) findViewById(R.id.btn_Listar);
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getCursorLivrosPos1950();
                cursor.moveToPosition(-1);
                while(cursor.moveToNext()) {
                    int idxTitulo = cursor.getColumnIndexOrThrow(BibliotecaContract.Livro.COLUMN_NAME_TITULO);
                    int idxAutor = cursor.getColumnIndexOrThrow(BibliotecaContract.Livro.COLUMN_NAME_AUTOR);
                    int idxAno = cursor.getColumnIndexOrThrow(BibliotecaContract.Livro.COLUMN_NAME_ANO);
                    String titulo = cursor.getString(idxTitulo);
                    String autor = cursor.getString(idxAutor);
                    Integer ano = cursor.getInt(idxAno);
                    Log.i("DBINFO", "titulo: " + titulo);
                    adapter.setCursor(getCursorLivrosPos1950());
                }
            }
        });

    }

    private Cursor getCursorLivrosPos1950() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] visao = {
                BibliotecaContract.Livro.COLUMN_NAME_TITULO,
                BibliotecaContract.Livro.COLUMN_NAME_AUTOR,
                BibliotecaContract.Livro.COLUMN_NAME_ANO
        };
        String restricoes = BibliotecaContract.Livro.COLUMN_NAME_ANO + " > ?";
        String[] params = {"1950"};
        String sort = BibliotecaContract.Livro.COLUMN_NAME_ANO + " DESC";
        return db.query(BibliotecaContract.Livro.TABLE_NAME, visao,restricoes,params,null,null,sort);
    }
}
