package com.sgf.cardviews;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class PessoaAdapter extends RecyclerView.Adapter<PessoaAdapter.PessoaViewHolder> {

    private static final String authority = "com.sgf.cardviews.fileprovider";
    public List<PessoaEntity> pessoas;
    private PessoaController dt;

    public PessoaAdapter(List<PessoaEntity> persons){
        this.pessoas = persons;
    }

    @NonNull
    @Override
    public PessoaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PessoaViewHolder pvh = new PessoaViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final PessoaViewHolder pessoaViewHolder, int i) {
        pessoaViewHolder.personName.setText(pessoas.get(i).name);
        pessoaViewHolder.personAge.setText(Integer.toString(pessoas.get(i).age));
        pessoaViewHolder.personPhoto.setImageURI(Uri.parse(pessoas.get(i).photoId));

        ImageButton imgButtonDelete = pessoaViewHolder.itemView.findViewById(R.id.btnDelete);
        imgButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());

                alertBuilder.setTitle("Confirmação");
                alertBuilder.setMessage("Deseja realmente excluir o item?");
                alertBuilder.setCancelable(false);
                alertBuilder.setIcon(R.drawable.ic_launcher_foreground);

                alertBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(view.getContext(),"Exclusão Cancelada",Toast.LENGTH_SHORT).show();
                    }
                });

                alertBuilder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertBuilder.create();
                alertBuilder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pessoas.size();
    }

    public static class PessoaViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        PessoaViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            personName = itemView.findViewById(R.id.person_name);
            personAge = itemView.findViewById(R.id.person_age);
            personPhoto = itemView.findViewById(R.id.person_photo);
        }
    }

    public void adicionaPessoa(PessoaEntity pessoa){
        pessoas.add(pessoa);
        notifyItemInserted(getItemCount());
    }

    private Activity getActivity(View view){
        Context context = view.getContext();
        while (context instanceof ContextWrapper){
            if(context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public void atualizarLista(PessoaEntity pessoa){
        pessoas.set(pessoas.indexOf(pessoa), pessoa);
        notifyItemChanged(pessoas.indexOf(pessoa));
    }
}
