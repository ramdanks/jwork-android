package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.w3c.dom.Text;

import javax.xml.transform.ErrorListener;

public class JobSelesaiActivity extends AppCompatActivity
implements Response.ErrorListener
{
    private int invoiceId;
    private static final int STATUS_CANCELLED = 0;
    private static final int STATUS_FINISHED = 1;

    private Button btnCancel;
    private Button btnFinish;
    private Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_job);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InvoiceJob inv = HistoryActivity.getSelectedItem();
        invoiceId = inv.id;

        setText(R.id.prRecruiter, inv.recruiter);
        setText(R.id.prInvoiceId, inv.id);
        setText(R.id.prInvoiceDate, inv.date);
        setText(R.id.prInvoiceStatus, inv.status);
        setText(R.id.prPayment, inv.type);
        setText(R.id.prReferral, inv.referralCode);
        setText(R.id.prExtraFee, inv.extraFee);
        setText(R.id.prJobName, inv.jobName);
        setText(R.id.prJobCategory, inv.jobCategory);
        setText(R.id.prJobFee, inv.jobFee);
        setText(R.id.prTotalFee, inv.jobFee + inv.extraFee);

        (btnCancel = findViewById(R.id.btnProcessCancel)).setOnClickListener(this::onCancelClick);
        (btnFinish = findViewById(R.id.btnProcessFinish)).setOnClickListener(this::onFinishClick);
        (btnReturn = findViewById(R.id.btnProcessReturn)).setOnClickListener(this::onReturnClick);

        setReadOnly(getIntent().getBooleanExtra("readOnly", true));
    }

    public void setReadOnly(boolean readOnly) {
        if (readOnly)
        {
            btnCancel.setVisibility(View.INVISIBLE);
            btnFinish.setVisibility(View.INVISIBLE);
            btnReturn.setVisibility(View.VISIBLE);
        }
        else
        {
            btnCancel.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.VISIBLE);
            btnReturn.setVisibility(View.INVISIBLE);
        }
    }

    private void onReturnClick(View view) {
        super.finish();
    }

    private void onCancelClick(View view) {
        JobBatalRequest req = new JobBatalRequest(invoiceId, this::onCancelResponse, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    private void onFinishClick(View view) {
        JobSelesaiRequest req = new JobSelesaiRequest(invoiceId, this::onFinishResponse, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    private void setText(int id, int text) {
        setText(id, String.valueOf(text));
    }

    private void setText(int id, String text) {
        TextView view = findViewById(id);
        view.setText(text);
    }

    private void onCancelResponse(String response) {
        processResponse(response, STATUS_CANCELLED);
    }

    private void onFinishResponse(String response) {
        processResponse(response, STATUS_FINISHED);
    }

    private void processResponse(String response, int status)  {
        if (response.isEmpty())
        {
            Toast.makeText(this, "Problem processing invoice!", Toast.LENGTH_LONG).show();
            return;
        }
        // ubah tampilan button
        setReadOnly(true);
        // proses data
        final int fromSel = HistoryActivity.SEL_ONGOING;
        final int toSel = status == STATUS_CANCELLED ? HistoryActivity.SEL_CANCELLED : HistoryActivity.SEL_FINISHED;
        final String prompt = status == STATUS_CANCELLED ? "Sucessfully cancelled!" : "Sucessfully finished!";
        String statusText = status == STATUS_CANCELLED ? "Cancelled" : "Finished";
        // buat perubahan pada tampilan
        setText(R.id.prInvoiceStatus, statusText);
        Toast.makeText(this, prompt, Toast.LENGTH_LONG).show();
        HistoryActivity.swapCategory(invoiceId, fromSel, toSel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
    }
}