package RamadhanKalih.jwork_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

public class HistoryActivity extends AppCompatActivity
implements TabLayout.OnTabSelectedListener, SearchView.OnQueryTextListener
{
    public static final int SEL_ONGOING = 0;
    public static final int SEL_FINISHED = 1;
    public static final int SEL_CANCELLED = 2;

    private static InvoiceJob selectedItem;
    private static HistoryListAdapter[] adapterList = new HistoryListAdapter[3];
    private static HistoryRunnable historyRunnable;
    private TabLayout tab;
    private ListView listView;
    private ProgressBar progressBar;

    // class async untuk memperbarui list adapter
    private class UpdateAdapter extends AsyncTask<Void, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // update progress
            while (historyRunnable.isOk() && !historyRunnable.isDone())
            {
                int lengthData;
                do { lengthData = historyRunnable.getLengthData(); } while (lengthData == HistoryRunnable.ON_PROGRESS);
                progressBar.setMax(lengthData);
                progressBar.setProgress(historyRunnable.getProgressData());
            }
            // update adapter
            if (historyRunnable.isOk()) {
                adapterList[SEL_ONGOING] = new HistoryListAdapter(HistoryActivity.this, historyRunnable.getListItemOnGoing());
                adapterList[SEL_FINISHED] = new HistoryListAdapter(HistoryActivity.this, historyRunnable.getListItemFinished());
                adapterList[SEL_CANCELLED] = new HistoryListAdapter(HistoryActivity.this, historyRunnable.getListItemCancelled());
            }
            return historyRunnable.isOk();
        }

        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            if (!result) return;
            int pos = tab.getSelectedTabPosition();
            listView.setAdapter(adapterList[pos]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tab = (TabLayout) findViewById(R.id.tabHistory);
        tab.addOnTabSelectedListener(this);

        listView = findViewById(R.id.lvHistory);
        listView.setOnItemClickListener(HistoryActivity.this::onClick);

        progressBar = findViewById(R.id.pbHistory);

        new UpdateAdapter().execute();
    }

    public static boolean prefetchInvoiceJob(Context context, int jobseekerId) {
        if (historyRunnable == null) {
            historyRunnable = new HistoryRunnable(context, jobseekerId);
            new Thread(historyRunnable).start();
            return true;
        }
        return false;
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

    public static InvoiceJob getSelectedItem() { return selectedItem; }

    public static boolean swapCategory(int invId, int fromSel, int toSel) {
        if (fromSel < SEL_ONGOING && toSel > SEL_CANCELLED)
            return false;
        InvoiceJob inv = adapterList[fromSel].removeItem(invId);
        if (inv == null)
            return false;
        adapterList[toSel].addItem(inv);
        return true;
    }

    private void onClick(AdapterView<?> adapterView, View view, int i, long l) {
        int pos = tab.getSelectedTabPosition();
        selectedItem = (InvoiceJob) adapterList[pos].getItem(i);
        Intent intent = new Intent(this, JobSelesaiActivity.class);
        // only ongoing invoice can be processed, else read only
        boolean readOnly = pos == SEL_ONGOING ? false : true;
        intent.putExtra("readOnly", readOnly);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = this.tab.getSelectedTabPosition();
        ListView view = findViewById(R.id.lvHistory);
        view.setAdapter(adapterList[pos]);
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        int pos = this.tab.getSelectedTabPosition();
        if (adapterList[pos] != null) {
            adapterList[pos].getFilter().filter(newText);
            return true;
        }
        return false;
    }
}