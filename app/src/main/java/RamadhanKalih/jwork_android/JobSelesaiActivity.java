package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class JobSelesaiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_job);

        InvoiceJob inv = HistoryActivity.getSelectedItem();

        setText(R.id.prRecruiter, inv.recruiter);
        setText(R.id.prInvoiceId, inv.id);
        setText(R.id.prInvoiceDate, inv.date);
        setText(R.id.prInvoiceStatus, inv.status);
        setText(R.id.prPayment, inv.type);
        setText(R.id.prReferral, inv.referralCode);
        setText(R.id.prExtraFee, inv.extraFee);
        setText(R.id.prJobName, inv.jobName);
        setText(R.id.prJobFee, inv.jobFee);
        setText(R.id.prTotalFee, inv.jobFee + inv.extraFee);
    }

    private void setText(int id, int text) {
        setText(id, String.valueOf(text));
    }

    private void setText(int id, String text) {
        TextView view = findViewById(id);
        view.setText(text);
    }
}