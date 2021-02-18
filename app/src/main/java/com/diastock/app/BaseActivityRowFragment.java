package com.diastock.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseActivityRowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseActivityRowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseActivityRowFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String text = "param1";
    private static final String title = "param2";

    public String getRowText() {
        return rowText;
    }

    public void setRowText(String rowText) {
        this.rowText = rowText;
    }

    public String getRowTitle() {
        return rowTitle;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public void setRowTitle(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    // TODO: Rename and change types of parameters
    private String rowText;
    private String rowTitle;
    private int rowNumber;

    private OnFragmentInteractionListener mListener;

    public BaseActivityRowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseActivityRowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseActivityRowFragment newInstance(String param1, String param2) {
        BaseActivityRowFragment fragment = new BaseActivityRowFragment();
        Bundle args = new Bundle();
        args.putString(text, param1);
        args.putString(title, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rowText = getArguments().getString(text);
            rowTitle = getArguments().getString(title);

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView tab = view.findViewById(R.id.baserowtab);
        tab.setText(rowTitle);
        TextView data = view.findViewById(R.id.baserowdata);
        data.setText(rowText);

        ImageView imageView = view.findViewById(R.id.baserowimage);
        imageView.setImageResource(getImageId(rowTitle));
    }

    private int getImageId(String rowTitle) {
        if (rowTitle.toLowerCase().contains("lotto"))
            return R.drawable.ic_action_batch;
        else if (rowTitle.toLowerCase().contains("scadenza"))
            return R.drawable.ic_action_expire;
        else if (rowTitle.toLowerCase().contains("fornitore")||rowTitle.toLowerCase().contains("cliente"))
            return R.drawable.ic_action_contact;
        else if (rowTitle.toLowerCase().contains("ordine"))
            return R.drawable.ic_action_document;
        else if (rowTitle.toLowerCase().contains("articolo")||rowTitle.toLowerCase().contains("descrizione"))
            return R.drawable.ic_action_sku;
        else if (rowTitle.toLowerCase().contains("quantit"))
            return R.drawable.ic_action_qty;
        else if (rowTitle.toLowerCase().contains("variant"))
            return R.drawable.ic_action_variant;
        else if (rowTitle.toLowerCase().contains("lotto"))
            return R.drawable.ic_action_batch;


        else
            return R.drawable.ic_action_element;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_activity_row, container, false);
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
/*        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
}
