package com.gn.control;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.suke.widget.SwitchButton;
import java.nio.charset.StandardCharsets;
public class MainActivity extends AppCompatActivity {

    TextView subText;
    private String hardwareAddress ="76347276483" ;

    private  Mqtt5Client client;
    private String Deepan;
    private String sw1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final deepan globaldata = (deepan) getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initialize(savedInstanceState);
        initializeLogic();




    }



    private void initialize(Bundle savedInstanceState) {
        for(int i =1; i<=6; i++) {
            int id = getResources().getIdentifier("layout"+i,"id", getPackageName());
            LinearLayout linearLayout = (LinearLayout) findViewById(id);
            linearLayout.setElevation(10);
            linearLayout.setBackground(new GradientDrawable() {
                public GradientDrawable getIns(int a, int b) {
                    this.setCornerRadius(a);
                    this.setColor(b);
                    return this;
                }
            }.getIns((int) 28, 0xFF3A3746));
        }
        ConstraintLayout outerConstraint = (ConstraintLayout) findViewById(R.id.outerConstraint);
        ConstraintLayout innerConstraint = (ConstraintLayout) findViewById(R.id.innerConstraint);

        outerConstraint.setBackgroundColor(0xFF242031);
        innerConstraint.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFF40385A, 0xFF242031}));
    }

    private void initializeLogic() {
        subText = (TextView)findViewById(R.id.textView2);


        final deepan globaldata1 = (deepan) getApplicationContext();

        subText = (TextView)findViewById(R.id.textView2);




        final String host = "6b5b817567834cb09f57c948075583b3.s1.eu.hivemq.cloud"; // use your host-name, it should look like '<alphanumeric>.s2.eu.hivemq.cloud'
        final String username = "deepan"; // your credentials
        final String password = "deepan2020G";
        client = Mqtt5Client.builder()
                .identifier("sensor-" + hardwareAddress) // use a unique identifier
                .serverHost(host)
                .automaticReconnectWithDefaultConfig() // the client automatically reconnects
                .serverPort(8883) // this is the port of your cluster, for mqtt it is the default port 8883
                .sslWithDefaultConfig() // establish a secured connection to HiveMQ Cloud using TLS
                .build();
        // 2. connect the client

        client.toBlocking().connectWith()
                .simpleAuth() // using authentication, which is required for a secure connection
                .username(username) // use the username and password you just created
                .password(password.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .willPublish() // the last message, before the client disconnects
                .topic("connect")
                .payload("not connected".getBytes())
                .applyWillPublish()
                .send();



        for(int i=1;i<=4;i++) {
            int id = getResources().getIdentifier("switch_button"+i,"id", getPackageName());
            SwitchButton switchButton = (SwitchButton) findViewById(id);
            switchButton.setShadowEffect(true);
            final int n = i;
            switchButton.setOnCheckedChangeListener((view, isChecked) -> {
                //TODO do your job
                if (switchButton.isChecked()) {
                    client.toBlocking().publishWith()
                            .topic("switch"+n)
                            .payload("1".getBytes())
                            .send();
                } else {
                    client.toBlocking().publishWith()
                            .topic("switch"+n)
                            .payload("0".getBytes())
                            .send();
                }
            });
        }


        for(int i=1;i<=4;i++) {


            client.toAsync().subscribeWith()
                    .topicFilter("switch"+ i +"_status")

                    .callback(deepan1 -> {
                        // Process the received message

                       final String da;
                        this.sw1= new String(deepan1.getPayloadAsBytes());

                        da=("Received message on topic " + deepan1.getTopic() + ": " + sw1);

                        System.out.println(da);
                    })


                    .send();



        }

System.out.println(this.sw1);







    }


}