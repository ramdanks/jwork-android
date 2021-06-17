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

/** Activity untuk menampilkan status invoice dan memproses perubahannya
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class JobSelesaiActivity extends AppCompatActivity
implements Response.ErrorListener
{
    /** konstan untuk status Cancelled */
    private static final int STATUS_CANCELLED = 0;
    /** konstan untuk status Finished */
    private static final int STATUS_FINISHED = 1;

    /** InvoiceJob yang ditampilkan */
    private InvoiceJob inv = null;
    /** button Cancel */
    private Button btnCancel;
    /** button Finish */
    private Button btnFinish;
    /** button Return */
    private Button btnReturn;

    /** dipanggil saat activity dibangun */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_job);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inv = HistoryActivity.getSelectedItem();

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

    /** read only tidak boleh ada perintah untuk cancel dan finish, hanya return */
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

    /** saat return button di click, sudahi activity */
    private void onReturnClick(View view) {
        super.finish();
    }

    /** saat cancel button di click, request untuk ubah status invoice melaui JobBatalRequest
     * @see JobBatalRequest */
    private void onCancelClick(View view) {
        JobBatalRequest req = new JobBatalRequest(inv.id, this::onCancelResponse, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    /** saat finish button di click, request untuk ubah status invoice melaui JobSelesaiRequest
     * @see JobSelesaiRequest */
    private void onFinishClick(View view) {
        JobSelesaiRequest req = new JobSelesaiRequest(inv.id, this::onFinishResponse, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    /** mengubah kontent TextView (diambil dari id) dengan text */
    private void setText(int id, int text) {
        setText(id, String.valueOf(text));
    }

    /** mengubah kontent TextView (diambil dari id) dengan text */
    private void setText(int id, String text) {
        TextView view = findViewById(id);
        view.setText(text);
    }

    /** memproses saat cancel button respon
     * @param response Response.Listener<String> dari VolleyRequest */
    private void onCancelResponse(String response) {
        processResponse(response, STATUS_CANCELLED);
    }

    /** memproses saat finish button respon
     * @param response Response.Listener<String> dari VolleyRequest */
    private void onFinishResponse(String response) {
        processResponse(response, STATUS_FINISHED);
    }

    /** proses response dengan mengubah tampilan dan memperbaru HistoryActivity
     * @param response Response.Listener<String> dari VolleyRequest
     * @param status perubahan status pada invoice yang dilakukan */
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
        inv.status = statusText;
        // buat perubahan pada tampilan
        setText(R.id.prInvoiceStatus, statusText);
        Toast.makeText(this, prompt, Toast.LENGTH_LONG).show();
        HistoryActivity.swapCategory(inv.id, fromSel, toSel);
    }

    /** saat tombol back di click, sudahi activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** tampilkan pesan error saat terjadi kegagalan koneksi */
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
    }
}