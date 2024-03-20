package com.affixstudio.whatsapptool.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.databinding.ContactListRowBinding;
import com.affixstudio.whatsapptool.databinding.CustomContactListRowBinding;
import com.affixstudio.whatsapptool.model.data.ContactHolder;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.model.utils.ContactsHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<ContactHolder> contactHolderArrayList;
    private Set<String> contactArrayCheckpoint = new HashSet<>();
    private final Context mContext;

    public static final int ITEM_TYPE_CONTACT = 0;
    public static final int ITEM_TYPE_CUSTOM = 1;

    public ContactListAdapter(Context context, ArrayList<ContactHolder> contactHolderArrayList) {
        this.contactHolderArrayList = contactHolderArrayList;
        mContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTACT) {
            ContactListRowBinding binding = ContactListRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        } else {
            CustomContactListRowBinding binding = CustomContactListRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CustomHolder(binding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return contactHolderArrayList.get(position).isCustom() ? ITEM_TYPE_CUSTOM : ITEM_TYPE_CONTACT;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_CONTACT) {
            ViewHolder holder = (ViewHolder) viewHolder;

            ContactListRowBinding binding = holder.getBinding();
            binding.contactCheckbox.setChecked(contactHolderArrayList.get(position).isChecked());
            binding.contactCheckbox.setText(contactHolderArrayList.get(position).getContactName());
            binding.contactCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                contactHolderArrayList.get(position).setChecked(isChecked);
                saveSelectedContactList();
            });
        } else {
            CustomHolder holder = (CustomHolder) viewHolder;

            CustomContactListRowBinding binding = holder.getBinding();
            binding.contactName.setText(contactHolderArrayList.get(position).getContactName());
            binding.deleteButton.setOnClickListener((v -> {
                contactHolderArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contactHolderArrayList.size());
                saveSelectedContactList();
            }));
        }
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getClass() == ViewHolder.class) {
            ViewHolder holder = (ViewHolder) viewHolder;
            ContactListRowBinding binding = holder.getBinding();
            binding.contactCheckbox.setOnCheckedChangeListener(null);
            super.onViewRecycled(holder);
        }
    }

    public void saveSelectedContactList() {
        Set<String> selectedContacts = new HashSet<>();
        Set<String> customContacts = new HashSet<>();
        for (ContactHolder contact : contactHolderArrayList) {
            if (contact.isCustom())
                customContacts.add(contact.getContactName());
            else if (contact.isChecked())
                selectedContacts.add(contact.getContactName());
        }
        PreferencesManager prefs = PreferencesManager.getPreferencesInstance(mContext);
        if (ContactsHelper.getInstance(mContext).hasContactPermission())
            prefs.setReplyToNames(selectedContacts);
        prefs.setCustomReplyNames(customContacts);
    }

    public void createCheckpoint() {
        contactArrayCheckpoint = new HashSet<>();
        for (ContactHolder contact : contactHolderArrayList) {
            if (contact.isChecked()) contactArrayCheckpoint.add(contact.getContactName());
        }
    }

    public void restoreCheckpoint() {
        for (int position = 0; position < contactHolderArrayList.size(); position++) {
            ContactHolder contact = contactHolderArrayList.get(position);
            boolean checked = contactArrayCheckpoint.contains(contact.getContactName());
            if (contact.isChecked() != checked) {
                contact.setChecked(checked);
                notifyItemChanged(position);
            }
        }
    }

    public void addCustomName(String name) {
        contactHolderArrayList.add(0, new ContactHolder(name, true, true));
        notifyItemInserted(0);
        notifyItemRangeChanged(1, contactHolderArrayList.size());
        saveSelectedContactList();
    }

    @Override
    public int getItemCount() {
        return contactHolderArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ContactListRowBinding binding;

        public ViewHolder(@NonNull @NotNull ContactListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ContactListRowBinding getBinding() {
            return binding;
        }
    }

    static class CustomHolder extends RecyclerView.ViewHolder {
        private final CustomContactListRowBinding binding;

        public CustomHolder(@NonNull @NotNull CustomContactListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public CustomContactListRowBinding getBinding() {
            return binding;
        }
    }

}
