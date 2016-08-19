package firebase.william.com.mainbluetoothactivity;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DiscoveredDevices extends ListActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int READ_BT_REQUEST_CODE = 12;
    /*  Um adaptador para conter os elementos da lista de dispositivos descobertos. */
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*  Esse trecho não é essencial, mas dá um melhor visual à lista.
            Adiciona um título à lista de dispositivos pareados utilizando
            o layout text_header.xml.
        */

        /*  Cria um modelo para a lista e o adiciona à tela.
            Para adicionar um elemento à lista, usa-se arrayAdapter.add().
         */
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(arrayAdapter);

        if (Build.VERSION.SDK_INT >= 23) {
            if (getApplicationContext().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DiscoveredDevices.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        READ_BT_REQUEST_CODE);
            }
        }

        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.text_header, lv, false);
        ((TextView) header.findViewById(R.id.textView)).setText("\nDispositivos próximos\n");
        lv.addHeaderView(header, null, false);

        /*alimentarCabecalho("\nDispositivos próximos\n");*/

        /*  Usa o adaptador Bluetooth padrão para iniciar o processo de descoberta. */
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        /*  Cria um filtro que captura o momento em que um dispositivo é descoberto.
            Registra o filtro e define um receptor para o evento de descoberta.
         */
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(receiver, filter);

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        btAdapter.startDiscovery();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode
                                         , String permissions[]
                                         , int[] grantResults) {
        switch (requestCode) {
            case READ_BT_REQUEST_CODE: {
                // Se o usuário não deu permissão o array está vazio.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissão concedida, executo minha atividade
                } else {
                    // permissão negada, mostra uma mensagem pro usuário informando que tal funcionalidade não poderá ser executada
                }
                return;
            }
        }
    }


    /*  Define um receptor para o evento de descoberta de dispositivo. */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        /*  Este método é executado sempre que um novo dispositivo for descoberto.
         */
        public void onReceive(Context context, Intent intent) {

            /*  Obtem o Intent que gerou a ação.
                Verifica se a ação corresponde à descoberta de um novo dispositivo.
                Obtem um objeto que representa o dispositivo Bluetooth descoberto.
                Exibe seu nome e endereço na lista.
             */
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                /*alimentarCabecalho("\nDispositivos próximos\n - Iniciou o Scan");*/
                /*arrayAdapter.add("Iniciou o scan");*/
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                /*alimentarCabecalho("\nDispositivos próximos\n - finalizou o Scan");*/
                /*arrayAdapter.add("finalizou o scan");*/
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    /*  Este método é executado quando o usuário seleciona um elemento da lista.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        /*  Extrai nome e endereço a partir do conteúdo do elemento selecionado.
            Nota: position-1 é utilizado pois adicionamos um título à lista e o
            valor de position recebido pelo método é deslocado em uma unidade.
         */
        String item = (String) getListAdapter().getItem(position - 1);
        String devName = item.substring(0, item.indexOf("\n"));
        String devAddress = item.substring(item.indexOf("\n") + 1, item.length());

        /*  Utiliza um Intent para encapsular as informações de nome e endereço.
            Informa à Activity principal que tudo foi um sucesso!
            Finaliza e retorna à Activity principal.
         */
        Intent returnIntent = new Intent();
        returnIntent.putExtra("btDevName", devName);
        returnIntent.putExtra("btDevAddress", devAddress);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /*  Executado quando a Activity é finalizada. */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        /*  Remove o filtro de descoberta de dispositivos do registro.
         */
        unregisterReceiver(receiver);
    }

}