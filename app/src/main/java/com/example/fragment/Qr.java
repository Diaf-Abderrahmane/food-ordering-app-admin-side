package com.example.fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Qr {
    private String Id;
    private int Price;
    private int Status;
    private String UserId;
    private ArrayList<qrOption> Options;

    static class qrOption extends Option {
        private int Quantity;

        public qrOption(String id, String name, int price, String imgName, String description, int quantity) {
            super(id, name, price, imgName, description);
            Quantity = quantity;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int quantity) {
            Quantity = quantity;
        }
    }

    static class qrCategory extends Category {
        public ArrayList<qrOption> AllqrOptions;

        public qrCategory(String id, String name, int index) {
            super(id, name, index);
        }

        public ArrayList<qrOption> getAllqrOptions() {
            return AllqrOptions;
        }

        public void setAllqrOptions(ArrayList<qrOption> allqrOptions) {
            AllqrOptions = allqrOptions;
        }
    }

    interface QrCode {
        void isQrChanged(int status);
    }

    interface AddQrCode {
        void QrCodeAdded(String id);
    }

    public Qr() {
    }

    public Qr(String id, int price, int status) {
        Id = id;
        Price = price;
        Status = status;
    }

    public Qr(int price, int status, ArrayList<qrOption> options) {
        Price = price;
        Status = status;
        Options = options;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public ArrayList<qrOption> getOptions() {
        return Options;
    }

    public void setOptions(ArrayList<qrOption> options) {
        Options = options;
    }

    public static void AddQr(Qr qr, AddQrCode addQrCode) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("QrCode").push();
        qr.setId(ref.getKey());
        ref.setValue(qr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) addQrCode.QrCodeAdded(ref.getKey());
            }
        });
    }

    public static void isQrChanged(String id, QrCode qrCode) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("QrCode").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                qrCode.isQrChanged(snapshot.child("status").getValue(int.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
