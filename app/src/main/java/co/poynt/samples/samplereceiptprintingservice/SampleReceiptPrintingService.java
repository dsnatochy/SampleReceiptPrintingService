package co.poynt.samples.samplereceiptprintingservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import co.poynt.api.model.Transaction;
import co.poynt.os.model.PrintedReceipt;
import co.poynt.os.services.v1.IPoyntReceiptPrintingService;
import co.poynt.os.services.v1.IPoyntReceiptPrintingServiceListener;

public class SampleReceiptPrintingService extends Service {
    private final String TAG = "ReceiptPrintingS";
    public SampleReceiptPrintingService() {
        Log.d(TAG, "SampleReceiptPrintingService STARTED!!!!!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IPoyntReceiptPrintingService.Stub mBinder = new IPoyntReceiptPrintingService.Stub(){
        public void printTransaction(String var1, Transaction var2, long var3, boolean var5, IPoyntReceiptPrintingServiceListener var6)
                throws RemoteException {
            Log.d(TAG, "printTransaction ");

        }

        public void printTransactionReceipt(String var1, String var2, long var3, IPoyntReceiptPrintingServiceListener var5) throws RemoteException {
            Log.d(TAG, "printTransactionReceipt ");
        }

        public void printOrderReceipt(String var1, String var2, IPoyntReceiptPrintingServiceListener var3) throws RemoteException {
            Log.d(TAG, "printOrderReceipt ");
        }

        public void printReceipt(String var1, PrintedReceipt var2, IPoyntReceiptPrintingServiceListener var3) throws RemoteException {
            Log.d(TAG, "printReceipt ");
        }

        public void printBitmap(String var1, Bitmap var2, IPoyntReceiptPrintingServiceListener var3) throws RemoteException {
            Log.d(TAG, "printBitmap ");
        }
    };
}
