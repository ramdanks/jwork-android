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
    private Context context;
    private int jobseekerId;

    private boolean isResponding = false;
    private Exception responseException = null;
    private VolleyError responseError = null;

    private ArrayList<InvoiceJob> listItemOnGoing = new ArrayList<>();
    private ArrayList<InvoiceJob> listItemFinished = new ArrayList<>();
    private ArrayList<InvoiceJob> listItemCancelled = new ArrayList<>();

    public HistoryRunnable(Context context, int jobseekerId) {
        this.context = context;
        this.jobseekerId = jobseekerId;
    }

    public boolean isResponding() { return isResponding; }
    public boolean isOk() { return responseException == null && responseError == null; }
    public Exception getException() { return responseException; }
    public VolleyError getError() { return responseError; }

    public ArrayList<InvoiceJob> getListItemOnGoing() { return listItemOnGoing; }
    public ArrayList<InvoiceJob> getListItemFinished() { return listItemFinished; }
    public ArrayList<InvoiceJob> getListItemCancelled() { return listItemCancelled; }

    @Override
    public void run() {
        JobFetchRequest req = new JobFetchRequest(jobseekerId, this, this);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(req);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        isResponding = true;
        responseError = error;
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONArray resp = new JSONArray(response);
            for (int i = 0; i < resp.length(); i++)
            {
                JSONObject invoiceJSON = resp.getJSONObject(i);
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

                    if (inv.status.equals("OnGoing"))        listItemOnGoing.add(inv);
                    else if (inv.status.equals("Finished"))  listItemFinished.add(inv);
                    else                                     listItemCancelled.add(inv);
                }
            }
        } catch(JSONException e) {
            responseException = e;
        } finally {
            isResponding = true;
        }
    }
}
