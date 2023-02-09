package com.example.chapter2_7

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chapter2_7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val imageLoadLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {uriList->
        updateImages(uriList)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var imgAdapter: ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            title = "사진 가져오기"
            setSupportActionBar(this)
        }

        binding.loadImg.setOnClickListener {
            checkPermission()
        }

        binding.navigateFrameActivityButton.setOnClickListener {
            navigateToFrameActivity()
        }

        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_add -> {
                checkPermission()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun navigateToFrameActivity(){
        val images = imgAdapter.currentList.filterIsInstance<ImageItems.Image>().map {it.uri.toString()}.toTypedArray()
        val intent = Intent(this, FrameActivity::class.java).putExtra("images",images)
        startActivity(intent)
    }

    private fun initRecyclerView(){
        imgAdapter = ImageAdapter(object : ImageAdapter.ItemClickListener{
            override fun onLoadMoreClick() {
                checkPermission()
            }

        })

        binding.recyclerView.apply {
            adapter = imgAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

    }

    private fun checkPermission(){
        when{
            ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
            loadImage()
        }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)->{
                showPermissionInfoDialog()
            }
            else -> {
                requestReadExternalStorage()
            }
        }

    }

    private fun showPermissionInfoDialog(){
        AlertDialog.Builder(this).apply {
            setMessage("이미지를 가져오기 위해 외부 저장소 읽기권한 필요")
            setNegativeButton("취소",null)
            setPositiveButton("동의"){_,_->
                requestReadExternalStorage()
            }
        }.show()
    }

    private fun requestReadExternalStorage(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
            REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    private fun loadImage(){
        imageLoadLauncher.launch("image/*") //image타입의 모든 파일을 가져옴
    }

    private fun updateImages(uriList : List<Uri>){
        Log.i("updateImages","$uriList")
        val images = uriList.map { ImageItems.Image(it) }
        val updatedImages = imgAdapter.currentList.toMutableList().apply { addAll(images) }
        imgAdapter.submitList(updatedImages)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            REQUEST_READ_EXTERNAL_STORAGE -> {
                val resultCode = grantResults.firstOrNull() ?: PackageManager.PERMISSION_DENIED
                if(resultCode == PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }
            }
        }
    }

    companion object {
        const val REQUEST_READ_EXTERNAL_STORAGE = 100
    }

}