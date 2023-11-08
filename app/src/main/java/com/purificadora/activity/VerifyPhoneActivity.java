package com.purificadora.activity;

import static com.purificadora.utils.Utiles.isvarification;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.purificadora.MyApplication;
import com.purificadora.R;
import com.purificadora.model.LoginUser;
import com.purificadora.model.RestResponse;
import com.purificadora.model.User;
import com.purificadora.notification.NotificationUtils;
import com.purificadora.notification.NotificationWorker;
import com.purificadora.retrofit.APIClient;
import com.purificadora.retrofit.GetResult;
import com.purificadora.utils.Common;
import com.purificadora.utils.CustPrograssbar;
import com.purificadora.utils.SessionManager;
import com.purificadora.utils.Utiles;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;

public class VerifyPhoneActivity extends AppCompatActivity implements GetResult.MyListener {
    @BindView(R.id.txt_mob)
    TextView txtMob;
    @BindView(R.id.ed_otp1)
    EditText edOtp1;
    @BindView(R.id.ed_otp2)
    EditText edOtp2;
    @BindView(R.id.ed_otp3)
    EditText edOtp3;
    @BindView(R.id.ed_otp4)
    EditText edOtp4;
    @BindView(R.id.ed_otp5)
    EditText edOtp5;
    @BindView(R.id.ed_otp6)
    EditText edOtp6;

    @BindView(R.id.btn_reenter)
    TextView btnReenter;
    @BindView(R.id.btn_timer)
    TextView btnTimer;
    private String verificationId;
    private FirebaseAuth mAuth;
    String phonenumber;

    private TextView btn_send;

    String phonecode;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    String verificationCode = generateRandomVerificationCode();
    public static final String TAG = VerifyPhoneActivity.class.getSimpleName();
    private static final String JOB_PERIODIC_TASK_TAG_NOTIFICATION = "io.hypertrack.android_scheduler_demo.JobPeriodicTask.JOB_PERIODIC_TASK_TAG_NOTIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(VerifyPhoneActivity.this);
        custPrograssbar = new CustPrograssbar();
        if (isvarification == 2) {
            user = (User) getIntent().getSerializableExtra("user");
        } else {
            user = sessionManager.getUserDetails();
        }
        mAuth = FirebaseAuth.getInstance();
        btn_send = findViewById(R.id.btn_send);

        phonenumber = getIntent().getStringExtra("phone");
        phonecode = getIntent().getStringExtra("code");

        if(isvarification == 0){
            btn_send.setText("Cambiar contraseña");

        }
     //  sendVerificationCode(phonecode + phonenumber);


    //    NotificationUtils.displayNotification(this, "Tu código de verificación es:",verificationCode );
    //    configPushNotification();



        sendVerificationCodeToServer(phonecode + phonenumber);


        edOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("fdlk","kgjd");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 1) {
                    edOtp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("fdlk","kgjd");

            }
        });
        edOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("fdlk","kgjd");

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("count", "" + count);
                Log.e("count", "" + count);
                Log.e("count", "" + count);

                if (s.length() == 1) {
                    edOtp3.requestFocus();
                }else if(count==0){
                    edOtp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("fdlk","kgjd");

            }
        });
        edOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("fdlk","kgjd");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 1) {
                    edOtp4.requestFocus();
                }else if(count==0){
                    edOtp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("fdlk","kgjd");

            }
        });
        edOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("fdlk","kgjd");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 1) {
                    edOtp5.requestFocus();
                }else if(count==0){
                    edOtp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("fdlk","kgjd");

            }
        });
        edOtp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("fdlk","kgjd");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 1) {
                    edOtp6.requestFocus();
                }else if(count==0){
                    edOtp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("fdlk","kgjd");

            }
        });
        edOtp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("fdlk","kgjd");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    edOtp6.requestFocus();
                }else if(count==0){
                    edOtp5.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.i("fdlk","kgjd");

            }
        });
 




        txtMob.setText("Enviamos un codigo de verificación al número " + phonecode + " " + phonenumber + " con 6 digitos.");
        try {
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                    btnTimer.setText(seconds + " segundos");
                }

                @Override
                public void onFinish() {
                    btnReenter.setVisibility(View.VISIBLE);
                    btnTimer.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateRandomVerificationCode() {
        // Genera un código de verificación aleatorio de 6 dígitos
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        int verificationCode = random.nextInt(max - min + 1) + min;
        return String.valueOf(verificationCode);
    }
    private void verifyCode() {
        createUser();
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        switch (isvarification) {
                            case 0:
                                Intent intent = new Intent(VerifyPhoneActivity.this, ChanegPasswordActivity.class);
                                intent.putExtra("phone", phonenumber);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
                            case 1:
                                createUser();
                                break;
                            case 2:
                                updateUser();
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
    // Método para enviar la solicitud POST con OkHttp
    private void sendVerificationCodeToServer(final String recipientNumber) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://purificadora.saint-remi.mx/otpverificacion.php"; // Reemplaza con tu URL

        RequestBody requestBody = new FormBody.Builder()
                .add("recipientNumber", recipientNumber) // Envía el código OTP al servidor
                // Puedes agregar otros parámetros si es necesario
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

          // Realiza la solicitud en un hilo en segundo plano
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();

                    // Actualiza la interfaz de usuario en el hilo principal
                    runOnUiThread(() -> handleServerResponse(responseData));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para manejar la respuesta del servidor
    private void handleServerResponse(String responseData) {
        if (responseData.equals("200")) {


            runOnUiThread(() -> {
             //   Toast.makeText(VerifyPhoneActivity.this, "Código OTP verificado con éxito", Toast.LENGTH_SHORT).show();

                // Aquí puedes realizar cualquier otra acción necesaria, como navegar a la siguiente actividad.
            });
        } else {


            runOnUiThread(() -> {
           //     Toast.makeText(VerifyPhoneActivity.this, "No se pudo verificar el código OTP. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();

                // Si deseas, puedes tomar medidas adicionales, como permitir que el usuario reintente la verificación.
            });
        }
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                edOtp1.setText("" + code.substring(0, 1));
                edOtp2.setText("" + code.substring(1, 2));
                edOtp3.setText("" + code.substring(2, 3));
                edOtp4.setText("" + code.substring(3, 4));
                edOtp5.setText("" + code.substring(4, 5));
                edOtp6.setText("" + code.substring(5, 6));
               verifyCode();
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            User user1 = new User();
            user1.setId("0");
            user1.setName("User");
            user1.setEmail("user@gmail.com");
            user1.setMobile("+91 8888888888");
            sessionManager.setUserDetails( user1);
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    @OnClick({R.id.btn_send, R.id.btn_reenter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (validation()) {

                    verifyCode();
                }
                if(isvarification == 0){
                 Intent intent = new Intent(VerifyPhoneActivity.this, ChanegPasswordActivity.class);
                intent.putExtra("phone", phonenumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                }


                break;



            case R.id.btn_reenter:
                btnReenter.setVisibility(View.GONE);
                btnTimer.setVisibility(View.VISIBLE);
                try {
                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                            btnTimer.setText(seconds + " segundos");
                        }

                        @Override
                        public void onFinish() {
                            btnReenter.setVisibility(View.VISIBLE);
                            btnTimer.setVisibility(View.GONE);
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
               // sendVerificationCode(phonecode + phonenumber);
//                NotificationUtils.displayNotification(this, "Tu código de verificación es:",verificationCode );
                sendVerificationCodeToServer(phonecode + phonenumber);



             //   configPushNotification();


                break;
            default:
                break;
        }
    }


        private void configPushNotification(){

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.cancelAllWorkByTag(JOB_PERIODIC_TASK_TAG_NOTIFICATION);

        String title = "Tu codigo de verificación para la app es:";
        String message = generateRandomVerificationCode() ;


        Data inputData = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();


       // int minutes = currentOrder.getTimePickUp();

        OneTimeWorkRequest notificationWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .addTag(JOB_PERIODIC_TASK_TAG_NOTIFICATION)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .build();

        workManager.enqueue(notificationWorkRequest);

    }

    private void createUser() {
        custPrograssbar.prograssCreate(VerifyPhoneActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("ccode", user.getCcode());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("refercode", user.getRcode());
            jsonObject.put("imei", Utiles.getIMEI(VerifyPhoneActivity.this));
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getRegister((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("name", user.getName());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("ccode", user.getCcode());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("imei", Utiles.getIMEI(VerifyPhoneActivity.this));
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().updateProfile((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            Log.e("response", "--->" + result);
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                isvarification = -1;
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(VerifyPhoneActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equals("true")) {
                    startActivity(new Intent(VerifyPhoneActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                isvarification = -1;
                Gson gson = new Gson();
                LoginUser response = gson.fromJson(result.toString(), LoginUser.class);
                Toast.makeText(VerifyPhoneActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equals("true")) {
                    sessionManager.setUserDetails( response.getUser());
                    startActivity(new Intent(VerifyPhoneActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validation() {

        if (edOtp1.getText().toString().isEmpty()) {
            edOtp1.setError("");
            return false;
        }
        if (edOtp2.getText().toString().isEmpty()) {
            edOtp2.setError("");
            return false;
        }
        if (edOtp3.getText().toString().isEmpty()) {
            edOtp3.setError("");
            return false;
        }
        if (edOtp4.getText().toString().isEmpty()) {
            edOtp4.setError("");
            return false;
        }
        if (edOtp5.getText().toString().isEmpty()) {
            edOtp5.setError("");
            return false;
        }
        if (edOtp6.getText().toString().isEmpty()) {
            edOtp6.setError("");
            return false;
        }
        return true;
    }
}
