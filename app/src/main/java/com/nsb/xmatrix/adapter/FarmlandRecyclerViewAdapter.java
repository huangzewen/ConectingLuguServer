/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nsb.xmatrix.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsb.xmatrix.R;
import com.nsb.xmatrix.adapter.entity.Farmland;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.util.ArrayList;
import java.util.List;

/**
 * FarmlandRecyclerViewAdapter
 *
 */
public class FarmlandRecyclerViewAdapter extends RecyclerView.Adapter<FarmlandRecyclerViewAdapter.ViewHolder> {

    private List<Farmland> mItems;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public FarmlandRecyclerViewAdapter() {
        mItems = new ArrayList<>();
    }

    public static List<Farmland> generateDatas(int count) {
        List<Farmland> mDatas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mDatas.add(new Farmland("Farm land "+String.valueOf(i)));
        }
        return mDatas;
    }

    public void addItem(int position) {
        if (position > mItems.size()) {
            return;
        }

        mItems.add(position, new Farmland(String.valueOf(position)));
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position >= mItems.size()) {
            return;
        }
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.adapter_item_farmland_recycler_view, parent, false);
        final ViewHolder holder = new ViewHolder(view,this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Farmland data = mItems.get(i);
        viewHolder.setText(data.text);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItemCount(int count) {
        mItems.clear();
        mItems.addAll(generateDatas(count));

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(RecyclerView.ViewHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RoundButton scanButton;
        private FarmlandRecyclerViewAdapter mAdapter;

        public ViewHolder(View itemView, FarmlandRecyclerViewAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAdapter = adapter;
            //mTextView = itemView.findViewById(R.id.textView);
            scanButton= itemView.findViewById(R.id.farmland_scan);
        }

        public void setText(String text) {
            //mTextView.setText(text);
        }

        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }
    }
}
