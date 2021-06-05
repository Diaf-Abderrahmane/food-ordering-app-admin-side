package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;


public class Qr_Generator extends Fragment {

    ArrayList<Qr.qrCategory> AllCategories;
    CustomAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout QrMenuL;
    LinearLayout VQR;
    LinearLayout VMenu;

    TextView Total;
    TextView Qrstatus;

    RadioGroup RGroup;
    com.google.android.material.radiobutton.MaterialRadioButton PWP;
    com.google.android.material.radiobutton.MaterialRadioButton EP;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_qr__generator, container, false);
        progressBar=view.findViewById(R.id.progressBar);
        QrMenuL=view.findViewById(R.id.QrMenuL);
        VMenu=view.findViewById(R.id.VMenu);
        VQR=view.findViewById(R.id.VQR);

        Qrstatus=view.findViewById(R.id.QR_status);

        recyclerView = view.findViewById(R.id.QRMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Total=view.findViewById(R.id.total);
        RGroup=view.findViewById(R.id.RGroup);
        PWP=view.findViewById(R.id.PWP);
        EP=view.findViewById(R.id.EP);

        Button QRbtn=view.findViewById(R.id.QR_btn);
        QRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PWP.isChecked() ||EP.isChecked()) {
                    ArrayList<Qr.qrOption> os = new ArrayList<>();
                    for (int i = 0; i < AllCategories.size(); i++)
                        for (int j = 0; j < AllCategories.get(i).getAllqrOptions().size(); j++)
                            if (AllCategories.get(i).getAllqrOptions().get(j).getQuantity() > 0) {
                                os.add(AllCategories.get(i).getAllqrOptions().get(j));
                            }
                    if (os.size() > 0) {
                        int QrStatus = 0;
                        if (PWP.isChecked()) QrStatus = 1;
                        Qr.AddQr(new Qr(r(), QrStatus, os), new Qr.AddQrCode() {
                            @Override
                            public void QrCodeAdded(String id) {
                                VQR.setVisibility(View.VISIBLE);
                                VMenu.setVisibility(View.GONE);

                                RGroup.clearCheck();


                                Total.setText("Total: 0 Dzd");

                                if (id.equals("")) id = "0";
                                ImageView img = view.findViewById(R.id.QR_img);
                                try {
                                    img.setImageBitmap(encodeAsBitmap(id));
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }

                                Qr.isQrChanged(id, new Qr.QrCode() {
                                    @Override
                                    public void isQrChanged(int status) {
                                        if (status == 0 || status == 1) {
                                            Qrstatus.setText("Waiting...");
                                        } else if (status == 2) {
                                            Qrstatus.setText("Points added successfully");
                                        } else if (status == 3) {
                                            Qrstatus.setText("Payment was successful");
                                        } else if (status == 4) {
                                            Qrstatus.setText("Not enough points");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
        Button GNQR=view.findViewById(R.id.GNQR);
        GNQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VQR.setVisibility(View.GONE);
                VMenu.setVisibility(View.VISIBLE);
                Refresh();
            }
        });


        Refresh();
        final Boolean[] c = {true};
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected && !c[0]) {
                    Refresh();
                    c[0] =true;
                } else {
                    c[0]=false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        return view;
    }
    public int r(){
        int T=0;

        for (int i=0;i<AllCategories.size();i++)
            for (int j=0;j<AllCategories.get(i).getAllqrOptions().size();j++)
                if(AllCategories.get(i).getAllqrOptions().get(j).getQuantity()>0) T+=AllCategories.get(i).getAllqrOptions().get(j).getQuantity()*AllCategories.get(i).getAllqrOptions().get(j).getPrice();
        Total.setText("Total: "+String.valueOf(T)+" Dzd");

        return T;
    }
    public void Refresh(){
        progressBar.setVisibility(View.VISIBLE);
        QrMenuL.setVisibility(View.INVISIBLE);

        Category.ReadCategories(new Category.CategoriesStatus() {
            @Override
            public void isLoaded(ArrayList<Category> allCategories) {
                ArrayList<Category> all=Category.OrderCategories(allCategories);
                AllCategories=new ArrayList<>();
                ArrayList<Qr.qrOption> allO;
                Option o;
                for (int i=0;i<all.size();i++){
                    AllCategories.add(new Qr.qrCategory(all.get(i).getId(),all.get(i).getName(),all.get(i).getIndex()));
                    allO=new ArrayList<>();
                    for (int j=0;j<all.get(i).getAllOptions().size();j++){
                        o=all.get(i).getAllOptions().get(j);
                        allO.add(new Qr.qrOption(o.getId(),o.getName(),o.getPrice(),o.getImgName(),o.getDescription(),0));
                    }
                    AllCategories.get(i).setAllqrOptions(allO);
                }


                adapter = new CustomAdapter();
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.INVISIBLE);
                QrMenuL.setVisibility(View.VISIBLE);
            }
        });
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 512, 512, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 512, 0, 0, w, h);
        return bitmap;
    }


    public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int ViewType=0;
        private int CategoryIndex;

        public class ViewHolder0 extends RecyclerView.ViewHolder {
            private final LinearLayout vCategory;
            private final TextView CategoryName;
            private final RecyclerView CategoryOptions;
            private final ImageButton CategoryShowOptions;

            public ViewHolder0(View view) {
                super(view);

                CategoryName = view.findViewById(R.id.CategoryName);
                CategoryOptions = view.findViewById(R.id.CategoryOptions);
                CategoryShowOptions=view.findViewById(R.id.CategoryShowOptions);
                vCategory=view.findViewById(R.id.vCategory);
            }

            public RecyclerView getCategoryOptions() { return CategoryOptions;    }
            public TextView getCategoryName() {  return CategoryName;  }
            public ImageButton getCategoryShowOptions() {  return CategoryShowOptions;  }
            public LinearLayout getvCategory() {  return vCategory;  }
        }

        public class ViewHolder1 extends RecyclerView.ViewHolder {
            private final TextView OptionName;
            private final TextView OptionPrice;
            private final TextView OptionQuantity;
            private final ImageButton OptionQuantityAdd;
            private final ImageButton OptionQuantityRemove;

            public ViewHolder1(View view) {
                super(view);

                OptionName = view.findViewById(R.id.OptionName);
                OptionPrice = view.findViewById(R.id.OptionPrice);
                OptionQuantity = view.findViewById(R.id.OptionQuantity);
                OptionQuantityAdd = view.findViewById(R.id.OptionQuantityAdd);
                OptionQuantityRemove = view.findViewById(R.id.OptionQuantityRemove);

            }

            public TextView getOptionName() { return OptionName;  }
            public TextView getOptionPrice() { return OptionPrice;  }
            public TextView getOptionQuantity() { return OptionQuantity;  }
            public ImageButton getOptionQuantityAdd() { return OptionQuantityAdd;  }
            public ImageButton getOptionQuantityRemove() { return OptionQuantityRemove;  }
        }


        public CustomAdapter() {
        }
        public CustomAdapter(int viewType, int categoryIndex) {
            ViewType=viewType;
            CategoryIndex=categoryIndex;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category_, parent, false);
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_option_, parent, false);

            switch (ViewType){
                case 1:
                    return new CustomAdapter.ViewHolder1(view1);
                default:
                    return new CustomAdapter.ViewHolder0(view0);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder Holder, final int position) {
            switch (ViewType) {
                case 1:
                    CustomAdapter.ViewHolder1 viewHolder1=(CustomAdapter.ViewHolder1) Holder;
                    viewHolder1.getOptionName().setText(AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getName());
                    String price = AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getPrice() +" DZD";
                    viewHolder1.getOptionPrice().setText(price);

                    viewHolder1.getOptionQuantityAdd().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllCategories.get(CategoryIndex).getAllqrOptions().get(position).setQuantity(AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getQuantity()+1);
                            viewHolder1.getOptionQuantity().setText(String.valueOf(AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getQuantity()));
                            r();
                        }
                    });
                    viewHolder1.getOptionQuantityRemove().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getQuantity()>0) {
                                AllCategories.get(CategoryIndex).getAllqrOptions().get(position).setQuantity(AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getQuantity()-1);
                                viewHolder1.getOptionQuantity().setText(String.valueOf(AllCategories.get(CategoryIndex).getAllqrOptions().get(position).getQuantity()));
                                r();
                            }
                        }
                    });

                    break;
                default:
                    CustomAdapter.ViewHolder0 viewHolder0=(CustomAdapter.ViewHolder0) Holder;
                    viewHolder0.getCategoryName().setText(AllCategories.get(position).getName());
                    viewHolder0.getCategoryShowOptions().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AutoTransition autoTransition = new AutoTransition();
                            autoTransition.setDuration(10);
                            TransitionManager.beginDelayedTransition(viewHolder0.getvCategory(),autoTransition);

                            if(viewHolder0.getCategoryOptions().getVisibility()==View.GONE){
                                viewHolder0.getCategoryOptions().setVisibility(View.VISIBLE);
                                viewHolder0.getCategoryShowOptions().setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up));
                            }
                            else{
                                viewHolder0.getCategoryOptions().setVisibility(View.GONE);
                                viewHolder0.getCategoryShowOptions().setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down));
                            }
                        }
                    });

                    viewHolder0.getCategoryOptions().setLayoutManager(new LinearLayoutManager(getActivity()));
                    CustomAdapter adapterO = new CustomAdapter(1,position);
                    viewHolder0.getCategoryOptions().setAdapter(adapterO);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            switch (ViewType){
                case 1:
                    return AllCategories.get(CategoryIndex).getAllqrOptions().size();
                default:
                    return AllCategories.size();
            }
        }
    }
}