package com.example.petcircle_proyectopsm.db

class SetDB {
    companion object{
        val DB_NAME =  "PetCircle"
        val DB_VERSION =  6
    }

    abstract class tblUsers{
        //DEFINIMOS LOS ATRIBUTOS DE LA CLASE USANDO CONTANTES
        companion object{
            val TABLE_NAME = "Users"
            val UserId =  "UserId"
            val FullName =  "FullName"
            val Password = "Password"
            val PhoneNumber =  "PhoneNumber"
            val NickName =  "NickName"
            val Img =  "Img"
            val Email =  "Email"
            val Status =  "Status"
            val CreationDate =  "CreationDate"
            val UpdatedDate =  "UpdatedDate"
        }
    }

    abstract class tblCategories{
        companion object{
            val TABLE_NAME = "Categories"
            val CategoryId =  "CategoryId"
            val Name =  "Name"
            val Description =  "Description"
            val Status =  "Status"
        }
    }

    abstract class tblUnsyncPosts{
        companion object{
            val TABLE_NAME = "UnsyncPosts"
            val PostId = "PostId"
            val UserId = "UserId"
            val CategoryId = "CategoryId"
            val Title = "Title"
            val Description = "Description"
            val CreationDate = "CreationDate"
            val UpdatedDate = "UpdatedDate"
            val Status = "Status"
        }
    }

    abstract class tblPosts{
        companion object{
            val TABLE_NAME = "Posts"
            val PostId = "PostId"
            val UserId = "UserId"
            val CategoryId = "CategoryId"
            val Title = "Title"
            val Description = "Description"
            val CreationDate = "CreationDate"
            val UpdatedDate = "UpdatedDate"
            val Status = "Status"
        }
    }

    abstract class tblUnsyncImages{
        companion object{
            val TABLE_NAME = "UnsyncImages"
            val ImgId = "ImgId"
            val PostId = "PostId"
            val Img = "Img"
        }
    }

    abstract class tblImages{
        companion object{
            val TABLE_NAME = "Images"
            val ImgId = "ImgId"
            val PostId = "PostId"
            val Img = "Img"
        }
    }
}