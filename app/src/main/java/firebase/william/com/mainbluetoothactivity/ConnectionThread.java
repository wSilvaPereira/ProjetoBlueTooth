package firebase.william.com.mainbluetoothactivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by william.pereira on 17/08/2016.
 */
public class ConnectionThread extends Thread {

    BluetoothSocket btSocket = null;
    BluetoothServerSocket btServerSocket = null;
    String btDevAdress = null;
    String myUUID = "1F32B5D3-5100-4755-8458-3F31AEEB4CD7";
    Boolean server;
    boolean running = false;

    public ConnectionThread() {
        this.server = true;
    }

    public ConnectionThread(String btDevAdress) {
        this.btDevAdress = btDevAdress;
        this.server = false;
    }

    public void run() {

        this.running = true;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (this.server) {

            try {

                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("@string/nomeAplicativo", UUID.fromString(myUUID));
                btSocket = btServerSocket.accept();

                if (btSocket != null) {
                    btServerSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }

        } else {

            try {

                BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAdress);
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

                btAdapter.cancelDiscovery();

                if (btSocket != null) {
                    btSocket.connect();
                }

            } catch (IOException e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }

        }

    }

    private void toMainActivity(byte[] data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        MainActivity.handler.sendMessage(message);
    }

}
