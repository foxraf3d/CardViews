package com.sgf.cardviews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBanco extends SQLiteOpenHelper{

    public static final String NOME_BANCO = "CardView.db";
    public static final String NOME_TABELA = "persons";
    public static final int VERSAO = 1;

    public CriaBanco(Context context) {
        super(context, NOME_BANCO,null,VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = queryTabelaPessoa();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlDropTablePessoa = dropTable(NOME_TABELA);
        db.execSQL(sqlDropTablePessoa);
        onCreate(db);
    }

    private String queryTabelaPessoa() {
        String sql = "CREATE TABLE IF NOT EXISTS " +NOME_TABELA+ "(" +
                "id integer PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL," +
                "idade TEXT NOT NULL, " +
                "fotoId TEXT NOT NULL)";
        return sql;
    }

    private String dropTable(String nomeTabela){
        String sql = "DROP TABLE IF EXISTS " + nomeTabela;
        return sql;
    }

}
