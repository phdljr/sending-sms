package com.example.android_sms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactSelectActivity : AppCompatActivity() {

    private val contactList = mutableListOf<Contact>()
    private val filteredList = mutableListOf<Contact>()
    private var preSelectedNumbers = listOf<String>()
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_select)

        preSelectedNumbers =
            intent.getStringArrayListExtra("selected_numbers") ?: emptyList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val searchInput = findViewById<EditText>(R.id.searchInput)

        contactList.addAll(
            loadContacts()
                .sortedBy { it.name }
                .map {
                    it.copy(
                        isSelected = preSelectedNumbers.contains(it.phone)
                    )
                }
        )
        filteredList.addAll(contactList)

        adapter = ContactAdapter(filteredList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().lowercase()
                filterContacts(query)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        confirmButton.setOnClickListener {
            returnSelectedContacts()
        }
    }

    private fun filterContacts(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(contactList)
        } else {
            filteredList.addAll(
                contactList.filter {
                    it.name.lowercase().contains(query) ||
                            it.phone.contains(query)
                }
            )
        }

        adapter.notifyDataSetChanged()
    }

    private fun loadContacts(): List<Contact> {
        val list = mutableListOf<Contact>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val phoneIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)

                list.add(
                    Contact(
                        name = name,
                        phone = phone.replace("-", "").trim()
                    )
                )
            }
        }

        return list.distinctBy { it.phone }
    }

    private fun returnSelectedContacts() {
        val selected = contactList.filter { it.isSelected }

        val resultIntent = Intent().apply {
            putStringArrayListExtra(
                "selected_numbers",
                ArrayList(selected.map { it.phone })
            )
            putStringArrayListExtra(
                "selected_names",
                ArrayList(selected.map { it.name })
            )
        }

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}