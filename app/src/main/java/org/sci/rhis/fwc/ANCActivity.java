package org.sci.rhis.fwc;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ANCActivity extends ClinicalServiceActivity {

    private PregWoman woman;
    private Date today;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    ExpandableListAdapter listAdapter2;
    ExpandableListView expListView2;
    List<String> listDataHeader2;
    HashMap<String, List<String>> listDataChild2;

    ExpandableListAdapter listAdapter3;
    ExpandableListView expListView3;
    List<String> listDataHeader3;
    HashMap<String, List<String>> listDataChild3;

    private static final String TAG_VISIT_NO = "ancVisit01";
    private static final String TAG_DATE = "2015-07-02";
    private static final String TAG_BLOOD_PRESSURE = "120";
    private static final String TAG_WEIGHT = "22";
    private static final String TAG_EDIMA = "22";
    private static final String TAG_J_HEIGHT = "5.6";
    private static final String TAG_FITNESS_PM = "FIT";
    private static final String TAG_PHITAL_PRESENTATION = "GOOD";
    private static final String TAG_HIMOGLOBIN = "2";
    private static final String TAG_JONDIS = "3";
    private static final String TAG_URIN_SUGAR = "3";
    private static final String TAG_URIN_TEST = "3";
    private static final String TAG_DANGER_SIGN = "OFF";
    private static final String TAG_DISADVANTAGE = "NA";
    private static final String TAG_DISEASE = "FEVER";
    private static final String TAG_TREATMENT = "OK";
    private static final String TAG_REFER = "DR";
    private static final String TAG_CENTER_NAME = "DHAKA";
    private static final String TAG_CAUSE = "MAN";

    JSONArray visits = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // woman = getIntent().getParcelableExtra("PregWoman");


       // Log.d("---woman---------",""+woman.toString());
        today = new Date();
//        if( woman.getAncThreshold().after(today)) { // add ANC threshold
//            Toast.makeText(this, "Too Late for ANC, ask delivery status ...", Toast.LENGTH_LONG).show();
//        } else {
//            setContentView(R.layout.activity_anc);
//        }

        setContentView(R.layout.activity_anc);

//        GridView gv = (GridView)findViewById(R.id.gridAncVisit);
 //       gv.setAdapter(new CustomGridAdapter(ANCActivity.this));


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        expListView2 = (ExpandableListView) findViewById(R.id.lvExp2);

        expListView3 = (ExpandableListView) findViewById(R.id.lvExp3);



        AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(ANCActivity.this);
        //SendPostRequestAsyncTask
        AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(ANCActivity.this);
        String queryString =   "{" +
                "pregNo:" + 3 + "," +
                "healthid:" + "43366275025436" + "," +
                "ancLoad:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        String servlet = "anc";
        String jsonRootkey = "ANCInfo";
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);


        // preparing list data
//        prepareListData();
//
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//
//        prepareListData2();
//        listAdapter2 = new ExpandableListAdapter(this, listDataHeader2, listDataChild2);

    //    prepareListData3();
    //    listAdapter3 = new ExpandableListAdapter(this, listDataHeader3, listDataChild3);


        // setting list adapter
    //    expListView.setAdapter(listAdapter);
   //     expListView2.setAdapter(listAdapter2);
   //     expListView3.setAdapter(listAdapter3);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();



                return false;
            }
        });

        // Listview Group click listener
        expListView2.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group click listener
//        expListView3.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,
//                                        int groupPosition, long id) {
//                // Toast.makeText(getApplicationContext(),
//                // "Group Clicked " + listDataHeader.get(groupPosition),
//                // Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Listview Group expanded listener
        expListView2.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group expanded listener
//        expListView3.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader2.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview Group collasped listener
        expListView2.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview Group collasped listener
//        expListView3.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader2.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        // Listview on child click listener
        expListView2.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        // Listview on child click listener
//        expListView3.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                // TODO Auto-generated method stub
//                Toast.makeText(
//                        getApplicationContext(),
//                        listDataHeader3.get(groupPosition)
//                                + " : "
//                                + listDataChild3.get(
//                                listDataHeader3.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
//                return false;
//            }
//        });
    }




    /*
 * Preparing the list data
 */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();


        listDataHeader.add(getString(R.string.history_visit1));


       // listDataChild.put(listDataHeader.get(1), nowShowing);
       // listDataChild.put(listDataHeader.get(2), comingSoon);
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anc, menu);
        return true;
    }

    @Override
    public void callbackAsyncTask(String result) {


        try {
            JSONObject jsonStr = new JSONObject(result);
            String key;

           // woman = PregWoman.CreatePregWoman(json);

            //DEBUG
            int m = 0;
            for ( Iterator<String> ii = jsonStr.keys(); ii.hasNext(); ) {
                key = ii.next();

                System.out.println("1.Key:" + key + " Value:\'" + jsonStr.get(key)+"\'");

                ArrayList<String> list = new ArrayList<String>();

                try {
                    JSONArray jsonArray = jsonStr.getJSONArray(key);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        System.out.println("======= "+jsonArray.get(i).toString());
                        list.add(jsonArray.get(i).toString());

                    }//end for
                    listDataHeader = new ArrayList<String>();
                    listDataChild = new HashMap<String, List<String>>();


                    listDataHeader.add(getString(R.string.history_visit1) + "" + jsonArray.get(0).toString() + " :");

                    listDataChild.put(listDataHeader.get(0), list);

                    listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

                   // expListView = new ExpandableListView(this);
                   // expListView.setAdapter(listAdapter);
                    if(m==0)
                    expListView.setAdapter(listAdapter);
                    else if(m==1)
                        expListView2.setAdapter(listAdapter);
                    else if(m==2)
                        expListView3.setAdapter(listAdapter);

                    m++;

                } catch (JSONException e) {
                    Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                    e.printStackTrace();
                }
            }


        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initiateCheckboxes(){};
    @Override
    protected void initiateEditTexts(){};
    @Override
    protected void initiateSpinners(){};
    @Override
    protected void initiateEditTextDates(){};
    @Override
    protected void initiateRadioGroups(){};
}
