package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
implements Response.ErrorListener, Response.Listener<String>
{
    ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    ArrayList<Job> listJobId = new ArrayList<>();
    HashMap<Recruiter, ArrayList<Job>> childMap = new HashMap<>();

    ExpandableListView listView;
    ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // preparing list data
        refreshList();
    }

    protected void refreshList() {
        MenuRequest req = new MenuRequest(this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONArray resp = new JSONArray(response);
            if (resp.isNull(0)) throw new RuntimeException();
            for (int i = 0; i < resp.length(); i++)
            {
                JSONObject job = resp.getJSONObject(i);
                JSONObject rec = job.getJSONObject("recruiter");
                JSONObject loc = rec.getJSONObject("location");

                Location location = new Location(
                        loc.getString("province"),
                        loc.getString("city"),
                        loc.getString("description")
                );
                Recruiter recruiter = new Recruiter(
                        rec.getInt("id"),
                        rec.getString("name"),
                        rec.getString("email"),
                        rec.getString("phoneNumber"),
                        location
                );
                Job obj = new Job(
                        job.getInt("id"),
                        job.getString("name"),
                        recruiter,
                        job.getInt("fee"),
                        job.getString("category")
                );

                listJobId.add(obj);
                listRecruiter.add(recruiter);
            }
        } catch (Exception e) { }

        // update childMap
        for (Recruiter r : listRecruiter)
        {
            ArrayList<Job> jobList = new ArrayList<>();
            for (Job j : listJobId)
            {
                if (j.getRecruiter().getName().equals(r.getName()) &&
                        j.getRecruiter().getEmail().equals(r.getEmail()) &&
                        j.getRecruiter().getPhoneNumber().equals(r.getPhoneNumber()))
                    jobList.add(j);
            }
            childMap.put(r, jobList);
        }

        // trigger
        listView = (ExpandableListView) findViewById(R.id.listView);
        listAdapter = new MainListAdapter(this, listRecruiter, childMap);
        listView.setAdapter(listAdapter);
    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }
}