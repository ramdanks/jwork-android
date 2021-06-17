package RamadhanKalih.jwork_android;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/** Runnable untuk meminta dan mengurai list InvoiceJob dari jobseeker
 * membolehkan kita membangun thread untuk menjalankan secara asinkron
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class HistoryRunnable implements Runnable,
Response.ErrorListener, Response.Listener<String>
{
    /** konstan penanda sedang dalam proses */
    public static final int ON_PROGRESS = -1;

    /** context yang menghandle VoleyRequest */
    private Context context;
    /** jobseeker yang di proses History nya */
    private int jobseekerId;
    /** response yang disimpan dari hasil VolleyRequest */
    private String response;
    /** panjang data keseluruhan */
    private int lengthData = ON_PROGRESS;
    /** panjang data yang telah diproses */
    private int progressData = ON_PROGRESS;

    /** exception dari pengolahan response */
    private Exception responseException = null;
    /** error dari koneksi ke server */
    private VolleyError responseError = null;

    /** list item InvoiceJob yang berstatus OnGoing */
    private ArrayList<InvoiceJob> listItemOnGoing = new ArrayList<>();
    /** list item InvoiceJob yang berstatus Finished */
    private ArrayList<InvoiceJob> listItemFinished = new ArrayList<>();
    /** list item InvoiceJob yang berstatus Cancelled */
    private ArrayList<InvoiceJob> listItemCancelled = new ArrayList<>();

    /** ctor inisiasi variabel */
    public HistoryRunnable(Context context, int jobseekerId) {
        this.context = context;
        this.jobseekerId = jobseekerId;
    }

    /** akses panjang data yang telah diproses */
    public int getLengthData() { return lengthData; }
    /** akses panjang data keseluruhan */
    public int getProgressData() { return progressData; }
    /** state runnable baik apabila tidak ada exception dan error */
    public boolean isOk() { return responseException == null && responseError == null; }
    /** state runnable selesai jika seluruh data telah diproses */
    public boolean isDone() { return progressData == lengthData; }
    /** akses Exception */
    public Exception getException() { return responseException; }
    /** akses VolleyError */
    public VolleyError getError() { return responseError; }
    /** akses list InvoiceJob berstatus OnGoing */
    public ArrayList<InvoiceJob> getListItemOnGoing() { return listItemOnGoing; }
    /** akses list InvoiceJob berstatus Finished */
    public ArrayList<InvoiceJob> getListItemFinished() { return listItemFinished; }
    /** akses list InvoiceJob berstatus Cancelled */
    public ArrayList<InvoiceJob> getListItemCancelled() { return listItemCancelled; }

    /** lakukan request dan penguraian data */
    @Override
    public void run() {
        // buat queue request
        JobFetchRequest req = new JobFetchRequest(jobseekerId, this, this);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(req);
        // tunggu sampai respon dari onResponse
        while(response == null);
        // proses respon, dasarnya melakukan parsing
        try {
            JSONArray resp = new JSONArray(response);
            lengthData = resp.length();
            for (progressData = 0; progressData < lengthData; progressData++)
            {
                JSONObject invoiceJSON = resp.getJSONObject(progressData);
                JSONArray jobsJSON = invoiceJSON.getJSONArray("jobs");
                for (int j = 0; j < jobsJSON.length(); j++)
                {
                    InvoiceJob inv = new InvoiceJob();

                    JSONObject jobJSON = jobsJSON.getJSONObject(j);
                    JSONObject recJSON = jobJSON.getJSONObject("recruiter");

                    inv.id = invoiceJSON.getInt("id");
                    inv.status = invoiceJSON.getString("invoiceStatus");
                    inv.date = invoiceJSON.getString("date");
                    inv.recruiter = recJSON.getString("name");
                    inv.jobName = jobJSON.getString("name");
                    inv.jobFee = jobJSON.getInt("fee");
                    inv.jobCategory = jobJSON.getString("category");
                    inv.type = invoiceJSON.getString("paymentType");

                    if (!invoiceJSON.isNull("bonus")) {
                        JSONObject bonusJSON = invoiceJSON.getJSONObject("bonus");
                        inv.referralCode = bonusJSON.getString("referralCode");
                        inv.extraFee = bonusJSON.getInt("extraFee");
                    }

                    if (inv.status.equals("OnGoing"))        listItemOnGoing.add(inv);
                    else if (inv.status.equals("Finished"))  listItemFinished.add(inv);
                    else                                     listItemCancelled.add(inv);
                }
                Thread.sleep(0);
            }
        } catch(Exception e) {
            responseException = e;
        }
    }

    /** respon terhadap VolleyError maka otomatis status runnable selesai */
    @Override
    public void onErrorResponse(VolleyError error) {
        responseError = error;
        progressData = 0; lengthData = 0;
    }

    /** memindahkan response supaya penguraian data dilakukan pada run(),
     * ini agar tidak blocking thread dari context */
    @Override
    public void onResponse(String response) {
        this.response = response;
    }
}
