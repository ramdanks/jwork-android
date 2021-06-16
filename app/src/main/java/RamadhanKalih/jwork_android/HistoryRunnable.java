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

public class HistoryRunnable implements Runnable,
Response.ErrorListener, Response.Listener<String>
{
    public static final int ON_PROGRESS = -1;

    private Context context;
    private int jobseekerId;
    private String response;
    private int lengthData = ON_PROGRESS;
    private int progressData = ON_PROGRESS;

    private Exception responseException = null;
    private VolleyError responseError = null;

    private ArrayList<InvoiceJob> listItemOnGoing = new ArrayList<>();
    private ArrayList<InvoiceJob> listItemFinished = new ArrayList<>();
    private ArrayList<InvoiceJob> listItemCancelled = new ArrayList<>();

    public HistoryRunnable(Context context, int jobseekerId) {
        this.context = context;
        this.jobseekerId = jobseekerId;
    }

    public int getLengthData() { return lengthData; }
    public int getProgressData() { return progressData; }
    public boolean isOk() { return responseException == null && responseError == null; }
    public boolean isDone() { return progressData == lengthData; }
    public Exception getException() { return responseException; }
    public VolleyError getError() { return responseError; }

    public ArrayList<InvoiceJob> getListItemOnGoing() { return listItemOnGoing; }
    public ArrayList<InvoiceJob> getListItemFinished() { return listItemFinished; }
    public ArrayList<InvoiceJob> getListItemCancelled() { return listItemCancelled; }

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
                Thread.sleep(500);
            }
        } catch(Exception e) {
            responseException = e;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        responseError = error;
        progressData = 0; lengthData = 0;
    }

    @Override
    public void onResponse(String response) {
        this.response = response;
    }
}
