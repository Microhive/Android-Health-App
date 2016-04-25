package dk.itu.ubicomp.android.allergytracker.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dk.itu.ubicomp.android.allergytracker.Activities.AllergyProductItemFragment.OnListFragmentInteractionListener;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;
import dk.itu.ubicomp.android.allergytracker.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AllergyProduct} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AllergyProductItemRecyclerViewAdapter extends RecyclerView.Adapter<AllergyProductItemRecyclerViewAdapter.ViewHolder> {

    private final List<AllergyProduct> mModels;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public AllergyProductItemRecyclerViewAdapter(Context context, List<AllergyProduct> items, OnListFragmentInteractionListener listener) {
        mModels = items;
        mListener = listener;
    }

    public void setModels(List<AllergyProduct> models) {
        mModels.clear();
        mModels.addAll(models);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_allergyproductitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mModels.get(position);
        holder.mTitleView.setText(mModels.get(position).getTitle());
        holder.mDescriptionView.setText(mModels.get(position).getDescription());
        if (mModels.get(position).getImage() != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mModels.get(position).getImage(), 0, mModels.get(position).getImage().length);
            holder.mImgThumbnail.setImageBitmap(bitmap);
        }
        else
        {
            holder.mImgThumbnail.setImageResource(R.drawable.stockfood_grayscaley);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final ImageView mImgThumbnail;

        public AllergyProduct mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
            mImgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }

    public void animateTo(List<AllergyProduct> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<AllergyProduct> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final AllergyProduct model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<AllergyProduct> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final AllergyProduct model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<AllergyProduct> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final AllergyProduct model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public AllergyProduct removeItem(int position) {
        final AllergyProduct model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, AllergyProduct model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final AllergyProduct model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
