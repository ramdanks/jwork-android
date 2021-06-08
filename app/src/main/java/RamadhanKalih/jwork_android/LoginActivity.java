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

public class LoginActivity extends AppCompatActivity
implements Response.Listener<String>, Response.ErrorListener
{

    private EditText etEmail;
    private EditText etPass;
    private Button btnLogin;
    private TextView tvRegister;

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

    private void onLoginClick(View view) {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        LoginRequest req = new LoginRequest(email, pass, this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    private void onRegisterClick(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
    }
}