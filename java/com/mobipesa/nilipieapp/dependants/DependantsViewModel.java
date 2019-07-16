package com.mobipesa.nilipieapp.dependants;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.models.DependantItem;

import java.util.HashMap;
import java.util.List;


public class DependantsViewModel extends AndroidViewModel {
    private ProjectRepository repo;
    private MediatorLiveData<List<DependantItem>> list_of_dependants;

    public DependantsViewModel(@NonNull Application application) {
        super(application);
        repo = ((App) application).getRepository();

        list_of_dependants = new MediatorLiveData<>();

    }

    public LiveData<List<DependantItem>> getListOfDependants(HashMap<String, Object> map) {
        return repo.listDependants(map);

    }

    public LiveData<List<DependantItem>> getListOfDependants() {
        return list_of_dependants;

    }
}