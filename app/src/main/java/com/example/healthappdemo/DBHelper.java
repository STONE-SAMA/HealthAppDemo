package com.example.healthappdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper dbHelper;
    private static int DBVersion = 1;


    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    static synchronized DBHelper getInstance(Context context){
        if (dbHelper == null){
            dbHelper = new DBHelper(context,"HealthApp.db",null,DBVersion);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户表
        String sql_user = "create table if not exists user(userID char(11) primary key,userPassword text," +
                "userName text,userSex text,userAge integer,userWeight double,userHeight double)";
        db.execSQL(sql_user);
        //食物信息表
        String sql_food = "create table if not exists food(foodID char(5) primary key,foodName text,foodEnergy integer,foodImg text,foodLevelImg text)";
        db.execSQL(sql_food);
        //运动信息表
        String sql_sport = "create table if not exists sport(sportID char(5) primary key,sportName text,sportEnergy integer,sportImg text)";
        db.execSQL(sql_sport);
        //用户摄入能量信息表
        String sql_take_energy = "create table if not exists userEnergyTake(userID char(11),engDate text,engType char(1),actID char(5),engNum int)";
        db.execSQL(sql_take_energy);
        //推荐用户摄入能量表
        String sql_rec_energy = "create table if not exists userRecEnergy(userID char(11) primary key,remdEgyTotal integer,remdEgyBreak text," +
                "remdEgyLunch text,remdEgyDinner text)";
        db.execSQL(sql_rec_energy);

        dataAdd(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //添加某日某项数据到数据库
    public void energyAdd(ContentValues cv){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //判断是否已经存在，若存在只加数量
        if(checkExists(cv) != 0){
            //修改数量
            String userID = cv.getAsString("userID");
            String date = cv.getAsString("engDate");
            String type = cv.getAsString("engType");
            String actID = cv.getAsString("actID");

            int num = cv.getAsInteger("engNum");
            num += checkExists(cv);
            ContentValues values = new ContentValues();
            values.put("engNum",num);

            sqLiteDatabase.update("userEnergyTake",values," userID = ? and engDate = ? and engType = ? and actID = ?",
                    new String[]{userID,date,type,actID});
        }else{
            //直接添加
            sqLiteDatabase.insert("userEnergyTake",null,cv);
        }
        sqLiteDatabase.close();
    }

    public int checkExists(ContentValues cv){
        int result = 0;
        String userID = cv.getAsString("userID");
        String date = cv.getAsString("engDate");
        String type = cv.getAsString("engType");
        String actID = cv.getAsString("actID");
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "select * from userEnergyTake where userID = ? and engDate = ? and engType = ? and actID = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{userID,date,type,actID});
        if(cursor.getCount() == 0){
            return result;//不存在
        }else{
            while (cursor.moveToNext()){
                result = cursor.getInt(cursor.getColumnIndex("engNum"));
            }
            return result;
        }
    }

    //搜索用户某日能量数据
    public Cursor search(String userID, String date, String type){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "select * from userEnergyTake where userID = ? and engDate = ? and engType = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{userID,date,type});
        return cursor;
    }

    //获取食物或是运动记录的详细信息
    public Cursor actInfo(String ID){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        if(ID.substring(0,1).equals("9")){//运动
            String sql = "select * from sport where sportID = ?";
            Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{ID});
            return cursor;
        }else{//食物
            String sql = "select * from food where foodID = ?";
            Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{ID});
            return cursor;
        }
    }

    //判断某日是否存在数据
    public Cursor chaeck(String userID, String date){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "select * from userEnergyTake where userID = ? and engDate = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{userID,date});
        return cursor;
    }

    //添加运动或是食物searchview
    public Cursor searchFood(String text){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "select * from food where foodID like ? or foodName like ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{"%"+text+"%","%"+text+"%"});
        return cursor;
    }
    public Cursor searchSport(String text){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "select * from sport where sportID like ? or sportName like ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{"%"+text+"%","%"+text+"%"});
        return cursor;
    }

    //删除食物、运动记录
    public void deleteFoodRecord(String userID,String date,String actID,String type){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("userEnergyTake","userID = ? and engDate = ? and actID = ? and engType = ?",new String[]{userID,date,actID,type});
        sqLiteDatabase.close();
    }


    //添加初始数据到数据库
    public void dataAdd(SQLiteDatabase db){
        /**
         * 运动信息
         */

        //SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sportID","99001");
        cv.put("sportName","跑步");
        cv.put("sportEnergy",553);
        cv.put("sportImg","run");
        db.insert("sport",null,cv);

        ContentValues cv1 = new ContentValues();
        cv1.put("sportID","99002");
        cv1.put("sportName","散步");
        cv1.put("sportEnergy",213);
        cv1.put("sportImg","walk");
        db.insert("sport",null,cv1);

        ContentValues cv2 = new ContentValues();
        cv2.put("sportID","99003");
        cv2.put("sportName","跳绳");
        cv2.put("sportEnergy",583);
        cv2.put("sportImg","ropeskip");
        db.insert("sport",null,cv2);

        ContentValues cv3 = new ContentValues();
        cv3.put("sportID","99004");
        cv3.put("sportName","游泳");
        cv3.put("sportEnergy",553);
        cv3.put("sportImg","swim");
        db.insert("sport",null,cv3);

        ContentValues cv4 = new ContentValues();
        cv4.put("sportID","99005");
        cv4.put("sportName","瑜伽");
        cv4.put("sportEnergy",201);
        cv4.put("sportImg","yoga");
        db.insert("sport",null,cv4);

        ContentValues cv5 = new ContentValues();
        cv5.put("sportID","99006");
        cv5.put("sportName","骑行");
        cv5.put("sportEnergy",583);
        cv5.put("sportImg","cycle");
        db.insert("sport",null,cv5);

        ContentValues cv6 = new ContentValues();
        cv6.put("sportID","99007");
        cv6.put("sportName","篮球");
        cv6.put("sportEnergy",553);
        cv6.put("sportImg","basketball");
        db.insert("sport",null,cv6);

        ContentValues cv7 = new ContentValues();
        cv7.put("sportID","99008");
        cv7.put("sportName","足球");
        cv7.put("sportEnergy",603);
        cv7.put("sportImg","football");
        db.insert("sport",null,cv7);

        ContentValues cv8 = new ContentValues();
        cv8.put("sportID","99009");
        cv8.put("sportName","排球");
        cv8.put("sportEnergy",472);
        cv8.put("sportImg","volleyball");
        db.insert("sport",null,cv8);

        ContentValues cv9 = new ContentValues();
        cv9.put("sportID","99010");
        cv9.put("sportName","网球");
        cv9.put("sportEnergy",402);
        cv9.put("sportImg","tennis");
        db.insert("sport",null,cv9);

        ContentValues cv10 = new ContentValues();
        cv10.put("sportID","99011");
        cv10.put("sportName","羽毛球");
        cv10.put("sportEnergy",452);
        cv10.put("sportImg","badminton");
        db.insert("sport",null,cv10);

        ContentValues cv11 = new ContentValues();
        cv11.put("sportID","99012");
        cv11.put("sportName","乒乓球");
        cv11.put("sportEnergy",302);
        cv11.put("sportImg","tabletennis");
        db.insert("sport",null,cv11);

        /**
         * 食物信息
         */

        ContentValues cv12 = new ContentValues();
        cv12.put("foodID","20001");
        cv12.put("foodName","豆浆");
        cv12.put("foodEnergy",34);
        cv12.put("foodImg","doujiang");
        cv12.put("foodLevelImg","point_green");
        db.insert("food",null,cv12);

        ContentValues cv13 = new ContentValues();
        cv13.put("foodID","20002");
        cv13.put("foodName","荷包蛋");
        cv13.put("foodEnergy",195);
        cv13.put("foodImg","hebaodan");
        cv13.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv13);

        ContentValues cv14 = new ContentValues();
        cv14.put("foodID","20003");
        cv14.put("foodName","蛋炒饭");
        cv14.put("foodEnergy",143);
        cv14.put("foodImg","danchaofan");
        cv14.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv14);

        ContentValues cv15 = new ContentValues();
        cv15.put("foodID","20004");
        cv15.put("foodName","牛奶");
        cv15.put("foodEnergy",54);
        cv15.put("foodImg","niunai");
        cv15.put("foodLevelImg","point_green");
        db.insert("food",null,cv15);

        ContentValues cv16 = new ContentValues();
        cv16.put("foodID","20005");
        cv16.put("foodName","馒头");
        cv16.put("foodEnergy",223);
        cv16.put("foodImg","mantou");
        cv16.put("foodLevelImg","point_green");
        db.insert("food",null,cv16);

        ContentValues cv17 = new ContentValues();
        cv17.put("foodID","20006");
        cv17.put("foodName","饺子");
        cv17.put("foodEnergy",240);
        cv17.put("foodImg","jiaozi");
        cv17.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv17);

        ContentValues cv18 = new ContentValues();
        cv18.put("foodID","20007");
        cv18.put("foodName","鸡蛋");
        cv18.put("foodEnergy",144);
        cv18.put("foodImg","jidan");
        cv18.put("foodLevelImg","point_green");
        db.insert("food",null,cv18);

        ContentValues cv19 = new ContentValues();
        cv19.put("foodID","20008");
        cv19.put("foodName","米饭");
        cv19.put("foodEnergy",116);
        cv19.put("foodImg","mifan");
        cv19.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv19);

        ContentValues cv20 = new ContentValues();
        cv20.put("foodID","20009");
        cv20.put("foodName","汉堡");
        cv20.put("foodEnergy",249);
        cv20.put("foodImg","hanbao");
        cv20.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv20);

        ContentValues cv21 = new ContentValues();
        cv21.put("foodID","20010");
        cv21.put("foodName","烤鸡翅");
        cv21.put("foodEnergy",185);
        cv21.put("foodImg","kaojichi");
        cv21.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv21);

        ContentValues cv22 = new ContentValues();
        cv22.put("foodID","20011");
        cv22.put("foodName","西瓜");
        cv22.put("foodEnergy",31);
        cv22.put("foodImg","xigua");
        cv22.put("foodLevelImg","point_green");
        db.insert("food",null,cv22);

        ContentValues cv23 = new ContentValues();
        cv23.put("foodID","20012");
        cv23.put("foodName","香蕉");
        cv23.put("foodEnergy",93);
        cv23.put("foodImg","xiangjiao");
        cv23.put("foodLevelImg","point_green");
        db.insert("food",null,cv23);

        ContentValues cv24 = new ContentValues();
        cv24.put("foodID","20013");
        cv24.put("foodName","苹果");
        cv24.put("foodEnergy",53);
        cv24.put("foodImg","pingguo");
        cv24.put("foodLevelImg","point_green");
        db.insert("food",null,cv24);

        ContentValues cv25 = new ContentValues();
        cv25.put("foodID","20014");
        cv25.put("foodName","糖醋鱼");
        cv25.put("foodEnergy",113);
        cv25.put("foodImg","tangcuyu");
        cv25.put("foodLevelImg","point_green");
        db.insert("food",null,cv25);

        ContentValues cv26 = new ContentValues();
        cv26.put("foodID","20015");
        cv26.put("foodName","牛肉面");
        cv26.put("foodEnergy",102);
        cv26.put("foodImg","niuroumian");
        cv26.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv26);

        ContentValues cv27 = new ContentValues();
        cv27.put("foodID","20016");
        cv27.put("foodName","肉夹馍");
        cv27.put("foodEnergy",228);
        cv27.put("foodImg","roujiamo");
        cv27.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv27);

        ContentValues cv28 = new ContentValues();
        cv28.put("foodID","20017");
        cv28.put("foodName","土豆丝");
        cv28.put("foodEnergy",119);
        cv28.put("foodImg","tudousi");
        cv28.put("foodLevelImg","point_green");
        db.insert("food",null,cv28);

        ContentValues cv29 = new ContentValues();
        cv29.put("foodID","20018");
        cv29.put("foodName","土豆烧鸡");
        cv29.put("foodEnergy",122);
        cv29.put("foodImg","tudoushaoji");
        cv29.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv29);

        ContentValues cv30 = new ContentValues();
        cv30.put("foodID","20019");
        cv30.put("foodName","鱼香全茄");
        cv30.put("foodEnergy",139);
        cv30.put("foodImg","yuxiangqiezi");
        cv30.put("foodLevelImg","point_red");
        db.insert("food",null,cv30);

        ContentValues cv31 = new ContentValues();
        cv31.put("foodID","20020");
        cv31.put("foodName","披萨");
        cv31.put("foodEnergy",235);
        cv31.put("foodImg","pisa");
        cv31.put("foodLevelImg","point_yellow");
        db.insert("food",null,cv31);

        /**
         * 用户能量推荐表
         */
        ContentValues cv99 = new ContentValues();
        cv99.put("userID","18851730833");
        cv99.put("remdEgyTotal",2500);
        cv99.put("remdEgyBreak","688-841");
        cv99.put("remdEgyLunch","917-1121");
        cv99.put("remdEgyDinner","688-841");
        db.insert("userRecEnergy",null,cv99);

    }

}
