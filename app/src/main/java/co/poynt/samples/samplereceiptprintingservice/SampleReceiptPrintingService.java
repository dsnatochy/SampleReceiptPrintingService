package co.poynt.samples.samplereceiptprintingservice;


import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import co.poynt.api.model.BalanceInquiry;
import co.poynt.api.model.Transaction;
import co.poynt.os.model.Intents;
import co.poynt.os.model.PrintedReceipt;
import co.poynt.os.model.PrintedReceiptLine;
import co.poynt.os.services.v1.IPoyntReceiptPrintingService;
import co.poynt.os.services.v1.IPoyntReceiptPrintingServiceListener;
import co.poynt.os.services.v1.IPoyntReceiptSendListener;

/**
 * @author Dennis Natochy
 * @date 3/6/2017
 *
 * This sample app shows how the default Poynt receipt service (IPoyntReceiptPrintingService)
 * can be overridden such that when the customer taps the receipt button on the second screen
 * *printReceipt* function gets called and a custom receipt can be created
 *
 * Overriding IPoyntReceiptPrintingService has an implication for all the other apps running on
 * the same terminal. So it is your responsibility to be a "good citizen" and implement all of the
 * methods of IPoyntReceiptPrintingService or forward them to the default implementation of the service.
 *
 * Since you are overriding the default receipt it is also your responsibility to make sure that all
 * required fields (e.g. AID, auth code,etc.) are printed. Consult your acquiring partner to make
 * sure your app meets their receipt specification
 *
 * As of 6/6/2016 changing the default receipt printing service requires Poynt to update business
 * settings for the terminal. Contact Poynt and provide the package name of your receipt service.
 */

public class SampleReceiptPrintingService extends Service {

    private static final String TAG = SampleReceiptPrintingService.class.getSimpleName();
    private IPoyntReceiptPrintingService poyntReceiptPrintingService;


