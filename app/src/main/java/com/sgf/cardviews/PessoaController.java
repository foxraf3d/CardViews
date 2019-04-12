package com.sgf.cardviews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class PessoaController {

    public List<PessoaEntity> persons;
    private SQLiteDatabase db;
    private CriaBanco banco;
    private Cursor cursor;
    private boolean bkupOk = false;


    public PessoaController(Context context) {
        banco = new CriaBanco(context);
    }

    public PessoaEntity salvarPessoa(String nome, String idade, String fotoId){
        ContentValues valores = new ContentValues();

        db = banco.getWritableDatabase();
        valores.put("NOME", nome);
        valores.put("IDADE", idade);
        valores.put("FOTOID", fotoId);

        db.insert(banco.NOME_TABELA, null, valores);
        PessoaEntity pessoa = retornaUltimoRegistro();
        db.close();
        return pessoa;
    }

    public List<PessoaEntity> listaPessoa() {
        List<PessoaEntity> listaPessoas = new ArrayList<>();
        db = banco.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + banco.NOME_TABELA + " ORDER BY id", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String nome = cursor.getString(cursor.getColumnIndex("nome"));
                int idade = cursor.getInt(cursor.getColumnIndex("idade"));
                String fotoid = cursor.getString(cursor.getColumnIndex("fotoId"));
                listaPessoas.add(new PessoaEntity(id, nome, idade, fotoid));
            }
            return listaPessoas;
        }
        db.close();
        return listaPessoas;

    }

    public PessoaEntity retornaUltimoRegistro(){
        db = banco.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " +banco.NOME_TABELA+ " ORDER BY id DESC", null);
        if (cursor!=null){
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nome = cursor.getString(cursor.getColumnIndex("nome"));
            int idade = cursor.getInt(cursor.getColumnIndex("idade"));
            String fotoid = cursor.getString(cursor.getColumnIndex("fotoId"));
            db.close();
            return new PessoaEntity(id, nome, idade, fotoid);
        }
        return null;
    }

    public void deleteData(String deleteNome){


    }

    public void copiaBanco() {
        try{
            db = banco.getWritableDatabase();
            String path = db.getPath();
            Log.i(TAG, "caminho pro banco "+path);
            File dbFile = new File(path);
            Log.i(TAG, "File criado "+dbFile);
            File bkup = new File(Environment.getExternalStorageDirectory(), dbFile.getName());
            Log.i(TAG, "File dest criado "+bkup);
            copyFile(dbFile, bkup);
            Log.i(TAG, "Copiado com sucesso ");
            bkupOk = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean copyFile(File from, File to) {

        try {

            FileChannel source = new FileInputStream(from).getChannel();
            FileChannel destination = new FileOutputStream(to).getChannel();

            System.out.println("from "+from.getAbsolutePath());
            System.out.println("to   "+to.getAbsolutePath());

            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



}


//List<Pessoa> listPessoaEntity = new ArrayList<>();
//persons = new ArrayList<>();
//persons.add(new Pessoa("Fillipe Cordeiro", "31 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Joao da Silva", "25 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Maria Dolores", "35 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Jaspion Spelvan", "135 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Ninja Jiraya", "250 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Ranger Verde", "65 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Black Kamen Rider", "340 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Incrível Hulk", "95 anos", R.drawable.ic_launcher_foreground));
//persons.add(new Pessoa("Gigante Gerreiro Daileon", "500 anos", R.drawable.ic_launcher_foreground));
