package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class Menu extends Fragment {

    ArrayList<Category> AllCategories;
    CustomAdapter adapter;
    CustomAdapter adapter2;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    ProgressBar progressBar;

    LinearLayout VMenu;
    AlertDialog.Builder alertDialog;
    Dialog dialog;
    View view;

    Bitmap bitmap;
    ImageView DialogOptionImage;
    ImageView DialogIImage;
    ImageView DialogIUpload;
    ImageView DialogIClear;

    interface PopUpOptionC{
        void btnClicked(Option option);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_menu, container, false);

        progressBar=view.findViewById(R.id.progressBar);
        VMenu=view.findViewById(R.id.VMenu);


        recyclerView = view.findViewById(R.id.Menu);
        recyclerView2 = view.findViewById(R.id.AllCategories);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        final Boolean[] c = {false};
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected && !c[0]) {
                    Refresh();
                    c[0] =true;
                } else {
                    /*
                    progressBar.setVisibility(View.VISIBLE);
                    VMenu.setVisibility(View.INVISIBLE);
                    */
                    c[0]=false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ImageView btn=view.findViewById(R.id.AddNC);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpCategory(-1);
            }
        });



        return view;
    }
    public void Refresh(){
        Category.ReadCategories(new Category.CategoriesStatus() {
            @Override
            public void isLoaded(ArrayList<Category> allCategories) {
                AllCategories = Category.OrderCategories(allCategories);
                adapter = new CustomAdapter();
                adapter2 = new CustomAdapter(2);
                recyclerView.setAdapter(adapter);
                recyclerView2.setAdapter(adapter2);
                progressBar.setVisibility(View.INVISIBLE);
                VMenu.setVisibility(View.VISIBLE);
            }
        });
    }

    public void PopUpCategory(int categoryIndex){
        alertDialog=new AlertDialog.Builder(getActivity());
        view=getLayoutInflater().inflate(R.layout.alert_dialog_category,null);
        TextView DialogTitle=view.findViewById(R.id.dialog_title);
        EditText DialogCategoryName=view.findViewById(R.id.dialog_category_name);
        Button DialogBtn=view.findViewById(R.id.dialog_btn);
        if(categoryIndex!=-1){
            DialogTitle.setText("Edit category name");
            DialogBtn.setText("Edit category name");
            DialogCategoryName.setText(AllCategories.get(categoryIndex).getName());
        }
        alertDialog.setView(view);
        dialog=alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        DialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CategoryName=DialogCategoryName.getText().toString();
                if(! CategoryName.isEmpty()){
                    dialog.dismiss();
                    if(categoryIndex!=-1) Category.EditCategoryName(AllCategories.get(categoryIndex).getId(), CategoryName, new Category.CategoriesStatusC() {
                        @Override
                        public void onDataChange() {
                            AllCategories.get(categoryIndex).setName(CategoryName);
                            adapter.notifyItemChanged(categoryIndex);
                            adapter2.notifyItemChanged(categoryIndex);
                        }
                    });
                    else{
                        Category category=new Category(CategoryName, AllCategories.size(),new ArrayList<Option>());
                        Category.AddCategory(category, new Category.CategoriesStatusC() {
                            @Override
                            public void onDataChange() {
                                AllCategories.add(category);
                                adapter.notifyItemInserted(AllCategories.size());
                                adapter.notifyItemRangeChanged(AllCategories.size()-1, 1);
                                adapter2.notifyItemInserted(AllCategories.size());
                                adapter2.notifyItemRangeChanged(AllCategories.size()-1, 1);
                            }
                        });
                    }
                }
            }
        });
    }

    public void PopUpOption(int categoryIndex, int position, Drawable OptionImg, PopUpOptionC popUpOptionC){
        alertDialog=new AlertDialog.Builder(getActivity());
        view=getLayoutInflater().inflate(R.layout.alert_dialog_option,null);

        TextView DialogTitle=view.findViewById(R.id.dialog_title);
        EditText DialogOptionName=view.findViewById(R.id.dialog_option_name);
        EditText DialogOptionPrice=view.findViewById(R.id.dialog_option_price);
        EditText DialogOptionDescription=view.findViewById(R.id.dialog_option_description);
        DialogOptionImage=view.findViewById(R.id.dialog_option_image);
        DialogIImage=view.findViewById(R.id.dialog_icon_image);
        DialogIUpload=view.findViewById(R.id.dialog_icon_upload);
        DialogIClear=view.findViewById(R.id.dialog_icon_clear);
        ProgressBar DialogProgressbar=view.findViewById(R.id.dialog_progressbar);

        Button DialogBtn=view.findViewById(R.id.dialog_btn);


        Option option=new Option();
        if(position!=-1){
            DialogTitle.setText("Edit Option");
            DialogBtn.setText("Edit Option");
            DialogOptionName.setText(AllCategories.get(categoryIndex).getAllOptions().get(position).getName());
            DialogOptionPrice.setText(String.valueOf(AllCategories.get(categoryIndex).getAllOptions().get(position).getPrice()));
            DialogOptionDescription.setText(AllCategories.get(categoryIndex).getAllOptions().get(position).getDescription());
            DialogOptionImage.setImageDrawable(OptionImg);
            DialogIImage.setVisibility(View.INVISIBLE);
            DialogIClear.setVisibility(View.VISIBLE);
            option.setImgName(AllCategories.get(categoryIndex).getAllOptions().get(position).getImgName());
        }else option.setImgName("default.jpg");

        alertDialog.setView(view);
        dialog=alertDialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DialogOptionName.getText().toString().isEmpty() && !DialogOptionPrice.getText().toString().isEmpty() && !DialogOptionDescription.getText().toString().isEmpty()){
                    dialog.dismiss();
                    option.setName(DialogOptionName.getText().toString());
                    option.setPrice(Integer.parseInt(DialogOptionPrice.getText().toString()));
                    option.setDescription(DialogOptionDescription.getText().toString());

                    if(position!=-1){
                        option.setId(AllCategories.get(categoryIndex).getAllOptions().get(position).getId());
                        Option.EditOption(AllCategories.get(categoryIndex).getId(), option, new Option.OptionsStatusC() {
                            @Override
                            public void onDataChange() {
                                popUpOptionC.btnClicked(option);
                            }
                        });
                    }
                    else {
                        Option.AddOption(AllCategories.get(categoryIndex).getId(), option, new Option.OptionsStatusC() {
                            @Override
                            public void onDataChange() {
                                popUpOptionC.btnClicked(option);
                            }
                        });
                    }
                }
            }
        });

        DialogIImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        DialogIUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogIUpload.setVisibility(View.INVISIBLE);
                DialogProgressbar.setVisibility(View.VISIBLE);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                Random random=new Random();
                int r0=random.nextInt(20);
                int r1=random.nextInt(20)+20;
                int r2=random.nextInt(20)+30;

                String OptionImgName="img_"+ categoryIndex +r0+r1+r2+ ".jpg";
                option.setImgName(OptionImgName);


                String imgPath="Menu/"+OptionImgName;
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(imgPath).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        DialogIUpload.setVisibility(View.VISIBLE);
                        DialogProgressbar.setVisibility(View.INVISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DialogProgressbar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        DialogIClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogIClear.setVisibility(View.INVISIBLE);
                DialogOptionImage.setImageDrawable(getActivity().getDrawable(R.drawable.bg));
                DialogIImage.setVisibility(View.VISIBLE);
                DialogIUpload.setVisibility(View.INVISIBLE);
                Option.DeleteOptionImg(option.getImgName());
                option.setImgName("default.jpg");
            }
        });
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK && reqCode == 1) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap b = BitmapFactory.decodeStream(imageStream);
                bitmap=Bitmap.createScaledBitmap(b, 392, 248, false);

                DialogOptionImage.setImageBitmap(bitmap);
                DialogIImage.setVisibility(View.INVISIBLE);
                DialogIUpload.setVisibility(View.VISIBLE);
                DialogIClear.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }




    public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int ViewType=0;
        private int CategoryIndex;

        public class ViewHolder0 extends RecyclerView.ViewHolder {
            private final TextView CategoryName;
            private final RecyclerView CategoryOptions;
            private androidx.appcompat.widget.Toolbar toolbar;

            public ViewHolder0(View view) {
                super(view);

                CategoryName = view.findViewById(R.id.CategoryName);
                CategoryOptions = view.findViewById(R.id.CategoryOptions);
                toolbar = view.findViewById(R.id.ToolBar);
            }

            public RecyclerView getCategoryOptions() { return CategoryOptions;    }
            public Toolbar getToolbar() { return toolbar;   }
            public TextView getCategoryName() {  return CategoryName;  }
        }

        public class ViewHolder1 extends RecyclerView.ViewHolder {
            private final TextView OptionName;
            private final TextView OptionDescription;
            private final TextView OptionPrice;
            private final ImageView OptionImg;
            private androidx.appcompat.widget.Toolbar toolbar;

            public ViewHolder1(View view) {
                super(view);

                OptionName = view.findViewById(R.id.OptionName);
                OptionDescription = view.findViewById(R.id.OptionDescription);
                OptionPrice = view.findViewById(R.id.OptionPrice);
                OptionImg = view.findViewById(R.id.OptionImg);
                toolbar = view.findViewById(R.id.ToolBar);

            }

            public TextView getOptionName() { return OptionName;  }
            public TextView getOptionDescription() { return OptionDescription;  }
            public TextView getOptionPrice() { return OptionPrice;  }
            public ImageView getOptionImg() { return OptionImg;  }
            public Toolbar getToolbar() { return toolbar;   }
        }


        public class ViewHolder2 extends RecyclerView.ViewHolder {
            private final TextView CategoryName;

            public ViewHolder2(View view) {
                super(view);

                CategoryName = view.findViewById(R.id.CategoryName);
            }

            public TextView getCategoryName() {
                return CategoryName;
            }
        }

        public CustomAdapter() {
        }
        public CustomAdapter(int viewType) {
            ViewType=viewType;
        }
        public CustomAdapter(int viewType, int categoryIndex) {
            ViewType=viewType;
            CategoryIndex=categoryIndex;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category, parent, false);
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_option, parent, false);
            View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_category, parent, false);

            switch (ViewType){
                case 1:
                    return new ViewHolder1(view1);
                case 2:
                    return new ViewHolder2(view2);
                default:
                    return new ViewHolder0(view0);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder Holder, final int position) {
            switch (ViewType) {
                case 1:
                    ViewHolder1 viewHolder1=(ViewHolder1) Holder;
                    viewHolder1.getOptionName().setText(AllCategories.get(CategoryIndex).getAllOptions().get(position).getName());
                    viewHolder1.getOptionDescription().setText(AllCategories.get(CategoryIndex).getAllOptions().get(position).getDescription());
                    String price = AllCategories.get(CategoryIndex).getAllOptions().get(position).getPrice() +" DZD";
                    viewHolder1.getOptionPrice().setText(price);
                    Option.getImg(AllCategories.get(CategoryIndex).getAllOptions().get(position).getImgName(), new Option.ImgStatus() {
                        @Override
                        public void isLoaded(Bitmap img) {
                            if(img!=null)viewHolder1.getOptionImg().setImageBitmap(img);
                            else Option.getImg("default.jpg", new Option.ImgStatus() {
                                @Override
                                public void isLoaded(Bitmap img) {
                                    viewHolder1.getOptionImg().setImageBitmap(img);
                                }
                            });
                        }
                    });


                    viewHolder1.getToolbar().getMenu().clear();
                    viewHolder1.getToolbar().inflateMenu(R.menu.toolbar_option);
                    viewHolder1.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.Edit:
                                    PopUpOption(CategoryIndex, position,viewHolder1.getOptionImg().getDrawable(), new PopUpOptionC() {
                                        @Override
                                        public void btnClicked(Option option) {
                                            AllCategories.get(CategoryIndex).getAllOptions().set(position,option);
                                            notifyItemChanged(position);
                                        }
                                    });
                                    break;
                                case R.id.Delete:
                                    Option.DeleteOption(AllCategories.get(CategoryIndex).getId(), AllCategories.get(CategoryIndex).getAllOptions().get(position), new Option.OptionsStatusC() {
                                        @Override
                                        public void onDataChange() {
                                            AllCategories.get(CategoryIndex).getAllOptions().remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, AllCategories.get(CategoryIndex).getAllOptions().size()-position);
                                        }
                                    });
                                    break;
                            }
                            return false;
                        }
                    });


                    break;
                case 2:
                    ViewHolder2 viewHolder2=(ViewHolder2) Holder;
                    viewHolder2.getCategoryName().setText(AllCategories.get(position).getName());
                    viewHolder2.getCategoryName().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), position);
                        }
                    });
                    break;

                default:
                    ViewHolder0 viewHolder0=(ViewHolder0) Holder;
                    viewHolder0.getCategoryName().setText(AllCategories.get(position).getName());

                    viewHolder0.getToolbar().getMenu().clear();
                    viewHolder0.getToolbar().inflateMenu(R.menu.toolbar_category);

                    viewHolder0.getCategoryOptions().setLayoutManager(new LinearLayoutManager(getActivity()));
                    CustomAdapter adapterO = new CustomAdapter(1,position);
                    viewHolder0.getCategoryOptions().setAdapter(adapterO);


                    viewHolder0.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.Edit:
                                    PopUpCategory(position);
                                    break;
                                case R.id.Add_New_Option:
                                    PopUpOption(position, -1,null, new PopUpOptionC() {
                                        @Override
                                        public void btnClicked(Option option) {
                                            AllCategories.get(position).getAllOptions().add(option);
                                            adapterO.notifyItemInserted(AllCategories.get(position).getAllOptions().size());
                                        }
                                    });
                                    break;
                                case R.id.Delete:
                                    Category.DeleteCategory(AllCategories, position, new Category.CategoriesStatusC() {
                                        @Override
                                        public void onDataChange() {
                                            AllCategories.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, AllCategories.size()-position);
                                            adapter2.notifyItemRemoved(position);
                                            adapter2.notifyItemRangeChanged(position, AllCategories.size()-position);
                                        }
                                    });
                                    break;
                                case R.id.up:
                                    if(position!=0){
                                        notifyItemMoved(position,position-1);
                                        Category.Moved(AllCategories,position,true);
                                        notifyItemRangeChanged(position-1, 2);
                                        adapter2.notifyItemRangeChanged(position-1, 2);
                                    }
                                    break;
                                case R.id.down:
                                    if(position!=AllCategories.size()-1){
                                        notifyItemMoved(position,position+1);
                                        Category.Moved(AllCategories,position,false);
                                        notifyItemRangeChanged(position, 2);
                                        adapter2.notifyItemRangeChanged(position, 2);
                                    }
                                    break;

                            }
                            return false;
                        }
                    });
                    break;
            }

        }

        @Override
        public int getItemCount() {
            switch (ViewType){
                case 1:
                    return AllCategories.get(CategoryIndex).getAllOptions().size();
                default:
                    return AllCategories.size();
            }
        }
    }

}