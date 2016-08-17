package firebase.william.com.mainbluetoothactivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    static TextView statusMessage;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    Button btnPareados;
    Button btnDescobrir;
    Button btnVisibilidade;
    Button btnHabilitar;

    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.statusMessage);

        btnPareados = (Button) findViewById(R.id.idButtonPairedDevices);
        btnDescobrir = (Button) findViewById(R.id.idButtonDiscoveredDevices);
        btnVisibilidade = (Button) findViewById(R.id.idButtonVisibility);
        btnHabilitar = (Button) findViewById(R.id.idButtonHabilitar);

        habilitarComponents(Boolean.FALSE);
        btnHabilitar.setEnabled(Boolean.FALSE);
        if (isBTExists()) {
            isBTEnabled();
        }

    }

    public void waitConnection(View view) {
        connect = new ConnectionThread();
        connect.start();
    }

    public void habilitarComponents(Boolean habilitar) {
        btnPareados.setEnabled(habilitar);
        btnDescobrir.setEnabled(habilitar);
        btnVisibilidade.setEnabled(habilitar);
        btnHabilitar.setEnabled(!habilitar);
    }

    public void searchPairedDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {
        Intent discoverDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(discoverDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    public void enableVisibility(View view) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
        startActivity(discoverableIntent);
    }

    public void HabilitarBT(View view) {
        isBTEnabled();
    }

    protected Boolean isBTExists() {
        if (btAdapter == null) {
            statusMessage.setText("Que pena! Hardware Bluetooth não está funcionando :(");
            return Boolean.FALSE;
        } else {
            statusMessage.setText("Ótimo! Hardware Bluetooth está funcionando :)");
            return Boolean.TRUE;
        }
    }

    protected void isBTEnabled() {
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
            statusMessage.setText("Solicitando ativação do Bluetooth...");
        } else {
            statusMessage.setText("Bluetooth já ativado :)");
            habilitarComponents(Boolean.TRUE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                statusMessage.setText("Bluetooth ativado :D");
                habilitarComponents(Boolean.TRUE);
            } else {
                statusMessage.setText("Bluetooth não ativado :(");
                habilitarComponents(Boolean.FALSE);
            }
        } else if (requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if (resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                                                         + data.getStringExtra("btDevAddress"));
            } else {
                statusMessage.setText("Nenhum dispositivo selecionado :(");
            }
        }
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                statusMessage.setText("Conectado :D");

        }
    };
}
