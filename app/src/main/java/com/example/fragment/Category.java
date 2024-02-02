package com.example.fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Category {
    private String Id;
    private String Name;
    private int Index;
    private ArrayList<Option> AllOptions;

    interface CategoriesStatus {
        void isLoaded(ArrayList<Category> allCategories);
    }

    interface CategoriesStatusC {
        void onDataChange();
    }

    public Category() {
    }

    public Category(String name, ArrayList<Option> allOptions) {
        Name = name;
        AllOptions = allOptions;
    }

    public Category(String name, int index, ArrayList<Option> allOptions) {
        Name = name;
        Index = index;
        AllOptions = allOptions;
    }

    public Category(String id, String name, int index, ArrayList<Option> allOptions) {
        Id = id;
        Name = name;
        Index = index;
        AllOptions = allOptions;
    }

    public Category(String id, String name, int index) {
        Id = id;
        Name = name;
        Index = index;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public ArrayList<Option> getAllOptions() {
        return AllOptions;
    }

    public void setAllOptions(ArrayList<Option> allOptions) {
        AllOptions = allOptions;
    }

    public static void ReadCategories(final CategoriesStatus categoriesStatus) {
        final ArrayList<Category> AllCategories = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Menu");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    AllCategories.clear();

                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        String name = ds.child("name").getValue(String.class);
                        int index = ds.child("index").getValue(int.class);

                        if (ds.child("All").exists()) {
                            int finalIndex = index;
                            Option.ReadOptions(ds.getKey(), new Option.OptionsStatus() {
                                @Override
                                public void isLoaded(ArrayList<Option> AllOptions) {
                                    AllCategories.add(new Category(ds.getKey(), name, finalIndex, AllOptions));
                                    if (task.getResult().getChildrenCount() == AllCategories.size())
                                        categoriesStatus.isLoaded(AllCategories);
                                }
                            });
                        } else {
                            AllCategories.add(new Category(ds.getKey(), name, index, new ArrayList<>()));
                            if (task.getResult().getChildrenCount() == AllCategories.size())
                                categoriesStatus.isLoaded(AllCategories);
                        }
                    }
                    if (!task.getResult().hasChildren()) categoriesStatus.isLoaded(AllCategories);
                }
            }
        });
    }

    public static void AddCategory(Category category, CategoriesStatusC categoriesStatusC) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Menu").push();
        category.setId(ref.getKey());
        ref.setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) categoriesStatusC.onDataChange();
            }
        });
    }

    public static void EditCategoryName(String id, String name, CategoriesStatusC categoriesStatusC) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Menu").child(id).child("name");
        ref.setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) categoriesStatusC.onDataChange();
            }
        });
    }

    public static void EditIndex(String id, int index, CategoriesStatusC categoriesStatusC) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Menu").child(id).child("index");
        ref.setValue(index).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) categoriesStatusC.onDataChange();
            }
        });
    }

    public static void DeleteCategory(ArrayList<Category> allCategories, int index, CategoriesStatusC categoriesStatusC) {
        final int[] k = {0};
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Menu").child(allCategories.get(index).getId());
        if (allCategories.get(index).getAllOptions().size() > 0)
            for (int i = 0; i < allCategories.get(index).getAllOptions().size(); i++)
                Option.DeleteOption(allCategories.get(index).getId(), allCategories.get(index).getAllOptions().get(i), new Option.OptionsStatusC() {
                    @Override
                    public void onDataChange() {
                        k[0]++;
                        if (k[0] == allCategories.get(index).getAllOptions().size())
                            ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    k[0] = index + 1;
                                    if (index + 1 == allCategories.size())
                                        categoriesStatusC.onDataChange();
                                    else for (int i = index + 1; i < allCategories.size(); i++) {
                                        allCategories.get(i).setIndex(i - 1);
                                        EditIndex(allCategories.get(i).getId(), i - 1, new CategoriesStatusC() {
                                            @Override
                                            public void onDataChange() {
                                                if (k[0] == allCategories.size() - 1)
                                                    categoriesStatusC.onDataChange();
                                                k[0]++;
                                            }
                                        });
                                    }
                                }
                            });
                    }
                });
        else
            ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    k[0] = index + 1;
                    if (index + 1 == allCategories.size()) categoriesStatusC.onDataChange();
                    else for (int i = index + 1; i < allCategories.size(); i++) {
                        allCategories.get(i).setIndex(i - 1);
                        EditIndex(allCategories.get(i).getId(), i - 1, new CategoriesStatusC() {
                            @Override
                            public void onDataChange() {
                                if (k[0] == allCategories.size() - 1)
                                    categoriesStatusC.onDataChange();
                                k[0]++;
                            }
                        });
                    }
                }
            });
    }

    public static ArrayList<Category> OrderCategories(ArrayList<Category> allCategories) {
        Category category;
        for (int i = 0; i < allCategories.size() - 1; i++)
            for (int j = i + 1; j < allCategories.size(); j++)
                if (allCategories.get(i).getIndex() >= allCategories.get(j).getIndex()) {
                    category = allCategories.get(i);
                    allCategories.set(i, allCategories.get(j));
                    allCategories.set(j, category);
                }
        return allCategories;
    }

    public static void Moved(ArrayList<Category> allCategories, int position, boolean up) {
        int m = 1;
        if (up) m = -1;

        Category category = allCategories.get(position);
        allCategories.set(position, allCategories.get(position + m));
        allCategories.set(position + m, category);

        int index = allCategories.get(position).getIndex();
        allCategories.get(position).setIndex(allCategories.get(position + m).getIndex());
        allCategories.get(position + m).setIndex(index);

        EditIndex(allCategories.get(position).getId(), position, new CategoriesStatusC() {
            @Override
            public void onDataChange() {

            }
        });
        EditIndex(allCategories.get(position + m).getId(), position + m, new CategoriesStatusC() {
            @Override
            public void onDataChange() {

            }
        });
    }
}
