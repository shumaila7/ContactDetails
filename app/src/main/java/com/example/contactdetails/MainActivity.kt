package com.example.contactdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.contactdetails.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private val CONTACT_CODE=2
    private val PERMISSION_CODE=1

    private lateinit var cursor1: Cursor
    private lateinit var cursor3: Cursor
    private lateinit var cursor2: Cursor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
       setContentView(binding.root)
        binding.fab.setOnClickListener {
            if(checkPermission()){
                pickContact()
            }
            else
            {
               requestPermission()
            }

        }
    }
    private fun checkPermission():Boolean{
        return ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED

    }
    private fun requestPermission(){
        val permission= arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this,permission,PERMISSION_CODE)

    }
    private fun pickContact(){
        val intent=Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent,CONTACT_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSION_CODE){
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                pickContact()
            }
            else
            {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            if(requestCode == CONTACT_CODE){
                binding.details.text=""
                var cursor1:Cursor
               val uri=data!!.data
                cursor1= contentResolver.query(uri!!,null,null,null,null)!!
                if(cursor1.moveToFirst()){
                    val contatID =cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val photo =cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
                   val number =cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    val idResult=number.toInt()

                  //  binding.details.append("ID: $contatID")
                    binding.details.append("\nName: $name")

                     if(photo!=null){
                      binding.profile.setImageURI(Uri.parse(photo))
                     }
                    else
                     {
                         binding.profile.setImageResource(R.drawable.ic_profile)
                     }
   if(idResult==1){
          cursor2= contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
              null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +contatID,
              null,
              null)!!

  while(cursor2.moveToNext()){
     val contactNumber=cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
     binding.details.append("\nPhone: $contactNumber")


  }
       cursor2.close()
   }
                    /////////////////////////

                    cursor3= contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " +contatID,
                        null,
                        null)!!

                    while(cursor3.moveToNext()){
                      //  val contactNumber=cursor3.getString(cursor3.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                      //  binding.details.append("\nPhone: $contactNumber")
                        val  email = cursor3.getString(cursor3.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                        binding.details.append("\nEmail: $email")

                    }
                    cursor3.close()
                    ////////////////
                    cursor1.close()
                }

            }

        }
        else
        {
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()

        }
    }

}