    private ServiceConnection receiptPrintingConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "connected to Poynt receipt printing service");
            poyntReceiptPrintingService = IPoyntReceiptPrintingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            poyntReceiptPrintingService = null;
            Log.d(TAG, "onServiceDisconnected: receiptPrintingConnection");
            bindPoyntReceiptPrintingService();
        }
    };


    private final IPoyntReceiptPrintingService.Stub mBinder = new IPoyntReceiptPrintingService.Stub() {
        public void printTransaction(String jobId, Transaction transaction, long
                tipAmount, boolean signatureCollected, IPoyntReceiptPrintingServiceListener callback)
                throws RemoteException {
            Log.d(TAG, "printTransaction");
            // call default
            if (poyntReceiptPrintingService != null){
                poyntReceiptPrintingService.printTransaction(jobId, transaction, tipAmount,
                        signatureCollected, callback);
            }
        }

        public void printTransactionReceipt(String jobId, String transactionId, long
                tipAmount, IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            Log.d(TAG, "printTransactionReceipt");
            // call default
            if (poyntReceiptPrintingService != null){
                poyntReceiptPrintingService.printTransactionReceipt(jobId, transactionId, tipAmount,
                        callback);
            }
        }

        public void printOrderReceipt(String jobId, String orderId,
                                      IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            Log.d(TAG, "printOrderReceipt ");
            // call default
            if (poyntReceiptPrintingService != null){
                poyntReceiptPrintingService.printOrderReceipt(jobId, orderId, callback);
            }
        }

        // This method will be called from the Payment Fragment
        public void printReceipt(final String jobId, final PrintedReceipt receipt,
                                 final IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            //Adding custom footer
            Log.d(TAG, "printReceipt with PrintedReceipt");

            List<PrintedReceiptLine> footerLines = receipt.getFooter();
            PrintedReceiptLine line = new PrintedReceiptLine();
            line.setText("Congrats! You've earned 50");
            PrintedReceiptLine line2 = new PrintedReceiptLine();
            line2.setText("points!");
            PrintedReceiptLine blank = new PrintedReceiptLine();
            blank.setText("");

            // insert custom content at the top of the footer lines
            footerLines.add(0, line);
            footerLines.add(1, line2);
            footerLines.add(2, blank);

            Handler h = new Handler(Looper.getMainLooper());
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        poyntReceiptPrintingService.printReceipt(jobId, receipt, callback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            if (poyntReceiptPrintingService != null){
                Log.d(TAG, "printReceiptPrintingService was not null");
                h.post(r);
            }else{
                Log.d(TAG, "printReceiptPrintingService was null: ");
                h.postDelayed(r, 2000l);
            }
        }

        public void printBitmap(String jobId, Bitmap bitmap,
                                IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            Log.d(TAG, "printBitmap ");
            // call default
            if (poyntReceiptPrintingService != null){
                poyntReceiptPrintingService.printBitmap(jobId, bitmap, callback);
            }
        }

        @Override
        public void printStayReceipt(String s, String s1,
                                     IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            Log.d(TAG, "printStayReceipt: ");
            // call default
            if (poyntReceiptPrintingService != null){
                poyntReceiptPrintingService.printStayReceipt(s, s1, callback);
            }
        }

        @Override
        public void printBalanceInquiry(String s, BalanceInquiry balanceInquiry,
                                        IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            Log.d(TAG, "printBalanceInquiry: ");
            // call default
            if (poyntReceiptPrintingService != null){
                poyntReceiptPrintingService.printBalanceInquiry(s, balanceInquiry, callback);
            }
        }

        @Override
        public void sendReceipt(String orderId, String transactionId, String email, String phoneNumber,
                                IPoyntReceiptSendListener callback) throws RemoteException {
            Log.d(TAG, "sendReceipt: ");
            if (poyntReceiptPrintingService != null) {
                poyntReceiptPrintingService.sendReceipt(orderId, transactionId, email, phoneNumber, callback);
            }else{
                bindPoyntReceiptPrintingService();
            }
        }

        @Override
        public void printTransactionReceiptWithOptions(String s, String s1, long l, Bundle bundle, IPoyntReceiptPrintingServiceListener iPoyntReceiptPrintingServiceListener) throws RemoteException {
            Log.d(TAG, "printTransactionReceiptWithOptions: ");
        }

        @Override
        public void printStayReceiptWithOptions(String s, String s1, Bundle bundle, IPoyntReceiptPrintingServiceListener iPoyntReceiptPrintingServiceListener) throws RemoteException {
            Log.d(TAG, "printStayReceiptWithOptions: ");
        }

        @Override
        public void printBalanceInquiryWithOptions(String s, BalanceInquiry balanceInquiry, Bundle bundle, IPoyntReceiptPrintingServiceListener iPoyntReceiptPrintingServiceListener) throws RemoteException {
            Log.d(TAG, "printBalanceInquiryWithOptions: ");
        }

        /**
         * Print a Receipt with the given Transaction/ORDER/STAY information passed as options.
         * If Transaction is passed, transaction information is always printed in addition to
         * order or stay objects as necessary as details.
         * <p>
         * <p>
         * Bundle can contain the following:
         * <p>
         * TRANSACTION: Transaction object associated with this payment
         * TIP_AMOUNT:  Tip Amount (type: Long) to override and print on receipt
         * <p>
         * STAY: Stay object associated with this payment
         * TRANSACTION_ACTION: TransactionAction action (string)  used for Stay Receipt
         * ADJUST_TO_ADD_CHARGES: Boolean to indicate adjustToAddCharges
         * <p>
         * ORDER: Order object associated with this payment
         * BALANCE_INQUIRY: BalanceInquiry object
         *
         *
         * @param jobId
         * @param receiptOptions
         * @param callback
         */
        public void printReceiptWithOptions(String jobId, Bundle receiptOptions, IPoyntReceiptPrintingServiceListener callback) throws RemoteException {
            Log.d(TAG, "printReceiptWithOptions: ");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        bindPoyntReceiptPrintingService();
    }

    private void bindPoyntReceiptPrintingService() {
        if (poyntReceiptPrintingService == null){
            bindService(Intents.getComponentIntent(Intents.COMPONENT_POYNT_RECEIPT_PRINTING_SERVICE),
                    receiptPrintingConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(receiptPrintingConnection);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return mBinder;
    }

}



