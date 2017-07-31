package android.infoglobo.com.criptografia

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import java.io.*
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        encrypt.setOnClickListener(this)
        dencrypt.setOnClickListener(this)
        open.setOnClickListener(this)
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }

    }

    @Throws(Exception::class)
    fun generateKey(password: String): ByteArray {
        val keyStart = password.toByteArray(charset("UTF-8"))

        val kgen = KeyGenerator.getInstance("AES")
        val sr = SecureRandom.getInstance("SHA1PRNG", "Crypto")
        sr.setSeed(keyStart)
        kgen.init(128, sr)
        val skey = kgen.generateKey()
        return skey.getEncoded()
    }

    @Throws(Exception::class)
    fun encodeFile(key: ByteArray, fileData: ByteArray?): ByteArray {

        val skeySpec = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)

        val encrypted = cipher.doFinal(fileData)

        return encrypted
    }

    @Throws(Exception::class)
    fun decodeFile(key: ByteArray, fileData: ByteArray?): ByteArray? {
        val skeySpec = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)

        val decrypted = cipher.doFinal(fileData)

        return decrypted
    }

    override fun onClick(id: View?) {
        when (id) {
            encrypt -> {
                val file = File("${Environment.getExternalStorageDirectory().path}/Globo/Teste.pdf")

                if(file.exists()) {
                    val byteArray = FileInputStream(file)
                            .readBytes()
                    val bos = BufferedOutputStream(FileOutputStream(file))
                    val yourKey = generateKey("password")
                    val filesBytes = encodeFile(yourKey, byteArray)
                    bos.write(filesBytes)
                    bos.flush()
                    bos.close()
                }
            }

            dencrypt -> {

                val file = File("${Environment.getExternalStorageDirectory().path}/Globo/Teste.pdf")
                if(file.exists()) {
                    val byteArray = FileInputStream(file)
                            .readBytes()
                    val yourKey = generateKey("password")
                    val decodedData = decodeFile(yourKey, byteArray)
                    val bos = BufferedOutputStream(FileOutputStream(file))
                    bos.write(decodedData)
                    bos.flush()
                    bos.close()
                }
            }

            open -> {
                val appFolder = File("${Environment.getExternalStorageDirectory().path}/Globo/Teste.pdf")
                val path = Uri.fromFile(appFolder)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(path, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Não é possivel abrir o arquivo",
                            Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}
