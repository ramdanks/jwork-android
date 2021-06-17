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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Activity menampilkan Riwayat Invoice milik Jobseeker tertentu
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class HistoryActivity extends AppCompatActivity
implements TabLayout.OnTabSelectedListener, SearchView.OnQueryTextListener
{
    /** posisi index TabLayout dari status invoice OnGoing */
    public static final int SEL_ONGOING = 0;
    /** posisi index TabLayout dari status invoice Finished */
    public static final int SEL_FINISHED = 1;
    /** posisi index TabLayout dari status invoice Cancelled */
    public static final int SEL_CANCELLED = 2;

    /** InvoiceJob yang terakhir kali di click dari activity */
    private static InvoiceJob selectedItem;
    /** jobseeker yang memiliki History InvoiceJob di activity */
    private static int jobseekerId = -1;
    /** Adapter untuk menampilkan listview 3 status invoice */
    private static HistoryListAdapter[] adapterList = new HistoryListAdapter[3];
    /** runnable untuk meminta dan mengurai data History di thread lain */
    private static HistoryRunnable historyRunnable;
    /** list untuk menyimpat konten yang pending akibat melakukan addInvoiceJob() tetapi
     * HistoryListAdapter belum selesai di proses oleh historyRunnable */
    private static HashMap<Integer, ArrayList<InvoiceJob>> pendingInvoiceJob = new HashMap<>();

    /** TabLayout untuk menampilkan kategori InvoiceJob */
    private TabLayout tab;
    /** ListView untuk menampilkan data dari status InvoiceJob */
    private ListView listView;
    /** ProgressBar untuk tampilan loading saat historyRunnable sedang berjalan */
    private ProgressBar progressBar;

    /** class untuk memperbarui tampilan History secara asinkron */
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
                // add item in the pending list
                adapterList[SEL_ONGOING].addItem(pendingInvoiceJob.get(SEL_ONGOING));
                adapterList[SEL_FINISHED].addItem(pendingInvoiceJob.get(SEL_FINISHED));
                adapterList[SEL_CANCELLED].addItem(pendingInvoiceJob.get(SEL_CANCELLED));
            }
            return historyRunnable.isOk();
        }

        protected void onProgressUpdate(Integer... progress) {}

        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            if (!result) return;
            int pos = tab.getSelectedTabPosition();
            listView.setAdapter(adapterList[pos]);
        }
    }

    /** metode entry saat activity dibangun */
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

    /** menyiapkan permintaan InvoiceJob yang dijalankan secara asinkron
     * @param context context yang menghandle VolleyRequest
     * @param jobseekerId id jobseeker yang ingin di ambil History Invoice nya */
    public static boolean prefetchInvoiceJob(Context context, int jobseekerId) {
        if (historyRunnable == null || HistoryActivity.jobseekerId != jobseekerId) {
            HistoryActivity.jobseekerId = jobseekerId;
            historyRunnable = new HistoryRunnable(context, jobseekerId);
            new Thread(historyRunnable).start();
            return true;
        }
        return false;
    }

    /** menambah InvoiceJob dan memperbarui tampilan
     * @param inv InvoiceJob yang ditambahkan
     * @param sel posisi index TabLayout untuk InvoiceJob ditempatkan
     * @return false jika sel tidak dikenali, true sebaliknya */
    public static boolean addInvoiceJob(InvoiceJob inv, int sel) {
        if (sel < 0 || sel > 2)
            return false;
        if (adapterList[sel] == null) {
            ArrayList<InvoiceJob> list = pendingInvoiceJob.get(sel);
            if (list == null)
                list = new ArrayList<InvoiceJob>();
            list.add(inv);
            pendingInvoiceJob.put(sel, list);
            return true;
        }
        adapterList[sel].addItem(inv);
        return true;
    }

    /** membangun search menu dalam topbar */
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

    /** akses terhadap InvoiceJob yang terakhir kali di pilih */
    public static InvoiceJob getSelectedItem() { return selectedItem; }

    /** memindahkan posisi index TabLayout dari sebuah InvoiceJob
     * @param invId id dari InvoiceJob yang ingin dipindah
     * @param fromSel asal tempat InvoiceJob berada
     * @param toSel destinari tempat InvoiceJob dipindahkan
     * @return false jika sel sama, tidak dikenali, atau InvoiceJob tidak ditemukan, true sebaliknya */
    public static boolean swapCategory(int invId, int fromSel, int toSel) {
        if (fromSel == toSel || fromSel < 0 || fromSel > 2 || toSel < 0 || toSel > 2)
            return false;
        InvoiceJob inv = adapterList[fromSel].removeItem(invId);
        if (inv == null)
            return false;
        adapterList[toSel].addItem(inv);
        return true;
    }

    /** saat item InvoiceJob di click, masuk ke activity JobSelesaiActivity
     * @see JobSelesaiActivity */
    private void onClick(AdapterView<?> adapterView, View view, int i, long l) {
        int pos = tab.getSelectedTabPosition();
        selectedItem = (InvoiceJob) adapterList[pos].getItem(i);
        Intent intent = new Intent(this, JobSelesaiActivity.class);
        // only ongoing invoice can be processed, else read only
        boolean readOnly = pos == SEL_ONGOING ? false : true;
        intent.putExtra("readOnly", readOnly);
        startActivity(intent);
    }

    /** apabila tombol back dipencet, sudahi activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** saat Tab dipencet, ubah tampilan ListView dengan adapter yang sesuai */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = this.tab.getSelectedTabPosition();
        ListView view = findViewById(R.id.lvHistory);
        view.setAdapter(adapterList[pos]);
    }

    /** tidak digunakan */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    /** tidak digunakan */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    /** tidak digunakan */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /** Proses Searching: kueri teks searching berubah maka lakukan filter terhadap adapter
     * di posisi TabLayout yang sesuai, filter akan memperbarui tampilan ListView */
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