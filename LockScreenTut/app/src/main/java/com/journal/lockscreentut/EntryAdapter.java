package com.journal.lockscreentut;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journal.database.model.Entry;
import com.journal.utils.Properties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.MyViewHolder> {
    private Context context;
    private List<Entry> itemList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView brief;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            brief = view.findViewById(R.id.brief);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public EntryAdapter(Context context, List<Entry> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //Item
        Entry note = itemList.get(position);
        //Set values for the the view node
        holder.brief.setText(note.getBrief());
        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml(Properties.DOT_COLOR));
        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(Properties.DATE_FORMAT_ALL);
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat(Properties.DATE_FORMAT);
            return fmtOut.format(date);
        } catch (ParseException e) {
            System.out.println( Properties.K_WARNING + " " + e.getMessage());
        }
        return "";
    }
}
