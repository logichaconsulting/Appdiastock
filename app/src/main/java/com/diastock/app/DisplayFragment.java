package com.diastock.app;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.diastock.app.InputArea.TYPE_DATE;
import static com.diastock.app.InputArea.TYPE_EANUCC;
import static com.diastock.app.InputArea.TYPE_LOCATION;
import static com.diastock.app.InputArea.TYPE_LOGUNIT;
import static com.diastock.app.InputArea.TYPE_QTY;
import static com.diastock.app.InputArea.TYPE_TEXT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private String mParam1;
    private String mParam2;
    private DisplayFragment.OnFragmentInteractionListener mListener;

    private ArrayList<BaseActivityRowFragment> videoRows = new ArrayList<BaseActivityRowFragment>();
    public ArrayList<ActivityItem> itemsList = new ArrayList<ActivityItem>();
    private ActivityItem currentItem;

    private List<TextWatcher> listeners = new ArrayList<TextWatcher>();

    InputArea input;
    int itemIndex;
    TextView inputTab;

    long lastScannerTime = 0;
    //private DataExchange ;

    public FragmentTransaction getTransaction() {
        if (transaction == null) transaction = getChildFragmentManager().beginTransaction();
        return transaction;
    }

    public void setTransaction(FragmentTransaction transaction) {
        this.transaction = transaction;
    }

    public void disposeTransaction() {
        if (transaction != null) transaction = null;
    }

    FragmentTransaction transaction;

    public DisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayFragment newInstance(String param1, String param2) {
        DisplayFragment fragment = new DisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onListFragmentInteraction(Uri uri) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        switch(keyCode){
//            case KeyEvent.KEYCODE_MENU:
//                Toast.makeText(this, "Menu key released", Toast.LENGTH_SHORT).show();
//                return true;
//            case KeyEvent.KEYCODE_SEARCH:
//                Toast.makeText(this, "Search key released", Toast.LENGTH_SHORT).show();
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_UP:​
//                if(event.isTracking() && !event.isCanceled())
//                    Toast.makeText(this, "Volumen Up released", Toast.LENGTH_SHORT).show();
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                Toast.makeText(this, "Volumen Down released", Toast.LENGTH_SHORT).show();
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display, container, true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    /*    if (context instanceof DisplayFragment.OnFragmentInteractionListener) {
            mListener = (DisplayFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    public void AddRow()
//    {
//        BaseActivityRowFragment row = BaseActivityRowFragment.newInstance("","");
//        //FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.add(R.id.baseLinearlayout, row);
//    }


    public void AddDisplayRow(String data, String label, boolean fontbold) {

        boolean found = false;

        Iterator iterator = videoRows.iterator();
        while (iterator.hasNext()) {
            BaseActivityRowFragment row = (BaseActivityRowFragment) iterator.next();
            if (row.getRowTitle() != null && row.getRowTitle().equals(currentItem.label)) {
                found = true;
            }
        }

        if (found) {
            RemoveDisplayRow(videoRows.size() - 1);
        }

        getTransaction();
        BaseActivityRowFragment row = BaseActivityRowFragment.newInstance(data, label);
        row.setRowNumber(videoRows.size());
        transaction.add(R.id.baseDisplayLinearlayout, row);
        getTransaction().commit();
        disposeTransaction();
        videoRows.add(row);

    }

    public void RemoveDisplayRow(int position) {
        if (videoRows.size() > position) {
            //position--;
            getTransaction();
            BaseActivityRowFragment row = videoRows.get(position);
            transaction.remove(row);
            getTransaction().commit();
            disposeTransaction();
            videoRows.remove(position);
        }
/*
        rowsPanel.Controls.Remove(videoRows[position]);
        BaseLabel label = videoRows.Find(delegate(BaseLabel l) { if (l.Row == position) return true; else return false; });
        videoRows.Remove(label);
*/
    }

    public void ClearDisplayArea(int start, int end) {
        for (int i = end; i >= start; i--) {
            RemoveDisplayRow(i);
        }
    }

    public void ClearAllDisplay() {
        int size = videoRows.size();
        for (int i = 0; i < size; i++) {
            RemoveDisplayRow(0);
        }
        videoRows.clear();
        itemsList.clear();

    }

    public ActivityItem GetCurrentItem() {
        return currentItem;
    }


    public void InitializeItem(ActivityItem item, boolean isAutomaticPicking) {
        SetCurrentItem(item, "", false, false, false, false, null, false, null, isAutomaticPicking);
    }

    public void InitializeItem(ActivityItem item) {
        SetCurrentItem(item, "", false, false, false, false);
    }

    public void InitializeItem(ActivityItem item, boolean showAction1, String actionTitle1) {
        SetCurrentItem(item, "", false, false, false, showAction1, actionTitle1, false);
    }

    public void InitializeItem(ActivityItem item, boolean showAction1, String actionTitle1, boolean showAction2, String actionTitle2) {
        SetCurrentItem(item, "", false, false, false, showAction1, actionTitle1, false);
    }

    public void InitializeItem(ActivityItem item, String value) {
        SetCurrentItem(item, value, false, false, false, false);
    }

    public void InitializeItem(ActivityItem item, String value, boolean noShowableItem) {
        SetCurrentItem(item, value, noShowableItem, false, false, false);
    }


    public void InitializeItem(ActivityItem item, String value, boolean noShowableItem, boolean multishoot) {
        SetCurrentItem(item, value, noShowableItem, multishoot, false, false);
    }

    public void InitializeItem(ActivityItem item, String value, boolean noShowableItem, boolean multishoot, boolean backWard) {
        SetCurrentItem(item, value, noShowableItem, multishoot, backWard, false);
    }

    public void InitializeItem(ActivityItem item, String value, boolean noShowableItem, boolean multishoot, boolean backWard, boolean isAutomaticPicking) {
        SetCurrentItem(item, value, noShowableItem, multishoot, backWard, isAutomaticPicking);
    }

    public void BackToItem(ActivityItem item) {
        SetCurrentItem(item, null, false, false, true, false);
    }

    public void SetCurrentItem(ActivityItem item, String value, boolean noShowableItem, boolean multishoot, boolean backWard, boolean isAutomaticPicking) {
        SetCurrentItem(item, value, noShowableItem, multishoot, backWard, false, null, isAutomaticPicking);
    }

    public void SetCurrentItem(ActivityItem item, String value, boolean noShowableItem, boolean multishoot, boolean backWard, boolean showAction, String actionTitle, boolean isAutomaticPicking) {
        SetCurrentItem(item, value, noShowableItem, multishoot, backWard, showAction, actionTitle, false, null, isAutomaticPicking);
    }

    public void SetCurrentItem(ActivityItem item, String value, boolean noShowableItem, boolean multishoot, boolean backWard,
                               boolean showAction, String actionTitle, boolean showAction2, String actionTitle2, boolean isAutomaticPicking) {

        Type t = DataExchange.class;


        if (input == null) {
            input = (InputArea) getActivity().findViewById(R.id.inputarea);
            inputTab = (TextView) getActivity().findViewById(R.id.inputareatab);

            input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                @Override
                                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                                                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                                                            event != null &&
                                                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                                        try {
                                                            if (Functions.ValidateBarcode(input.getText().toString(), currentItem, input)) {

                                                                if (currentItem.datatype == TYPE_EANUCC) {
//                                                                    if (input.getText().toString().length() > 0 &&
//                                                                        (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku()) ||
//                                                                                DataFunctions.isNullOrEmpty(DataExchange.getInstance().getBatch()) ||
//                                                                                DataFunctions.isNullOrEmpty(DataExchange.getInstance().getExpire())))
//                                                                        {
//                                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                                                                        alertDialogBuilder.setMessage("Aggiungere un altro Barcode?").setCancelable(false)
//                                                                                .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                                        input.setText(input.getText().toString() + (char)UCC_SEPARATOR);
//                                                                                        input.setSelection(input.getText().toString().length());
//                                                                                    }
//                                                                                })
//                                                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                                        ((BaseActivityInterface) getActivity()).AcceptText(input.getText().toString(), false);
//                                                                                    }
//                                                                                }).show();
//                                                                    }
//                                                                    else {
                                                                    ((BaseActivityInterface) getActivity()).AcceptText(input.getText().toString(), 0);
                                                                    return true;
                                                                    //}
                                                                    /*Snackbar sbw = Snackbar.make(getActivity().findViewById(R.id.testMainLayout), "...", Snackbar.LENGTH_LONG)
                                                                    .setAction("Altro Barcode", new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            //((BaseActivityInterface) getActivity()).AcceptText(input.getText().toString(), false);
                                                                            //SetCurrentItem(GetPreviousItem(), "", false, false, false);
                                                                            return;
                                                                        }
                                                                    }).setActionTextColor(Color.parseColor("#ffffff")).setDuration(8000);
                                                                    sbw.getView().setBackgroundColor(Color.parseColor("#108a1f"));
                                                                    sbw.show();*/
                                                                }

                                                                //if(currentItem.datatype == TYPE_LOCATION && isAutomaticPicking)
                                                                //    ((BaseActivityInterface) getActivity()).AcceptText(input.getText().toString(), 0);

                                                                if (currentItem.datatype == TYPE_LOCATION) {
                                                                    if (isAutomaticPicking) {
                                                                        if (input.getText().toString().length() > 0 && input.getText().toString().length() < WarehouseLocation.KEYBOARD_INPUT_LENGHT)
                                                                            return true;
                                                                    } else {
                                                                        if (input.getText().toString().length() < WarehouseLocation.KEYBOARD_INPUT_LENGHT)
                                                                            return true;
                                                                    }
                                                                }

                                                                /*if (isAutomaticPicking && currentItem.datatype == TYPE_LOCATION && input.getText().toString().length() > 0 && input.getText().toString().length() < WarehouseLocation.KEYBOARD_INPUT_LENGHT)
                                                                    return true;

                                                                if ((!isAutomaticPicking && currentItem.datatype == TYPE_LOCATION && input.getText().toString().length() < WarehouseLocation.KEYBOARD_INPUT_LENGHT) &&
                                                                        (currentItem.datatype == TYPE_LOCATION && !isAutomaticPicking))
                                                                    return true;*/

                                                                if (currentItem.datatype == TYPE_DATE &&
                                                                        input.getText().toString().length() < 10)
                                                                    return true;

                                                                if (currentItem.datatype != TYPE_EANUCC && (event == null || !event.isShiftPressed())) {
//                                                                    if(event != null && event.getEventTime() != lastScannerTime)
//                                                                        lastScannerTime = event.getEventTime();
                                                                    ((BaseActivityInterface) getActivity()).AcceptText(input.getText().toString(), 0);
                                                                    //return true; // consume.
                                                                }

                                                                return true;
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }


                                                    return false;
                                                }

                                            }
            );


            ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.btn_back);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivityInterface) getActivity()).OnBackAction();
                }
            });

            ImageButton imageButtonScan = (ImageButton) getActivity().findViewById(R.id.btn_scan);

            imageButtonScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                    try {
                        IntentIntegrator integrator = new IntentIntegrator(getActivity());


                        integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES, getFrontFacingCameraId(manager));
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        try {
            Button button1 = (Button) getActivity().findViewById(R.id.buttonAction1);
            Boolean visible1 = false, visible2 = false;

            if (showAction && !Functions.isNullOrEmpty(actionTitle)) {
                button1.setVisibility(View.VISIBLE);
                button1.setText(actionTitle);
                visible1 = true;
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivityInterface) getActivity()).CreateAction1();
                    }
                });
            } else {
                button1.setVisibility(View.INVISIBLE);
                button1.setText(null);
            }

            Button button2 = (Button) getActivity().findViewById(R.id.buttonAction2);

            if (showAction2 && !Functions.isNullOrEmpty(actionTitle2)) {

                button2.setVisibility(View.VISIBLE);
                button2.setText(actionTitle);
                visible2 = true;
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivityInterface) getActivity()).CreateAction2();
                    }
                });

            } else {
                button2.setVisibility(View.INVISIBLE);
                button2.setText(null);
            }

            RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.baseRelativeLayout);

            final float scale = getContext().getResources().getDisplayMetrics().density;

            int heightLayout = visible1 && visible2 ? 150 : (!visible1 && !visible1 ? 85 : 120);

            int px = (int) (heightLayout * scale + 0.5f);  // replace 100 with your dimensions
            relativeLayout.getLayoutParams().height = px;
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentItem = item;
        input.setHint(item.label);

        ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.btn_scan);
        imageButton.setVisibility(item.allowScanner ? View.VISIBLE : View.INVISIBLE);

        InputFilter lenFilter = new InputFilter.LengthFilter(currentItem.maxlen);
        input.setFilters(new InputFilter[]{lenFilter});
        inputTab.setText(item.label);

        while (!listeners.isEmpty()) {
            input.removeTextChangedListener(listeners.get(0));
            listeners.remove(0);
        }

        MaskWatcher locationMask = new MaskWatcher("#####-#####-###-###-###", input);
        MaskWatcher dateMask = new MaskWatcher("##/##/####", input);
        MaskWatcher eanuccMask = new MaskWatcher("#########################################################################################################", input);
        MaskWatcher logunitMask = new MaskWatcher("##################", input);
        MaskWatcher qtyMask = new MaskWatcher("######", input);

        switch (currentItem.datatype) {
            case TYPE_EANUCC:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.addTextChangedListener(eanuccMask);
                listeners.add(eanuccMask);
                break;
            case TYPE_TEXT:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TYPE_LOGUNIT:
                input.setMaxLength(18);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                listeners.add(logunitMask);
                break;
            case TYPE_DATE:
                input.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
                input.addTextChangedListener(dateMask);
                listeners.add(dateMask);
                input.setMaxLength(10);
                break;
            case TYPE_QTY:
                //input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.setMaxLength(6);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);//| InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.addTextChangedListener(qtyMask);
                listeners.add(qtyMask);
                break;
            case TYPE_LOCATION:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setMaxLength(WarehouseLocation.KEYBOARD_INPUT_LENGHT);
                input.addTextChangedListener(locationMask);
                listeners.add(locationMask);
                break;

        }

        input.setMaxLength(currentItem.maxlen);

        itemIndex = itemsList.indexOf(currentItem);
        boolean found = false;

        if (itemIndex < 0) {
            Iterator iterator = videoRows.iterator();
            while (iterator.hasNext()) {
                BaseActivityRowFragment row = (BaseActivityRowFragment) iterator.next();
                if (row.getRowTitle() != null && row.getRowTitle().equals(currentItem.label)) {
                    /*if (isAutomaticPicking)
                        currentItem.row = (itemsList.get(itemsList.size() - 1).row);
                    else*/
                    currentItem.row = row.getRowNumber();
                    found = true;
                }
            }

            if (!found)
                currentItem.row = videoRows.size();

            itemsList.add(currentItem);
        } else {
            currentItem.row = itemsList.get(itemIndex).row;
            ResetFields(itemIndex, itemsList);
        }


        try {
            if (value != null && !(item.datatype == InputArea.TYPE_DATE && DataFunctions.isStringNewDateTime(value))) {
                setInputText(value);
                item.lastValue = value;

                if (isAutomaticPicking) {
                    if (item.datatype == TYPE_TEXT ||
                            item.datatype == InputArea.TYPE_SKU ||
                            item.datatype == TYPE_LOGUNIT ||
                            item.datatype == TYPE_DATE) {
                        input.selectAll();
                    } else {
                        input.setSelection(input.getText().length());
                    }
                }

            } else if (value != null && DataFunctions.isStringNewDateTime(value)) {
                setInputText(null);
                item.lastValue = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (backWard)
            ClearDisplayArea(currentItem.row + 1, videoRows.size() - 1);// itemsList.get(itemsList.size()-1).row);
        else if (!multishoot && currentItem.row >= 0 && videoRows.size() > 0 && !noShowableItem)
            ClearDisplayArea(currentItem.row, videoRows.size() - 1); //itemsList.get(itemsList.size()-1).row);

    }


    public void ResetFields(int fromIndex, List<ActivityItem> itemsList) {

        try {
            Type t = DataExchange.class;

            for (int i = fromIndex; i < itemsList.size(); i++) {
                if (this.itemsList.get(i).dataFieldName == null)
                    return;

                if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_QTY)) {
                    DataExchange.getInstance().setQty(0);
                } else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_REQ_QTY)) {
                    DataExchange.getInstance().setRequiredQty(0);
                    DataExchange.getInstance().setPalletcode(null);
                    DataExchange.getInstance().setReserved(null);
                    DataExchange.getInstance().setFinalContainer(null);
                    DataExchange.getInstance().setFinalUnit(null);
                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                } else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_BATCH))
                    DataExchange.getInstance().setBatch(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_VARIANTID1))
                    DataExchange.getInstance().setVariantId1(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_VARIANTID2))
                    DataExchange.getInstance().setVariantId2(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_VARIANTID3))
                    DataExchange.getInstance().setVariantId3(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_UNIT)) {
                    DataExchange.getInstance().setUnit(null);
                    DataExchange.getInstance().setCurrentArticle(new Article());
                    DataExchange.getInstance().setBatch(null);
                    DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setVariantId1(null);
                    DataExchange.getInstance().setVariantId2(null);
                    DataExchange.getInstance().setVariantId3(null);
                    DataExchange.getInstance().setRequiredQty(0);
                    DataExchange.getInstance().setPalletcode(null);
                    DataExchange.getInstance().setReserved(null);
                    DataExchange.getInstance().setFinalContainer(null);
                    DataExchange.getInstance().setFinalUnit(null);
                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setSwitchedUnit(null);
                    DataExchange.getInstance().setSwitchedBatch(null);
                    DataExchange.getInstance().setSwitchedExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setSwitchedVariant1(null);
                    DataExchange.getInstance().setSwitchedVariant2(null);
                    DataExchange.getInstance().setSwitchedVariant3(null);
                    DataExchange.getInstance().setLockedforshelflife(false);
                    DataExchange.getInstance().setCardboard(null);
                } else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_CONTAINER)) {
                    DataExchange.getInstance().setContainer(null);
                    DataExchange.getInstance().setUnit(null);
                    DataExchange.getInstance().setCurrentArticle(new Article());
                    DataExchange.getInstance().setBatch(null);
                    DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setVariantId1(null);
                    DataExchange.getInstance().setVariantId2(null);
                    DataExchange.getInstance().setVariantId3(null);
                    DataExchange.getInstance().setRequiredQty(0);
                    DataExchange.getInstance().setPalletcode(null);
                    DataExchange.getInstance().setReserved(null);
                    DataExchange.getInstance().setFinalContainer(null);
                    DataExchange.getInstance().setFinalUnit(null);
                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setSwitchedUnit(null);
                    DataExchange.getInstance().setSwitchedBatch(null);
                    DataExchange.getInstance().setSwitchedExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setSwitchedVariant1(null);
                    DataExchange.getInstance().setSwitchedVariant2(null);
                    DataExchange.getInstance().setSwitchedVariant3(null);
                    DataExchange.getInstance().setLockedforshelflife(false);
                    DataExchange.getInstance().setCardboard(null);
                } else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_CUSTOMER))
                    DataExchange.getInstance().setCustomer(new Customer());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_ORDER))
                    DataExchange.getInstance().setOrder(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_LISTNR))
                    DataExchange.getInstance().setListnr(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_LISTID))
                    DataExchange.getInstance().setListid(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_FROMLOCATION))
                    DataExchange.getInstance().setFromLocation(new WarehouseLocation());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_TOLOCATION))
                    DataExchange.getInstance().setToLocation(new WarehouseLocation());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_CURRENTARTICLE)) {
                    DataExchange.getInstance().setCurrentArticle(new Article());
                    DataExchange.getInstance().setBatch(null);
                    DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setVariantId1(null);
                    DataExchange.getInstance().setVariantId2(null);
                    DataExchange.getInstance().setVariantId3(null);
                    DataExchange.getInstance().setRequiredQty(0);
                    DataExchange.getInstance().setPalletcode(null);
                    DataExchange.getInstance().setReserved(null);
                    DataExchange.getInstance().setFinalContainer(null);
                    DataExchange.getInstance().setFinalUnit(null);
                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setSwitchedUnit(null);
                    DataExchange.getInstance().setSwitchedBatch(null);
                    DataExchange.getInstance().setSwitchedExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setSwitchedVariant1(null);
                    DataExchange.getInstance().setSwitchedVariant2(null);
                    DataExchange.getInstance().setSwitchedVariant3(null);
                    DataExchange.getInstance().setLockedforshelflife(false);
                    DataExchange.getInstance().setCardboard(null);
                } else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_SUPPLIER))
                    DataExchange.getInstance().setSupplier(new Supplier());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_EXPIRE))
                    DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_DOCNR))
                    DataExchange.getInstance().setDocnr(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_DOCDATE))
                    DataExchange.getInstance().setDocdate(DataFunctions.getEmptyDate());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_DOCTYPE))
                    DataExchange.getInstance().setDoctype(new DocType());
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_GEN_STR_1))
                    DataExchange.getInstance().setGenericString1(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_GEN_STR_2))
                    DataExchange.getInstance().setGenericString2(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_GEN_INT_1))
                    DataExchange.getInstance().setGenericInt1(0);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_GEN_INT_2))
                    DataExchange.getInstance().setGenericInt2(0);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_GEN_COORDINATES))
                    DataExchange.getInstance().setGenericCoordinates(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_ROW))
                    DataExchange.getInstance().setRow(0);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_PALLETCODE))
                    DataExchange.getInstance().setPalletcode(null);
                else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_EANUCC)) {
                    DataExchange.getInstance().setUnit(null);
                    DataExchange.getInstance().setCurrentArticle(new Article());
                    DataExchange.getInstance().setBatch(null);
                    DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setVariantId1(null);
                    DataExchange.getInstance().setVariantId2(null);
                    DataExchange.getInstance().setVariantId3(null);
                    DataExchange.getInstance().setRequiredQty(0);
                    DataExchange.getInstance().setPalletcode(null);
                    DataExchange.getInstance().setReserved(null);
                    DataExchange.getInstance().setFinalContainer(null);
                    DataExchange.getInstance().setFinalUnit(null);
                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setSwitchedUnit(null);
                    DataExchange.getInstance().setSwitchedBatch(null);
                    DataExchange.getInstance().setSwitchedExpire(DataFunctions.getEmptyDate());
                    DataExchange.getInstance().setSwitchedVariant1(null);
                    DataExchange.getInstance().setSwitchedVariant2(null);
                    DataExchange.getInstance().setSwitchedVariant3(null);
                    DataExchange.getInstance().setLockedforshelflife(false);
                    DataExchange.getInstance().setCardboard(null);
                } else if (this.itemsList.get(i).dataFieldName.equals(DataExchange.PROP_CARDBOARD))
                    DataExchange.getInstance().setCardboard(null);
                //else if (itemsList.get(i).dataFieldName.equals(DataExchange.PROP_SERIALNR)
                //    t.getClass().getDeclaredField(DataExchange.PROP_SERIALNR).set(DataExchange.getInstance(), null);
            }

            DataExchange.getInstance().setMessage("");
            DataExchange.getInstance().setMessagetype(DataExchange.MessageType.VOID);

            int nextIndex = fromIndex + 1;

            if (itemsList.size() >= nextIndex) {
                int size = itemsList.size();
                for (int x = size - 1; x >= nextIndex; x--)
                    itemsList.remove(itemsList.size() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public ActivityItem GetPreviousItem() {
        int currentIndex = itemsList.indexOf(currentItem);

        if (currentIndex == 0)
            return null;
        else
            return itemsList.get(currentIndex - 1);
    }

    public void setInputText(String inputText) {
        this.input.setText(inputText);
        this.input.setSelection(input.getText().length());
    }


    private int getFrontFacingCameraId(CameraManager cManager) throws CameraAccessException {
        try {
            String cameraId;
            int cameraOrientation;
            CameraCharacteristics characteristics;
            for (int i = 0; i < cManager.getCameraIdList().length; i++) {
                cameraId = cManager.getCameraIdList()[i];
                characteristics = cManager.getCameraCharacteristics(cameraId);
                cameraOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cameraOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                    return Integer.parseInt(cameraId);
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }
}