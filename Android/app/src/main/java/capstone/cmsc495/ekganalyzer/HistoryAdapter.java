package capstone.cmsc495.ekganalyzer;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<String> values;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHistory;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHistory = v.findViewById(R.id.history_line);
        }
    }

    public void add(int position, String item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(List<String> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Get history item from dataset at this position
        // Replace the contents of the view with that element
        final String name = values.get(position);
        final ViewHolder holderFinal = holder; // To allow for getContext() later (requires final)

        holder.txtHistory.setText(name);
        holder.txtHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context = holderFinal.itemView.getContext();
                Intent intent = new Intent(context, HistoricEKGActivity.class);

                // Use this extra data to tell HistoricEKGActivity which record to pull
                intent.putExtra("history_date_time", name);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}