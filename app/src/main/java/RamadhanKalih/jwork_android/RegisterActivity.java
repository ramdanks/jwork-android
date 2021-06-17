package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/** Activity untuk menampilkan dan memproses pendaftaran jobseeker
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class RegisterActivity extends AppCompatActivity
implements Response.Listener<String>, Response.ErrorListener
{
    /** EditText nama jobseeker */
    private EditText etName;
    /** EditText email jobseeker */
    private EditText etEmail;
    /** EditText password jobseeker */
    private EditText etPass;
    /** Button Register */
    private Button btnRegister;
    /** Button Login */
    private TextView tvLoginInstead;

    /** dipanggil saat activity dibangun, inisiasi variable dan set listener */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPass = findViewById(R.id.etRegisterPass);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginInstead = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(this::onRegisterClick);
        tvLoginInstead.setOnClickListener(this::onLoginInsteadClick);
    }

    /** menjawab response untuk request pendaftaran jobseeker */
    @Override
    public void onResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj == null) throw new RuntimeException();
        } catch (Exception e) {
            Toast.makeText(this, "Register Failed", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Register Successful", Toast.LENGTH_LONG).show();
    }

    /** menampilkan feedback jika terdapat kegagalan koneksi */
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Register Failed", Toast.LENGTH_LONG).show();
    }

    /** memproses data jobseeker saat button Register di click dan lakukan request */
    private void onRegisterClick(View view) {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        String name = etName.getText().toString();

        RegisterRequest req = new RegisterRequest(name, email, pass, this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    /** Jalankan activity Login jika button Login di click */
    private void onLoginInsteadClick(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}