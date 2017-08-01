package android.infoglobo.com.criptografia

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }

    }

    override fun onClick(id: View?) {
        when (id) {
            encrypt -> {
                val file = File("${Environment.getExternalStorageDirectory().path}/Globo/Teste.pdf")//Arquivo a ser criptografado

                if (file.exists()) {
                    try {
                        val bos = BufferedOutputStream(FileOutputStream(file))
                        //Chamada do método responsavel por criar os arrays de bytes da chave de criptografia
                        val key = CryptoHelper.generateKey("password")
                        //Chamada do método responsavel por criptografar o arquivo, passando por paramentro o array de bytes da chave de criptografia
                        //e o array de bytes do arquivo a ser criptografado
                        val filesBytes = CryptoHelper.encodeFile(key, FileInputStream(file).readBytes())
                        bos.write(filesBytes)
                        bos.flush()
                        bos.close()
                    }catch (e: Exception){
                        Toast.makeText(context, "O arquivo já está descriptografado",
                                Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "O arquivo não existe",
                            Toast.LENGTH_SHORT).show()
                }
            }

            dencrypt -> {

                val file = File("${Environment.getExternalStorageDirectory().path}/Globo/Teste.pdf")
                if (file.exists()) {

                    try {
                        //Chamada do método responsavel por criar os arrays de bytes da chave de criptografia
                        val key = CryptoHelper.generateKey("password")
                        //Chamada do método responsavel por descriptografar o arquivo, passando por paramentro o array de bytes da chave de criptografia
                        //e o array de bytes do arquivo a ser descriptografado
                        val decodedData = CryptoHelper.decodeFile(key, FileInputStream(file).readBytes())
                        val bos = BufferedOutputStream(FileOutputStream(file))
                        bos.write(decodedData)
                        bos.flush()
                        bos.close()
                        Toast.makeText(context, "O arquivo foi descriptografado",
                                Toast.LENGTH_SHORT).show()
                    }catch (e: Exception){
                        Toast.makeText(context, "O arquivo já está descriptografado",
                                Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "O arquivo não existe",
                            Toast.LENGTH_SHORT).show()
                }
            }

            open -> {
                val file = File("${Environment.getExternalStorageDirectory().path}/Globo/Teste.pdf")
                if (file.exists()) {
                    val path = Uri.fromFile(file)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(path, "application/pdf")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "Não é possivel abrir o arquivo",
                                Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "O arquivo não existe",
                            Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
