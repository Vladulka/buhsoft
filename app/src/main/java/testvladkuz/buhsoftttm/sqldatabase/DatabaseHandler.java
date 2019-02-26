package testvladkuz.buhsoftttm.sqldatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.util.ArrayList;

import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.classes.Settings;
import testvladkuz.buhsoftttm.classes.TTM;

public class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "test4";
    private static final String MAIN = "main";
    private static final String FOOTER = "footer";
    private static final String ALCT = "alc";
    private static final String PROFILE = "profile";

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String FSRAR = "fsrar";
    private static final String GUID = "guid";
    private static final String DATE = "date";
    private static final String INN = "inn";
    private static final String SHORTNAME = "shortname";
    private static final String STATUS = "status";
    private static final String FILEID = "fileid";
    private static final String TYPE = "type";

    private static final String DOCID = "docid";
    private static final String ITEMID = "itemid";
    private static final String CAPACITY = "capacity";
    private static final String ALCCODE = "alccode";
    private static final String BARCODE = "barcode";
    private static final String VOLUME = "volume";
    private static final String FAR1 = "far1";
    private static final String FAR2 = "far2";
    private static final String NUMS = "nums";
    private static final String FACTNUMS = "factnums";

    private static final String ALCMARK = "alc";

    private static final String KEY_ID = "id";
    private static final String ZNAC = "name";
    private static final String TEXT = "text";

    Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INFO = "CREATE TABLE " + MAIN + "("
                + ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT,"
                + FSRAR + " TEXT," + GUID + " TEXT," + DATE + " TEXT," + INN + " TEXT," + SHORTNAME + " TEXT," + STATUS + " TEXT," + FILEID + " TEXT," + TYPE + " TEXT" + ")";
        db.execSQL(CREATE_INFO);

        CREATE_INFO = "CREATE TABLE " + FOOTER + "("
                + ID + " INTEGER PRIMARY KEY," + DOCID + " TEXT," + TITLE + " TEXT," + ALCCODE + " TEXT," + CAPACITY + " TEXT," + VOLUME + " TEXT," + FAR1 + " TEXT," + FAR2 + " TEXT," + NUMS + " TEXT," + FACTNUMS + " TEXT,"  + BARCODE + " TEXT" + ")";
        db.execSQL(CREATE_INFO);

        CREATE_INFO = "CREATE TABLE " + ALCT + "("
                + ID + " INTEGER PRIMARY KEY," + DOCID + " TEXT," + ITEMID + " TEXT," + ALCMARK + " TEXT," + STATUS + " TEXT" + ")";
        db.execSQL(CREATE_INFO);

         CREATE_INFO = "CREATE TABLE " + PROFILE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + ZNAC + " TEXT,"
                + TEXT + " TEXT" + ")";
        db.execSQL(CREATE_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MAIN);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + FOOTER);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + ALCT);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE);
        onCreate(db);
    }

    @Override
    public int getTTNSize() {
        int size = 0;
        String selectQuery = "SELECT  * FROM " + MAIN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                size++;
            } while (cursor.moveToNext());
        }

        return size;
    }

    @Override
    public int getItemsSize() {
        int size = 0;
        String selectQuery = "SELECT  * FROM " + FOOTER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                size++;
            } while (cursor.moveToNext());
        }

        return size;
    }

    @Override
    public int getUsersSize() {
        int i = 0;
        String selectQuery = "SELECT  * FROM " + PROFILE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                i++;
            } while (cursor.moveToNext());
        }

        return i;
    }

    @Override
    public void addNewTTN(TTM ttm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, ttm.getTitle());
        values.put(FSRAR, ttm.getFsrar());
        values.put(GUID, ttm.getGuid());
        values.put(DATE, ttm.getDate());
        values.put(INN, ttm.getInn());
        values.put(SHORTNAME, ttm.getShortname());
        values.put(STATUS, ttm.getShortname());
        values.put(FILEID, ttm.getFileid());
        values.put(TYPE, ttm.getType());

        db.insert(MAIN, null, values);
        db.close();
    }

    public void deleteTTN(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MAIN, ID	+ "	= ?", new String[] { id });
    }

    @Override
    public void addNewToFooter(Items items) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DOCID, items.getDocid());
        values.put(TITLE, items.getTitle());
        values.put(ALCCODE, items.getAlccode());
        values.put(CAPACITY, items.getCapacity());
        values.put(VOLUME, items.getVolume());
        values.put(FAR1, items.getFar1());
        values.put(FAR2, items.getFar2());
        values.put(NUMS, String.valueOf(items.getNums()));
        values.put(FACTNUMS, items.getFactnums());
        values.put(BARCODE, items.getCode());

        db.insert(FOOTER, null, values);
        db.close();
    }

    @Override
    public void deleteFooter(String id){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(FOOTER, DOCID	+ "	= ?", new String[] { id });
    }

    @Override
    public void addNewALC(ALC alc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DOCID, alc.getDocid());
        values.put(ITEMID, alc.getItemid());
        values.put(ALCMARK, alc.getAlc());
        values.put(STATUS, alc.getStatus());

        db.insert(ALCT, null, values);
        db.close();
    }

    @Override
    public void addUserInfo(Settings info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ZNAC, info.getZn());
        values.put(TEXT, info.getText());

        db.insert(PROFILE, null, values);
        db.close();
    }

    @Override
    public void deleteALC(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ALCT, DOCID + "	= ?", new String[]{id});
    }

    @Override
    public ArrayList<TTM> getAllTTM() {
        ArrayList<TTM> list = new ArrayList<TTM>();
        String selectQuery = "SELECT  * FROM " + MAIN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TTM obj = new TTM();
                obj.setId(Integer.parseInt(cursor.getString(0)));
                obj.setTitle(cursor.getString(1));
                obj.setFsrar(cursor.getString(2));
                obj.setGuid(cursor.getString(3));
                obj.setDate(cursor.getString(4));
                obj.setInn(cursor.getString(5));
                obj.setShortname(cursor.getString(6));
                obj.setStatus(cursor.getString(7));
                obj.setFileid(cursor.getString(8));
                obj.setType(cursor.getString(9));
                list.add(obj);
            } while (cursor.moveToNext());
        }

        return list;
    }

    @Override
    public ArrayList<Items> getAllItems(String id) {
        ArrayList<Items> list = new ArrayList<Items>();
        String selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + DOCID + " = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Items obj = new Items();

                obj.setId(Integer.parseInt(cursor.getString(0)));
                obj.setDocid(cursor.getString(1));
                obj.setTitle(cursor.getString(2));
                obj.setAlccode(cursor.getString(3));
                obj.setCapacity(cursor.getString(4));
                obj.setVolume(cursor.getString(5));
                obj.setFar1(cursor.getString(6));
                obj.setFar2(cursor.getString(7));
                obj.setNums(Integer.parseInt(cursor.getString(8)));
                obj.setFactnums(cursor.getString(9));
                obj.setCode(cursor.getString(10));

                list.add(obj);
            } while (cursor.moveToNext());
        }

        return list;
    }

    @Override
    public String getUserInfo(String info) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PROFILE, new String[] { KEY_ID,
                        ZNAC, TEXT}, ZNAC + "=?",
                new String[] { info }, null, null, null, null);
        String inf = "";
        if (cursor != null){
            cursor.moveToFirst();
            inf =  cursor.getString(2);
        } else {
            inf = "";
        }

        return inf;
    }

    @Override
    public int findTTNByFileId(String fileId) {

        String selectQuery = null;
        selectQuery = "SELECT  * FROM " + MAIN + " WHERE " + FILEID + " = '" + fileId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 0) {
            return -1;
        }
        return 1;
    }

    @Override
    public int findTTNByGuid(String guid) {
        String selectQuery = null;
        selectQuery = "SELECT  * FROM " + MAIN + " WHERE " + GUID + " = '" + guid + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 0) {
            return -1;
        }
        return 1;
    }

    @Override
    public String findALC(String alc, String id) {

        String selectQuery = null;
        String id_alc = null;
        String pdf417 = alc;
        int type = 0;

        if(alc.length() == 68) {
            alc = new BigInteger(alc.substring(3, 18), 36).toString();
            if(alc.length() < 19) {
                for(int i = 0; i < 20 - alc.length(); i++) {
                    alc = "0" + alc;
                }
            }
            selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + ALCCODE + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";
        } else if(alc.length() > 12 && alc.length() < 20) {
            type = 1;
            selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + BARCODE + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";
        } else {
            type = 2;
            selectQuery = "SELECT  * FROM " + ALCT + " WHERE " + ALCMARK + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 0) {
            return "-1";
        }

        if (cursor.moveToFirst()) {
            if(type == 0 && cursor.getString(4).equals("1")) {
                return "-2";
            } else if(type == 0) {
                id_alc = cursor.getString(2);
                updateItemStatus(id_alc);
                updateAlcStatus(alc);
            }
            if(type == 2) {
                id_alc = cursor.getString(0);
                selectQuery = "SELECT  * FROM " + ALCT + " WHERE " + ALCMARK + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";
                db = this.getWritableDatabase();
                cursor = db.rawQuery(selectQuery, null);
                if(cursor.getCount() != 0) {
                    return "-2";
                } else {
                    ContentValues values = new ContentValues();
                    selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + ALCCODE + " = '" + alc + "'";

                    db = this.getWritableDatabase();
                    cursor = db.rawQuery(selectQuery, null);

                    if (cursor.moveToFirst()) {
                        do {

                            values.put(FACTNUMS, Integer.valueOf(cursor.getString(9)) + 1);

                        } while (cursor.moveToNext());
                    }

                    db.update(FOOTER, values, ID 	+ "	= ?", new String[] { id });
                    addNewALC(new ALC( id, id_alc, pdf417, "1"));
                }
            }
        }
        return id_alc;
    }

    @Override
    public String findALCByMatrix(String alc, String id) {

        String selectQuery = "SELECT  * FROM " + ALCT + " WHERE " + ALCMARK + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";
        String id_alc = null;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 0) {
            return "-1";
        }

        if (cursor.moveToFirst()) {
            id_alc = cursor.getString(0);
            if(cursor.getString(4).equals("0")) {
                return "-2";
            } else {
                ContentValues values = new ContentValues();
                selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + ALCCODE + " = '" + alc + "'";

                db = this.getWritableDatabase();
                cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {

                        values.put(FACTNUMS, Integer.valueOf(cursor.getString(9)) + 1);

                    } while (cursor.moveToNext());
                }

                db.update(FOOTER, values, ID 	+ "	= ?", new String[] { id });
                updateAlcStatus(alc);
            }
        }
        return id_alc;
    }

    @Override
    public String findALCByPDF417(String alc, String id, boolean type) {

        String selectQuery = null;
        String pdf417 = null;

        if(type) {
            pdf417 = alc.substring(3, 19);
            pdf417 = new BigInteger(pdf417, 36).toString();
            if(pdf417.length() < 19) {
                for(int i = 0; i < 20 - pdf417.length(); i++) {
                    pdf417 = "0" + pdf417;
                }
            }
            selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + ALCCODE + " = '" + pdf417 + "' AND " + DOCID + " = '" + id + "'";
        } else {
            selectQuery = "SELECT  * FROM " + ALCT + " WHERE " + ALCMARK + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 0) {
            return "-1";
        }

        if (cursor.moveToFirst() && type) {
            ContentValues values = new ContentValues();
            String id_alc = cursor.getString(0);

            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    values.put(FACTNUMS, Integer.valueOf(cursor.getString(9)) + 1);

                } while (cursor.moveToNext());
            }

            db.update(FOOTER, values, ID 	+ "	= ?", new String[] { id_alc });
            return id_alc;
        }

        return "-2";
    }

    @Override
    public String findALCByShtrih(String alc, String id) {

        String selectQuery = null;
        String id_alc = null;
        selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + BARCODE + " = '" + alc + "' AND " + DOCID + " = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 0) {
            return "-1";
        }

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + BARCODE + " = '" + alc + "'";
            id_alc = cursor.getString(0);

            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    values.put(FACTNUMS, Integer.valueOf(cursor.getString(9)) + 1);

                } while (cursor.moveToNext());
            }

            db.update(FOOTER, values, BARCODE 	+ "	= ?", new String[] { alc });
        }
        return id_alc;
    }

    @Override
    public void updateAlcStatus(String alc){
        ContentValues values = new ContentValues();
        values.put(STATUS, "1");
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(ALCT, values, ALCMARK	+ "	= ?", new String[] { alc });
    }

    @Override
    public void updateItemStatus(String id){
        ContentValues values = new ContentValues();
        String selectQuery = "SELECT  * FROM " + FOOTER + " WHERE " + ID + " = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                values.put(FACTNUMS, Integer.valueOf(cursor.getString(9)) + 1);

            } while (cursor.moveToNext());
            }

        db.update(FOOTER, values, ID 	+ "	= ?", new String[] { id });
    }

    @Override
    public int updateUserInfo(Settings contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TEXT, contact.getText());

        return db.update(PROFILE, values, ZNAC + " = ?",
                new String[] { String.valueOf(contact.getZn()) });
    }


//
//    @Override
//    public String getUserInfo(String info) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(PROFILE, new String[] { KEY_ID,
//                        ZNAC, TEXT}, ZNAC + "=?",
//                new String[] { info }, null, null, null, null);
//        String inf = "";
//        if (cursor != null){
//            cursor.moveToFirst();
//            inf =  cursor.getString(2);
//        } else {
//            inf = "";
//        }
//
//        return inf;
//    }
//
//

//
//    @Override
//    public int updateUserInfo(Settings contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(TEXT, contact.getText());
//
//        return db.update(PROFILE, values, ZNAC + " = ?",
//                new String[] { String.valueOf(contact.getZn()) });
//    }
//
//    @Override
//    public void deleteAll() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete("profile", null, null);
//        db.close();
//    }
}
