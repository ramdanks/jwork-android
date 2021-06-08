package RamadhanKalih.jwork_android;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<InvoiceJob> listItem;

    public HistoryListAdapter(Context context, ArrayList<InvoiceJob> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        InvoiceJob inv = (InvoiceJob) getItem(i);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_history_job, null);

        TextView date = (TextView) view.findViewById(R.id.tvHistoryDate);
        TextView detail = (TextView) view.findViewById(R.id.tvHistoryDetail);
        TextView title = (TextView) view.findViewById(R.id.tvHistoryTitle);
        TextView payment = (TextView) view.findViewById(R.id.tvHistoryPayment);
        ImageView pict = (ImageView) view.findViewById(R.id.ivHistoryPicture);

        title.setText(inv.jobName);
        payment.setText(inv.type);
        date.setText(inv.date);
        detail.setText("Recruiter: " + inv.recruiter + ", Total Fee: " + inv.jobFee + inv.extraFee);

        return view;
    }
}