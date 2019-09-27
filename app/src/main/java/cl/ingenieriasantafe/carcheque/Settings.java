package cl.ingenieriasantafe.carcheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText codigoverificacion;
    Button confirmacion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingscode);
        codigoverificacion= (EditText)findViewById(R.id.txtcodigo);
        confirmacion= (Button)findViewById(R.id.btnconfirm);


        confirmacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = codigoverificacion.getText().toString();
                if (codigo.equals("0000")){
                    Intent nuevoform = new Intent(Settings.this, Ajustes.class);
                    startActivity(nuevoform);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "CODIGO INCORRECTO", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
