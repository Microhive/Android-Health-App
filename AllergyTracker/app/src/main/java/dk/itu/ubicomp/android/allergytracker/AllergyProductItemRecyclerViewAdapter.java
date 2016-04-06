package dk.itu.ubicomp.android.allergytracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.itu.ubicomp.android.allergytracker.AllergyProductItemFragment.OnListFragmentInteractionListener;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AllergyProduct} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AllergyProductItemRecyclerViewAdapter extends RecyclerView.Adapter<AllergyProductItemRecyclerViewAdapter.ViewHolder> {

    private final List<AllergyProduct> mValues;
    private final OnListFragmentInteractionListener mListener;

    public AllergyProductItemRecyclerViewAdapter(List<AllergyProduct> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_allergyproductitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId().toString());
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mDescriptionView.setText(mValues.get(position).getDescription());
//        holder.mBarcodeView.setText(mValues.get(position).getBarcode());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
//        public final TextView mBarcodeView;

        public AllergyProduct mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
//            mBarcodeView = (TextView) view.findViewById(R.id.barcode);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
