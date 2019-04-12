package com.sgf.cardviews;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CadastroPessoaActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String caminhoFoto;
    private Uri fotoUri;
    private Bitmap photo;

    private static final String authority = "com.sgf.cardviews.fileprovider";

    private EditText nome;
    private EditText idade;
    private ImageButton capturaFoto;
    private ImageView foto;
    private Button gravar;
    private Button backup;
    private TextView listarPessoas;
    private PessoaController pessoaController;
    private PessoaAdapter pessoaAdapter;
    private PessoaEntity pessoa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pessoa);

        //Recuperando Nome e Idade
        nome = findViewById(R.id.editNomeID);
        idade = findViewById(R.id.editIdadeID);

        //Gravando na base
        gravar = findViewById(R.id.btnGravarID);
        gravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fotoUri == null){
                    Toast.makeText(CadastroPessoaActivity.this, "Favor capturar uma foto", Toast.LENGTH_LONG).show();
                }else {
                    String nomeString = nome.getText().toString();
                    String idadeString = idade.getText().toString();
                    String fotoIdString = fotoUri.toString();
                    String camposValidos = validaPreenchimento(nomeString, idadeString, fotoIdString);

                    if (camposValidos.equalsIgnoreCase("")) {
                        pessoaController = new PessoaController(CadastroPessoaActivity.this);
                        pessoaController.salvarPessoa(nomeString, idadeString, fotoIdString);
                        limparCampos();
                        Toast.makeText(CadastroPessoaActivity.this, "Usuário Inserido Com Sucesso!: " + camposValidos, Toast.LENGTH_LONG).show();
                        pessoaAdapter = new PessoaAdapter(pessoaAdapter.pessoas);
                        //pessoaAdapter.adicionaPessoa();
                    } else {
                        Toast.makeText(CadastroPessoaActivity.this, "Favor preencher o campo: " + camposValidos, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Capturar Foto
        foto = findViewById(R.id.imgCapturaID);
        capturaFoto = findViewById(R.id.imgButtonCameraID);
        capturaFoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (cameraIntent.resolveActivity(getPackageManager())!= null){
                        //Cria o arquivo que armazenará a foto
                        File arquivoFoto = null;
                        try {
                            arquivoFoto = criaArquivoImagem();
                        }catch (Exception e){
                            Log.d("ERRO: Captura de Foto", e.getMessage());
                            e.printStackTrace();
                        }
                        if (arquivoFoto != null){
                            fotoUri = FileProvider.getUriForFile(CadastroPessoaActivity.this, authority, arquivoFoto);
                            //Uri fotoUri = Uri.fromFile(arquivoFoto);

                            //Corrigindo erro : "exposed beyond app through ClipData.Item.getUri"
//                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                            StrictMode.setVmPolicy(builder.build());

                            // Se a versão for menor que M (antes das permissões em runtime), então envia a permissão via intent
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                List<ResolveInfo> resInfoList = CadastroPessoaActivity.this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                                for (ResolveInfo resolveInfo : resInfoList) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    CadastroPessoaActivity.this.grantUriPermission(packageName, fotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }

                                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }

                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);

                        }
                    }

                }
            }
        });

        //Acesso à activity de listagem
        listarPessoas = findViewById(R.id.txtListarID);
        listarPessoas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroPessoaActivity.this, MainActivity.class);
                intent.putExtra("FOTO", photo);
                startActivity(intent);
            }
        });

        //Backup Base
        backup = findViewById(R.id.btnBackupID);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PessoaController(getBaseContext()).copiaBanco();
            }
        });

    }

    private void limparCampos() {
        nome.getText().clear();
        idade.getText().clear();
        foto = null;
    }

    public File criaArquivoImagem() throws IOException {
        //Cria o nome do arquivo de imagem
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String nomeAquivoImagem = "JPEG_" +timeStamp+ "_";
        File armazenamento = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagem = File.createTempFile(nomeAquivoImagem, ".jpeg", armazenamento);
        //Salvando o arquivo: caminho para utilizar com a ActionView intent
        caminhoFoto = imagem.getAbsolutePath();
        return imagem;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fotoUri);
                foto.setImageBitmap(photo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String validaPreenchimento(String nome, String idade, String fotoID) {
        if (nome.isEmpty())
            return "Nome";
        if(idade.isEmpty())
            return "Idade";
        if (fotoID.isEmpty())
            return "Capturar Foto!";
        return "";
    }

}

