package com.example.petcircle_proyectopsm.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.petcircle_proyectopsm.Image
import com.example.petcircle_proyectopsm.Post
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.CreatePost
import java.io.ByteArrayOutputStream
import java.io.InputStream
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

            val createUnsyncPostsTable:String = "CREATE TABLE " + SetDB.tblUnsyncPosts.TABLE_NAME + "(" +
                    SetDB.tblUnsyncPosts.PostId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SetDB.tblUnsyncPosts.UserId + " INTEGER," +
                    SetDB.tblUnsyncPosts.CategoryId + " INTEGER," +
                    SetDB.tblUnsyncPosts.Title + " TEXT," +
                    SetDB.tblUnsyncPosts.Description + " TEXT," +
                    SetDB.tblUnsyncPosts.CreationDate + " TEXT," +
                    SetDB.tblUnsyncPosts.UpdatedDate + " TEXT," +
                    SetDB.tblUnsyncPosts.Status + " INTEGER)"

            db?.execSQL(createUnsyncPostsTable)

            val createUnsyncImagesTable:String = "CREATE TABLE " + SetDB.tblUnsyncImages.TABLE_NAME + "(" +
                    SetDB.tblUnsyncImages.ImgId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SetDB.tblUnsyncImages.PostId + " INTEGER," +
                    SetDB.tblUnsyncImages.Img + " BLOB)"

            db?.execSQL(createUnsyncImagesTable)

            val createPostsTable:String = "CREATE TABLE " + SetDB.tblPosts.TABLE_NAME + "(" +
                    SetDB.tblPosts.PostId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SetDB.tblPosts.UserId + " INTEGER," +
                    SetDB.tblPosts.CategoryId + " INTEGER," +
                    SetDB.tblPosts.Title + " TEXT," +
                    SetDB.tblPosts.Description + " TEXT," +
                    SetDB.tblPosts.CreationDate + " TEXT," +
                    SetDB.tblPosts.UpdatedDate + " TEXT," +
                    SetDB.tblPosts.Status + " INTEGER)"

            db?.execSQL(createPostsTable)

            val createImagesTable:String = "CREATE TABLE " + SetDB.tblImages.TABLE_NAME + "(" +
                    SetDB.tblImages.ImgId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SetDB.tblImages.PostId + " INTEGER," +
                    SetDB.tblImages.Img + " BLOB)"

            db?.execSQL(createImagesTable)

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

    public fun getUser():User?{
        var user: User? = null
        val database:SQLiteDatabase = this.writableDatabase

        val columns:Array<String> = arrayOf(
            SetDB.tblUsers.UserId,
            SetDB.tblUsers.FullName,
            SetDB.tblUsers.Password,
            SetDB.tblUsers.PhoneNumber,
            SetDB.tblUsers.NickName,
            SetDB.tblUsers.Img,
            SetDB.tblUsers.Email,
            SetDB.tblUsers.Status,
            SetDB.tblUsers.CreationDate,
            SetDB.tblUsers.UpdatedDate
            )

        val data = database.query(SetDB.tblUsers.TABLE_NAME,
            columns, null, null, null, null, null)

        if (data.moveToFirst()) {
            user = User(
                UserId = data.getInt(data.getColumnIndexOrThrow(SetDB.tblUsers.UserId)),
                FullName = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.FullName)),
                Password = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.Password)),
                PhoneNumber = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.PhoneNumber)),
                NickName = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.NickName)),
                Img = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.Img)),
                Email = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.Email)),
                Status = data.getInt(data.getColumnIndexOrThrow(SetDB.tblUsers.Status)),
                CreationDate = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.CreationDate)),
                UpdatedDate = data.getString(data.getColumnIndexOrThrow(SetDB.tblUsers.UpdatedDate))
            )
        }
        data.close()
        return user
    }

    @SuppressLint("Range")
    public fun getUnsyncPosts():MutableList<Post>{
        val List:MutableList<Post> = ArrayList()

        val dataBase:SQLiteDatabase = this.writableDatabase

        val columns:Array<String> = arrayOf(
            SetDB.tblUnsyncPosts.PostId,
            SetDB.tblUnsyncPosts.UserId,
            SetDB.tblUnsyncPosts.CategoryId,
            SetDB.tblUnsyncPosts.Title,
            SetDB.tblUnsyncPosts.Description,
            SetDB.tblUnsyncPosts.CreationDate,
            SetDB.tblUnsyncPosts.UpdatedDate,
            SetDB.tblUnsyncPosts.Status)

        val data = dataBase.query(
            SetDB.tblUnsyncPosts.TABLE_NAME,
            columns,
            null, null, null, null, null)

        if(data.moveToFirst()){
            do{
                val post= Post(
                    PostId = 0,
                    UserId = data.getInt(data.getColumnIndex(SetDB.tblUnsyncPosts.UserId)),
                    CategoryId = data.getInt(data.getColumnIndex(SetDB.tblUnsyncPosts.CategoryId)),
                    Title = data.getString(data.getColumnIndexOrThrow(SetDB.tblUnsyncPosts.Title)),
                    Description = data.getString(data.getColumnIndexOrThrow(SetDB.tblUnsyncPosts.Description)),
                    CreationDate = data.getString(data.getColumnIndexOrThrow(SetDB.tblUnsyncPosts.CreationDate)),
                    UpdatedDate = data.getString(data.getColumnIndexOrThrow(SetDB.tblUnsyncPosts.UpdatedDate)),
                    Status = data.getInt(data.getColumnIndex(SetDB.tblUnsyncPosts.Status)),
                    CategoryName = "",
                    Images = getUnsyncImagesByPostId(data.getInt(data.getColumnIndex(SetDB.tblUnsyncPosts.PostId)))
                )
                List.add(post)
            }while (data.moveToNext())
        }
        return List
    }

    @SuppressLint("Range")
    public fun getUnsyncImagesByPostId(intID:Int):List<Image>{
        val List:MutableList<Image> = ArrayList()

        val dataBase:SQLiteDatabase = this.writableDatabase

        val columns:Array<String> = arrayOf(
            SetDB.tblUnsyncImages.ImgId,
            SetDB.tblUnsyncImages.PostId,
            SetDB.tblUnsyncImages.Img)

        val where:String =  SetDB.tblUnsyncImages.PostId + "= ${intID.toString()}"

        val data = dataBase.query(
            SetDB.tblUnsyncImages.TABLE_NAME,
            columns,
            where, null, null, null, null)

        if(data.moveToFirst()){
            do{
                val image= Image(
                    ImgId = 0,
                    PostId = 0,
                    Img = convertBlobToBase64(data.getBlob(data.getColumnIndex(SetDB.tblUnsyncImages.Img)))
                )
                List.add(image)
            }while (data.moveToNext())
        }
        return List;
    }

    public fun saveUnsyncPost(post: Post): Long {

        val dataBase:SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()
        var postId: Long = -1

        values.put(SetDB.tblUnsyncPosts.UserId, post.UserId)
        values.put(SetDB.tblUnsyncPosts.CategoryId, post.CategoryId)
        values.put(SetDB.tblUnsyncPosts.Title, post.Title)
        values.put(SetDB.tblUnsyncPosts.Description, post.Description)
        values.put(SetDB.tblUnsyncPosts.CreationDate, post.CreationDate)
        values.put(SetDB.tblUnsyncPosts.UpdatedDate, post.UpdatedDate)
        values.put(SetDB.tblUnsyncPosts.Status, post.Status)

        try{
            postId = dataBase.insert(SetDB.tblUnsyncPosts.TABLE_NAME, null, values)

            if(postId == -1L){
                Log.e("DatabaseError", "Failed to insert post.")
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }
            else {
                Log.d("DatabaseSuccess", "Post saved successfully with ID: $postId")
                Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()
            }

        }catch (e: Exception){
            Log.e("Execption", e.toString())
        } finally {
            dataBase.close()
        }
        return postId
    }

    public fun saveUnsyncImage(img: Image):Boolean{

        val dataBase:SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()
        var boolResult:Boolean =  true

        values.put(SetDB.tblUnsyncImages.PostId, img.PostId.toInt())
        values.put(SetDB.tblUnsyncImages.Img, base64ToBlob(img.Img))

        try{
            val result = dataBase.insert(SetDB.tblUnsyncImages.TABLE_NAME, null, values)

            if (result == (0).toLong()) {
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()
            }

        }catch (e: Exception){
            Log.e("Exception", e.toString())
            boolResult =  false
        }

        dataBase.close()

        return boolResult
    }

    public fun deleteUnsyncPost(postId:Int):Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{

            val where:String =  SetDB.tblUnsyncPosts.PostId + "=?"
            val _success = db.delete(SetDB.tblUnsyncPosts.TABLE_NAME, where, arrayOf(postId.toString()))
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }

    public fun deleteUnsyncImages(postId:Int):Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{

            val where:String =  SetDB.tblUnsyncImages.PostId + "=?"
            val _success = db.delete(SetDB.tblUnsyncImages.TABLE_NAME, where, arrayOf(postId.toString()))
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }

    fun convertBlobToBase64(blob: ByteArray): String {
        return try{
            Base64.encodeToString(blob, Base64.DEFAULT)
        }
        catch(e: Exception){
            Log.e("ImageConversion", "Error converting image to Base64", e)
            ""
        }
    }

    fun base64ToBlob(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
    }
}