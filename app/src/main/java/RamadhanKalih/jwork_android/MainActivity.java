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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Activity untuk menampilkan list job yang tersedia
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class MainActivity extends AppCompatActivity
implements Response.ErrorListener, Response.Listener<String>,
ExpandableListView.OnChildClickListener, SearchView.OnQueryTextListener
{
    private ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    private ArrayList<Job> listJobId = new ArrayList<>();
    private HashMap<Recruiter, ArrayList<Job>> childMap = new HashMap<>();

    private static Job selectedJob;
    private static int jobseekerId;

    private ExpandableListView listView;
    private MainListAdapter listAdapter;

    /** dipanggil saat activity dibangun, persiapkan data */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get id from login activity
        jobseekerId = getIntent().getIntExtra("id", -1);
        // preparing list data
        refreshList();
        // click listener
        findViewById(R.id.btnAppliedJob).setOnClickListener(this::onAppliedJobClick);
        // prepare History of Invoice Job
        HistoryActivity.prefetchInvoiceJob(this, jobseekerId);
    }

    /** buat sebuah search bar untuk pencarian konten list view */
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

    /** akses jobseeker id yang login */
    public static int getJobseekerId() { return jobseekerId; }
    /** akses job yang dipilih dari listview */
    public static Job getSelectedJob() { return selectedJob; }

    /** saat button applied job di click, jalankan activity History */
    private void onAppliedJobClick(View view) {
        Intent i = new Intent(this, HistoryActivity.class);
        startActivity(i);
    }

    /** lakukan request untuk meminta list job melalui MenuRequest */
    private void refreshList() {
        MenuRequest req = new MenuRequest(this, this);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    /** respon terhadap VolleyRequest, uraikan data sehingga dapat ditampilkan di ListView */
    @Override
    public void onResponse(String response) {
        try {
            JSONArray resp = new JSONArray(response);
            if (resp.isNull(0)) throw new RuntimeException();
            // buat list unik berdasarkan id, agar tidak duplikat recruiter
            HashSet<Integer> listRecruiterId = new HashSet<>();
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

    /** berikan feedback saat terjadi gangguan koneksi */
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Connection Error", Toast.LENGTH_LONG).show();
    }

    /** saat job (child dari ListView) dipilih, pindah ke activity ApplyJob */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        selectedJob = childMap.get(listRecruiter.get(groupPosition)).get(childPosition);
        Intent i = new Intent(this, ApplyJobActivity.class);
        startActivity(i);
        return true;
    }

    /** tidak digunakan */
    @Override
    public boolean onQueryTextSubmit(String query) {
         return false;
    }

    /** proses pencarian: perubahan pada kueri teks search akan mengakibatkan filter
     * kemudian memperbarui tampilan ListView agar sesuai dengan kata pencarian */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (listAdapter != null) {
            listAdapter.getFilter().filter(newText);
            return true;
        }
        return false;
    }
}