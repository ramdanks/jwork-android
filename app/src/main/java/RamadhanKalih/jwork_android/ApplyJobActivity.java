package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ApplyJobActivity extends AppCompatActivity
implements RadioGroup.OnCheckedChangeListener, Response.ErrorListener,
        TextWatcher
{
    private RadioGroup group;
    private Job job;
    private static final int BANK_ID = 1;
    private static final int EWALLET_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        job = MainActivity.getSelectedJob();

        TextView jname = findViewById(R.id.tvJobName);
        TextView jcat  = findViewById(R.id.tvJobCategory);
        TextView jfee  = findViewById(R.id.tvJobFee);
        TextView jtot  = findViewById(R.id.tvTotalFee);

        group = findViewById(R.id.rgPayment);
        group.setOnCheckedChangeListener(this);

        jname.setText(job.getName());
        jcat.setText(job.getCategory());
        jfee.setText(String.valueOf(job.getFee()));
        jtot.setText("0");

        findViewById(R.id.btnApply).setEnabled(false);
        ((EditText) findViewById(R.id.etRefCode)).addTextChangedListener(this);

        findViewById(R.id.btnCount).setOnClickListener(this::onCountClick);
        findViewById(R.id.btnApply).setOnClickListener(this::onApplyClick);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        TextView tvRefCode = findViewById(R.id.tvRefCode);
        EditText etRefCode = findViewById(R.id.etRefCode);
        int visibility = getRadioSelection() == EWALLET_ID ? View.VISIBLE : View.INVISIBLE;
        tvRefCode.setVisibility(visibility);
        etRefCode.setVisibility(visibility);
    }

    public void onCountClick(View view) {
        TextView jtot  = findViewById(R.id.tvTotalFee);
        if (getRadioSelection() == EWALLET_ID)
        {
            EditText etRefCode = findViewById(R.id.etRefCode);
            String refCode = etRefCode.getText().toString();
            if (!refCode.isEmpty())
            {
                BonusRequest req = new BonusRequest(refCode, this::onCountResponse, this);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(req);
                jtot.setText("Calculating...");
                return;
            }
        }
        findViewById(R.id.btnApply).setEnabled(true);
        jtot.setText(String.valueOf(job.getFee()));
    }

    public void onApplyClick(View view)
    {
        ApplyJobRequest req = null;
        int jobseekerId = MainActivity.getJobseekerId();
        ArrayList<Integer> jobIdList = new ArrayList<>();
        jobIdList.add(job.getId());

        if (getRadioSelection() == BANK_ID)
        {
            req = new ApplyJobRequest(jobIdList, jobseekerId, this::onApplyResponse, this);
        }
        else
        {
            EditText etRefCode = findViewById(R.id.etRefCode);
            String referralCode = etRefCode.getText().toString();
            req = new ApplyJobRequest(jobIdList, jobseekerId, referralCode, this::onApplyResponse, this);
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    private int getRadioSelection() {
        int checkedId = group.getCheckedRadioButtonId();
        View radioButton = group.findViewById(checkedId);
        return group.indexOfChild(radioButton);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
    }

    public void onApplyResponse(String response) {
        String promptMessage = null;
        promptMessage = response.isEmpty() ? "Request failed" : "Applied successfully";
        Toast.makeText(this, promptMessage, Toast.LENGTH_LONG).show();
    }

    public void onCountResponse(String response) {
        int totalFee = job.getFee();
        String promptMessage = null;
        try {
            if (response.isEmpty())
                throw new Exception("Referral not exists!");
            // get as json object
            JSONObject obj = new JSONObject(response);
            // bonus needs to be active
            if (!obj.getBoolean("active"))
                throw new Exception("Referral not active!");
            // bonus min total fee should be <= totalFee
            if (obj.getInt("minTotalFee") > totalFee)
                throw new Exception("Referral requirements not met!");
            // if all requirements met, increase totalFee
            totalFee += obj.getInt("extraFee");
            promptMessage = "Referral code applied!";
            findViewById(R.id.btnApply).setEnabled(true);
        } catch (JSONException e) {
            promptMessage = "Processing failed!";
        } catch (Exception e) {
            promptMessage = e.getMessage();
        } finally {
            Toast.makeText(this, promptMessage, Toast.LENGTH_LONG).show();
            TextView jtot = findViewById(R.id.tvTotalFee);
            jtot.setText(String.valueOf(totalFee));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        View view = findViewById(R.id.btnApply);
        if (view.isEnabled())
            view.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {}
}