package com.pie4all;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "productDB.db";
	private static final String TABLE_PRODUCTS = "products";
	
	public static final String CATEGORIES_COLUMN_ID = "cat_id";
	public static final String CATEGORIES_COLUMN_PRODUCTNAME = "categoryname";
	
	public static final String COLUMN_ID = "prod_id";
	public static final String COLUMN_PRODUCTNAME = "productname";
	public static final String COLUMN_PRICE = "price";
	public static final String COLUMN_QUANTITY = "quantity";
	
	public static final String PINF_COLUMN_ID = "pinf_id";
	public static final String PINF_COLUMN_PRODUCTNAME = "productname";
	public static final String PINF_COLUMN_PRICE = "price";
	public static final String PINF_COLUMN_QUANTITY = "quantity";
	
	public MyDBHandler(Context context, String name, 
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CATEGORIES_TABLE = "CREATE TABLE categories(cat_id INTEGER PRIMARY KEY, cat_name TEXT)";
	    db.execSQL(CREATE_CATEGORIES_TABLE);
	    
	    
	    System.out.println("db aangemaakt.. schijnt..");
	    
	    /*String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
	             TABLE_PRODUCTS + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_PRODUCTNAME + COLUMN_PRICE
	             + " TEXT," + COLUMN_QUANTITY + " INTEGER" + ")";
	    db.execSQL(CREATE_PRODUCTS_TABLE);
	    
	    String CREATE_PRODUCTINFO_TABLE = "CREATE TABLE " +
	             TABLE_PRODUCTS + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_PRODUCTNAME + COLUMN_PRICE
	             + " TEXT," + COLUMN_QUANTITY + " INTEGER" + ")";
	    db.execSQL(CREATE_PRODUCTINFO_TABLE);*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS categories");
		System.out.println("db aangemaakt.. schijnt..");
	    onCreate(db);
	}
	
	public void addProduct(Db_Product product) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME, product.getProductName());
        values.put(COLUMN_PRICE, product.getQuantity());
        values.put(COLUMN_QUANTITY, product.getQuantity());
 
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
	}
	
	public Db_Product findProduct(String productname) {
		String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " =  \"" + productname + "\"";
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		Db_Product product = new Db_Product();
		
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			product.setID(Integer.parseInt(cursor.getString(0)));
			product.setProductName(cursor.getString(1));
			product.setQuantity(Integer.parseInt(cursor.getString(2)));
			cursor.close();
		} else {
			product = null;
		}
	        db.close();
		return product;
	}
	
	public boolean deleteProduct(String productname) {
		
		boolean result = false;
		
		String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " =  \"" + productname + "\"";

		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		Db_Product product = new Db_Product();
		
		if (cursor.moveToFirst()) {
			product.setID(Integer.parseInt(cursor.getString(0)));
			db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?",
		            new String[] { String.valueOf(product.getID()) });
			cursor.close();
			result = true;
		}
	        db.close();
		return result;
	}

} 