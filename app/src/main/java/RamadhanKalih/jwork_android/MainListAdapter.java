package RamadhanKalih.jwork_android;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapter untuk memproses tampilan ListView Recruiter dengan Jobnya
 * @author Ramadhan Kalih Sewu (1806148826)
 * @version 210617
 */
public class MainListAdapter extends BaseExpandableListAdapter
implements Filterable
{
    private static final boolean SEARCH_JOBNAME_ALTERNATIVE = true;

    private Context context;
    // seluruh header (group) yang dibangun melalui ctor
    private List<Recruiter> listAllDataHeader;
    // header yang ditampilkan menurut search view
    private ArrayList<Recruiter> listDataHeader;
    // seluruh peta data berupa recruiter dengan job-nya
    private HashMap<Recruiter, ArrayList<Job>> listDataChild;
    // filter
    private Filter filter;

    public MainListAdapter(Context context, ArrayList<Recruiter> listDataHeader,
                           HashMap<Recruiter, ArrayList<Job>> listChildData) {
        this.context = context;
        this.listAllDataHeader = listDataHeader; // shallow copy
        this.listDataHeader = new ArrayList<>(listDataHeader); // deep copy
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(listDataHeader.get(groupPosition))
                .get(childPosititon).getName();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_job, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition).getName();
    }

    @Override
    public int getGroupCount() { return listDataHeader.size(); }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_recruiter, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    // initiate object with initial size
                    List<Recruiter> filtered = new ArrayList<>(listAllDataHeader.size());
                    // logic
                    if (constraint == null || constraint.length() == 0)
                        filtered.addAll(listAllDataHeader);
                    else {
                        for (Recruiter r : listAllDataHeader) {
                            if (r.getName().contains(constraint))
                                filtered.add(r);
                        }
                    }
                    // apabila tidak ada recruiter tsb, maka cari sebagai jobname
                    if (SEARCH_JOBNAME_ALTERNATIVE) {
                        if (filtered.isEmpty()) {
                            for (Recruiter r : listAllDataHeader) {
                                ArrayList<Job> jobList = listDataChild.get(r);
                                for (Job j : jobList) {
                                    if (j.getName().contains(constraint))
                                        filtered.add(r);
                                }
                            }
                        }
                    }
                    FilterResults res = new FilterResults();
                    res.values = filtered;
                    return res;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    listDataHeader.clear();
                    listDataHeader.addAll((List) results.values);
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }
}