package org.kerlinmichel.motiondynamics.instruments;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class InstrumentNetworkClient extends AsyncTask<String, Void, Void> {

    private String host;
    private int port;

    public InstrumentNetworkClient(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    private Socket socket;
    private PrintWriter serverWriter;
    private BufferedReader in;
    private boolean blockSend;

    @Override
    protected Void doInBackground(String... params) {
        blockSend = true;
        try {
            socket = new Socket(this.host, this.port);
            serverWriter = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("failed server connect");
            return null;
        }
        while(true) {
            update();
            if(!blockSend) {
                String msg = generateMsg();
                serverWriter.write(msg);
                serverWriter.flush();
                try {
                    System.out.println(in.readLine());
                } catch (IOException e) {
                    System.out.println(e);
                    System.out.println("failed read from server");
                }
                blockSend = true;
            }
        }
    }

    public abstract void update();

    public abstract String generateMsg();

    public void triggerSend() {
        blockSend = false;
    }
} 