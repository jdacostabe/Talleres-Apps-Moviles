package co.edu.unal.companydirectory;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String COMPANY_TABLE_NAME = "companies";
    public static final String COMPANY_COLUMN_ID = "id";
    public static final String COMPANY_COLUMN_NAME = "name";
    public static final String COMPANY_COLUMN_URL = "url";
    public static final String COMPANY_COLUMN_TELEPHONE = "telephone";
    public static final String COMPANY_COLUMN_EMAIL = "email";
    public static final String COMPANY_COLUMN_PRODUCTS = "products";
    public static final String COMPANY_COLUMN_TYPE = "type";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table companies " +
                        "(id integer primary key, name text, url text, telephone text, email text, products text, type text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS companies");
        onCreate(db);
    }

    public boolean insertCompany (String name, String url, String telephone, String email, String products,String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", url);
        contentValues.put("telephone", telephone);
        contentValues.put("email", email);
        contentValues.put("products", products);
        contentValues.put("type", type);

        db.insert("companies", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies where id="+id+"", null );
        return res;
    }

    public boolean updateCompany (Integer id, String name, String url, String telephone, String email, String products,String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", url);
        contentValues.put("telephone", telephone);
        contentValues.put("email", email);
        contentValues.put("products", products);
        contentValues.put("type", type);

        db.update("companies", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteCompany (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("companies",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Pair<String, String>> getAllCompanies() {
        ArrayList<Pair<String, String>> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndex(COMPANY_COLUMN_NAME));
            String type = res.getString(res.getColumnIndex(COMPANY_COLUMN_TYPE));
            array_list.add(new Pair<>(name, type));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<ArrayList<String>> getCompaniesByName(String name){
        ArrayList<ArrayList<String>> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies where name like '%"+name+"%'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){ //Se debe añadir también el id
            Integer finalId = res.getInt(res.getColumnIndex(COMPANY_COLUMN_ID));
            String finalName = res.getString(res.getColumnIndex(COMPANY_COLUMN_NAME));
            String finalType = res.getString(res.getColumnIndex(COMPANY_COLUMN_TYPE));

            ArrayList<String> aux = new ArrayList<String>(){
                {add(finalId.toString()); add(finalName); add(finalType);}
            };
            array_list.add(aux);

            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<ArrayList<String>> getCompaniesByType(String type){
        ArrayList<ArrayList<String>> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies where type like '%"+type+"%'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Integer finalId = res.getInt(res.getColumnIndex(COMPANY_COLUMN_ID));
            String finalName = res.getString(res.getColumnIndex(COMPANY_COLUMN_NAME));
            String finalType = res.getString(res.getColumnIndex(COMPANY_COLUMN_TYPE));

            ArrayList<String> aux = new ArrayList<String>(){
                {add(finalId.toString()); add(finalName); add(finalType);}
            };
            array_list.add(aux);

            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<ArrayList<String>> getCompaniesByNameAndType(String name, String type){
        ArrayList<ArrayList<String>> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies where name like '%"+name+"%' and type like '%"+type+"%'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Integer finalId = res.getInt(res.getColumnIndex(COMPANY_COLUMN_ID));
            String finalName = res.getString(res.getColumnIndex(COMPANY_COLUMN_NAME));
            String finalType = res.getString(res.getColumnIndex(COMPANY_COLUMN_TYPE));

            ArrayList<String> aux = new ArrayList<String>(){
                {add(finalId.toString()); add(finalName); add(finalType);}
            };
            array_list.add(aux);

            res.moveToNext();
        }

        return array_list;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, COMPANY_TABLE_NAME);
        return numRows;
    }
}