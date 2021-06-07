package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity
implements Response.ErrorListener, Response.Listener<String>,
ExpandableListView.OnChildClickListener
{
    HashSet<Integer> listRecruiterId = new HashSet<>();
    ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    ArrayList<Job> listJobId = new ArrayList<>();
    HashMap<Recruiter, ArrayList<Job>> childMap = new HashMap<>();

    private static Job selectedJob;
    private static int jobseekerId;

    ExpandableListView listView;
    ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get id from login activity
        jobseekerId = getIntent().getIntExtra("id", -1);
        // preparing list data
        refreshList();
    }

    public static int getJobseekerId() { return jobseekerId; }
    public static Job getSelectedJob() { return selectedJob; }

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
                // if recruiter id is not already in the list, insert
                if (listRecruiterId.add(recruiter.getId()))
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
        listView.setOnChildClickListener(this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        selectedJob = childMap.get(listRecruiter.get(groupPosition)).get(childPosition);
        Intent i = new Intent(this, ApplyJobActivity.class);
        startActivity(i);
        return true;
    }
}