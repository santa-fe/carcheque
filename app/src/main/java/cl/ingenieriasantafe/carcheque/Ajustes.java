package cl.ingenieriasantafe.carcheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Ajustes extends AppCompatActivity {


    TextView menuimpresora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);
        menuimpresora = (TextView)findViewById(R.id.txtimpresora);


        menuimpresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ajustes.this, bluetooth_list.class);
                startActivity(intent);
            }
        });



    }

}
