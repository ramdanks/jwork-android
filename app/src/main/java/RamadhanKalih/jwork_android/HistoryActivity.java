package RamadhanKalih.jwork_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity
implements Response.ErrorListener, Response.Listener<String>,
TabLayout.OnTabSelectedListener
{
    private static final int SEL_ONGOING = 0;
    private static final int SEL_FINISHED = 1;
    private static final int SEL_CANCELLED = 2;

    private static InvoiceJob selectedItem;
    private HistoryListAdapter[] adapterList = new HistoryListAdapter[3];
    private TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        refreshList();

        tab = (TabLayout) findViewById(R.id.tabHistory);
        tab.addOnTabSelectedListener(this);
    }

    private void refreshList() {
        JobFetchRequest req = new JobFetchRequest(MainActivity.getJobseekerId(), this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    public static InvoiceJob getSelectedItem() { return selectedItem; }

    private void onClick(AdapterView<?> adapterView, View view, int i, long l) {
        int pos = tab.getSelectedTabPosition();
        selectedItem = (InvoiceJob) adapterList[pos].getItem(i);
        Intent intent = new Intent(this, JobSelesaiActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = this.tab.getSelectedTabPosition();
        ListView view = (ListView) findViewById(R.id.lvHistory);
        view.setAdapter(adapterList[pos]);
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public void onResponse(String response) {

        ArrayList<InvoiceJob> listItemOnGoing = new ArrayList<>();
        ArrayList<InvoiceJob> listItemFinished = new ArrayList<>();
        ArrayList<InvoiceJob> listItemCancelled = new ArrayList<>();

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

                    if (inv.status.equals("OnGoing"))        listItemOnGoing.add(inv);
                    else if (inv.status.equals("Finished"))  listItemFinished.add(inv);
                    else                                     listItemCancelled.add(inv);
                }
            }
        } catch(JSONException e) {
            return;
        }

        // trigger list view
        ListView view = (ListView) findViewById(R.id.lvHistory);
        adapterList[SEL_ONGOING] = new HistoryListAdapter(this, listItemOnGoing);
        adapterList[SEL_FINISHED] = new HistoryListAdapter(this, listItemFinished);
        adapterList[SEL_CANCELLED] = new HistoryListAdapter(this, listItemCancelled);
        view.setAdapter(adapterList[0]);
        view.setOnItemClickListener(this::onClick);
    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }
}