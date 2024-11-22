package com.example.petcircle_proyectopsm.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.petcircle_proyectopsm.model.User
import java.lang.Exception
class DbHelper (var context: Context): SQLiteOpenHelper(context, SetDB.DB_NAME, null, SetDB.DB_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        try {

            val createUsersTable:String = "CREATE TABLE " + SetDB.tblUsers.TABLE_NAME + "(" +
                    SetDB.tblUsers.UserId + " INTEGER PRIMARY KEY," +
                    SetDB.tblUsers.FullName + " TEXT," +
                    SetDB.tblUsers.Password + " TEXT," +
                    SetDB.tblUsers.PhoneNumber + " TEXT," +
                    SetDB.tblUsers.NickName + " TEXT," +
                    SetDB.tblUsers.Img + " BLOB," +
                    SetDB.tblUsers.Email + " TEXT," +
                    SetDB.tblUsers.Status + " INTEGER," +
                    SetDB.tblUsers.CreationDate + " TEXT," +
                    SetDB.tblUsers.UpdatedDate + " TEXT)"

            db?.execSQL(createUsersTable)

            val createCategoriesTable:String = "CREATE TABLE " + SetDB.tblCategories.TABLE_NAME + "(" +
                    SetDB.tblCategories.CategoryId + " INTEGER PRIMARY KEY," +
                    SetDB.tblCategories.Name + " TEXT," +
                    SetDB.tblCategories.Description + " TEXT," +
                    SetDB.tblCategories.Status + " INTEGER)"

            db?.execSQL(createCategoriesTable)

            Log.e("ENTRO","CREO TABLAS")

        }catch (e: Exception){
            Log.e("Execption", e.toString())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            // Eliminar tablas existentes
            db?.execSQL("DROP TABLE IF EXISTS ${SetDB.tblUsers.TABLE_NAME}")
            db?.execSQL("DROP TABLE IF EXISTS ${SetDB.tblCategories.TABLE_NAME}")

            // Volver a crear tablas
            onCreate(db)
            Log.e("Database Upgrade", "Base de datos actualizada de versión $oldVersion a $newVersion")
        } catch (e: Exception) {
            Log.e("Exception in onUpgrade", e.toString())
        }
    }

    public fun insertUser(user: User):Boolean{

        val dataBase:SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()
        var boolResult:Boolean =  true

        values.put(SetDB.tblUsers.UserId, user.UserId)
        values.put(SetDB.tblUsers.FullName, user.FullName)
        values.put(SetDB.tblUsers.Password, user.Password)
        values.put(SetDB.tblUsers.PhoneNumber, user.PhoneNumber)
        values.put(SetDB.tblUsers.NickName, user.NickName)
        values.put(SetDB.tblUsers.Img, user.Img)
        values.put(SetDB.tblUsers.Email, user.Email)
        values.put(SetDB.tblUsers.Status, user.Status)
        values.put(SetDB.tblUsers.CreationDate, user.CreationDate)
        values.put(SetDB.tblUsers.UpdatedDate, user.UpdatedDate)

        try {
            val result = dataBase.insert(SetDB.tblUsers.TABLE_NAME, null, values)

            if(result == (0).toLong()){
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            Log.e("Execption", e.toString())
            boolResult =  false
        }

        dataBase.close()

        return boolResult
    }

    public fun onLogOut() {
        try {
            val deleted = context.deleteDatabase(SetDB.DB_NAME)
            if (deleted) {
                Log.e("Database", "Base de datos eliminada exitosamente")
            } else {
                Log.e("Database", "No se pudo eliminar la base de datos")
            }
        } catch (e: Exception) {
            Log.e("Exception in onLogOut", e.toString())
        }
    }
}