package RamadhanKalih.jwork_android;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends BaseAdapter
implements Filterable
{
    public static final int NOT_FOUND = -1;

    private Context context;
    private ArrayList<InvoiceJob> listAllItem;
    private ArrayList<InvoiceJob> listItem;
    private Filter filter;

    public HistoryListAdapter(Context context, ArrayList<InvoiceJob> listItem) {
        this.context = context;
        this.listItem = listItem; // shallow copy
        this.listAllItem = new ArrayList<>(listItem); // deep copy
    }

    private int getItemIndex(int invId, ArrayList<InvoiceJob> list) {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).id == invId)
                return i;
        }
        return NOT_FOUND;
    }

    public InvoiceJob removeItem(int invId) {
        // lakukan linear search untuk mencari index pada array
        final int iAll = getItemIndex(invId, listAllItem);
        // apabila tidak di Seluruh Item, sudah pasti tidak ada di View, langsung return null
        if (iAll == NOT_FOUND) return null;
        // apabila besar list sama, maka dijamin konten isinya sama persis (tidak ada filter)
        final int iView = listItem.size() == listAllItem.size() ? iAll : getItemIndex(invId, listItem);
        if (iView != NOT_FOUND) {
            listItem.remove(iView);
            super.notifyDataSetChanged();
        }
        return listAllItem.remove(iAll);
    }

    public void addItem(InvoiceJob inv) {
        listAllItem.add(inv);
        listItem.add(inv);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() { return listItem.size(); }

    @Override
    public Object getItem(int i) { return listItem.get(i); }

    @Override
    public long getItemId(int i) { return i; }

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

        switch (inv.jobCategory)
        {
            case "WebDeveloper":
                pict.setImageResource(R.drawable.webdeveloper);
                break;
            case "FrontEnd":
                pict.setImageResource(R.drawable.frontend);
                break;
            case "BackEnd":
                pict.setImageResource(R.drawable.backend);
                break;
            case "UI":
                pict.setImageResource(R.drawable.ui);
                break;
            case "UX":
                pict.setImageResource(R.drawable.ux);
                break;
            case "Devops":
                pict.setImageResource(R.drawable.devops);
                break;
            case "DataScientist":
                pict.setImageResource(R.drawable.datascientist);
                break;
            case "DataAnalyst":
                pict.setImageResource(R.drawable.dataanalyst);
                break;
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    // initiate object with initial size
                    List<InvoiceJob> filtered = new ArrayList<>(listAllItem.size());
                    // logic
                    if (constraint == null || constraint.length() == 0)
                        filtered.addAll(listAllItem);
                    else {
                        for (InvoiceJob inv : listAllItem) {
                            if (inv.jobName.contains(constraint))
                                filtered.add(inv);
                        }
                    }
                    FilterResults res = new FilterResults();
                    res.values = filtered;
                    return res;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    listItem.clear();
                    listItem.addAll((List) results.values);
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }
}