package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/** Activity untuk memproses jobseeker login
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class LoginActivity extends AppCompatActivity
implements Response.Listener<String>, Response.ErrorListener
{
    /** EditText email */
    private EditText etEmail;
    /** EditText password */
    private EditText etPass;
    /** button Login */
    private Button btnLogin;
    /** button Register */
    private TextView tvRegister;

    /** dipanggil saat activity dibangun, inisiasi variabel dan set listener */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPass = findViewById(R.id.etLoginPass);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        etEmail.setText("ramadhan.kalih@ui.ac.id");
        etPass.setText("Ramadhan123");

        btnLogin.setOnClickListener(this::onLoginClick);
        tvRegister.setOnClickListener(this::onRegisterClick);
    }

    /** meminta Request untuk Login saat login button di click */
    private void onLoginClick(View view) {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        LoginRequest req = new LoginRequest(email, pass, this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    /** membangun activity Register saat register button di click */
    private void onRegisterClick(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    /** respon terhadap Request login, apabila berhasil, jalankan activity Main */
    @Override
    public void onResponse(String response) {
        Intent i = new Intent(this, MainActivity.class);
        try {
            JSONObject obj = new JSONObject(response);
            i.putExtra("id", obj.getInt("id"));
        } catch (Exception e) {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
        startActivity(i);
    }

    /** tampilkan pesan feedback jika terjadi kegagalan koneksi */
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
    }
}