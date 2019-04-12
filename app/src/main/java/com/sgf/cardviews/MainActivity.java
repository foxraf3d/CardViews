package com.sgf.cardviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton delete;
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    private PessoaController pessoaController;
    private PessoaAdapter pessoaAdapter;
    private List<PessoaEntity> listaCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(layoutManager);

        pessoaController = new PessoaController(getBaseContext());
        List<PessoaEntity> listaPessoas = pessoaController.listaPessoa();

        PessoaAdapter pessoaAdapter = new PessoaAdapter(listaPessoas);
        rv.setAdapter(pessoaAdapter);

        //configuraRecicler();


    }

  /*  private void configuraRecicler() {
        rv = findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        pessoaController = new PessoaController(getBaseContext());
        listaCards = pessoaController.listaPessoa();

        pessoaAdapter = new PessoaAdapter(listaCards);
        rv.setAdapter(pessoaAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

*/

}
