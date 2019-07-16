/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobipesa.nilipieapp.dependants;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.databinding.ListItemMyDependantsBinding;
import com.mobipesa.nilipieapp.models.DependantItem;

import java.util.List;
import java.util.Objects;

public class DependantsAdapter extends RecyclerView.Adapter<DependantsAdapter.MydependantsViewHolder> {

    private List<? extends DependantItem> myDependantList;
    //private NotificationClickCallback notificationClickCallback;

    public DependantsAdapter() {
        //this.notificationClickCallback = clickCallback;
    }

    public void setMyDependantsList(final List<? extends DependantItem> newList) {
        if (myDependantList == null) {
            myDependantList = newList;
            notifyItemRangeInserted(0, newList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return myDependantList.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return myDependantList .get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    DependantItem newProduct = newList.get(newItemPosition);
                    DependantItem oldProduct = newList.get(oldItemPosition);
                    return Objects.equals(newProduct.getName(), oldProduct.getName())
                            && Objects.equals(newProduct.getUpperLimit().getClass(), oldProduct.getUpperLimit().getClass())
                            && Objects.equals(newProduct.getLimitBalance().getClass(), oldProduct.getLimitBalance().getClass());
                }
            });
            myDependantList = newList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public MydependantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemMyDependantsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_my_dependants,
                parent, false);
        return new MydependantsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MydependantsViewHolder holder, int position) {
        holder.binding.setDependants(myDependantList.get(position));
        //holder.binding.setCallback(notificationClickCallback);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return myDependantList == null ? 0 : myDependantList.size();
    }

    static class MydependantsViewHolder extends RecyclerView.ViewHolder {

        final ListItemMyDependantsBinding binding;

        private MydependantsViewHolder(ListItemMyDependantsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
