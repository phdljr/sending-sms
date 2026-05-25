package com.example.android_sms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val PICK_CONTACT = 1001

    private var selectedNumbers = listOf<String>()
    private var selectedNames = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageInput = findViewById<EditText>(R.id.messageInput)
        val pickButton = findViewById<Button>(R.id.pickButton)
        val sendButton = findViewById<Button>(R.id.sendButton)

        pickButton.setOnClickListener {
            val intent = Intent(this, ContactSelectActivity::class.java)

            intent.putStringArrayListExtra(
                "selected_numbers",
                ArrayList(selectedNumbers)
            )

            startActivityForResult(intent, PICK_CONTACT)
        }

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()

            if (selectedNumbers.isEmpty()) {
                Toast.makeText(this, "선택된 연락처 없음", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("문자 전송 확인")
                .setMessage("${selectedNumbers.size}명에게 전송하시겠습니까?")
                .setPositiveButton("전송") { _, _ ->
                    sendSms(message)
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    private fun sendSms(message: String) {
        val smsManager = getSystemService(SmsManager::class.java)

        for (number in selectedNumbers) {
            val parts = smsManager.divideMessage(message)

            smsManager.sendMultipartTextMessage(
                number,
                null,
                parts,
                null,
                null
            )
        }

        Toast.makeText(this, "전송 완료", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {

            selectedNumbers = data
                ?.getStringArrayListExtra("selected_numbers")
                ?: emptyList()

            selectedNames = data
                ?.getStringArrayListExtra("selected_names")
                ?: emptyList()

            val selectedInfoText = findViewById<TextView>(R.id.selectedInfoText)
            selectedInfoText?.text = "선택된 사람: ${selectedNames.joinToString()}"

            Toast.makeText(this, "${selectedNumbers.size}명 선택됨", Toast.LENGTH_SHORT).show()
        }
    }
}