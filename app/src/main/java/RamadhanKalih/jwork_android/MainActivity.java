package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.WorkSource;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
ExpandableListView.OnChildClickListener, SearchView.OnQueryTextListener
{
    private HashSet<Integer> listRecruiterId = new HashSet<>();
    private ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    private ArrayList<Job> listJobId = new ArrayList<>();
    private HashMap<Recruiter, ArrayList<Job>> childMap = new HashMap<>();

    private static Job selectedJob;
    private static int jobseekerId;

    private ExpandableListView listView;
    private MainListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get id from login activity
        jobseekerId = getIntent().getIntExtra("id", -1);
        // preparing list data
        refreshList();

        findViewById(R.id.btnAppliedJob).setOnClickListener(this::onAppliedJobClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    public static int getJobseekerId() { return jobseekerId; }
    public static Job getSelectedJob() { return selectedJob; }

    private void onAppliedJobClick(View view) {
        Intent i = new Intent(this, HistoryActivity.class);
        startActivity(i);
    }

    private void refreshList() {
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
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        selectedJob = childMap.get(listRecruiter.get(groupPosition)).get(childPosition);
        Intent i = new Intent(this, ApplyJobActivity.class);
        startActivity(i);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
         return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (listAdapter != null) {
            listAdapter.getFilter().filter(newText);
            return true;
        }
        return false;
    }
}