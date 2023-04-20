package Atividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import Ajuda.ConFirebase;
import Modelos.Vistorias;
import br.com.patrimoniomv.R;

public class Atualizar extends AppCompatActivity {
    private Vistorias anuncio;

    private EditText tipoItem;
    private EditText localizacao;
    private EditText nomeItem;
    private EditText outrasInformacoes;
    private EditText placa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar);
        anuncio = (Vistorias) getIntent().getSerializableExtra("vistorias");

        // Inicializa os campos da tela

        localizacao = findViewById(R.id.Edit_localizacao);
        nomeItem = findViewById(R.id.Edit_nome_item);
        outrasInformacoes = findViewById(R.id.Edit_outras_informacoes);
        placa = findViewById(R.id.Edit_placa);
        //imprimir=findViewById(R.id.TelaImpri);
        if (anuncio != null) {
            localizacao.setText(anuncio.getLocalizacao());
            nomeItem.setText(anuncio.getNomeItem());
            outrasInformacoes.setText(anuncio.getOutrasInformacoes());
            placa.setText(anuncio.getPlaca());
        } else {
            // Lida com o caso em que o objeto 'anuncio' é nulo
            Toast.makeText(this, "Anúncio inválido!", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Configura o botão salvar
        Button btnSalvar = findViewById(R.id.btn_salvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDados();
            }
        });

    }

    private void salvarDados() {
        String novaPlaca = placa.getText().toString().toUpperCase();
        if (!anuncio.getPlaca().equalsIgnoreCase(novaPlaca)) {
            Vistorias.verificarPlacaExistente(novaPlaca).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.isSuccessful()) {
                        boolean placaExistente = task.getResult();
                        if (placaExistente) {
                            Toast.makeText(Atualizar.this, "A placa já existe em outro anúncio!", Toast.LENGTH_SHORT).show();
                        } else {
                            atualizarAnuncio(novaPlaca);
                        }
                    } else {
                        Toast.makeText(Atualizar.this, "Erro ao verificar a placa.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            atualizarAnuncio(novaPlaca);
        }
    }

    private void atualizarAnuncio(String novaPlaca) {
        // Atualiza os dados do anúncio
        anuncio.setLocalizacao(localizacao.getText().toString());
        anuncio.setNomeItem(nomeItem.getText().toString());
        anuncio.setOutrasInformacoes(outrasInformacoes.getText().toString());
        anuncio.setPlaca(novaPlaca);

        // Chama o método atualizarAnuncio do model Anuncios
        String idUsuario = ConFirebase.getIdUsuario();
        Task<Void> atualizarAnuncioTask = Vistorias.atualizarAnuncio(anuncio, idUsuario);
        Task<Void> atualizarAnuncioPuTask = Vistorias.atualizarAnuncioPu(anuncio, idUsuario);

        // Combina as tarefas para garantir que ambas sejam concluídas com sucesso
        Task<Void> combinedTask = Tasks.whenAll(atualizarAnuncioTask, atualizarAnuncioPuTask);

        combinedTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Atualizar.this, "Anúncio atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    // Retorna para a tela de lista de anúncios
                    finish();
                } else {
                    Toast.makeText(Atualizar.this, "Erro ao atualizar o anúncio.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}

